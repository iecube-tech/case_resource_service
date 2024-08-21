package com.iecube.community.model.pst_article.mapper;

import com.iecube.community.model.pst_article.entity.PSTArticle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PSTArticleMapper {
    Integer insert(PSTArticle pstArticle);

    PSTArticle getByPstId(Integer pstId);
}
