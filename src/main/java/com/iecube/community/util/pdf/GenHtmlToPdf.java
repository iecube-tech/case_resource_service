package com.iecube.community.util.pdf;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class GenHtmlToPdf {

    public static String fontPath = "/community/service/fonts/simhei.ttf";
    public static String fontPathW = "D:\\work\\iecube_community\\service\\community\\src\\main\\resources\\fonts\\simhei.ttf";

//    public static PdfFont TextFont;
    public GenHtmlToPdf(){
        if(isWindows()){
            fontPath = fontPathW;
        }
    }
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }


    public void genHtmlToPdf(String htmlContent) throws IOException {
        String wholeHtml = this.genWholeHtml(htmlContent);
        String filePath = "output.pdf";
//        TextFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);
//        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(filePath));
//        PdfDocument pdf = new PdfDocument(pdfWriter);
//        pdf.setDefaultPageSize(PageSize.A4);
//        Document document = new Document(pdf);
//        Document document = new Document();
//        document.open();
//        PdfDocument pdfDocument = new PdfDocument();
//        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
//        XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
//        fontImp.register(fontPath);
//        XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document,
//                new ByteArrayInputStream(htmlContent.getBytes("UTF-8")),null, Charset.forName("UTF-8"),fontImp);
//        document.close();
    }

//    public static List convertHtmlToDocument(String html) throws IOException{
//        InputStream htmlStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
//        ConverterProperties properties = creatBaseFont(fontPath);
////        HtmlConverter.convertToDocument(htmlStream,pdf, properties);
//        List<IElement>  elements = HtmlConverter.convertToElements(htmlStream, properties);
//        return elements;
//    }

//    private static ConverterProperties creatBaseFont(String fontPath) throws IOException{
//        ConverterProperties properties = new ConverterProperties();
//        FontProvider fontProvider = new DefaultFontProvider();
//        FontProgram fontProgram;
//        fontProgram = FontProgramFactory.createFont(fontPath);
//        fontProvider.addFont(fontProgram);
//        properties.setFontProvider(fontProvider);
//        return properties;
//    }


    public String genWholeHtml(String htmlContent) throws IOException {
        File file = new File("src/main/resources/static/html_to_pdf/head.txt");
        FileInputStream io = new FileInputStream(file);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int len;
        while ((len = io.read(buf)) > 0) {
            os.write(buf, 0, len);
        }
        io.close();
        String header = os.toString();
        String footer = "</body>\n\n</html>";
        String wholeHtml = header+htmlContent+footer;


        org.jsoup.nodes.Document doc = Jsoup.parse(wholeHtml);
        // jsoup标准化标签，生成闭合标签
        doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        return doc.html().replaceAll("<br>","<br />");
    }
}
