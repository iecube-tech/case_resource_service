package com.iecube.community.util.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class ITextDemo2 {
    public static Font FangSong;
    public static void main(String[] args) {
        String filePath = "testfile2.pdf";

        try {
            // 创建文档对象并设置页面大小
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            String fontPath = "src/main/resources/fonts/simfang.ttf";
            FangSong = FontFactory.getFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);

            // 添加5个表格
            for (int i = 1; i <= 15; i++) {
                PdfPTable table = createTable(3,5); // 创建表格
                document.add(table);
                document.add(new Paragraph("\n")); // 添加空行分隔表格
            }


            document.close();
            System.out.println("PDF generated successfully.");
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
            System.out.println("Failed to generate PDF.");
        }
    }

    // 创建一个带有指定行列数的表格
    private static PdfPTable createTable(int rows, int columns) {
        PdfPTable table = new PdfPTable(columns); // 创建表格对象
        table.setWidthPercentage(100); // 设置表格宽度为100%

        // 添加表头
        for (int j = 1; j <= columns; j++) {
            PdfPCell headerCell = new PdfPCell(new Paragraph("Header" + j, FangSong));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell); // 添加表头单元格
        }

        // 添加表格内容
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                PdfPCell cell = new PdfPCell(new Paragraph("行 " + i + ", 列 " + j, FangSong));
                table.addCell(cell); // 添加内容单元格
            }
        }

        return table;
    }
}
