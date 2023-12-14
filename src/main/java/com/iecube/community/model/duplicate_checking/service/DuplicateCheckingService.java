package com.iecube.community.model.duplicate_checking.service;


import com.iecube.community.model.duplicate_checking.vo.RepetitiveRateVo;

import java.util.List;

public interface DuplicateCheckingService {
    void DuplicateCheckingByPSTid(Integer pstId);
    void DuplicateCheckingByTaskId(Integer taskId);

    List<RepetitiveRateVo>  getRepetitiveRateByTask(Integer taskId);

    List<RepetitiveRateVo> getRepetitiveRateByPstId(Integer pstId);
}
