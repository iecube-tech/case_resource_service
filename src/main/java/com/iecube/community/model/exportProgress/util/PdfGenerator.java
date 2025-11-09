package com.iecube.community.model.exportProgress.util;
import com.iecube.community.model.exportProgress.dto.PstReportCommentDto;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class PdfGenerator {
    public static PdfFont TitleFont;
    public static PdfFont TextFont;
    public static String fontPath = "/community/service/fonts/simsun.ttf";
    public static String fontPathW = "D:\\work\\iecube_community\\service\\community\\src\\main\\resources\\fonts\\simsun.ttf";

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static File generatePdf(String genFileDir, String fileName, List<PstReportCommentDto> PstReportCommentDtoList) throws IOException {
        if(isWindows()){
            fontPath = fontPathW;
        }
        String filePath = genFileDir+"/" + fileName;

        // 设置字体
        TitleFont = PdfFontFactory.createFont(fontPath, PdfEncodings.UTF8);
        TextFont = PdfFontFactory.createFont(fontPath, PdfEncodings.UTF8);

        File file = new File(filePath);
        // 创建文档对象并设置页面大小
        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream(file)));
        pdf.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdf);
        document.setFont(TextFont);

        for(PstReportCommentDto commentDto : PstReportCommentDtoList){
            Paragraph paragraph = genText(commentDto.getPayload());
            document.add(paragraph);
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
}
