package com.iecube.community.model.major.service.Impl;

import com.iecube.community.model.major.entity.ClassAndGrade;
import com.iecube.community.model.major.entity.Collage;
import com.iecube.community.model.major.entity.Major;
import com.iecube.community.model.major.entity.School;
import com.iecube.community.model.major.mapper.MajorMapper;
import com.iecube.community.model.major.service.MajorService;
import com.iecube.community.model.major.vo.*;
import com.iecube.community.model.teacher.entity.Teacher;
import com.iecube.community.model.teacher.mapper.TeacherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MajorServiceImpl implements MajorService {

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private TeacherMapper teacherMapper;


    @Override
    public List<MajorClass> teacherMajorClasses(Integer teacherId) {
        List<MajorClass> majorClasses = majorMapper.TeacherMajorClass(teacherId);
        for(MajorClass majorClass: majorClasses){
            List<ClassAndGrade> classAndGrades = majorMapper.getClassesByMajorId(majorClass.getMajorId());
            majorClass.setMajorClasses(classAndGrades);
        }
        return majorClasses;
    }

    @Override
    public SchoolCollage teacherCollage(Integer teacherId) {
        Teacher teacher = teacherMapper.findById(teacherId);
        Integer collageId = teacher.getCollageId();
        SchoolCollage schoolCollage = majorMapper.SchoolCollage(collageId);
        return schoolCollage;
    }

    @Override
    public SchoolCollageMajors getSchoolCollageMajorsByTeacher(Integer teacherId) {
        SchoolCollageMajors schoolCollageMajors = new SchoolCollageMajors();
        School school =  majorMapper.getSchoolByTeacher(teacherId);
        schoolCollageMajors.setSchoolId(school.getId());
        schoolCollageMajors.setSchoolName(school.getName());
        List<Collage> collages = majorMapper.getCollagesBySchool(school.getId());
        List<CollageMajors> collageMajorsList = new ArrayList<>();
        for(Collage collage :collages){
            CollageMajors collageMajors = new CollageMajors();
            collageMajors.setCollageId(collage.getId());
            collageMajors.setCollageName(collage.getName());
            collageMajors.setSchoolId(collage.getSchoolId());
            List<Major> majors = majorMapper.getMajorsByCollage(collage.getId());
            collageMajors.setMajorList(majors);
            collageMajorsList.add(collageMajors);
        }
        schoolCollageMajors.setCollageMajorsList(collageMajorsList);
        return schoolCollageMajors;
    }

    @Override
    public List<School> schoolList() {
        List<School> schoolList = majorMapper.schoolList();
        return schoolList;
    }

    @Override
    public List<CollageListOfSchool> collageListOfSchoolList() {
        List<School> schoolList = majorMapper.schoolList();
        List<CollageListOfSchool> collageListOfSchoolList = new ArrayList<>();
        for(School school:schoolList){
            CollageListOfSchool collageListOfSchool = new CollageListOfSchool();
            collageListOfSchool.setId(school.getId());
            collageListOfSchool.setName(school.getName());
            List<Collage> collageList = majorMapper.getCollagesBySchool(school.getId());
            collageListOfSchool.setCollageList(collageList);
            collageListOfSchoolList.add(collageListOfSchool);
        }
        return collageListOfSchoolList;
    }
}
