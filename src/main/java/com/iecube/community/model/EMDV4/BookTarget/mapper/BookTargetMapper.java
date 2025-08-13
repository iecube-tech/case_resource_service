package com.iecube.community.model.EMDV4.BookTarget.mapper;

import com.iecube.community.model.EMDV4.BookTarget.entity.BookTarget;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookTargetMapper {
    int insert(BookTarget bookTarget);

    BookTarget getById(Long id);

    BookTarget selectByBookIdAndTargetId(Long bookId, Long targetId);
}
