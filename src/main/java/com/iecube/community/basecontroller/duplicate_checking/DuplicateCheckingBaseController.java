package com.iecube.community.basecontroller.duplicate_checking;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.duplicate_checking.service.ex.NoPDFFilesException;
import com.iecube.community.model.duplicate_checking.service.ex.NoRepetitiveRateVoException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class DuplicateCheckingBaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof NoPDFFilesException) {
            result.setState(2002);
        } else if(e instanceof NoRepetitiveRateVoException){
            result.setState(2001);
        }
        return result;
    }
}


