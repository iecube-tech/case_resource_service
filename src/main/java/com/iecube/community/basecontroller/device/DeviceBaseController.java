package com.iecube.community.basecontroller.device;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.device.service.ex.IpConflictException;
import com.iecube.community.model.device.service.ex.OnlineBoxHandleException;
import com.iecube.community.model.device.service.ex.OnlineBoxHasBeenUsedException;
import com.iecube.community.model.tcpClient.ex.CannotConnectException;
import com.iecube.community.model.tcpClient.ex.OnlineBoxResponseTimeoutException;
import com.iecube.community.model.tcpClient.ex.ReceivedMessageException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class DeviceBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e) {
        JsonResult<Void> result = new JsonResult<>(e);
        // contentService resourceService
        if (e instanceof InsertException){
            result.setState(8000);
        } else if (e instanceof OnlineBoxHasBeenUsedException) {
            result.setState(8001);
        } else if (e instanceof IpConflictException) {
            result.setState(8002);
        } else if (e instanceof CannotConnectException){
            result.setState(8003);
        } else if (e instanceof OnlineBoxResponseTimeoutException) {
            result.setState(8005);
        } else if (e instanceof ReceivedMessageException){
            result.setState(8006);
        } else if (e instanceof OnlineBoxHandleException) {
            result.setState(8007);
        }
        return result;
    }
}
