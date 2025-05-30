package com.iecube.community.model.louYongle.mapper;

import com.iecube.community.model.louYongle.entity.XiDianGrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LouYongLeMapper {
    int insert(XiDianGrade xiDianGrade);

    List<XiDianGrade> getAll();
}
