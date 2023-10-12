package com.iecube.community.model.major.service;

import com.iecube.community.model.major.entity.School;
import com.iecube.community.model.major.vo.CollageListOfSchool;
import com.iecube.community.model.major.vo.MajorClass;
import com.iecube.community.model.major.vo.SchoolCollage;
import com.iecube.community.model.major.vo.SchoolCollageMajors;

import java.util.List;

public interface MajorService {
    List<MajorClass> teacherMajorClasses(Integer teacherId);

    SchoolCollage teacherCollage(Integer teacherId);

    SchoolCollageMajors getSchoolCollageMajorsByTeacher(Integer teacherId);

    List<School> schoolList();

    List<CollageListOfSchool> collageListOfSchoolList();
}
