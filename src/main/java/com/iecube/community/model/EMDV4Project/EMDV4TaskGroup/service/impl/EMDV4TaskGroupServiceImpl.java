package com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.service.impl;

import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity.EMDV4GroupStudent;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity.EMDV4TaskGroup;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.mapper.EMDV4TaskGroupMapper;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.mapper.EMDV4TaskGroupStudentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.qo.TaskGroupQo;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.service.EMDV4TaskGroupService;
import com.iecube.community.model.EMDV4Project.project.mapper.EMDV4ProjectMapper;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.util.uuid.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.time.DateUtils.addDays;

@Service
public class EMDV4TaskGroupServiceImpl implements EMDV4TaskGroupService {

    @Autowired
    private EMDV4TaskGroupMapper emdV4TaskGroupMapper;

    @Autowired
    private EMDV4TaskGroupStudentMapper emdV4TaskGroupStudentMapper;

    @Autowired
    private EMDV4ProjectMapper emdV4ProjectMapper;

    // 查询task的学生列表 已分组 未分组 状态
    // 查询分组 查询分组信息（组信息，人员列表）
    // 新建分组 添加学生到分组 删除分组学生 删除分组
    // 分组邀请码（考虑分组邀请码重复的问题， 根具taskId区分）
    // 加入分组 根据邀请码加入分组

    @Override
    public EMDV4TaskGroup getTaskStudentGroup(Long taskId, Integer studentId) {
        EMDV4TaskGroup emdv4TaskGroup = emdV4TaskGroupMapper.getGroupByTaskIdAndStuId(taskId, studentId);
        if(emdv4TaskGroup==null || emdv4TaskGroup.getId()==null){
            return null;
        }
        emdv4TaskGroup.setIsCreator(Objects.equals(emdv4TaskGroup.getCreator(), studentId));
        List<Student> groupStudents = emdV4TaskGroupStudentMapper.getStudentsByGroupId(emdv4TaskGroup.getId());
        emdv4TaskGroup.setStudentList(groupStudents);
        return emdv4TaskGroup;
    }

    @Override
    public EMDV4TaskGroup createTaskGroup(TaskGroupQo taskGroupQo, Integer studentId) {
        Project project = emdV4ProjectMapper.getProjectByEMDV4Task(taskGroupQo.getTaskId());
        if(project==null || project.getId()==null){
            throw new InsertException("未找到相关数据");
        }
        EMDV4TaskGroup taskGroup = new EMDV4TaskGroup();
        taskGroup.setName(taskGroupQo.getName());
        taskGroup.setEmdv4TaskId(taskGroupQo.getTaskId());
        taskGroup.setLimitNum(project.getGroupLimit());
        taskGroup.setStatus(0);
        taskGroup.setCode(this.genGroupCode());
        taskGroup.setCreator(studentId);
        int res=emdV4TaskGroupMapper.insert(taskGroup);
        if(res!=1){
            throw new InsertException("新增分组数据异常");
        }
        EMDV4GroupStudent emdv4GroupStudent = new EMDV4GroupStudent();
        emdv4GroupStudent.setGroupId(taskGroup.getId());
        emdv4GroupStudent.setStudentId(studentId);
        int res2 = emdV4TaskGroupStudentMapper.insert(emdv4GroupStudent);
        if(res2!=1){
            throw new InsertException("新增分组学生数据异常");
        }
        return this.getTaskStudentGroup(taskGroupQo.getTaskId(), studentId);
    }

    @Override
    public EMDV4TaskGroup taskGroupFreshCode(Long groupId, Integer studentId) {
        EMDV4TaskGroup taskGroup = emdV4TaskGroupMapper.getById(groupId);
        if(taskGroup==null || taskGroup.getId()==null){
            throw new UpdateException("未找到相关数据");
        }
        if(!taskGroup.getCreator().equals(studentId)){
            throw new UpdateException("没有权限");
        }
        int res = emdV4TaskGroupMapper.updateCode(groupId, this.genGroupCode());
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return this.getTaskStudentGroup(taskGroup.getEmdv4TaskId(), studentId);
    }

