package com.iecube.community.model.markdown.mapper;

import com.iecube.community.model.markdown.entity.MDArticle;
import com.iecube.community.model.markdown.entity.MDChapter;
import com.iecube.community.model.markdown.entity.MDCourse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MarkdownMapper {
    /**
     * 课程列表-
     * 实验列表-
     * 实验内容-
     * 创建课程
     * 添加实验
     * 编辑实验内容
     * 保存实验内容
     * 编辑实验
     * 编辑课程
     * 删除实验
     * 删除课程
     */

    List<MDCourse> allCourse();
    List<MDChapter> allChapterByCourseId(Integer courseId);

    MDChapter chapterById(Integer id);

    MDArticle articleByChapterId(Integer chapterId);

    MDArticle articleById(Integer id);

    Integer addCourse(MDCourse course);

    Integer addChapter(MDChapter chapter);

    Integer updateChapter(MDChapter chapter);

    Integer addArticle(MDArticle article);

    Integer updateArticle(MDArticle article);

    Integer updateArticleReadNum(Integer id, Integer readNum);

    Integer delChapter(Integer id);

    Integer delCourse(Integer id);



}
