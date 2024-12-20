package com.iecube.community.model.major.mapper;

import com.iecube.community.model.major.entity.ClassAndGrade;
import com.iecube.community.model.major.entity.Collage;
import com.iecube.community.model.major.entity.Major;
import com.iecube.community.model.major.entity.School;
import com.iecube.community.model.major.vo.CollageMajors;
import com.iecube.community.model.major.vo.MajorClass;
import com.iecube.community.model.major.vo.SchoolCollage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MajorMapper {
    List<MajorClass> TeacherMajorClass(Integer teacherId);

    List<ClassAndGrade> getClassesByMajorId(Integer majorId);

    List<ClassAndGrade> findClassesByMajorId(Integer majorId);

    Integer addGradeClass(ClassAndGrade gradeClass);

    Integer addMajor(Major major);

    Integer TeacherMajorId(String majorName, Integer teacherId);

    List<Major> getMajorsByCollage(Integer collageId);

    Integer MajorClassId(String classGrade, String className, Integer majorId);

    Integer addCollage(Collage collage);

    SchoolCollage SchoolCollage(Integer collageId);

    List<Collage> getCollagesBySchool(Integer schoolId);

    List<School> schoolList();

    School getSchoolByTeacher(Integer teacherId);

    School getSchoolByName(String schoolName);

    Integer addSchool(School school);
}
