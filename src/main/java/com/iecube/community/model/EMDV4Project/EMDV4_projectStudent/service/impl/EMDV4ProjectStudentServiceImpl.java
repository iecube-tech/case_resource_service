package com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.service.impl;

import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.mapper.EMDV4ProjectStudentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.service.EMDV4ProjectStudentService;
import com.iecube.community.model.EMDV4Project.project.qo.EMDV4ProjectQo;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.student.entity.StudentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EMDV4ProjectStudentServiceImpl implements EMDV4ProjectStudentService {

    @Autowired
    private EMDV4ProjectStudentMapper emdV4ProjectStudentMapper;


    @Override
    public List<EMDV4ProjectStudent> createProjectStudents(Project project, EMDV4ProjectQo emdv4ProjectQo, List<StudentDto> studentDtoList) {
        List<EMDV4ProjectStudent> emdV4ProjectStudentList = new ArrayList<>();
        studentDtoList.forEach(studentDto -> {
            EMDV4ProjectStudent ps = new EMDV4ProjectStudent();
            ps.setGradeClass(project.getGradeClass());
            ps.setProjectId(project.getId());
            ps.setStudentId(studentDto.getId());
            ps.setScore(0.0);
            ps.setTotalNumOfLabs(0); //todo
            ps.setDoneNumOfLabs(0);
            ps.setTotalNumOfTags(0); //todo
            ps.setAchievedNumOfTags(0);
            ps.setStartTime(null);
            ps.setStatus(0);
            ps.setDoneTime(null);
            ps.setCreator(project.getCreator());
            ps.setCreateTime(new Date());
            ps.setLastModifiedUser(project.getCreator());
            ps.setLastModifiedTime(new Date());
            emdV4ProjectStudentList.add(ps);
        });
        int res = emdV4ProjectStudentMapper.batchInsert(emdV4ProjectStudentList);
        if(res!=emdV4ProjectStudentList.size()){
            throw new InsertException("课程学生添加异常");
        }
        return emdV4ProjectStudentMapper.getByProjectId(project.getId());
    }

    @Override
    public void updateProjectStudentTotalNum(Integer projectId, int totalNumOfLabs, int totalNumOfTags){
        emdV4ProjectStudentMapper.updateProjectTotalNum(projectId, totalNumOfLabs, totalNumOfTags);
    }

    @Override
    public EMDV4ProjectStudent getByStuProject(int studentId, int projectId) {

        return emdV4ProjectStudentMapper.getByStudentIdAndProjectId(studentId, projectId);
    }
}
