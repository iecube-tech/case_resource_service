package com.iecube.community.util.pdf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;
import com.iecube.community.model.task.entity.PSTBaseDetail;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.Element;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;


public class MdArticleStudentReportGen {
    private static String genFileDir;

    public static PdfFont TitleFont;
    public static PdfFont TextFont1;
    public static PdfFont TextFont;
    public static String fontPath = "/community/service/fonts/simfang.ttf";
    public static String fontPathW = "D:\\work\\iecube_community\\service\\community\\src\\main\\resources\\fonts\\simfang.ttf";

    private static String IMAGEPath="D:/community/service/resource/image/";

    private static String IMAGEPathW="D:\\learn\\java\\resources\\image\\";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public MdArticleStudentReportGen(String genFilePath){
        this.genFileDir = genFilePath;
        if(isWindows()){
            fontPath = fontPathW;
            IMAGEPath = IMAGEPathW;
        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public MultipartFile startGen(PSTBaseDetail pstBaseDetail, List<PSTArticleCompose> pstArticleComposeList) throws IOException {
        String FileName = pstBaseDetail.getProjectName()+"_"+pstBaseDetail.getStudentId()+"_"+
                pstBaseDetail.getStudentName()+"_"+pstBaseDetail.getTaskNum()+"_"+
                pstBaseDetail.getTaskName()+"_"+pstBaseDetail.getGrade()+"分.pdf";
        String filePath = genFileDir+"/"+FileName;
        // 设置字体
        try{
            TitleFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
            TextFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
        }catch (Exception e){
            e.printStackTrace();
        }
        // 创建文档对象并设置页面大小
        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(filePath)));
        pdf.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdf);
        document.setFont(TextFont);
        Paragraph studentInfoParagraph = studentInfo(pstBaseDetail);
        document.add(studentInfoParagraph);
        Paragraph gradeParagraph = studentGrade(pstBaseDetail);
        document.add(gradeParagraph);
        for(PSTArticleCompose pstArticleCompose: pstArticleComposeList){
           Paragraph paragraph = composeGen(pstArticleCompose, document);
           if(paragraph != null){
               document.add(paragraph);
           }
        }
        document.close();
        pdf.close();
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


    public static Paragraph studentInfo(PSTBaseDetail pstBaseDetail){
        String studentInfo = "姓名："+pstBaseDetail.getStudentName() +"    学号：" + pstBaseDetail.getStudentId() +"    实验："+
                pstBaseDetail.getTaskName();

        Paragraph paragraph =new Paragraph(studentInfo);
        paragraph.setFont(TitleFont);
        paragraph.setFontSize(12);
        paragraph.setTextAlignment(TextAlignment.LEFT);
        paragraph.setMargins(20, 10, 20, 10); // 设置上、右、下、左边距
        // paragraph.setPadding(10); // 设置内边距
        return paragraph;
    }

    public static Paragraph studentGrade(PSTBaseDetail pstBaseDetail){
        String Grade = "得分：" + pstBaseDetail.getGrade();
        Paragraph paragraph = new Paragraph(Grade);
        paragraph.setFont(TitleFont);
        paragraph.setFontSize(12);
        paragraph.setFontColor(new DeviceCmyk(0.0f,1.0f,1.0f,0.0f));
        paragraph.setTextAlignment(TextAlignment.RIGHT);
        paragraph.setMargins(5, 10, 5, 10); // 设置上、右、下、左边距
        return paragraph;
    }


    public static Paragraph composeGen(PSTArticleCompose pstArticleCompose, Document document){

        switch (pstArticleCompose.getQType()){
            case 0:
                return null;
            case 1:
                return typeOne(pstArticleCompose);
            case 2:
                return typeTwo(pstArticleCompose);
            case 3:
                return typeThree(pstArticleCompose, document);
            case 4:
                return typeFour(pstArticleCompose);
            case 5:
                return typeFive(pstArticleCompose);
            default:
                return new Paragraph(pstArticleCompose.getName()).setFont(TitleFont).setFontSize(12);
        }

    }

