package com.iecube.community.model.tag.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TagMapperTests {
    @Autowired
    private TagMapper tagMapper;

    @Test
    public void getTeacherTags(){
        System.out.println(tagMapper.getTagsByTeacherProject(12, 6));
    }
}
