package com.iecube.community.model.markdown.vo;

import lombok.Data;

@Data
public class ArticleVo {
    Integer chapterId;
    Integer courseId;
    Integer articleId;
    String chapterName;
    String content;
    String catalogue;
    Integer readNum;
}
