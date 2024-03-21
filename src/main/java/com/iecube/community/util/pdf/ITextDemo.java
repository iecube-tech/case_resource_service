package com.iecube.community.util.pdf;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class ITextDemo {
    public static void main(String[] args) {
        String filePath = "testfile.pdf";
        String content = "This is a long text that will automatically \n wrap to the next line if it exceeds the width of the page.";

        generatePdf(filePath, content);
    }

    public static void generatePdf(String filePath, String content) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Paragraph paragraph = new Paragraph(content);
            paragraph.setAlignment(Paragraph.ALIGN_LEFT);
            paragraph.setIndentationLeft(20); // 设置左缩进距离
            paragraph.setIndentationRight(20); // 设置右缩进距离
            paragraph.setSpacingAfter(10); // 设置段落间距

            document.add(paragraph);
            document.close();

            System.out.println("PDF generated successfully.");
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
            System.out.println("Failed to generate PDF.");
        }
    }
}
