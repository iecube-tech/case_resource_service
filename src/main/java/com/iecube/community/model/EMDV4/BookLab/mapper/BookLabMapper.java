package com.iecube.community.model.EMDV4.BookLab.mapper;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookLabMapper {

    int insert(BookLabCatalog record);

    int update(BookLabCatalog record);

    int delete(Long id);

    /**
     * 查询根节点（pId为null或0）
     */
    List<BookLabCatalog> selectRootNodes();


    /**
     * 根据父节点ID查询所有直接子节点
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<BookLabCatalog> selectChildrenByParentId(@Param("parentId") Long parentId);

    BookLabCatalog getById(@Param("id") Long id);

    /**
     * 检查指定节点是否有子节点
     * @param nodeId 节点ID
     * @return 有子节点返回true，否则返回false
     */
    boolean hasChildren(@Param("nodeId") Long nodeId);

    int batchUpOrder(List<BookLabCatalog> list);
}
