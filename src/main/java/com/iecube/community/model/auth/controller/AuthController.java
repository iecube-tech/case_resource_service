package com.iecube.community.model.auth.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.auth.entity.Authority;
import com.iecube.community.model.auth.service.AuthService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    // 获取所有权限
    @GetMapping("/all")
    public JsonResult<List<Authority>> getAllAuthority() {
        return new JsonResult<>(OK,authService.allAuth());
    }

}