    @Override
    public EMDV4TaskGroup taskGroupSetDoneStatus(Long groupId, Integer studentId) {
        EMDV4TaskGroup taskGroup = emdV4TaskGroupMapper.getById(groupId);
        if(taskGroup==null || taskGroup.getId()==null){
            throw new UpdateException("未找到相关数据");
        }
        if(!taskGroup.getCreator().equals(studentId)){
           if(taskGroup.getStatus()==1){
               return this.getTaskStudentGroup(taskGroup.getEmdv4TaskId(), studentId);
           }
           else {
               throw new UpdateException("请等待小组组长确定分组完成");
           }
        }
        int res = emdV4TaskGroupMapper.updateStatus(groupId, 1);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return this.getTaskStudentGroup(taskGroup.getEmdv4TaskId(), studentId);
    }

    @Override
    public EMDV4TaskGroup deleteTaskGroup(Long id, Integer studentId) {
        EMDV4TaskGroup taskGroup = emdV4TaskGroupMapper.getById(id);
        if(taskGroup==null || taskGroup.getId()==null){
            throw new  DeleteException("未找到相关数据");
        }
        if(!taskGroup.getCreator().equals(studentId)){
            throw new DeleteException("没有权限");
        }
        if(taskGroup.getStatus().equals(1)){
            throw new InsertException("小组已开始实验操作，不支持小组变更");
        }
        int res = emdV4TaskGroupMapper.deleteById(id);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return null;
    }

    /**
     * 添加学生到分组
     * @param taskGroupQo group 的 id 和studentList
     * @param studentId 操作人
     * @return EMDV4TaskGroup
     */
    @Override
    public EMDV4TaskGroup addStudentsToTaskGroup(TaskGroupQo taskGroupQo, Integer studentId) {
        EMDV4TaskGroup taskGroup = emdV4TaskGroupMapper.getById(taskGroupQo.getId());
        if(taskGroup == null ){
            throw new InsertException("未找到相关数据");
        }
        if(!taskGroup.getCreator().equals(studentId)){
            throw new InsertException("非小组组长，没有权限");
        }
        if(taskGroup.getStatus().equals(1)){
            throw new InsertException("小组已开始实验操作，不支持小组成员变更");
        }
        List<Student> taskStudents = emdV4TaskGroupStudentMapper.getStudentsByEMDV4TaskId(taskGroup.getEmdv4TaskId());
        List<Integer> taskStudentIds = taskStudents.stream().map(Student::getId).toList();
        List<Student> studentsOfJoinedGroup = emdV4TaskGroupStudentMapper.getStudentJoinedTaskGroup(taskGroup.getEmdv4TaskId());
        List<Integer> joinedGroupId = studentsOfJoinedGroup.stream().map(Student::getId).toList();
        List<EMDV4GroupStudent> groupStudentList = new ArrayList<>();

        List<Student> notInTask = new ArrayList<>();
        List<Student> hasJoinedGroup = new ArrayList<>();
        for (Student student : taskGroupQo.getStudents()) {
            // 判断学生是不是在task中
            if (!taskStudentIds.contains(student.getId())) {
                notInTask.add(student);
                break;
            }
            // 判断学生是不是已经在小组内，
            if (joinedGroupId.contains(student.getId())) {
                hasJoinedGroup.add(student);
                break;
            }
            EMDV4GroupStudent groupStudent = new EMDV4GroupStudent();
            groupStudent.setGroupId(taskGroup.getId());
            groupStudent.setStudentId(student.getId());
            groupStudentList.add(groupStudent);
        }
        if(!notInTask.isEmpty()){
            StringBuilder studentsName = new StringBuilder();
            for (Student ns : notInTask) {
                studentsName.append(ns.getStudentName()).append(",");
            }
            throw new InsertException(studentsName+"不在本实验中");
        }
        if(!hasJoinedGroup.isEmpty()){
            StringBuilder studentsName = new StringBuilder();
            for (Student ns : hasJoinedGroup) {
                studentsName.append(ns.getStudentName()).append(",");
            }
            throw new InsertException(studentsName+"已经加入其他小组");
        }
        int res = emdV4TaskGroupStudentMapper.batchInsert(groupStudentList);
        if(res!=groupStudentList.size()){
            throw new InsertException("新增数据异常");
        }
        return this.getTaskStudentGroup(taskGroupQo.getTaskId(), studentId);
    }

