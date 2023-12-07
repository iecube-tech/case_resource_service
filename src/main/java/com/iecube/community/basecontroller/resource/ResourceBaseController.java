package com.iecube.community.basecontroller.resource;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.resource.service.ex.*;
import com.iecube.community.util.JsonResult;
import com.iecube.community.util.ex.SystemException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ResourceBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler({FileUploadException.class, SizeLimitExceededException.class}) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof InsertException) {
            result.setState(8001);
        }else if(e instanceof DeleteException){
            result.setState(8002);
        }else if(e instanceof ResourceNotFoundException) {
            result.setState(8003);
        }else if (e instanceof FileCreateFailedException){
            result.setState(8000);
        }else if (e instanceof FileEmptyException){
            result.setState(8000);
            result.setErrno(1);
        }else if (e instanceof FileSizeException){
            result.setState(8000);
        }else if (e instanceof FileTypeException){
            result.setState(8000);
        }else if (e instanceof SizeLimitExceededException){
            result.setState(8000);
            result.setMessage("文件太大，控制单个文件小于1GB");
        } else if (e instanceof SystemException) {
            result.setState(404);
            result.setMessage("文件未找到");
        }
        return result;
    }
}
