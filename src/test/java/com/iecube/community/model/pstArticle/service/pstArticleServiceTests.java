package com.iecube.community.model.pstArticle.service;

import com.iecube.community.model.pst_article.service.PSTArticleService;
import com.iecube.community.model.pst_article_compose.mapper.PSTArticleComposeMapper;
import com.iecube.community.model.pst_article_compose.service.PSTArticleComposeService;
import com.iecube.community.model.task.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class pstArticleServiceTests {
    @Autowired
    private TaskService taskService;

    @Test
    public void genReportTest(){
//        taskService.genMdArticleReport(8340);
    }
}
