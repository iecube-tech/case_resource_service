package com.iecube.community.basecontroller.classification;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.*;
import com.iecube.community.model.classification.service.ex.ClassificationDuplicateException;
import com.iecube.community.model.classification.service.ex.ClassificationNotFoundException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ClassificationBaseController extends BaseController {
    public static final int OK=200;

    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if (e instanceof PermissionDeniedException){
            result.setState(7000);
        } else if (e instanceof ClassificationDuplicateException) {
            result.setState(7001);
        } else if (e instanceof ClassificationNotFoundException) {
            result.setState(7002);
        } else if (e instanceof InsertException) {
            result.setState(7003);
        } else if (e instanceof UpdateException) {
            result.setState(7004);
        } else if (e instanceof DeleteException) {
            result.setState(7005);
        }
        return result;
    }
}
