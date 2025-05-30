package com.iecube.community.model.louYongle.service.impl;

import com.iecube.community.model.louYongle.entity.XiDianGrade;
import com.iecube.community.model.louYongle.mapper.LouYongLeMapper;
import com.iecube.community.model.louYongle.service.LouYongLeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LouYongLeServiceImpl implements LouYongLeService {

    @Autowired
    private LouYongLeMapper louYongLeMapper;

    @Override
    public List<XiDianGrade> getAll() {
        return louYongLeMapper.getAll();
    }

    @Override
    public void insert(List<XiDianGrade> xiDianGradeList) {
        xiDianGradeList.forEach(item->{
            louYongLeMapper.insert(item);
        });
    }
}
