package com.iecube.community.basecontroller.tag;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.PasswordNotMatchException;
import com.iecube.community.model.student.service.ex.StudentNotFoundException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class TagBaseController extends BaseController { public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof StudentNotFoundException) {
            result.setState(8001);
        } else if(e instanceof PasswordNotMatchException) {
            result.setState(1001);
        }
        return result;
    }

}
