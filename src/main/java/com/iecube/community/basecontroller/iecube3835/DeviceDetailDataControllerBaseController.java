package com.iecube.community.basecontroller.iecube3835;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.iecube3835.service.ex.GenerateStudentReportException;
import com.iecube.community.model.iecube3835.service.ex.ReportIOException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class DeviceDetailDataControllerBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof InsertException) {
            result.setState(2002);
        } else if(e instanceof UpdateException){
            result.setState(2001);
        } else if (e instanceof ReportIOException) {
            result.setState(2003);
        } else if (e instanceof GenerateStudentReportException) {
            result.setState(2004);
        }
        return result;
    }
}
