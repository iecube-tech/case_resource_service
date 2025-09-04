package com.iecube.community.model.EMDV4.BookLab.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class BookLabServiceTest {
    @Autowired
    private BookLabService bookLabService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void wholeTree(){
        BookLabCatalog res = bookLabService.wholeBookLabCatalogById(5L);
        try {
            System.out.println(objectMapper.writeValueAsString(res));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
