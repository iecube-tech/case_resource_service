package com.iecube.community.model.iecube3835.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.iecube3835.dto.GenStudentContentPdf;
import com.iecube.community.model.iecube3835.dto.Question;
import com.iecube.community.model.iecube3835.dto.StudentSubmitContentDetails;
import com.iecube.community.model.iecube3835.dto.TaskDataTables;
import com.iecube.community.model.iecube3835.entity.PSTDetailDevice;
import com.iecube.community.model.iecube3835.mapper.PSTDetailsDeviceMapper;
import com.iecube.community.model.iecube3835.service.DeviceDetailDataService;
import com.iecube.community.model.iecube3835.service.ex.GenerateStudentReportException;
import com.iecube.community.model.iecube3835.service.ex.ReportIOException;
import com.iecube.community.model.project.service.ex.GenerateFileException;
import com.iecube.community.model.project_student_group.entity.GroupStudent;
import com.iecube.community.model.project_student_group.mapper.ProjectStudentGroupMapper;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.mapper.StudentMapper;
import com.iecube.community.model.task.entity.ProjectStudentTask;
import com.iecube.community.model.task.entity.StudentTaskDetailVo;
import com.iecube.community.model.task.mapper.TaskMapper;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.util.pdf.GenerateStudentReport;
//import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DeviceDetailDataServiceImpl implements DeviceDetailDataService {

    @Autowired
    private PSTDetailsDeviceMapper pstDetailsDeviceMapper;

    @Autowired
    private ProjectStudentGroupMapper projectStudentGroupMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskService taskService;

    @Value("${generated-report}")
    private String genStudentReportDir;

    @Override
    public PSTDetailDevice updatePstDetail(PSTDetailDevice pstDetailDevice) {
        Integer row;
        pstDetailDevice.setSubmit(false);
        if(pstDetailDevice.getId() == null){
            // insert
            row = pstDetailsDeviceMapper.insert(pstDetailDevice);
            if(row != 1){
                throw new InsertException("插入数据异常");
            }
        }else{
            //update
            row = pstDetailsDeviceMapper.updateData(pstDetailDevice);
            if(row != 1){
                throw new UpdateException("更新数据异常");
            }
        }
        return pstDetailDevice;
    }

    @Override
    public PSTDetailDevice updateGroupPstDetail(Integer groupId, Integer pstId, PSTDetailDevice pstDetailDevice) {
        List<ProjectStudentTask> groupProjectStudentTask =  this.getGroupProjectStudentTask(groupId, pstId);
        // groupProjectStudentTask 中包含了pstId; 需要判断pst_detail_device中有没有pst的数据。没有新增，有则更新。
        for(ProjectStudentTask oneOfProjectStudentTask : groupProjectStudentTask){
            // 判断是否有数据
            PSTDetailDevice oldPSTDetailDevice = pstDetailsDeviceMapper.getByPSTId(oneOfProjectStudentTask.getId());
            if(oldPSTDetailDevice==null){
                PSTDetailDevice newPstDetailDevice = new PSTDetailDevice();
                newPstDetailDevice.setPstId(oneOfProjectStudentTask.getId());
                newPstDetailDevice.setData(pstDetailDevice.getData());
                newPstDetailDevice.setSubmit(false);
                // 原来没有数据 插入
                pstDetailsDeviceMapper.insert(newPstDetailDevice);
            }else{
                // 原来由数据 更新
                oldPSTDetailDevice.setData(pstDetailDevice.getData());
                oldPSTDetailDevice.setSubmit(false);
                pstDetailsDeviceMapper.updateData(oldPSTDetailDevice);
            }

        }
        PSTDetailDevice rePSTDetailDevice = pstDetailsDeviceMapper.getByPSTId(pstId);
        return rePSTDetailDevice;
    }

    public List<ProjectStudentTask> getGroupProjectStudentTask(Integer groupId, Integer pstId){
        //根据groupId 获取学生列表
        List<GroupStudent> groupStudentList = projectStudentGroupMapper.getStudentsByGroupId(groupId);
        // 根据pstId获取projectId和taskId
        ProjectStudentTask projectStudentTask = taskMapper.getProjectStudentTaskById(pstId);
        Integer projectId = projectStudentTask.getProjectId();
        Integer taskId = projectStudentTask.getTaskId();
        //获取该projectId和该taskId的所有的pst
        List<ProjectStudentTask> allProjectStudentTask = taskMapper.getProjectStudentTaskByProjectIdAndTaskId(projectId, taskId);
        List<ProjectStudentTask> groupProjectStudentTask = new ArrayList<>();
        // 固定的projectId和taskId 结合学生id 确定pstId
        for(int i=0; i<groupStudentList.size(); i++){
            for(int j=0; j<allProjectStudentTask.size(); j++){
                if(groupStudentList.get(i).getStudentId().equals(allProjectStudentTask.get(j).getStudentId())){
                    groupProjectStudentTask.add(allProjectStudentTask.get(j));
                }
            }
        }
        return groupProjectStudentTask;
    }

    @Override
    public PSTDetailDevice getByPstId(Integer pstId) {
        PSTDetailDevice pstDetailDevice = pstDetailsDeviceMapper.getByPSTId(pstId);
        return pstDetailDevice;
    }

    @Override
    public PSTDetailDevice submit(Integer pstId, Integer studentId) {
        PSTDetailDevice pstDetailDevice = pstDetailsDeviceMapper.getByPSTId(pstId);
//        if(pstDetailDevice.getSubmit()){
//            return null;
//        }
        StudentDto studentDto = studentMapper.getById(studentId);
        StudentTaskDetailVo studentTaskDetailVo = taskMapper.findStudentTaskByPSTId(pstId);
        String jsonString = pstDetailDevice.getData();
        GenerateStudentReport generateStudentReport =new GenerateStudentReport(genStudentReportDir);
        try{
            MultipartFile file = generateStudentReport.startGen(studentDto, studentTaskDetailVo, jsonString);
            taskService.submitFile(file,pstId,studentId);
            taskMapper.updatePSTStatus(pstId, 2);
            Integer row = pstDetailsDeviceMapper.updateSubmit(pstDetailDevice.getId(),true);
            if(row != 1){
                throw new UpdateException("更新数据异常");
            }
            pstDetailDevice.setSubmit(true);
            return pstDetailDevice;
        }catch (IOException e){
            e.printStackTrace();
            throw new ReportIOException("报告IO操作异常");
        }
//        catch (DocumentException e){
//            throw new GenerateStudentReportException("报告生成异常");
//        }

    }

    @Override
    public PSTDetailDevice groupSubmit(Integer groupId, Integer pstId, Integer studentId) {
        log.info("学生"+studentId+"提交了小组实验报告");
        List<ProjectStudentTask> groupProjectStudentTask =  this.getGroupProjectStudentTask(groupId, pstId);
        for(ProjectStudentTask oneOfProjectStudentTask : groupProjectStudentTask){
            this.submit(oneOfProjectStudentTask.getId(),oneOfProjectStudentTask.getStudentId());
        }
        // group 设置submitted
        projectStudentGroupMapper.updateGroupSubmitted(groupId);
        PSTDetailDevice rePSTDetailDevice = pstDetailsDeviceMapper.getByPSTId(pstId);
        return rePSTDetailDevice;
    }

//    public void synchronousData(Integer groupId, Integer studentId){
//
//    }

//    @Override
//    public void genTest(String html){
//        GenHtmlToPdf genHtmlToPdf = new GenHtmlToPdf();
//        try {
//            genHtmlToPdf.genHtmlToPdf(html);
//        }
//        catch (IOException){
//            e.printStackTrace();
//        }
//    }
}
