package com.iecube.community.model.markdown.service;

import com.iecube.community.model.markdown.entity.MDArticle;
import com.iecube.community.model.markdown.entity.MDChapter;
import com.iecube.community.model.markdown.entity.MDCourse;
import com.iecube.community.model.markdown.qo.MDArticleQo;
import com.iecube.community.model.markdown.vo.MDCatalogue;

import java.util.List;

public interface MarkdownService {
    List<MDCatalogue> getCatalogue();

    MDChapter getChapterById(Integer chapterId);

    MDArticle getArticleByChapter(Integer chapterId);

    List<MDCatalogue> addCourse(MDCourse mdCourse);

    List<MDCatalogue> addChapter(MDChapter mdChapter);

    MDArticle updateArticle(MDArticleQo articleQo);

    MDChapter updateChapter(MDChapter chapter);

    List<MDCatalogue> delChapter(Integer id);

    List<MDCatalogue> delCourse(Integer id);
}
