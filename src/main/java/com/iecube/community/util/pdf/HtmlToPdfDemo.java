package com.iecube.community.util.pdf;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HtmlToPdfDemo {

    public static void main(String[] args) throws IOException {
        // HTML 内容
        String htmlContent = "<p>This is <strong>bold</strong> and <em>italic</em> text.</p>";

        // 创建 PDF 文件
        File pdfFile = new File("output.pdf");

        // 初始化 PDF Writer 和 Document
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(pdfFile));
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        // 使用 ConverterProperties 和 HtmlConverter 将 HTML 转换为 PDF 段落
        ConverterProperties converterProperties = new ConverterProperties();
        HtmlConverter.convertToDocument(new FileInputStream(htmlContent), pdfDocument, converterProperties);

        // 添加段落到文档中
        Paragraph paragraph = new Paragraph(htmlContent);
        document.add(paragraph);

        // 关闭文档
        document.close();

//        System.out.println("PDF created successfully.");
    }
}
