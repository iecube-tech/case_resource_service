package com.iecube.community.basecontroller.project;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.project.service.ex.GenerateFileException;
import com.iecube.community.model.remote_project.service.ex.DeviceHasBeenUsedException;
import com.iecube.community.model.resource.service.ex.FileUploadException;
import com.iecube.community.util.JsonResult;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ProjectBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler({FileUploadException.class, SizeLimitExceededException.class, ServiceException.class}) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof InsertException) {
            result.setState(8001);
        } else if (e instanceof GenerateFileException) {
            result.setState(8002);
        } else if (e instanceof DeviceHasBeenUsedException){
            result.setState(8003);
        }
        return result;
    }
}
