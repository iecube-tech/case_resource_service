package com.iecube.community.basecontroller.task;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.resource.service.ex.FileDeletedFailedException;
import com.iecube.community.model.resource.service.ex.FileNotFoundException;
import com.iecube.community.model.resource.service.ex.FileUploadException;
import com.iecube.community.model.resource.service.ex.ResourceNotFoundException;
import com.iecube.community.model.student.service.ex.StudentNotFoundException;
import com.iecube.community.model.task.service.ex.PSTResourceNotFoundException;
import com.iecube.community.model.task.service.ex.PermissionDeniedException;
import com.iecube.community.model.task.service.ex.SQLBatchProcessingException;
import com.iecube.community.util.JsonResult;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class TaskBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof StudentNotFoundException) {
            result.setState(8001);
        } else if (e instanceof PSTResourceNotFoundException) {
            result.setState(8001);
        } else if (e instanceof PermissionDeniedException) {
            result.setState(8000);
        }else if(e instanceof FileNotFoundException){
            result.setState(8001);
        }else if (e instanceof ResourceNotFoundException){
            result.setState(8001);
        }else if(e instanceof FileDeletedFailedException){
            result.setState(8001);
        } else if (e instanceof DeleteException) {
            result.setState(8002);
        } else if (e instanceof SQLBatchProcessingException) {
            result.setState(8003);
        }
        return result;
    }
}
