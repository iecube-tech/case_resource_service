package com.iecube.community.basecontroller.project_student_group;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.task_student_group.service.ex.GroupCodeException;
import com.iecube.community.model.task_student_group.service.ex.GroupGenCodeException;
import com.iecube.community.model.task_student_group.service.ex.GroupLimitException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ProjectStudentGroupBaseController extends BaseController {
    public static final int OK=200;
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if(e instanceof InsertException) {
            result.setState(10001);
        } else if (e instanceof UpdateException) {
            result.setState(10002);
        } else if (e instanceof DeleteException) {
            result.setState(10003);
        } else if (e instanceof GroupGenCodeException) {
            result.setState(10004);
        } else if (e instanceof GroupCodeException) {
            result.setState(10005);
        } else if (e instanceof GroupLimitException) {
            result.setState(10006);
        }
        return result;
    }
}
