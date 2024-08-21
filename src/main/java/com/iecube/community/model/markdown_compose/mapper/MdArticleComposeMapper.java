package com.iecube.community.model.markdown_compose.mapper;

import com.iecube.community.model.markdown_compose.entity.MdArticleCompose;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MdArticleComposeMapper {
    Integer batchAdd(List<MdArticleCompose> list);

    Integer deleteByArticle(Integer articleId);

    Integer updateValById(Integer id, String val);

    Integer updateAnswerById(Integer id, String answer);

    Integer updateScoreById(Integer id, Integer score);

    Integer updateSubjectiveById(Integer id, Boolean subjective);

    MdArticleCompose getByArticleIndex(Integer articleId, Integer index);

    MdArticleCompose getById(Integer id);

    List<MdArticleCompose> getByArticle(Integer articleId);
}
