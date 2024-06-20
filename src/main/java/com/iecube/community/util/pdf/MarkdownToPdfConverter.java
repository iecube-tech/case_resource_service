package com.iecube.community.util.pdf;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
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
import com.vladsch.flexmark.util.options.MutableDataSet;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.*;
import java.io.IOException;
import java.util.Arrays;


public class MarkdownToPdfConverter {
    public static String fontPath = "D:\\work\\iecube_community\\service\\community\\src\\main\\resources\\fonts\\simfang.ttf";

    public static void main(String[] args) {
        String markdown = "# 你好，世界！\n\n这是一段*Markdown*文本。\n\n $`a=b+c`$";
        try {
            String html = convertMarkdownToHtml(markdown);
//            System.out.println(html);
            convertHtmlToPdf(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertMarkdownToHtml(String markdown) throws IOException {
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

    public static String convertHtmlToPdf(String html) throws IOException{
        String pdfFilePath ="html_output.pdf";
        // 创建一个PdfWriter实例
        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFilePath));
        // 初始化一个PdfDocument实例
        PdfDocument pdfDocument = new PdfDocument(writer);

        // 设置页面大小
        pdfDocument.setDefaultPageSize(PageSize.A4);

        // 创建一个Document实例
        Document document = new Document(pdfDocument);

        // 将HTML字符串转换为InputStream
        InputStream htmlStream = new ByteArrayInputStream(html.getBytes("UTF-8"));

        ConverterProperties properties = creatBaseFont(fontPath);
        // 使用HtmlConverter将HTML字符串转换为PDF
        HtmlConverter.convertToPdf(htmlStream, pdfDocument, properties);
        Document document1 = HtmlConverter.convertToDocument(htmlStream,pdfDocument,properties);
        // 关闭文档
        document.close();
        return(pdfFilePath);
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