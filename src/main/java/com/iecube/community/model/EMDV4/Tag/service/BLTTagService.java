package com.iecube.community.model.EMDV4.Tag.service;

import com.iecube.community.model.EMDV4.Tag.qo.BLTTagQo;
import com.iecube.community.model.EMDV4.Tag.vo.BLTTagVo;

import java.util.List;

public interface BLTTagService {
    List<BLTTagVo> getBLTTagVoByBookId(Long bookId);

    List<BLTTagVo> addBookTag(BLTTagQo qo);

    List<BLTTagVo> UpdateBookTag(BLTTagQo qo);

    List<BLTTagVo> deleteBookTag(Long tagId);
}
