package com.iecube.community.model.markdown.entity;

import com.iecube.community.entity.BaseEntity;
import com.iecube.community.model.markdown_compose.entity.MdArticleCompose;
import lombok.Data;

import java.util.List;


@Data
public class MDArticle extends BaseEntity {
    Integer id;
    Integer chapterId;
    String content;
    String catalogue;
    Integer readNum;
    List<MdArticleCompose> composeList;
}
