package com.iecube.community.model.markdown_compose.service;


import com.iecube.community.model.markdown_compose.entity.MdArticleCompose;

import java.util.List;

public interface MdArticleComposeService {

    //新建的markdown文档保存，涉及修改markdown的组件，会更新所有组件，组件数据也会初始化
    List<MdArticleCompose> MdArticleUpdate(Integer articleId, List<MdArticleCompose> newMdArticleComposeList);

    //获取文章的所有compose数据
    List<MdArticleCompose> getComposeByArticleId(Integer articleId);

    //获取单个compose的数据
    MdArticleCompose getCompose(Integer articleId, Integer index);

    MdArticleCompose mdComposerUpdateVal(Integer composeId, String val);

    MdArticleCompose mdComposerUpdateAnswer(Integer composeId, String answer);

    MdArticleCompose mdComposerUpdateScore(Integer composeId, Integer score);

    MdArticleCompose mdComposerUpdateSubjective(Integer composeId, Boolean subjective);
}
