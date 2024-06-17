package com.iecube.community.model.markdown.controller;

import com.iecube.community.basecontroller.markdown.MarkdownBaseController;
import com.iecube.community.model.markdown.entity.MDArticle;
import com.iecube.community.model.markdown.entity.MDChapter;
import com.iecube.community.model.markdown.entity.MDCourse;
import com.iecube.community.model.markdown.qo.MDArticleQo;
import com.iecube.community.model.markdown.service.MarkdownService;
import com.iecube.community.model.markdown.vo.ArticleVo;
import com.iecube.community.model.markdown.vo.MDCatalogue;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/md")
public class MarkdownController extends MarkdownBaseController {

    @Autowired
    private MarkdownService markdownService;

    /**
     * 所有的文章目录
     * @return
     */
    @GetMapping("/ca")
    public JsonResult<List> getCatalogue(){
        List<MDCatalogue> catalogueList = markdownService.getCatalogue();
        return new JsonResult<>(OK, catalogueList);
    }

    /**
     * getChapterById
     * @param chapterId
     * @return
     */
    @GetMapping("/ch/{chapterId}")
    public JsonResult<MDChapter> getChapter(@PathVariable Integer chapterId){
        MDChapter chapter = markdownService.getChapterById(chapterId);
        return new JsonResult<>(OK, chapter);
    }

    /**
     *
     * @param chapterId
     * @return MdArticle  单篇文章
     */
    @GetMapping("/ar/{chapterId}")
    public JsonResult<MDArticle> getArticle(@PathVariable Integer chapterId){
        MDArticle article = markdownService.getArticleByChapter(chapterId);
        return new JsonResult<>(OK,article);
    }

    @PostMapping("/co/add")
    public JsonResult<List> addCourse(@RequestBody MDCourse mdCourse){
        List<MDCatalogue> catalogueList = markdownService.addCourse(mdCourse);
        return new JsonResult<>(OK, catalogueList);
    }

    @PostMapping("/ch/add")
    public JsonResult<List> addChapter(@RequestBody MDChapter mdChapter){
        List<MDCatalogue> catalogueList = markdownService.addChapter(mdChapter);
        return new JsonResult<>(OK, catalogueList);
    }

    @PostMapping("/ar/up")
    public JsonResult<MDArticle> updateArticle(@RequestBody MDArticleQo mdArticleQo){
        MDArticle mdArticle = markdownService.updateArticle(mdArticleQo);
        return new JsonResult<>(OK, mdArticle);
    }

    @PostMapping("/ch/up")
    public JsonResult<MDChapter> updateChapter(@RequestBody MDChapter chapter){
        MDChapter chapter1 = markdownService.updateChapter(chapter);
        return new JsonResult<>(OK, chapter1);
    }

    @DeleteMapping("/ch/del")
    public JsonResult<List> delChapter(Integer id){
        List<MDCatalogue> catalogueList = markdownService.delChapter(id);
        return new JsonResult<>(OK, catalogueList);
    }

    @DeleteMapping("/co/del")
    public JsonResult<List> delCourse(Integer id){
        List<MDCatalogue> catalogueList = markdownService.delCourse(id);
        return new JsonResult<>(OK, catalogueList);
    }


    /**
     *
     * @param courseId
     * @return 该课程的章节列表
     */
    @GetMapping("/ch/list/{courseId}")
    public JsonResult<List> getChapterListByCourseId(@PathVariable Integer courseId){
        List<MDChapter> chapterList = markdownService.getChapterListByCourseId(courseId);
        return new JsonResult<>(OK, chapterList);
    }

    @GetMapping("/vo/ar/{chapterId}")
    public JsonResult<ArticleVo> getArticleVoByChapterId(@PathVariable Integer chapterId){
        ArticleVo articleVo = markdownService.getArticleVoByChapterId(chapterId);
        return new JsonResult<>(OK, articleVo);
    }

    @GetMapping("/vo/ar_list/{courseId}")
    public JsonResult<List> getArticleVoListByCourseId(@PathVariable Integer courseId){
        List<ArticleVo> articleVoList = markdownService.getArticleVoListByCourseId(courseId);
        return new JsonResult<>(OK, articleVoList);
    }



}
