package com.iecube.community.model.pst_article.service;

import com.iecube.community.model.pst_article.entity.PSTArticle;

import java.util.List;

public interface PSTArticleService {
    void addedProject(List<PSTArticle> pstArticleList);

    PSTArticle getByPstId(Integer pstId);

    Double computeGrade(Integer pstId);
}
