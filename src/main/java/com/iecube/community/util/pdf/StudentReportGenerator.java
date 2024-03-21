package com.iecube.community.util.pdf;

import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.util.PdfGenerator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StudentReportGenerator {
    private static float PageYStart=790;
    private static float PageYEnd=50;
    private static float Margin=50;
    private static float PageWidth=460;
    private static PDFont FONT;
    private static float FONTSize=12;
    public void startGenerator(){
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            System.out.println(page.getMediaBox().getHeight()+"*"+page.getMediaBox().getWidth());
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            File fontFile = new File("src/main/resources/fonts/simfang.ttf");
            PDFont font = PDType0Font.load(document,fontFile);
            FONT=font;
            // 添加文本
            drawStudentInfo(page,contentStream);
            // 添加图片
            // 这里需要根据你的实际需求加载图片并添加到PDF中

            // 添加表格
            // 这里需要根据你的实际需求创建表格并添加到PDF中

            contentStream.close();

            document.save("generated.pdf");
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void drawTable(){

    }

    public static void drawStudentInfo(PDPage page,PDPageContentStream contentStream){
        StudentDto studentDto = new StudentDto();
        studentDto.setStudentName("曾小慧");
        studentDto.setStudentId("17408070401");
        try{
            contentStream.beginText();
            contentStream.setFont(FONT, FONTSize);
            contentStream.setLeading(10f);
            contentStream.newLineAtOffset(Margin, PageYStart);
            contentStream.showText("姓名："+studentDto.getStudentName()+"  "+"学号：" + studentDto.getStudentId());
            contentStream.endText();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
