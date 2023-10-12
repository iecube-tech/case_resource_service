package com.iecube.community.basecontroller.analysis;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class AnalysisBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e) {
        JsonResult<Void> result = new JsonResult<>(e);
//        if (e instanceof PhoneNumDuplicatedException) {
//            result.setState(4000);
//            result.setMessage("手机号已经存在");
//        }
        return result;
    }
}
