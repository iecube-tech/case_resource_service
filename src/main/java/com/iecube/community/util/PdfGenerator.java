package com.iecube.community.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;

public class PdfGenerator {

    public void generatePdf() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // 添加文本
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Hello, World!");
            contentStream.endText();

            // 添加图片
            // 这里需要根据你的实际需求加载图片并添加到PDF中

            // 添加表格
            // 这里需要根据你的实际需求创建表格并添加到PDF中

            contentStream.close();

            document.save("generated.pdf");
            System.out.println("done");
        }
    }

    public static void main(String[] args) throws IOException {
        PdfGenerator pdfGenerator = new PdfGenerator();
        pdfGenerator.generatePdf();
    }
}

