package com.iecube.community.basecontroller;

import javax.servlet.http.HttpSession;

public class BaseController {
    /**
     * 获取session对象的id
     * @param session session对象
     * @return 当前登录用户的userid
     */
    public final Integer getUserIdFromSession(HttpSession session){
        return Integer.valueOf(session.getAttribute("userid").toString());
    }

    /**
     * 获取session对象的username
     * @param session session 对象
     * @return 当前登录用户的username
     */
    public final String getUsernameFromSession(HttpSession session){
        return session.getAttribute("username").toString();
    }

    public final String getUserTypeFromSession(HttpSession session){
        return session.getAttribute("type").toString();
    }
}
