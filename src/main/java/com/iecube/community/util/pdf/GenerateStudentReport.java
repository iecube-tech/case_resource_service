package com.iecube.community.util.pdf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.iecube3835.dto.StudentSubmitContentDetails;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.task.entity.StudentTaskDetailVo;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.html2pdf.attach.impl.tags.ImgTagWorker;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TextRenderer;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class GenerateStudentReport {

    private static String genFileDir;

    public static PdfFont TitleFont;
    public static PdfFont TextFont1;
    public static PdfFont TextFont;
    public static String fontPath = "/community/service/fonts/simfang.ttf";
    public static String fontPathW = "D:\\work\\iecube_community\\service\\community\\src\\main\\resources\\fonts\\simfang.ttf";


    public GenerateStudentReport(String genFilePath){
        this.genFileDir = genFilePath;
        if(isWindows()){
            fontPath = fontPathW;
        }
    }


    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public MultipartFile startGen(StudentDto studentDto, StudentTaskDetailVo studentTaskDetailVo, String studentData) throws IOException {
        String FileName = studentTaskDetailVo.getProjectId() +"-"+studentTaskDetailVo.getTaskNum()+"-"+studentDto.getStudentName()
                +"-"+studentTaskDetailVo.getTaskName()+""+".pdf";
        String filePath = genFileDir+"/"+FileName;
        // 设置字体
        TitleFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
        TextFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
        // 创建文档对象并设置页面大小
        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(filePath)));
        pdf.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdf);
        document.setFont(TextFont);

        Paragraph studentInfoParagraph = studentInfo(studentDto, studentTaskDetailVo);
        document.add(studentInfoParagraph);
        JsonNode tableJsonNode = genTableJsonNode(studentData);
        JsonNode questionSJsonNode = genQuestionJsonNode(studentData);
        if(tableJsonNode != null){
            for(JsonNode tableData: tableJsonNode){
                // 表格名称
                Paragraph nameParagraph = new Paragraph(tableData.get("name").asText()).setFont(TextFont).setFontSize(10);
                nameParagraph.setHorizontalAlignment(HorizontalAlignment.CENTER);
                nameParagraph.setTextAlignment(TextAlignment.CENTER);
                // 表格参数
                Paragraph paramsParagraph = null;
                if(tableData.get("params").size()>0){
                    String params = "";
                    for(JsonNode param: tableData.get("params")){
                        params = params+param.get("name").asText()+"：" +param.get("value").asText()+"    ";
                    }
                    paramsParagraph = new Paragraph(params).setFont(TextFont).setFontSize(10);
                    paramsParagraph.setPaddingBottom(10); // 设置段落间距
                }
                Table table = createTable(tableData);

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

        if(questionSJsonNode!=null){
            createQuestion(questionSJsonNode,document);
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

    public static Paragraph studentInfo(StudentDto studentDto, StudentTaskDetailVo studentTaskDetailVo){
        String studentInfo = "姓名："+studentDto.getStudentName() +"    学号：" + studentDto.getStudentId() +"    实验："+
                studentTaskDetailVo.getTaskName();
        Paragraph paragraph =new Paragraph(studentInfo);
        paragraph.setFont(TitleFont);
        paragraph.setFontSize(12);
        paragraph.setTextAlignment(TextAlignment.LEFT);
        paragraph.setMargins(20, 10, 20, 10); // 设置上、右、下、左边距
        // paragraph.setPadding(10); // 设置内边距
        return paragraph;
    }

    public static Table createTable(JsonNode jsonNode){
        int columns = jsonNode.get("columnList").size();
        int rows = jsonNode.get("rowData").size();
        Table table = new Table(columns); // 创建表格对象
        table.setWidth(UnitValue.createPercentValue(100)); // 设置表格宽度为100%
        // 添加表头
        for (int j = 0; j < columns; j++) {
            Cell headerCell = new Cell();
            Paragraph paragraph = new Paragraph().setFont(TextFont).setFontSize(10);
            String value="";

            if(jsonNode.get("columnList").get(j).get("label")!= null){
                value = jsonNode.get("columnList").get(j).get("label").asText();
            }
            paragraph.add(value);
            headerCell.add(paragraph);
            headerCell.setHorizontalAlignment(HorizontalAlignment.CENTER);
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
                Cell cell = new Cell().add(new Paragraph(value).setFont(TextFont).setFontSize(10));
                table.addCell(cell); // 添加内容单元格
            }
        }
        return table;
    }

    public static void createQuestion(JsonNode questionSJsonNode, Document document) throws IOException{
        String markdownText = "";
        for(JsonNode question: questionSJsonNode){
            String ques = question.get("question").asText();
            String answer = "";
            if(question.get("answer").asText()!="null" && question.get("answer")!=null){
                answer = question.get("answer").asText();
            }
            markdownText = markdownText + "\n\n" + ques + "\n\n\n" + answer + "\n\n\n";
        }
        String htmlContent = convertMarkdownToHtml(markdownText);
        List<IElement> elements = convertHtmlToDocument(htmlContent);
        PageSize pageSize = document.getPdfDocument().getDefaultPageSize();
        float pageWidth = pageSize.getWidth();
        float pageHeight = pageSize.getHeight();

        for(IElement iElement : elements){
            if(iElement instanceof Paragraph){
                List<IElement> childElements = ((Paragraph) iElement).getChildren();
                for (IElement child : childElements){
                    if(child instanceof Image){
                        float imgWidth = ((Image) child).getImageWidth();
                        float imgHeight = ((Image) child).getImageHeight();
                        if(imgWidth > pageWidth-document.getLeftMargin()-document.getRightMargin()){
                            float newImgWidth = pageWidth-document.getLeftMargin()-document.getRightMargin();
                            float newImgHeight = imgHeight*newImgWidth / imgWidth;
                            ((Image) child).setHeight(newImgHeight);
                            ((Image) child).setWidth(newImgWidth);
                        }
                    }
                }
            }
            document.add((IBlockElement)iElement);
        }
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

    public static String convertMarkdownToHtml(String markdown) throws IOException {
        //markdown = markdown.replaceAll("\\$(.*?)\\$", "\\$`$1`\\$");
        //markdown = markdown.replaceAll("\\$\\$(.*?)\\$\\$", "\\$`$1`\\$");
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(
                AbbreviationExtension.create(),
                AutolinkExtension.create(),
                EmojiExtension.create(),
                StrikethroughExtension.create(),
                TablesExtension.create(),
                FootnoteExtension.create(),
                TaskListExtension.create(),
                GitLabExtension.create()
        )).toImmutable();

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        String htmlWithoutCss  =  renderer.render(parser.parse(markdown));
        String htmlWithCss = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"> <link rel=\"modulepreload\" href=\"https://cdnjs.cloudflare.com/ajax/libs/mermaid/10.6.1/mermaid.esm.min.mjs\"\n" +
                "    id=\"md-editor-mermaid-m\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/KaTeX/0.16.9/katex.min.css\"\n" +
                "    id=\"md-editor-katexCss\">\n" +
                "  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/KaTeX/0.16.9/katex.min.js\" id=\"md-editor-katex\"></script>\n" +
                "  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/highlight.min.js\"\n" +
                "    id=\"md-editor-hljs\"></script>\n" +
                "  <link href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/atom-one-dark.min.css\" rel=\"stylesheet\"\n" +
                "    id=\"md-editor-hlCss\"><style>" +  "</style></head><body>\n" +
                htmlWithoutCss + "\n"+
                "</body>\n"+
                "<script>\n" +
                "    (function () {\n" +
                "        document.addEventListener(\"DOMContentLoaded\", function () {\n" +
                "            var mathElems = document.getElementsByClassName(\"katex\");\n" +
                "            var elems = [];\n" +
                "            for (const i in mathElems) {\n" +
                "                if (mathElems.hasOwnProperty(i)) elems.push(mathElems[i]);\n" +
                "            }\n" +
                "\n" +
                "            elems.forEach(elem => {\n" +
                "                katex.render(elem.textContent, elem, { throwOnError: false, displayMode: elem.nodeName !== 'SPAN', });\n" +
                "            });\n" +
                "        });\n" +
                "    })();\n" +
                "</script> </html>";
        return htmlWithCss;
    }

    public static List convertHtmlToDocument(String html) throws IOException{
        InputStream htmlStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
        ConverterProperties properties = creatBaseFont(fontPath);
//        HtmlConverter.convertToDocument(htmlStream,pdf, properties);
        List<IElement>  elements = HtmlConverter.convertToElements(htmlStream, properties);
        return elements;
    }

    private static ConverterProperties creatBaseFont(String fontPath) throws IOException{
        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider();
        FontProgram fontProgram;
        fontProgram = FontProgramFactory.createFont(fontPath);
        fontProvider.addFont(fontProgram);
        properties.setFontProvider(fontProvider);
        return properties;
    }
}
