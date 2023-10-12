package com.iecube.community.basecontroller.usergroup;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.PermissionDeniedException;
import com.iecube.community.model.usergroup.service.ex.UserGroupNotFoundException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * 统一处理userGroupService抛出的异常
 */
public class UserGroupBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof InsertException) {
            result.setState(5000);
            result.setMessage("添加用户组产生未知异常");
        }else if(e instanceof UserGroupNotFoundException){
            result.setState(5001);
            result.setMessage("用户组未找到");
        }else if(e instanceof PermissionDeniedException) {
            result.setState(5002);
        }
        return result;
    }

}