    private static Paragraph typeOne(PSTArticleCompose pstArticleCompose) {
        Paragraph paragraph = new Paragraph();
        String ques = pstArticleCompose.getQuestion()+"("+pstArticleCompose.getScore()+"分) ";
        // 题目
        Paragraph titleParagraph = genHtmlQues(ques);
        paragraph.add(titleParagraph);
        // 得分
        Paragraph gradePar = new Paragraph(((pstArticleCompose.getResult() != null)?pstArticleCompose.getResult().toString():"0")+"\n")
                .setFont(TextFont).setFontSize(10).setFontColor(new DeviceCmyk(0.0f,1.0f,1.0f,0.0f));
        paragraph.add(gradePar);
        // 作答内容
        JsonNode val = genJsonNode(pstArticleCompose.getVal());
        if(val!=null){
            Paragraph valParagraph = new Paragraph(val.get("val").asText()).setFont(TextFont).setFontSize(10);
            paragraph.add("\n");
            paragraph.add(valParagraph);
        }
        // 参考答案
        JsonNode answer = genJsonNode(pstArticleCompose.getAnswer());
        String answerStr = "";
        if(answer!=null){
            answerStr = answer.get("val").asText();
        }
        Paragraph answerParagraph = new Paragraph("参考答案："+answerStr)
                .setFont(TextFont).setFontSize(10).setFontColor(new DeviceCmyk(0.88f,0.0f,0.58f,0.28f));
        paragraph.add("\n");
        paragraph.add(answerParagraph);
        return paragraph;
    }

    private static Paragraph typeTwo(PSTArticleCompose pstArticleCompose){
        Paragraph paragraph= new Paragraph();
        String ques = pstArticleCompose.getQuestion()+"("+pstArticleCompose.getScore()+"分) ";
        // 题目
        Paragraph titleParagraph = genHtmlQues(ques);
        paragraph.add(titleParagraph);
        // 得分
        Paragraph gradePar = new Paragraph((pstArticleCompose.getResult() != null)?pstArticleCompose.getResult().toString():"0")
                .setFont(TextFont).setFontSize(10).setFontColor(new DeviceCmyk(0.0f,1.0f,1.0f,0.0f));
        paragraph.add(gradePar);

        // 作答内容
        JsonNode val = genJsonNode(pstArticleCompose.getVal());
        Table table = genTable(val);
        paragraph.add("\n");
        paragraph.add(table);

        // 答案
        if(pstArticleCompose.getAnswer()!=null){
            JsonNode answer = genJsonNode(pstArticleCompose.getAnswer());
            Table tableAnswer  = genTable(answer);
            Paragraph answerParagraph = new Paragraph("参考答案：\n")
                    .setFont(TextFont).setFontSize(10).setFontColor(new DeviceCmyk(0.88f,0.0f,0.58f,0.28f));
            answerParagraph.add(tableAnswer);
            paragraph.add("\n");
            paragraph.add(answerParagraph);
        }
        return paragraph;
    }

    private static Paragraph typeThree(PSTArticleCompose pstArticleCompose, Document document){
        Paragraph paragraph = new Paragraph();
        String ques = pstArticleCompose.getQuestion()+"("+pstArticleCompose.getScore()+"分) ";
        // 题目
        Paragraph titleParagraph = genHtmlQues(ques);
        paragraph.add(titleParagraph);
        // 得分
        Paragraph gradePar = new Paragraph((pstArticleCompose.getResult() != null)?pstArticleCompose.getResult().toString():"0")
                .setFont(TextFont).setFontSize(10).setFontColor(new DeviceCmyk(0.0f,1.0f,1.0f,0.0f));
        paragraph.add(gradePar);

        // 作答内容
        JsonNode val = genJsonNode(pstArticleCompose.getVal());
        JsonNode picList = val.get("picList");
        Paragraph resParagraph = genPic(picList, document);
        paragraph.add("\n");
        paragraph.add(resParagraph);

        // 参考答案
        if(pstArticleCompose.getAnswer() != null){
            JsonNode answer = genJsonNode(pstArticleCompose.getAnswer());
            JsonNode picListAnswer = answer.get("picList");
            Paragraph answerParagraph = genPic(picListAnswer, document);
            paragraph.add(new Paragraph("参考答案：\n")
                    .setFont(TextFont).setFontSize(10).setFontColor(new DeviceCmyk(0.88f,0.0f,0.58f,0.28f)));
            paragraph.add("\n");
            paragraph.add(answerParagraph);
        }
        return paragraph;
    }

