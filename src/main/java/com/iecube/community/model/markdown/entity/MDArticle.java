package com.iecube.community.model.markdown.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;


@Data
public class MDArticle extends BaseEntity {
    Integer id;
    Integer chapterId;
    String content;
    Integer readNum;
}
