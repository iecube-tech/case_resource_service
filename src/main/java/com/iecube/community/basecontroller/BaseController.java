package com.iecube.community.basecontroller;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.exception.NotFoundException;
import com.iecube.community.exception.ParameterException;
import com.iecube.community.model.auth.service.ex.AuthException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.content.service.ex.ContentNotFoundException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.project.service.ex.ProjectNotFoundException;
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
        System.out.println(e.getMessage());
        System.out.println(e.getStackTrace());
        System.out.println(e);
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof SystemException) {
            result.setState(404);
            result.setMessage("文件未找到");
        } else if (e instanceof AuthException) {
            result.setState(401);
            result.setMessage(e.getMessage());
        }else if (e instanceof ParameterException) {
            result.setState(8001);
            result.setMessage(e.getMessage());
        }
        else if (e instanceof NotFoundException) {
            result.setState(8002);
            result.setMessage(e.getMessage());
        }
        else if (e instanceof ContentNotFoundException) {
            result.setState(8002);
            result.setMessage(e.getMessage());
        } else if (e instanceof ProjectNotFoundException) {
            result.setState(8002);
            result.setMessage(e.getMessage());
        } else if (e instanceof UpdateException) {
            result.setState(8003);
            result.setMessage(e.getMessage());
        }
        else if (e instanceof DeleteException) {
            result.setState(8004);
            result.setMessage(e.getMessage());
        }
        else if (e instanceof InsertException) {
            result.setState(8005);
            result.setMessage(e.getMessage());
        }
        else if (e instanceof ServiceException) {
            result.setState(8000);
            result.setMessage(e.getMessage());
            result.setCause(e.getCause()==null?"":e.getCause().getMessage());
        }
        return result;
    }
}