    private static Paragraph typeFour(PSTArticleCompose pstArticleCompose){
        Paragraph paragraph = new Paragraph();
        String ques = pstArticleCompose.getQuestion()+"("+pstArticleCompose.getScore()+"分) ";
        // 题目
        Paragraph titleParagraph = genHtmlQues(ques);
        paragraph.add(titleParagraph);
        // 得分
        Paragraph gradePar = new Paragraph(((pstArticleCompose.getResult() != null)?pstArticleCompose.getResult().toString():"0")+"\n")
                .setFont(TextFont).setFontSize(10).setFontColor(new DeviceCmyk(0.0f,1.0f,1.0f,0.0f));
        paragraph.add(gradePar);
        paragraph.add("\n");
        // 单选
        JsonNode args = genJsonNode(pstArticleCompose.getArgs());
        List<String> argList = arrayJsonNodeToList(args,String.class);
        List<String> optionList = new ArrayList<>(argList.subList(2, argList.size()));
        // 作答内容
        JsonNode val = genJsonNode(pstArticleCompose.getVal());
        Integer valValue= val.get("val").asInt(); //学生选项
        // 参考答案
        JsonNode answer = genJsonNode(pstArticleCompose.getAnswer());
        Integer answerValue = answer.get("val").asInt(); // 参考答案选项

        Paragraph submitParagraph = new Paragraph();
        Paragraph answerParagraph = new Paragraph();
        answerParagraph.add("参考答案：\n");
        for(int i= 0; i<optionList.size();i++){
            String option = optionList.get(i);
            Paragraph optionParagraph = genHtmlQues(option);
            Paragraph answerOptionParagraph = new Paragraph();
            answerOptionParagraph = optionParagraph;
            if(valValue.equals(i)){
                submitParagraph.add(new Paragraph("-->").setFontColor(new DeviceCmyk(0.88f,0.0f,0.58f,0.28f)));
            }
            submitParagraph.add(optionParagraph);
            submitParagraph.add("\n");
//            optionParagraph.setFontColor(new DeviceCmyk(0f,0f,0f,1f));

            if(answerValue.equals(i)){
                answerParagraph.add(new Paragraph("-->").setFontColor(new DeviceCmyk(0.88f,0.0f,0.58f,0.28f)));
            }
            answerParagraph.add(answerOptionParagraph);
            answerParagraph.add("\n");
        }
        paragraph.add(submitParagraph);
        paragraph.add("\n");
        paragraph.add(answerParagraph);
        return paragraph;
    }

    private static Paragraph typeFive(PSTArticleCompose pstArticleCompose){
        Paragraph paragraph = new Paragraph();
        String ques = pstArticleCompose.getQuestion()+"("+pstArticleCompose.getScore()+"分) ";
        // 题目
        Paragraph titleParagraph = genHtmlQues(ques);
        paragraph.add(titleParagraph);
        // 得分
        Paragraph gradePar = new Paragraph(((pstArticleCompose.getResult() != null)?pstArticleCompose.getResult().toString():"0")+"\n")
                .setFont(TextFont).setFontSize(10).setFontColor(new DeviceCmyk(0.0f,1.0f,1.0f,0.0f));
        paragraph.add(gradePar);
        // 多选
        paragraph.add("\n");
        JsonNode args = genJsonNode(pstArticleCompose.getArgs());
        List<String> argList = arrayJsonNodeToList(args,String.class);
        List<String> optionList = new ArrayList<>(argList.subList(2, argList.size()));

        // 作答
        JsonNode val = genJsonNode(pstArticleCompose.getVal()).get("val");
        List submitList = arrayJsonNodeToList(val, Integer.class); //学生选项
        // 答案
        JsonNode answer = genJsonNode(pstArticleCompose.getAnswer()).get("val");
        List<Integer> answerList = arrayJsonNodeToList(answer, Integer.class);
        Paragraph submitParagraph = new Paragraph();
        Paragraph answerParagraph = new Paragraph();
        answerParagraph.add("参考答案：\n");
        for(Integer i= 0; i<optionList.size();i++){
            Paragraph optionParagraph = genHtmlQues(optionList.get(i));
            Paragraph answerOptionParagraph = new Paragraph();
            answerOptionParagraph = optionParagraph;
            if(submitList.contains(i)){
                submitParagraph.add(new Paragraph("-->").setFontColor(new DeviceCmyk(0.88f,0.0f,0.58f,0.28f)));
            }
            submitParagraph.add(optionParagraph);
            submitParagraph.add("\n");
//            optionParagraph.setFontColor(new DeviceCmyk(0f,0f,0f,1f));
            if(answerList.contains(i)){
                answerParagraph.add(new Paragraph("-->").setFontColor(new DeviceCmyk(0.88f,0.0f,0.58f,0.28f)));
            }
            answerParagraph.add(answerOptionParagraph);
            answerParagraph.add("\n");
        }
        paragraph.add(submitParagraph);
        paragraph.add("\n\n");
        paragraph.add(answerParagraph);
        return paragraph;
    }

