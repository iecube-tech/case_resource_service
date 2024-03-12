package com.iecube.community.config;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        //获取httpsession对象
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        System.out.println("执行到这了");
        System.out.println(HttpSession.class.getName());
        System.out.println(httpSession);
        // 将httpSession对象保存
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
