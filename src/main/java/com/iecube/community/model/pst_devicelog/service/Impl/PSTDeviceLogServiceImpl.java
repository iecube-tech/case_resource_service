package com.iecube.community.model.pst_devicelog.service.Impl;

import com.google.gson.Gson;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.project.service.ProjectService;
import com.iecube.community.model.pst_devicelog.dto.PSTDeviceLogParseDto;
import com.iecube.community.model.pst_devicelog.dto.PSTOperations;
import com.iecube.community.model.pst_devicelog.dto.StudentLogOverview;
import com.iecube.community.model.pst_devicelog.entity.PSTDeviceLog;
import com.iecube.community.model.pst_devicelog.entity.TaskInfo;
import com.iecube.community.model.pst_devicelog.mapper.PSTDeviceLogMapper;
import com.iecube.community.model.pst_devicelog.service.PSTDeviceLogService;
import com.iecube.community.model.pst_devicelog.vo.StudentTasksOperations;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import com.iecube.community.model.task.entity.StudentTaskVo;
import com.iecube.community.model.task.mapper.TaskMapper;
import com.iecube.community.util.LogParsing.LogParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PSTDeviceLogServiceImpl implements PSTDeviceLogService {
    @Autowired
    private PSTDeviceLogMapper pstDeviceLogMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ResourceMapper resourceMapper;

    @Value("${resource-location}/file")
    private String files;


    @Override
    public PSTDeviceLog uploadPSTDeviceLog(Integer pstId, Resource resource) {
        PSTDeviceLog pstDeviceLog = new PSTDeviceLog();
        pstDeviceLog.setPstId(pstId);
        pstDeviceLog.setResourceId(resource.getId());
        Integer row = pstDeviceLogMapper.addDeviceLog(pstDeviceLog);
        if(row != 1){
            throw new InsertException("添加数据异常");
        }
        List<Resource> PSTResources = this.getPSTDeviceLogsByPstId(pstId);
        List<String> dataList = new ArrayList<>();
        List startAndEndTimes = new ArrayList<>();
        Pattern timePattern = Pattern.compile("\\[(.*?)\\]");
        for(Resource resource1 : PSTResources ){
            List<String> list = LogParser.parseLog(new File(files, resource1.getFilename()).getAbsolutePath());
            //取第一个和最后一个作为开始时间和结束时间
            List<String> startAndEndTime = new ArrayList<>();
            String startTime = "";
            String endTime = "";
            Matcher mStartTime = timePattern.matcher(list.get(0));
            Matcher mEndTime = timePattern.matcher(list.get(list.size()-1));
            if(mStartTime.find()){
                startTime=mStartTime.group(1);
            }
            if(mEndTime.find()){
                endTime=mEndTime.group(1);
            }
            startAndEndTime.add(startTime);
            startAndEndTime.add(endTime);
            startAndEndTimes.add(startAndEndTime);
            dataList.addAll(list);
        }
        List<String> parseLogResult = LogParser.parse(dataList); // 第一个元素为类别 第二个元素为数据
        String operationsJsonResult = LogParser.operationsTime(dataList);
        Gson gson = new Gson();
        String startAndEndTimesJsonResult = gson.toJson(startAndEndTimes);
        PSTDeviceLogParseDto newPStDeviceLogParseDto = new PSTDeviceLogParseDto();
        newPStDeviceLogParseDto.setCategories(parseLogResult.get(0));
        newPStDeviceLogParseDto.setData(parseLogResult.get(1));
        newPStDeviceLogParseDto.setPstId(pstId);
        newPStDeviceLogParseDto.setTimes(startAndEndTimesJsonResult);
        newPStDeviceLogParseDto.setOperations(operationsJsonResult);
        PSTDeviceLogParseDto oldPSTDeviceLogParseDto = pstDeviceLogMapper.getPSTLogParseByPstId(pstId);
        int co=0;
        if(oldPSTDeviceLogParseDto == null){
            co = pstDeviceLogMapper.insertPSTLogParse(newPStDeviceLogParseDto);
        }else {
            co = pstDeviceLogMapper.updatePSTLogParse(newPStDeviceLogParseDto);
        }
        if(co != 1){
            throw new InsertException("添加数据异常");
        }
        return pstDeviceLog;
    }

    @Override
    public List<Resource> getPSTDeviceLogsByPstId(Integer pstId) {
        List<PSTDeviceLog> pstDeviceLogList = pstDeviceLogMapper.getPSTDeviceLogs(pstId);
        List<Resource> resourceList = new ArrayList<>();
        for(PSTDeviceLog pstDeviceLog : pstDeviceLogList){
            Resource resource = resourceMapper.getById(pstDeviceLog.getResourceId());
            resourceList.add(resource);
        }
        return resourceList;
    }

    @Override
    public TaskInfo getTaskDetailByPSTId(Integer pstId) {
        TaskInfo taskInfo=pstDeviceLogMapper.getTaskInfoByPSTId(pstId);
        return taskInfo;
    }

    @Override
    public PSTDeviceLogParseDto getLogVisualization(Integer pstId) {
        PSTDeviceLogParseDto oldPSTDeviceLogParseDto = pstDeviceLogMapper.getPSTLogParseByPstId(pstId);
        return oldPSTDeviceLogParseDto;
    }

    @Override
    public List<StudentTasksOperations> getProjectLogCompare(Integer projectId) {
        List<ProjectStudentVo> projectStudentVoList = projectService.projectStudentAndStudentTasks(projectId);
        List<StudentLogOverview> StudentLogOverviewList = pstDeviceLogMapper.getProjectLogCompare(projectId);

        List<StudentTasksOperations> studentTasksOperationsList = new ArrayList<>();
        for(ProjectStudentVo projectStudentVo : projectStudentVoList){
            StudentTasksOperations studentTasksOperations = new StudentTasksOperations();
            studentTasksOperations.setId(projectStudentVo.getId());
            studentTasksOperations.setStudentId(projectStudentVo.getStudentId());
            studentTasksOperations.setStudentName(projectStudentVo.getStudentName());
            List<PSTOperations> pstOperationsList = new ArrayList<>();
            for(StudentTaskVo studentTaskVo : projectStudentVo.getStudentTasks()){
                PSTOperations pstOperations = new PSTOperations();
                pstOperations.setTaskNum(studentTaskVo.getTaskNum());
                pstOperations.setPstId(studentTaskVo.getPSTId());
                for(StudentLogOverview studentLogOverview :StudentLogOverviewList){
                    if(studentLogOverview.getPstId().equals(studentTaskVo.getPSTId())){
                        pstOperations.setTimes(studentLogOverview.getTimes());
                        pstOperations.setOperations(studentLogOverview.getOperations());
                    }
                }
                pstOperationsList.add(pstOperations);
            }
            studentTasksOperations.setTasksOperations(pstOperationsList);
            studentTasksOperationsList.add(studentTasksOperations);
        }
        return studentTasksOperationsList;
    }
}