    private static Paragraph genPic(JsonNode picList, Document document){
        PageSize pageSize = document.getPdfDocument().getDefaultPageSize();
        float pageWidth = pageSize.getWidth();
        Paragraph paragraph = new Paragraph();
        for(int i=0; i<picList.size(); i++){
            String picName = picList.get(i).asText();
            try{
                Image image = new Image(ImageDataFactory.create(IMAGEPath+picName));
                float imageWidth = image.getImageWidth();
                float imgHeight = image.getImageHeight();
                if(imageWidth>pageWidth-document.getLeftMargin()-document.getRightMargin()){
                    float newImgWidth = pageWidth-document.getLeftMargin()-document.getRightMargin();
                    float newImgHeight = imgHeight*newImgWidth / imageWidth;
                    image.setWidth(newImgWidth);
                    image.setHeight(newImgHeight);
                }
                paragraph.add(image);
                paragraph.add("\n");
            }catch (Exception e){
//                System.out.println(IMAGEPath+picName);
                e.printStackTrace();
                paragraph.add("加载失败的图片\n").setFontSize(8);
            }
        }
        return paragraph;
    }

    private static Table genTable(JsonNode val){
        JsonNode tableData = val.get("tableData");
        int col = val.get("col").asInt();
        int row = val.get("row").asInt();
        Table table = new Table(col); // 创建表格对象
        table.setWidth(UnitValue.createPercentValue(100)); // 设置表格宽度为100%
        for (int i=0; i<row; i++){
            for(int j=0; j<col;j++){
                String value = tableData.get(i).get(j).get("value").asText();
                Paragraph cellParagraph = new Paragraph();
                cellParagraph.add(value);
                boolean isEdit = tableData.get(i).get(j).get("edit").asBoolean();

                if(isEdit){
                    if(tableData.get(i).get(j).get("res").asBoolean()){
                        // 答案正确
                        cellParagraph.setFontColor(new DeviceCmyk(0.88f,0.0f,0.58f,0.28f));
                    }
                }
                Cell cell = new Cell();
                cell.add(cellParagraph);
                if(isEdit){
                    cell.setUnderline();
                }
                table.addCell(cell);
            }
        }
        return table;
    }

    private static <T> List<T> arrayJsonNodeToList(JsonNode jsonNodeArray, Class<T> elementType){
        List<T> list = new ArrayList<>();
        if (jsonNodeArray != null && jsonNodeArray.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNodeArray;
            Iterator<JsonNode> elements = arrayNode.elements();
            while (elements.hasNext()) {
                JsonNode node = elements.next();
                T element = convertJsonNode(node, elementType);
                if (element != null) {
                    list.add(element);
                }
            }
        }
        return list;
    }

    private static <T> T convertJsonNode(JsonNode node, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(node, clazz);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exception as needed
            return null;
        }
    }

    private static JsonNode genJsonNode(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try{
            jsonNode = objectMapper.readTree(jsonString);
        }catch (Exception e){
           return null;
        }
        return jsonNode;
    }

    private static Paragraph genHtmlQues(String ques){
        Paragraph paragraph = new Paragraph();
        if(ques.contains("katex")){
            try{
                String htmlQues = genHtmlString(ques);
                // 标题内容
                List<IElement> elements = convertHtmlToDocument(htmlQues);
                for(IElement iElement : elements){
                    paragraph.add((IBlockElement)iElement);
                }
            }catch (Exception e){
                e.printStackTrace();
                return paragraph.add(ques);
            }
        }else {
            paragraph.add(ques);
        }
        return paragraph;
    }


    public static String genHtmlString(String text){
        String htmlWithCss = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                " <link rel=\"modulepreload\" href=\"https://cdnjs.cloudflare.com/ajax/libs/mermaid/10.6.1/mermaid.esm.min.mjs\"\n" +
                "    id=\"md-editor-mermaid-m\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/KaTeX/0.16.9/katex.min.css\"\n" +
                "    id=\"md-editor-katexCss\">\n" +
                "  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/KaTeX/0.16.9/katex.min.js\" id=\"md-editor-katex\"></script>\n" +
                "<style>" +  "" +
                "</style>" +
                "</head>" +
                "<body>\n" +
                "       <div>"+
                            text+ "\n"+
                "       </div>"+
                "</body>\n"+
                " </html>";
        return htmlWithCss;
    }

    public static List<IElement> convertHtmlToDocument(String html) throws IOException{
        InputStream htmlStream=null;
        try{
             htmlStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
            ConverterProperties properties = creatBaseFont(fontPath);
            List<IElement>  elements = HtmlConverter.convertToElements(htmlStream, properties);
            return elements;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }finally {
            if(htmlStream!=null){
                htmlStream.close();
            }
        }
    }

    private static ConverterProperties creatBaseFont(String fontPath){
        try{
            ConverterProperties properties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider();
            FontProgram fontProgram;
            fontProgram = FontProgramFactory.createFont(fontPath);
            fontProvider.addFont(fontProgram);
            properties.setFontProvider(fontProvider);
            return properties;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ConverterProperties();
    }
}
