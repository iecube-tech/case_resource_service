package com.iecube.community.model.project_student_group.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.project.service.ProjectService;
import com.iecube.community.model.project_student_group.entity.Group;
import com.iecube.community.model.project_student_group.entity.GroupCode;
import com.iecube.community.model.project_student_group.entity.GroupStudent;
import com.iecube.community.model.project_student_group.entity.ProjectStudentsWithGroup;
import com.iecube.community.model.project_student_group.mapper.ProjectStudentGroupMapper;
import com.iecube.community.model.project_student_group.service.ProjectStudentGroupService;
import com.iecube.community.model.project_student_group.service.ex.GroupCodeException;
import com.iecube.community.model.project_student_group.service.ex.GroupGenCodeException;
import com.iecube.community.model.project_student_group.service.ex.GroupLimitException;
import com.iecube.community.model.project_student_group.vo.GroupVo;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.student.entity.StudentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.time.DateUtils.addDays;

@Service
public class ProjectStudentGroupServiceImpl implements ProjectStudentGroupService {

    @Autowired
    private ProjectStudentGroupMapper projectStudentGroupMapper;

    @Autowired
    private ProjectService projectService;

    @Override
    public int addGroup(Group group) {
        Integer row = projectStudentGroupMapper.addGroup(group);
        if(row != 1){
            throw new InsertException("新增数据异常");
        }
        GroupStudent groupStudent = new GroupStudent();
        groupStudent.setGroupId(group.getId());
        groupStudent.setStudentId(group.getCreator());
        Integer row2 = projectStudentGroupMapper.GroupAddStudent(groupStudent);
        if(row2 != 1){
            projectStudentGroupMapper.delGroup(group.getId());
            throw new InsertException("新增数据异常");
        }
        GroupCode groupCode = new GroupCode();
        for(int i=0; i<3; i++){
            groupCode = this.genGroupCode(group);
            List<GroupCode> resultList = projectStudentGroupMapper.getGroupCodeByCode(groupCode.getCode());
            if(resultList.size()==0){
                this.GroupAddCode(groupCode);
                return group.getId();
            }
        }
        throw new GroupGenCodeException("邀请码生成失败");
    }

