package com.iecube.community.basecontroller.student;
import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.PasswordNotMatchException;
import com.iecube.community.model.student.service.ex.StudentDuplicateException;
import com.iecube.community.model.student.service.ex.StudentKeyFieldNotExistException;
import com.iecube.community.model.student.service.ex.StudentNotFoundException;
import com.iecube.community.model.student.service.ex.UnprocessableException;
import com.iecube.community.util.JsonResult;
import com.iecube.community.util.ex.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;


public class StudentBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof StudentNotFoundException) {
            result.setState(8001);
        } else if(e instanceof PasswordNotMatchException) {
            result.setState(1001);
        } else if (e instanceof StudentKeyFieldNotExistException) {
            result.setState(8002);
        } else if( e instanceof StudentDuplicateException){
            result.setState(8003);
        } else if (e instanceof UnprocessableException) {
            result.setState(8004);
        } else if (e instanceof SystemException){
            result.setState(8005);
        }
        return result;
    }
}
