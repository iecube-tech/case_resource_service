package com.iecube.community.model.test;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.util.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController extends BaseController {
    @GetMapping("/islogin")
    public JsonResult<String> isLoginTest(){
//        System.out.println(currentUserType()+currentUserId()+currentUserEmail());
        return new JsonResult<>(200,"已登录");
    }
}