    @Override
    public List<Student> hasNotJoinedGroupStudent(Long taskId){
        List<Student> taskStudents = emdV4TaskGroupStudentMapper.getStudentsByEMDV4TaskId(taskId);
        List<Student> studentsOfJoinedGroup = emdV4TaskGroupStudentMapper.getStudentJoinedTaskGroup(taskId);
        List<Integer> joinedGroupId = studentsOfJoinedGroup.stream().map(Student::getId).toList();
        taskStudents.forEach(student -> {
            if(joinedGroupId.contains(student.getId())){
                taskStudents.remove(student);
            }
        });
        return taskStudents;
    }

    @Override
    public EMDV4TaskGroup removeStudentsFromTaskGroup(Long groupId, Integer stuId, Integer studentId){
        EMDV4GroupStudent groupStudent = emdV4TaskGroupStudentMapper.getByGroupAndStudentId(groupId, stuId);
        if(groupStudent==null||groupStudent.getGroupId()==null){
            throw new DeleteException("未找到相关数据");
        }
        if(groupStudent.getStudentId().equals(studentId)){
            throw new DeleteException("不支持移除自己");
        }

        EMDV4TaskGroup taskGroup = emdV4TaskGroupMapper.getById(groupStudent.getGroupId());
        if(!taskGroup.getCreator().equals(studentId)){
            throw new DeleteException("没有权限");
        }
        if(taskGroup.getStatus().equals(1)){
            throw new InsertException("小组已开始实验操作，不支持小组成员变更");
        }
        int res = emdV4TaskGroupStudentMapper.deleteById(groupStudent.getId());
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return this.getTaskStudentGroup(taskGroup.getEmdv4TaskId(), studentId);
    }

    @Override
    public EMDV4TaskGroup joinGroup(Long taskId, String code, Integer studentId) {
        EMDV4TaskGroup hasJoinedGroup = this.getTaskStudentGroup(taskId, studentId);
        if(hasJoinedGroup!=null){
            throw new InsertException("您已在小组"+hasJoinedGroup.getName()+"中");
        }
        EMDV4TaskGroup taskGroup = emdV4TaskGroupMapper.getByTaskAndCode(taskId, code);
        List<Student> groupStudents = emdV4TaskGroupStudentMapper.getStudentsByGroupId(taskGroup.getId());
        if(groupStudents.size()>= taskGroup.getLimitNum()){
            throw new InsertException("小组成员已满");
        }
        if(taskGroup.getStatus().equals(1)){
            throw new InsertException("小组已开始实验操作，不支持小组成员变更");
        }
        EMDV4GroupStudent groupStudent = new EMDV4GroupStudent();
        groupStudent.setGroupId(taskGroup.getId());
        groupStudent.setStudentId(studentId);
        int res = emdV4TaskGroupStudentMapper.insert(groupStudent);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return this.getTaskStudentGroup(taskId, studentId);
    }


    public String genGroupCode(){
        Date createTime = new Date();
        Date unableTime = addDays(createTime,7);
        String md5String = DigestUtils.md5DigestAsHex((UUIDGenerator.generateUUID() +"-"+createTime.getTime()+"-"+unableTime.getTime()).getBytes()).toUpperCase();
        return md5String.substring(1,7);
    }


}
