package com.iecube.community.basecontroller.content;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.PermissionDeniedException;
import com.iecube.community.model.classification.service.ex.ClassificationDuplicateException;
import com.iecube.community.model.content.service.ex.ContentNotFoundException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ContentBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e) {
        JsonResult<Void> result = new JsonResult<>(e);
        // contentService resourceService
        if (e instanceof PermissionDeniedException){
            result.setState(8000);
        } else if (e instanceof ContentNotFoundException) {
            result.setState(8001);
        }
        return result;
    }
}
