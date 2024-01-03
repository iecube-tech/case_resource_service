package com.iecube.community.model.question_bank.mapper;

import com.iecube.community.model.question_bank.entity.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuestionBankMapperTests {

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Test
    public void getQuestion1(){
       List<Question> questionList = questionBankMapper.getQuestion1(1350);
        System.out.println(questionList);
    }
}
