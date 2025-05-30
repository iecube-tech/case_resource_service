package com.iecube.community.model.louYongle.service;

import com.iecube.community.model.louYongle.entity.XiDianGrade;

import java.util.List;

public interface LouYongLeService {

    List<XiDianGrade> getAll();

    void insert(List<XiDianGrade> xiDianGradeList);
}
