package com.iecube.community.model.task.service;

import com.iecube.community.model.task.entity.PSTBaseDetail;
import com.iecube.community.util.pdf.MdArticleStudentReportGen;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.IElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
@EnableAsync
public class TaskServiceTests {
    @Autowired
    private TaskService taskService;

    @Test
    public void tasks(){
        System.out.println(taskService.findStudentTaskByProjectId(12,35));
    }

    @Test
    public void getProjectTasks(){
        System.out.println(taskService.getProjectTasks(30));
    }

    @Test
    public void getPstResourceVoByPSTId(){
        System.out.println(taskService.findPSTResourceVo(7962));
    }

    @Test
    public void genStudentMdDocReport(){
//        PSTBaseDetail pstBaseDetail = taskService.readOverPSTArticle(8774, 6);
//        taskService.genMdArticleReport(8774, pstBaseDetail, 6);
//        PSTBaseDetail pstBaseDetail = taskService.readOverPSTArticle(8346, 6);
//        taskService.genMdArticleReport(8346, pstBaseDetail, 6);
    }
    @Test
    public void test1(){
//        String FileName ="test.pdf";
        String a = " <span class=\"katex\"><span class=\"katex-mathml\"><math xmlns=\"http://www.w3.org/1998/Math/MathML\">" +
            "<semantics><mrow><mi>a</mi><mo>=</mo><mi>b</mi><mo>∗</mo><mi>c</mi></mrow><annotation encoding=\"application/x-tex\">" +
            "a=b*c</annotation></semantics></math></span><span class=\"katex-html\" aria-hidden=\"true\"><span class=\"base\">" +
            "<span class=\"strut\" style=\"height:0.4306em;\"></span><span class=\"mord mathnormal\">a</span><span class=\"mspace\" " +
            "style=\"margin-right:0.2778em;\"></span><span class=\"mrel\">=</span><span class=\"mspace\" style=\"margin-right:0.2778em;\">" +
            "</span></span><span class=\"base\"><span class=\"strut\" style=\"height:0.6944em;\"></span><span class=\"mord mathnormal\">b</span><span " +
            "class=\"mspace\" style=\"margin-right:0.2222em;\"></span><span class=\"mbin\">∗</span><span class=\"mspace\" style=\"margin-right:0.2222em;\">" +
            "</span></span><span class=\"base\"><span class=\"strut\" style=\"height:0.4306em;\"></span><span class=\"mord mathnormal\">" +
            "c</span></span></span></span> ";
//        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream()));

//        String htmlStr = MdArticleStudentReportGen.genHtmlString(a);
        try{
//            List<IElement> elements = MdArticleStudentReportGen.convertHtmlToDocument(htmlStr);
//            System.out.println(elements);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
