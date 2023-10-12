package com.iecube.community.model.task_reference_link.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReferenceLinkMapperTests {
    @Autowired
    private ReferenceLinkMapper referenceLinkMapper;

    @Test
    public void getReferenceLinksByTaskId(){
        System.out.println(referenceLinkMapper.getReferenceLinksByTaskId(43));
    }
}
