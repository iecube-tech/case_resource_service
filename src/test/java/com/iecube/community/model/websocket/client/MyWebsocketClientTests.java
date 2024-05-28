package com.iecube.community.model.websocket.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MyWebsocketClientTests {
    @Test
    public void clientTest(){
        WebSocketClientManager clientManager = new WebSocketClientManager();
        String url = "ws://192.168.1.4:5000";
        String messageToSend = "Hello, WebSocket!";
        try{
            StompSession session = clientManager.connect(url);
            clientManager.sendMessage(session, "", messageToSend);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
