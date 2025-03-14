package com.iecube.community.model.AI.aiMiddlware;

import com.iecube.community.util.SessionManager;
import org.springframework.stereotype.Component;


@Component
public class WebSocketSessionManage {
    /**
     * chatId --> clientSession
     * clientSession --> chatId
     * 以上为 和 AI 方连接的管理
     * ------------------------------
     * 以下为服务端的链接管理
     * chatId --> serviceSession
     * serviceSession -- chatId
     * 需求：
     * 需要在 onTextMessage 时 能够知道当前的chatId 是什么
     * 需要在知道chatId的时候 知道给那个session发送消息
     * */
    public final SessionManager clientSessionManager = new SessionManager();
    public final SessionManager serverSessionManager = new SessionManager();

}
