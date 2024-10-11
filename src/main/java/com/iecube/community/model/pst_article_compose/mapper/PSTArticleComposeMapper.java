package com.iecube.community.model.pst_article_compose.mapper;

import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PSTArticleComposeMapper {
    Integer batchAdd(List<PSTArticleCompose> list);

    List<PSTArticleCompose> getByArticleId(Integer pstArticleId);

    PSTArticleCompose getByArticleIndex(Integer articleId, Integer index);

    PSTArticleCompose getById(Integer id);

    Integer composeUpdateVal(Integer id, String val);

    Integer composeUpdateResult(Integer id, Double result);

    Integer composeUpdateResultStatus(Integer id, Double result);

    List<PSTArticleCompose> composeListByPstId(Integer pstId);

    Integer composeUpdateByPstIdIndex(PSTArticleCompose pstArticleCompose);
}
