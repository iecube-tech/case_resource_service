package com.iecube.community.model.task_reference_file.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReferenceFileMapperTests {
    @Autowired
    private ReferenceFileMapper referenceFileMapper;

    @Test
    public void getReferenceFilesByTaskId(){
        System.out.println(referenceFileMapper.getReferenceFilesByTaskId(43));
    }
}
