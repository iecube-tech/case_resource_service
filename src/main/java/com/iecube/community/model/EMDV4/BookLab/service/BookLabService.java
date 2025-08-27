package com.iecube.community.model.EMDV4.BookLab.service;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;

import java.util.List;

public interface BookLabService {
    /**
     * 获取所有根节点
     */
    List<BookLabCatalog> getRootNodes();

    /**
     * 根据父节点ID获取子节点列表（懒加载）
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<BookLabCatalog> getChildrenByParentId(Long parentId);


    List<BookLabCatalog> createBookLabCatalog(BookLabCatalog record);

    BookLabCatalog updateBookLabCatalog(BookLabCatalog record);

    List<BookLabCatalog> deleteById(Long id);

    /**
     * 获取一个完整的实验指导书
     * @param id  实验指导书的ID
     * @return BookLabCatalog
     */
    BookLabCatalog wholeBookLabCatalogById(Long id);
}
