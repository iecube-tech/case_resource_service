package com.iecube.community.model.EMDV4.BookTarget.service;


import com.iecube.community.model.EMDV4.BookTarget.vo.BookTargetVo;

import java.util.List;

public interface BookTargetService {
    BookTargetVo bookAddTarget(Long bookId, Long targetId);

    BookTargetVo getBookTargets(Long bookId);

}
