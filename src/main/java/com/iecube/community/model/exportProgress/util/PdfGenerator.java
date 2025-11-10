package com.iecube.community.model.exportProgress.util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.exportProgress.dto.PstReportCommentDto;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
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
import com.itextpdf.tool.xml.XMLWorkerHelper;
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
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class PdfGenerator {
    public static PdfFont TitleFont;
    public static PdfFont TextFont;
    public static String fontPath = "/community/service/fonts/simsun.ttf";
    public static String fontPathW = "D:\\work\\iecube_community\\service\\community\\src\\main\\resources\\fonts\\simsun.ttf";
//    public static String fontPathW = "D:\\java_project\\case_resource_service\\src\\main\\resources\\fonts\\simsun.ttf";

    public static ObjectMapper objectMapper = new ObjectMapper();

    private static Document document;
    private static final PageSize pageSize = PageSize.A4;

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public File generatePdf(String genFileDir, String fileName, List<PstReportCommentDto> PstReportCommentDtoList) throws IOException {
        if(isWindows()){
            fontPath = fontPathW;
        }
        String filePath = genFileDir+"/" + fileName;

        // 设置字体
        TitleFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
        TextFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

        File file = new File(filePath);
        // 创建文档对象并设置页面大小
        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(file)));
        pdf.setDefaultPageSize(pageSize);
        document = new Document(pdf);
        document.setFont(TextFont);

        for(PstReportCommentDto commentDto : PstReportCommentDtoList){
//            Paragraph paragraph = genText(commentDto.getPayload());
//            document.add(paragraph);
            JsonNode jsonNode = objectMapper.readTree(commentDto.getPayload());
            if(commentDto.getType().equals("QA")){
                document.add(genQA(commentDto.getName(),jsonNode));
            }
            if(commentDto.getType().equals("CHOICE")){
                document.add(genChoice(commentDto.getName(),jsonNode));
            }
            if(commentDto.getType().equals("MULTIPLECHOICE")){
                document.add(genMultipleChoice(commentDto.getName(),jsonNode));
            }
            if(commentDto.getType().equals("TABLE") || commentDto.getType().equals("TRACELINE")){
                document.add(genTable(commentDto.getName(), jsonNode));
            }
        }
        document.close();
        pdf.close();
        return file;
    }

    private static Paragraph genText(String text) {
        Paragraph paragraph =new Paragraph(text);
        paragraph.setFont(TitleFont);
        paragraph.setFontSize(12);
        paragraph.setTextAlignment(TextAlignment.LEFT);
        paragraph.setMargins(10, 10, 10, 10); // 设置上、右、下、左边距
        return paragraph;
    }

    private static Paragraph genQA1(String name, JsonNode jsonNode) throws IOException {
        String question = jsonNode.get("question").get("question") ==null?"":jsonNode.get("question").get("question").asText();
        String reAnswer = jsonNode.get("question").get("answer") ==null?"":jsonNode.get("question").get("answer").asText();
        String stuAnswer = jsonNode.get("stuAnswer").get("answer") ==null?"":jsonNode.get("stuAnswer").get("answer").asText();
        String res = name+ "</br>问题：" + question + "</br>参考答案：" +reAnswer + "</br>学生答案：" + stuAnswer;
        Paragraph paragraph =new Paragraph();
        String mQuestion = convertMarkdownToHtml(res);
        List<IElement> elements = convertHtmlToDocument(mQuestion);
        addElementsToParagraph(paragraph, elements);
        return paragraph;
    }

    private static Paragraph genQA(String name, JsonNode jsonNode) throws IOException {
        String question = jsonNode.get("question").get("question") ==null?"":jsonNode.get("question").get("question").asText();
        String reAnswer = jsonNode.get("question").get("answer") ==null?"":jsonNode.get("question").get("answer").asText();
        String stuAnswer = jsonNode.get("stuAnswer").get("answer") ==null?"":jsonNode.get("stuAnswer").get("answer").asText();
        String res = name+ "\n问题：" + question + "\n参考答案：" +reAnswer + "\n学生答案：" + stuAnswer;
        Paragraph paragraph =new Paragraph();
        paragraph.add(res);
        return paragraph;
    }

    private static Paragraph genChoice(String name, JsonNode jsonNode) throws JsonProcessingException {
        String question = jsonNode.get("question").get("question") ==null?"":jsonNode.get("question").get("question").asText();
        StringBuilder optionsStr = new StringBuilder();
        JsonNode options = jsonNode.get("question").get("options");
        if(options!=null && options.isArray()){
            for(int i=0; i<options.size(); i++){
                optionsStr.append(options.get(i).get("label").asText()).append(".").append(options.get(i).get("value").asText()).append("\n");
            }
        }
        String reAnswer = jsonNode.get("question").get("answer") ==null?"":jsonNode.get("question").get("answer").asText();
        String stuAnswer = jsonNode.get("stuAnswer").get("answer") ==null?"":jsonNode.get("stuAnswer").get("answer").asText();
        String res = name+ "\n问题：" + question + "\n"+ optionsStr + "参考答案：" +reAnswer + "\n学生答案：" + stuAnswer;
        Paragraph paragraph =new Paragraph();
        paragraph.add(res);
        return paragraph;
    }

    private static Paragraph genMultipleChoice(String name, JsonNode jsonNode) throws JsonProcessingException {
        String question = jsonNode.get("question").get("question") ==null?"":jsonNode.get("question").get("question").asText();
        StringBuilder optionsStr = new StringBuilder();
        JsonNode options = jsonNode.get("question").get("options");
        if(options!=null && options.isArray()){
            for(int i=0; i<options.size(); i++){
                optionsStr.append(options.get(i).get("label").asText()).append(".").append(options.get(i).get("value").asText()).append("\n");
            }
        }
        StringBuilder reAnswer = new StringBuilder();
        JsonNode reAnswerNode = jsonNode.get("question").get("answerOption");
        if(reAnswerNode!=null && reAnswerNode.isArray()){
            for(int i=0; i<reAnswerNode.size();i++){
                reAnswer.append(reAnswerNode.get(i).asText());
            }
        }
        StringBuilder stuAnswer = new StringBuilder();
        JsonNode stuAnswerNode = jsonNode.get("stuAnswer").get("answerOption");
        if(stuAnswerNode!=null && stuAnswerNode.isArray()){
            for(int i=0; i<stuAnswerNode.size();i++){
                stuAnswer.append(stuAnswerNode.get(i).asText());
            }
        }
        String res = name+ "\n问题：" + question + "\n"+ optionsStr + "参考答案：" +reAnswer + "\n学生答案：" + stuAnswer;
        Paragraph paragraph =new Paragraph();
        paragraph.add(res);
        return paragraph;
    }

    private static Paragraph genUploadImage(String name, JsonNode jsonNode) throws JsonProcessingException {
        Paragraph paragraph =new Paragraph();

        return paragraph;
    }

    private static Paragraph genTable(String name, JsonNode jsonNode) throws JsonProcessingException {
        Color blue = new DeviceRgb(0, 191, 255);
        int columns = jsonNode.get("table").get("col").asInt();
        int rows = jsonNode.get("table").get("row").asInt();
        Table table = new Table(columns); // 创建表格对象
        table.setWidth(UnitValue.createPercentValue(100)); // 设置表格宽度为100%
        // 添加表头
        for (int j = 0; j < columns; j++) {
            Cell headerCell = new Cell();
            Paragraph paragraph = new Paragraph().setFont(TextFont).setFontSize(10);
            String headerName = jsonNode.get("table").get("tableHeader").get(j).get("value")==null?"":jsonNode.get("table").get("tableHeader").get(j).get("value").asText();
            paragraph.add(headerName);
            headerCell.add(paragraph);
            headerCell.setHorizontalAlignment(HorizontalAlignment.CENTER);
            JsonNode isNeedInput = jsonNode.get("table").get("tableHeader").get(j).get("colIsNeedInput");
            table.addCell(headerCell); // 添加表头单元格
        }
        // 添加表格内容
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JsonNode rowNodes = jsonNode.get("table").get("tableRow"); //二维数组
                JsonNode isNeedInput = rowNodes.get(i).get(j).get("isNeedInput");
                Cell cell=new Cell();
                if(isNeedInput!=null && isNeedInput.asBoolean()){
                    // 填写学生输入数据
                    String value = rowNodes.get(i).get(j).get("stuVlaue")==null?"":rowNodes.get(i).get(j).get("stuVlaue").asText();
                    if(value.isEmpty()){
                        value="\n";
                    }
                    cell.add(new Paragraph(value).setFont(TextFont).setFontSize(10));
                    cell.setBackgroundColor(blue);
                }else {
                    // 填写预设数据
                    String value = rowNodes.get(i).get(j).get("value")==null?"":rowNodes.get(i).get(j).get("value").asText();
                    cell.add(new Paragraph(value).setFont(TextFont).setFontSize(10));
                }
                table.addCell(cell); // 添加内容单元格
            }
        }
        Paragraph paragraph =new Paragraph();
        paragraph.add(jsonNode.get("table").get("tableName")==null?"":jsonNode.get("table").get("tableName").asText()+"\n");
        paragraph.add(table);
        return paragraph;
    }

    public static void addElementsToParagraph(Paragraph paragraph, List<IElement> elements){
        float pageWidth = pageSize.getWidth();
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
            paragraph.add((IBlockElement)iElement);
        }
    }


    public static List<IElement> convertHtmlToDocument(String html) throws IOException{
        InputStream htmlStream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        ConverterProperties properties = creatBaseFont(fontPath);
//        HtmlConverter.convertToDocument(htmlStream,pdf, properties);
        return HtmlConverter.convertToElements(htmlStream, properties);
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

    private static String convertMarkdownToHtml(String markdown){
        //markdown = markdown.replaceAll("\\$(.*?)\\$", "\\$`$1`\\$");
//        markdown = markdown.replaceAll("\\$\\$(.*?)\\$\\$", "\\$`$1`\\$");
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
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        /* 全局字体：支持中文（iText 5.x 需指定亚洲字体） */
                        body { font-family: SimSun, "宋体", Arial, sans-serif; font-size: 12pt; line-height: 1.6; }
                        /* 标题样式 */
                        h1 { font-size: 20pt; color: #2c3e50; margin-bottom: 16px; }
                        h2 { font-size: 16pt; color: #34495e; margin-top: 24px; margin-bottom: 12px; }
                        /* 列表样式 */
                        ul, ol { margin-left: 20px; margin-bottom: 12px; }
                        li { margin-bottom: 4px; }
                        /* 表格样式 */
                        table { border-collapse: collapse; width: 80%; margin: 16px 0; }
                        th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }
                        th { background-color: #f8f9fa; }
                        /* 代码片段样式 */
                        code { background-color: #f5f5f5; padding: 2px 4px; border-radius: 3px; }
                        /* 引用样式 */
                        blockquote { border-left: 3px solid #ccc; padding-left: 10px; color: #666; }
                    </style>
                </head>
                <body>
                    """ + htmlWithoutCss + """
                </body>
                </html>
                """;
    }


}
