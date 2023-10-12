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
    Integer addMajor(Major major);

    Integer addCollage(Collage collage);
    Integer addGradeClass(ClassAndGrade gradeClass);
    Integer TeacherMajorId(String majorName, Integer teacherId);
    Integer MajorClassId(String classGrade, String className, Integer majorId);
    SchoolCollage SchoolCollage(Integer collageId);
    School getSchoolByTeacher(Integer teacherId);

    List<Collage> getCollagesBySchool(Integer schoolId);

    List<Major> getMajorsByCollage(Integer collageId);

    List<School> schoolList();
}
