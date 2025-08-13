package com.iecube.community.model.EMDV4.BookLab.service.impl;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4.BookLab.mapper.BookLabMapper;
import com.iecube.community.model.EMDV4.BookLab.service.BookLabService;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BookLabServiceImpl implements BookLabService {

    @Autowired
    private BookLabMapper bookLabMapper;

    /**
     * 获取所有根节点
     */
    @Override
    public List<BookLabCatalog> getRootNodes() {
        List<BookLabCatalog> rootNodes = bookLabMapper.selectRootNodes();
        // 初始化children列表，避免前端处理null
        rootNodes.forEach(node -> node.setChildren(null));
        return rootNodes;
    }

    /**
     * 根据父节点ID获取子节点列表（懒加载）
     */
    @Override
    public List<BookLabCatalog> getChildrenByParentId(Long parentId) {
        if (parentId == null) {
            throw new IllegalArgumentException("父节点ID不能为空");
        }

        List<BookLabCatalog> children = bookLabMapper.selectChildrenByParentId(parentId);
        // 初始化children列表，避免前端处理null
        children.forEach(child -> child.setChildren(null));
        return children;
    }

    @Override
    public List<BookLabCatalog> createBookLabCatalog(BookLabCatalog record) {
        if (record == null) {
            throw new InsertException("参数错误");
        }
        record.setVersion(4);
        if(record.getPId()!=null && record.getPId()!=0){
            // 存在pid
            if(record.getOrder()==null){
                List<BookLabCatalog> bookLabCatalogs = bookLabMapper.selectChildrenByParentId(record.getPId());
                record.setOrder(bookLabCatalogs.size()+1);
            }
            int res = bookLabMapper.insert(record);
            if (res != 1) {
                throw new InsertException("新增数据异常");
            }
            return bookLabMapper.selectChildrenByParentId(record.getPId());
        }
        else {
            // 不存在pId
            if(record.getOrder()==null){
                List<BookLabCatalog> bookLabCatalogs = bookLabMapper.selectRootNodes();
                record.setOrder(bookLabCatalogs.size()+1);
            }
            int res = bookLabMapper.insert(record);
            if (res != 1) {
                throw new InsertException("新增数据异常");
            }
            return bookLabMapper.selectRootNodes();
        }
    }

    @Override
    public BookLabCatalog updateBookLabCatalog(BookLabCatalog record) {
        if (record == null) {
            throw new InsertException("参数错误");
        }
        int res = bookLabMapper.update(record);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
        return bookLabMapper.getById(record.getId());
    }

    @Override
    public List<BookLabCatalog> deleteById(Long id) {
        if (id == null) {
            throw new InsertException("参数错误");
        }
        BookLabCatalog bookLabCatalog = bookLabMapper.getById(id);
        if (bookLabCatalog == null) {
            throw new DeleteException("未找到相关数据");
        }
        int res = bookLabMapper.delete(id);
        if(res != 1){
            throw new DeleteException("删除数据异常");
        }
        log.warn("删除数据 实验指导书数据: {}", bookLabCatalog);
        if(bookLabCatalog.getPId()!=null && bookLabCatalog.getPId()!=0){
            return bookLabMapper.selectChildrenByParentId(bookLabCatalog.getPId());
        }else {
            return bookLabMapper.selectRootNodes();
        }
    }
}
