package com.iecube.community.basecontroller.auth;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.auth.service.ex.*;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.usergroup.service.ex.UserGroupNotFoundException;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**控制层类的基类
 * 做异常的捕获处理
 * **/
public class AuthBaseController extends BaseController {
    //操作成功的状态码
    public static final int OK=200;

    // 请求处理方法   这个方法的返回值就是需要传递给前端的数据
    // 被ExceptionHandler 修饰的方法 会自动将异常对象传递给此方法的参数列表
    // 当项目中产生了异常 会被统一拦截到此方法中， 这个方法此时就充当请求处理的方法， 方法的返回值直接返回前端
    @ExceptionHandler(ServiceException.class) //用于统一处理抛出的异常
    public JsonResult<Void> handleException(Throwable e){
        JsonResult<Void> result = new JsonResult<>(e);
        if (e instanceof PhoneNumDuplicatedException){
            result.setState(4000);
            result.setMessage("手机号已经存在");
        } else if (e instanceof EmailDuplicateException) {
            result.setState(4001);
            result.setMessage("邮箱已存在");
        }else if (e instanceof AuthCodeErrorException) {
            result.setState(4002);
            result.setMessage("验证码错误");
        } else if (e instanceof UserNotFoundException) {
            result.setState(4003);
            result.setMessage("用户不存在");
        } else if (e instanceof PasswordNotMatchException) {
            result.setState(4004);
            result.setMessage("密码错误");
        } else if (e instanceof UserGroupNotFoundException) {
            result.setState(4005);
            result.setMessage("用户组未找到");
        } else if (e instanceof CanNotAddToUserGroupException) {
            result.setState(4006);
            result.setMessage("该不能加入该用户组");
        } else if (e instanceof OrganizationNotFoundException) {
            result.setState(4007);
            result.setMessage("组织未找到");
        } else if (e instanceof CanNotAddToOrganizationException) {
            result.setState(4008);
            result.setMessage("该用户不能加入改组织");
        } else if (e instanceof NotFoundProductDirectionException) {
            result.setState(4009);
            result.setMessage("产品方向未找到");
        } else if (e instanceof PermissionDeniedException) {
            result.setState(4010);
            result.setMessage("权限不足");
        } else if (e instanceof InsertException) {
            result.setState(4011);
            result.setMessage("注册时产生未知异常");
        } else if (e instanceof UpdateException) {
            result.setState(4012);
            result.setMessage("更新数据时产生未知异常");
        }
        return result;
    }

}
