package com.iecube.community.model.EMDV4.BookTarget.service.impl;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4.BookLab.mapper.BookLabMapper;
import com.iecube.community.model.EMDV4.BookTarget.entity.BookTarget;
import com.iecube.community.model.EMDV4.BookTarget.mapper.BookTargetMapper;
import com.iecube.community.model.EMDV4.BookTarget.service.BookTargetService;
import com.iecube.community.model.EMDV4.BookTarget.vo.BookTargetVo;
import com.iecube.community.model.EMDV4.CourseTarget.entity.CourseTarget;
import com.iecube.community.model.EMDV4.CourseTarget.mapper.CourseTargetMapper;
import com.iecube.community.model.auth.service.ex.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookTargetServiceImpl implements BookTargetService {

    @Autowired
    private BookTargetMapper bookTargetMapper;

    @Autowired
    private CourseTargetMapper courseTargetMapper;

    @Autowired
    private BookLabMapper bookLabMapper;


    /**
     * 给实验指导书添加课程目标
     * @param bookId 实验指导书
     * @param targetId 课程目标
     */
    @Override
    public BookTargetVo bookAddTarget(Long bookId, Long targetId) {
        BookLabCatalog book = bookLabMapper.getById(bookId);
        CourseTarget courseTarget = courseTargetMapper.getById(targetId);

        if(book.getLevel()!=0){
            throw new ServiceException("请选择实验指导书");
        }
        if(courseTarget.getLevel()!=2){
            throw new ServiceException("请选择课程目标");
        }

        BookTarget bookTarget = new BookTarget();
        bookTarget.setBookId(book.getId());
        bookTarget.setTargetId(courseTarget.getId());
        int res = bookTargetMapper.insert(bookTarget);
        if(res==1){
            throw new InsertException("新增数据异常");
        }
        return this.getBookTargets(bookId);
    }

    @Override
    public BookTargetVo getBookTargets(Long bookId) {
        BookLabCatalog book = bookLabMapper.getById(bookId);
        List<CourseTarget> courseTargetList = courseTargetMapper.getCourseTargetByBookId(bookId);
        BookTargetVo bookTargetVo = new BookTargetVo();
        bookTargetVo.setBook(book);
        bookTargetVo.setTargetList(courseTargetList);
        return bookTargetVo;
    }
}
