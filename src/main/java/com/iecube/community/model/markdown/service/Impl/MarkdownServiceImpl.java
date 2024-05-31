package com.iecube.community.model.markdown.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.markdown.entity.MDArticle;
import com.iecube.community.model.markdown.entity.MDChapter;
import com.iecube.community.model.markdown.entity.MDCourse;
import com.iecube.community.model.markdown.mapper.MarkdownMapper;
import com.iecube.community.model.markdown.qo.MDArticleQo;
import com.iecube.community.model.markdown.service.MarkdownService;
import com.iecube.community.model.markdown.vo.MDCatalogue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MarkdownServiceImpl implements MarkdownService {

    @Autowired
    private MarkdownMapper markdownMapper;

    @Override
    public List<MDCatalogue> getCatalogue(){
        List<MDCourse> courseList = markdownMapper.allCourse();
        List<MDCatalogue> mdCatalogueList = new ArrayList<>();
        for(MDCourse mdCourse : courseList){
            MDCatalogue mdCatalogue = new MDCatalogue();
            List<MDChapter> chapterList = markdownMapper.allChapterByCourseId(mdCourse.getId());
            mdCatalogue.setCourseId(mdCourse.getId());
            mdCatalogue.setCourseName(mdCourse.getName());
            mdCatalogue.setChapterList(chapterList);
            mdCatalogueList.add(mdCatalogue);
        }
        return mdCatalogueList;
    }

    @Override
    public MDArticle getArticleByChapter(Integer chapterId){
        MDArticle article = markdownMapper.articleByChapterId(chapterId);
        this.updateReadNum(article.getId(), article.getReadNum()+1);
        return article;
    }

    public void updateReadNum(Integer id, Integer readNum){
        markdownMapper.updateArticleReadNum(id,readNum);
    }

    @Override
    public List<MDCatalogue> addCourse(MDCourse mdCourse){
        Integer row = markdownMapper.addCourse(mdCourse);
        if(row != 1){
            throw new InsertException("新增数据异常");
        }
        List<MDCatalogue> mdCatalogueList = this.getCatalogue();
        return mdCatalogueList;
    }

    @Override
    public List<MDCatalogue> addChapter(MDChapter mdChapter){
        if(mdChapter.getCourseId()==null){
            throw new InsertException("数据提交错误");
        }
        if(mdChapter.getName() == "" || mdChapter.getCourseId() == null){
            throw new InsertException("数据提交错误");
        }
        Integer row = markdownMapper.addChapter(mdChapter);
        if(row != 1){
            throw new InsertException("新增数据异常");
        }
        MDArticle article = new MDArticle();
        article.setChapterId(mdChapter.getId());
        article.setContent("");
        article.setCreator(0);
        article.setCreateTime(new Date());
        article.setLastModifiedTime(new Date());
        article.setLastModifiedUser(0);
        Integer res = markdownMapper.addArticle(article);
        if(res != 1){
            markdownMapper.delChapter(mdChapter.getId());
            throw new InsertException("新增数据异常");
        }
        List<MDCatalogue> mdCatalogueList = this.getCatalogue();
        return mdCatalogueList;
    }

    @Override
    public MDArticle updateArticle(MDArticleQo articleQo){
        if(articleQo.getId() == null){
            throw new UpdateException("数据提交异常");
        }
        MDArticle article = new MDArticle();
        article.setId(articleQo.getId());
        article.setLastModifiedUser(0);
        article.setLastModifiedTime(new Date());
        article.setContent(articleQo.getContent());
        Integer row = markdownMapper.updateArticle(article);
        if(row != 1){
            throw new UpdateException("更新数据异常");
        }
        MDArticle newArticle = markdownMapper.articleById(articleQo.getId());
        return newArticle;
    }


}
