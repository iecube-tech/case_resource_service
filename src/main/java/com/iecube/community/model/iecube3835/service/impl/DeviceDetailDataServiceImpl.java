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
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.mapper.StudentMapper;
import com.iecube.community.model.task.entity.StudentTaskDetailVo;
import com.iecube.community.model.task.mapper.TaskMapper;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.util.pdf.GenerateStudentReport;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class DeviceDetailDataServiceImpl implements DeviceDetailDataService {

    @Autowired
    private PSTDetailsDeviceMapper pstDetailsDeviceMapper;

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
    public PSTDetailDevice getByPstId(Integer pstId) {
        PSTDetailDevice pstDetailDevice = pstDetailsDeviceMapper.getByPSTId(pstId);
        return pstDetailDevice;
    }

    @Override
    public PSTDetailDevice submit(Integer pstId, Integer studentId) {
        PSTDetailDevice pstDetailDevice = pstDetailsDeviceMapper.getByPSTId(pstId);
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
        }catch (DocumentException e){
            throw new GenerateStudentReportException("报告生成异常");
        }

    }
}
