package com.iecube.community.model.pst_article.entity;

import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;
import lombok.Data;

import java.util.List;

@Data
public class PSTArticle {
    Integer id;
    Integer pstId;
    String content;
    String catalogue;
    List<PSTArticleCompose> composeList;
}
