package com.iecube.community.model.EMDV4.Tag.mapper;

import com.iecube.community.model.EMDV4.Tag.entity.BLTTag;
import com.iecube.community.model.EMDV4.Tag.vo.BLTTagVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BLTTagMapper {
    int insert(BLTTag record);

    int delete(Long id);

    int update(BLTTag record);

    BLTTag getById(Long id);

    BLTTagVo getVoById(Long id);

    List<BLTTag> getTagByBookId(Long bookId);

    List<BLTTagVo> getTagVoByBookId(Long bookId);
}
