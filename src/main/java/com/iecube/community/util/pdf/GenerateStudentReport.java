package com.iecube.community.util.pdf;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.iecube3835.dto.StudentSubmitContentDetails;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.task.entity.StudentTaskDetailVo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;


import java.io.*;
import java.nio.file.Files;

public class GenerateStudentReport{

    private String genFileDir;

    public static Font TitleFont;
    public static Font TextFont1;
    public static Font TextFont;
    public static String fontPath = "/community/service/fonts/simfang.ttf";

    public GenerateStudentReport(String genFilePath){
        this.genFileDir = genFilePath;
    }

    public MultipartFile startGen(StudentDto studentDto, StudentTaskDetailVo studentTaskDetailVo, String studentData) throws IOException, DocumentException{
//        fontPath = ResourceUtils.getFile("classpath:fonts/simfang.ttf").getAbsolutePath();
        String FileName = studentTaskDetailVo.getProjectId() +"-"+studentTaskDetailVo.getTaskNum()+"-"+studentDto.getStudentName()
                +"-"+studentTaskDetailVo.getTaskName()+""+".pdf";
        String filePath = genFileDir+"/"+FileName;
            // 创建文档对象并设置页面大小
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        TitleFont = FontFactory.getFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
        TextFont1 = FontFactory.getFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10);
        TextFont = FontFactory.getFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8);
        // 添加学生信息
        Paragraph studentInfoParagraph = studentInfo(studentDto, studentTaskDetailVo);
        document.add(studentInfoParagraph);
        JsonNode tableJsonNode = genTableJsonNode(studentData);
        JsonNode questionSJsonNode = genQuestionJsonNode(studentData);
        // 添加表格
        if(tableJsonNode != null){
            for(JsonNode tableData: tableJsonNode){
                Paragraph nameParagraph = new Paragraph(tableData.get("name").asText(),TextFont1);
                Paragraph paramsParagraph = null;
                if(tableData.get("params").size()>0){
                    String params = "";
                    for(JsonNode param: tableData.get("params")){
                        params = params+param.get("name").asText()+"：" +param.get("value").asText()+"    ";
                    }
                    paramsParagraph = new Paragraph(params,TextFont);
                    paramsParagraph.setSpacingAfter(10); // 设置段落间距
                }
                PdfPTable table = createTable(tableData);

                if(nameParagraph!=null){
                    document.add(nameParagraph);
                }
                if(paramsParagraph!=null){
                    document.add(paramsParagraph);
                }
                if(table!=null){
                    document.add(table);
                }
                document.add(new Paragraph("\n")); // 添加空行分隔表格
            }

        }
        //添加思考题
        if(questionSJsonNode!=null){
            for(JsonNode question: questionSJsonNode){
                Paragraph quesAnswerParagraph = createQuestion(question);
                document.add(quesAnswerParagraph);
            }
        }
        document.close();

        File file = new File(genFileDir,FileName);
        MultipartFile multipartFile=new MultipartFile() {
            @Override
            public String getName() {
                return FileName;
            }

            @Override
            public String getOriginalFilename() {
                return FileName;
            }

            @Override
            public String getContentType() {
                return "application/pdf";
            }

            @Override
            public boolean isEmpty() {
                if (file.length() ==0) {
                    return true;
                }
                return false;
            }

            @Override
            public long getSize() {
                return file.length();
            }

            @Override
            public byte[] getBytes() throws IOException {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                return fileBytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                InputStream inputStream = new FileInputStream(file);
                return inputStream;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
            }
        };
        return multipartFile;
    }

    public static Paragraph studentInfo(StudentDto studentDto, StudentTaskDetailVo studentTaskDetailVo){
        String studentInfo = "姓名："+studentDto.getStudentName() +"    学号：" + studentDto.getStudentId() +"    实验："+
                studentTaskDetailVo.getTaskName();
        Paragraph paragraph =new Paragraph(studentInfo, TitleFont);
        paragraph.setAlignment(Paragraph.ALIGN_LEFT);
        paragraph.setIndentationLeft(20); // 设置左缩进距离
        paragraph.setIndentationRight(20); // 设置右缩进距离
        paragraph.setSpacingAfter(10); // 设置段落间距
        return paragraph;
    }

    public static PdfPTable createTable(JsonNode jsonNode){
        int columns = jsonNode.get("columnList").size();
        int rows = jsonNode.get("rowData").size();
        PdfPTable table = new PdfPTable(columns); // 创建表格对象
        table.setWidthPercentage(100); // 设置表格宽度为100%
        // 添加表头
        for (int j = 0; j < columns; j++) {
            PdfPCell headerCell = new PdfPCell(new Paragraph(jsonNode.get("columnList").get(j).get("label").asText(), TextFont));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell); // 添加表头单元格
        }
        // 添加表格内容
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String prop=jsonNode.get("columnList").get(j).get("prop").asText();
                String value="";
                if(jsonNode.get("rowData").get(i).get(prop) != null && jsonNode.get("rowData").get(i).get(prop).asText()!="null"){
                    value=jsonNode.get("rowData").get(i).get(prop).asText();
                }
                PdfPCell cell = new PdfPCell(new Paragraph(value, TextFont));
                table.addCell(cell); // 添加内容单元格
            }
        }
        return table;
    }

    public static Paragraph createQuestion(JsonNode question){
        String ques = question.get("question").asText();
        String answer = "";
        if(question.get("answer").asText()!="null" && question.get("answer")!=null){
            answer = question.get("answer").asText();
        }
        Paragraph quesAnswer = new Paragraph();

        Paragraph questionParagraph = new Paragraph(ques, TextFont1);
        questionParagraph.setSpacingAfter(10);
        Paragraph answerParagraph = new Paragraph(answer, TextFont);
        answerParagraph.setIndentationLeft(10); // 设置左缩进距离
        answerParagraph.setIndentationRight(10); // 设置右缩进距离
        answerParagraph.setSpacingAfter(10); // 设置段落间距

        quesAnswer.add(questionParagraph);
        quesAnswer.add(answerParagraph);
        return quesAnswer;
    }

    public static JsonNode genTableJsonNode(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeTaskDataTables = null;
        try{
            StudentSubmitContentDetails studentSubmitContentDetails = objectMapper.readValue(jsonString,StudentSubmitContentDetails.class);
            String jsonStringTaskDataTables = studentSubmitContentDetails.getTaskDataTables();
            jsonNodeTaskDataTables = objectMapper.readTree(jsonStringTaskDataTables);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonNodeTaskDataTables;
    }

    public static JsonNode genQuestionJsonNode(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodeQuestions = null;
        try{
            StudentSubmitContentDetails studentSubmitContentDetails = objectMapper.readValue(jsonString,StudentSubmitContentDetails.class);
            String jsonStringTaskQuestions = studentSubmitContentDetails.getTaskQestion();
            jsonNodeQuestions = objectMapper.readTree(jsonStringTaskQuestions);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonNodeQuestions;
    }



}
