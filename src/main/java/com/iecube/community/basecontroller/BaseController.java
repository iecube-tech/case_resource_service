package com.iecube.community.basecontroller;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.AuthException;
import com.iecube.community.util.JsonResult;

import com.iecube.community.util.ex.SystemException;
import com.iecube.community.util.jwt.AuthUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;


public class BaseController {

    public static final int OK=200;

    public final Integer currentUserId(){
        return AuthUtils.getCurrentUserId();
    }

    public final String currentUserType(){
        return AuthUtils.getCurrentUserType();
    }

    public final String currentUserEmail(){
        return AuthUtils.getCurrentUserEmail();
    }

    @ExceptionHandler(ServiceException.class)
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof SystemException) {
            result.setState(404);
            result.setMessage("文件未找到");
        } else if (e instanceof AuthException) {
            result.setState(401);
            result.setMessage(e.getMessage());
        }
        else if (e instanceof ServiceException) {
            result.setState(500);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
