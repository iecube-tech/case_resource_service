package com.iecube.community.model.pst_article_compose.service;

import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;

import java.util.List;

public interface PSTArticleComposeService {
    PSTArticleCompose getCompose(Integer articleId, Integer index);

    PSTArticleCompose composeUpdateVal(Integer composeId, String val);

    PSTArticleCompose composeUpdateResult(Integer composeId, Double result);

    List<PSTArticleCompose> composeListByPstId(Integer pstId);
}