    @Override
    public void updateGroup(Group group) {
        Integer row = projectStudentGroupMapper.updateGroup(group);
        if(row !=1 ){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public GroupVo delGroup(Integer id) {
        Group group = projectStudentGroupMapper.getGroupById(id);
        if(group.getSubmitted().equals(1)){
            throw new GroupLimitException("小组已提交报告，不可删除");
        }
        List<Student> groupStudent = projectStudentGroupMapper.getStudentByGroup(id);
        if(groupStudent.size()>1){
            throw new GroupLimitException("请先移除小组内其它成员再删除小组");
        }
        Integer row = projectStudentGroupMapper.delGroup(id);
        if(row != 1){
            throw new DeleteException("删除数据异常");
        }
        GroupVo groupVo = this.getGroupVoByGroupId(id);
        return groupVo;
    }

    /**
     * 创建group时添加本人用
     * @param groupStudent
     */
    @Override
    public void GroupAddStudent(GroupStudent groupStudent) {
        // 判断是否满足limit
            // 查询组内学生 判断组内人员数量
        Group group = projectStudentGroupMapper.getGroupById(groupStudent.getGroupId());
        List<GroupStudent> groupStudentList = projectStudentGroupMapper.getStudentsByGroupId(groupStudent.getGroupId());
        if(groupStudentList.size() >= group.getLimitNum()){
            throw new GroupLimitException("小组人员已满");
        }
        Integer row = projectStudentGroupMapper.GroupAddStudent(groupStudent);
        if(row !=1){
            throw new InsertException("新增数据异常");
        }
    }

    @Override
    public void GroupRemoveStudent(Integer groupId, Integer studentId) {
        Integer row = projectStudentGroupMapper.GroupRemoveStudent(groupId, studentId);
        if(row != 1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public void GroupAddCode(GroupCode groupCode) {
        Integer row = projectStudentGroupMapper.GroupAddCode(groupCode);
        if(row != 1){
            throw new InsertException("新增数据异常");
        }
    }

    @Override
    public GroupVo getGroupVoByGroupId(Integer groupId) {
        Group group = projectStudentGroupMapper.getGroupById(groupId);
        if(group == null){
            return new GroupVo();
        }
        List<Student> students = projectStudentGroupMapper.getStudentByGroup(groupId);
        List<GroupCode> codes = projectStudentGroupMapper.getGroupCodeByGroupId(groupId);
        GroupCode code = codes.get(codes.size()-1);
        GroupVo groupVo = new GroupVo();
        groupVo.setGroupId(group.getId());
        groupVo.setGroupName(group.getName());
        groupVo.setCreator(group.getCreator());
        groupVo.setLimitNum(group.getLimitNum());
        groupVo.setGroupStudents(students);
        groupVo.setCode(code.getCode());
        groupVo.setCodeUnableTime(code.getUnableTime());
        return groupVo;
    }

    @Override
    public GroupVo getGroupVoByProjectStudent(Integer projectId, Integer studentId){
        Group group = projectStudentGroupMapper.getGroupByProjectStudent(projectId,studentId);
        if(group == null){
            return null;
        }
        List<Student> students = projectStudentGroupMapper.getStudentByGroup(group.getId());
        List<GroupCode> codes = projectStudentGroupMapper.getGroupCodeByGroupId(group.getId());
        GroupCode code = codes.get(codes.size()-1);
        GroupVo groupVo = new GroupVo();
        groupVo.setGroupId(group.getId());
        groupVo.setGroupName(group.getName());
        groupVo.setCreator(group.getCreator());
        groupVo.setLimitNum(group.getLimitNum());
        groupVo.setGroupStudents(students);
        groupVo.setCode(code.getCode());
        groupVo.setCodeUnableTime(code.getUnableTime());
        return groupVo;
    }

    @Override
    public GroupVo studentJoinGroup(String code, Integer projectId, Integer studentId) {
        List<GroupCode> groupCodeList = projectStudentGroupMapper.getGroupCodeByCode(code);
        if(groupCodeList.size()!=1){
            throw new GroupCodeException("无法识别请码");
        }

        Group group = projectStudentGroupMapper.getGroupById(groupCodeList.get(0).getGroupId());
        if(!projectId.equals(group.getProjectId())){
            throw new GroupCodeException("非同一课程");
        }
        if(group.getSubmitted() == 1){
            throw new GroupLimitException("该小组已有实验提交报告");
        }
        List<GroupStudent> groupStudentList = projectStudentGroupMapper.getStudentsByGroupId(group.getId());
        if(groupStudentList.size() >= group.getLimitNum()){
            throw new GroupLimitException("小组人员已满");
        }

        GroupStudent groupStudent = new GroupStudent();
        groupStudent.setStudentId(studentId);
        groupStudent.setGroupId(group.getId());
        Integer row = projectStudentGroupMapper.GroupAddStudent(groupStudent);
        if(row != 1){
            throw new InsertException("加入小组失败");
        }
        GroupVo groupVo = this.getGroupVoByGroupId(group.getId());
        return groupVo;
    }

    public GroupCode genGroupCode(Group group){
        // 生成邀请码
        Date createTime = new Date();
        Date unableTime = addDays(createTime,7);
        String md5String = DigestUtils.md5DigestAsHex((group.getId()+"-"+createTime.getTime()+"-"+unableTime.getTime()).getBytes()).toUpperCase();
        String code = md5String.substring(1,7);
        // 保存邀请码
        GroupCode groupCode = new GroupCode();
        groupCode.setGroupId(group.getId());
        groupCode.setCode(code);
        groupCode.setCreateTime(createTime);
        groupCode.setUnableTime(unableTime);
        return groupCode;
    }

    @Override
    public GroupCode refreshGroupCode(Integer groupId){
        Group group = projectStudentGroupMapper.getGroupById(groupId);
        GroupCode groupCode = this.genGroupCode(group);
        projectStudentGroupMapper.delGroupCode(groupId);
        Integer row = projectStudentGroupMapper.GroupAddCode(groupCode);
        if(row!=1){
            throw new InsertException("更新邀请码失败");
        }
        return groupCode;
    }

    @Override
    public GroupVo removeStudentFromGroup(Integer groupId, Integer studentId) {
        Group group = projectStudentGroupMapper.getGroupById(groupId);
        if(group.getSubmitted().equals(1)){
            throw new GroupLimitException("小组已提交实验报告，不可更改成员");
        }
        projectStudentGroupMapper.GroupRemoveStudent(groupId, studentId);
        GroupVo groupVo = this.getGroupVoByGroupId(groupId);
        return groupVo;
    }

    @Override
    public GroupVo addStudentsToGroup(List<ProjectStudentsWithGroup> studentList, Integer groupId){
        Group group = projectStudentGroupMapper.getGroupById(groupId);
        GroupVo groupVo = this.getGroupVoByGroupId(groupId);
        if(group.getSubmitted().equals(1)){
            throw new GroupLimitException("小组已提交实验报告，不可更改成员");
        }
        Integer groupStudentNum = groupVo.getGroupStudents().size();
        Integer willAddNum = studentList.size();
        if(groupStudentNum+willAddNum > group.getLimitNum()){
            throw new GroupLimitException("小组限制人数为:"+group.getLimitNum());
        }

        if(groupVo.getGroupStudents().size()>0){
            for(int i=0; i<groupVo.getGroupStudents().size(); i++){
                for (int j=0; j<studentList.size(); j++){
                    if(groupVo.getGroupStudents().get(i).getId().equals(studentList.get(j).getId())){
                        throw new GroupLimitException(groupVo.getGroupStudents().get(i).getStudentName()+"已在小组中");
                    }
                }
            }
        }

        for(ProjectStudentsWithGroup student: studentList){
            GroupStudent groupStudent = new GroupStudent();
            groupStudent.setStudentId(student.getId());
            groupStudent.setGroupId(group.getId());
            Integer row = projectStudentGroupMapper.GroupAddStudent(groupStudent);
            if(row != 1){
                throw new InsertException(student.getStudentName()+"加入小组失败");
            }
        }
        GroupVo res = this.getGroupVoByGroupId(groupId);
        return res;
    }

    @Override
    public GroupVo updateGroupName(Integer groupId, String groupName){
        Group group = projectStudentGroupMapper.getGroupById(groupId);
        group.setName(groupName);
        Integer row = projectStudentGroupMapper.updateGroup(group);
        if(row != 1){
            throw new UpdateException("更新数据异常");
        }
        GroupVo groupVo = this.getGroupVoByGroupId(groupId);
        return groupVo;
    }

    @Override
    public List<ProjectStudentsWithGroup> projectStudentsWithGroup(Integer projectId) {
        List<StudentDto> studentDtoList = projectService.getProjectStudents(projectId);
        List<ProjectStudentsWithGroup> studentsJoinedGroupList = projectStudentGroupMapper.getProjectStudentsWithGroup(projectId);
        List<Integer> JoinedStudentId = new ArrayList<>();
        for(ProjectStudentsWithGroup P_swg:  studentsJoinedGroupList){
            JoinedStudentId.add(P_swg.getId());
        }
        List<ProjectStudentsWithGroup> res = new ArrayList<>();
        for(StudentDto student: studentDtoList){
            if(JoinedStudentId.contains(student.getId())){
                continue;
            }
            ProjectStudentsWithGroup studentNotJoin = new ProjectStudentsWithGroup();
            studentNotJoin.setId(student.getId());
            studentNotJoin.setStudentName(student.getStudentName());
            studentNotJoin.setStudentId(student.getStudentId());
            res.add(studentNotJoin);
        }
        res.addAll(studentsJoinedGroupList);
        return res;
    }

    public GroupCode getGroupCodeByCode(String code){
        List<GroupCode> groupCodeList = projectStudentGroupMapper.getGroupCodeByCode(code);
        if(groupCodeList.size()>1){
            throw new GroupCodeException("无法识别的邀请码");
        }
        else if(groupCodeList.size()==0){
            throw new GroupCodeException("无效的邀请码");
        }
        else {
            Date currTime = new Date();
            if(currTime.after(groupCodeList.get(0).getUnableTime())){
                throw new GroupCodeException("邀请码已过期");
            }
            else {
                return groupCodeList.get(0);
            }
        }
    }

}
