package com.iecube.community.model.duplicate_checking.service;


import com.iecube.community.model.duplicate_checking.vo.RepetitiveRateVo;

import java.util.List;

public interface DuplicateCheckingService {
    Void DuplicateCheckingByPSTid(Integer pstId);
    Void DuplicateCheckingByTaskId(Integer taskId);
    List<RepetitiveRateVo>  getRepetitiveRateByTask(Integer taskId);

    Void regenerate(Integer taskId);
}
