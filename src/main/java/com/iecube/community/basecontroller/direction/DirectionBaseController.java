package com.iecube.community.basecontroller.direction;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.PermissionDeniedException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.direction.service.ex.DirectionDuplicateException;
import com.iecube.community.model.direction.service.ex.DirectionNotFoundException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class DirectionBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof PermissionDeniedException) {
            result.setState(6000);
        } else if(e instanceof DirectionDuplicateException){
            result.setState(6001);
        } else if(e instanceof DirectionNotFoundException) {
            result.setState(6002);
        } else if(e instanceof UpdateException) {
            result.setState(6003);
        } else if(e instanceof DeleteException) {
            result.setState(6004);
        } else if(e instanceof InsertException) {
            result.setState(6005);
        }
        return result;
    }
}
