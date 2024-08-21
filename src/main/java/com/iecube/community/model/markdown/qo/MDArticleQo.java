package com.iecube.community.model.markdown.qo;

import com.iecube.community.model.markdown_compose.entity.MdArticleCompose;
import lombok.Data;

import java.util.List;

@Data
public class MDArticleQo {
    Integer id;
    String content;
    List<MdArticleCompose> composeList;
    String catalogue;
}
