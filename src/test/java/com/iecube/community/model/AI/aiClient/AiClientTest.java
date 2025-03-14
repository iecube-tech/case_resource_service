package com.iecube.community.model.AI.aiClient;

import com.iecube.community.model.AI.aiClient.service.AiApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class AiClientTest {
    @Autowired
    private AiApiService aiApiService;

    @Test
    public void genChatTest(){
        String chatId = aiApiService.genChat();
        System.out.println(chatId);
    }

    @Test
    public void connectToChat(){
        String chatId = "5Q3LJGphRSPKeIdldHq3Le" ;
        aiApiService.webSocketConnect(chatId);
    }
}
