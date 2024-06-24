package com.iecube.community.util.pdf;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MarkdownToPDFDemo {

    public static void main(String[] args) throws IOException {
        String markdownText = "# Title\n\nThis is a **Markdown** formatted text.\n\n- Item 1\n- Item 2 \n![](https://www.iecube.online/dev-api/files/image/26522277cbff429fbbaa73351b5db9ab.png 'image.png')";

        // Convert Markdown to PDF
        try (FileOutputStream pdfOutput = new FileOutputStream("markdown_to_pdf_example.pdf")) {
            PdfWriter writer = new PdfWriter(pdfOutput);
            PdfDocument pdf = new PdfDocument(writer);

            // Convert Markdown to HTML
            String htmlContent = convertMarkdownToHtml(markdownText);

            // Convert HTML to PDF
            HtmlConverter.convertToPdf(new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)), pdf);
        }
    }

    private static String convertMarkdownToHtml(String markdownText) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));

        Parser parser = Parser.builder(options).build();
//        com.vladsch.flexmark.ast.Node document = parser.parse(markdownText);

        // Render to HTML
//        String htmlContent = com.vladsch.flexmark.html.HtmlRenderer.builder(options).build().render(document);

        return null;
    }
}
