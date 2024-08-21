package com.iecube.community.model.pst_article.service.impl;

import com.iecube.community.model.pst_article.entity.PSTArticle;
import com.iecube.community.model.pst_article.mapper.PSTArticleMapper;
import com.iecube.community.model.pst_article.service.PSTArticleService;
import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;
import com.iecube.community.model.pst_article_compose.mapper.PSTArticleComposeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PSTArticleServiceImpl implements PSTArticleService {

    @Autowired
    private PSTArticleMapper pstArticleMapper;

    @Autowired
    private PSTArticleComposeMapper pstArticleComposeMapper;

    // 发布markdown课程时触发
    @Override
    public void addedProject(List<PSTArticle> pstArticleList) {
        //取到的 pstArticle id值为null  compose的id， articleId 为null
        List<PSTArticleCompose> pstArticleComposeList = new ArrayList<>();
        pstArticleList.forEach(pstArticle -> {
            pstArticleMapper.insert(pstArticle);
            Integer articleId = pstArticle.getId();
            pstArticle.getComposeList().forEach(pstArticleCompose -> {
                pstArticleCompose.setPstArticleId(articleId);
                pstArticleCompose.setStatus(0);
                pstArticleComposeList.add(pstArticleCompose);
            });
        });
        pstArticleComposeMapper.batchAdd(pstArticleComposeList);
    }

    @Override
    public PSTArticle getByPstId(Integer pstId) {
        PSTArticle pstArticle = pstArticleMapper.getByPstId(pstId);
        List<PSTArticleCompose> pstArticleComposeList = pstArticleComposeMapper.getByArticleId(pstArticle.getId());
        pstArticle.setComposeList(pstArticleComposeList);
        return pstArticle;
    }

    @Override
    public Double computeGrade(Integer pstId) {
        List<PSTArticleCompose> pstArticleComposeList = pstArticleComposeMapper.composeListByPstId(pstId);
        int allScore = 0;
        int allResult = 0;
        for (PSTArticleCompose pstArticleCompose : pstArticleComposeList) {
            if (pstArticleCompose.getScore() != null) {
                allScore += pstArticleCompose.getScore();
            }
            if(pstArticleCompose.getResult() != null){
                allResult += pstArticleCompose.getResult();
            }
        }
//        System.out.println(allResult);
//        System.out.println(allScore);
        BigDecimal result = new BigDecimal(allResult).multiply(new BigDecimal(100))
                .divide(new BigDecimal(allScore), 2, RoundingMode.HALF_UP);

//        System.out.println(result);
        double finalResult = result.doubleValue();
        return finalResult;
    }
}
