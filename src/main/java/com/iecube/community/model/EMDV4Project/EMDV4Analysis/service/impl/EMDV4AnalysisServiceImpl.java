package com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.AnalysisType;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgressData;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.mapper.AnalysisProgressMapper;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.AnalysisDataGenService;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.EMDV4AnalysisService;
import com.iecube.community.model.student.mapper.StudentMapper;
import com.iecube.community.util.uuid.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class EMDV4AnalysisServiceImpl implements EMDV4AnalysisService {
    @Autowired
    private AnalysisProgressMapper progressMapper;

    @Autowired
    private AnalysisDataGenService dataGenService;

    @Autowired
    private StudentMapper studentMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AnalysisProgress createGenProgress(Integer projectId){
        AnalysisProgress existProgress = progressMapper.getApLatestByProjectId(projectId);
        if(existProgress != null && !existProgress.getFinished()){
            return existProgress;
        }
        AnalysisProgress progress = new AnalysisProgress();
        progress.setId(UUIDGenerator.generateUUID());
        progress.setProjectId(projectId);
        progress.setCreateTime(new Date());
        progress.setTotalCount(AnalysisType.size());
        progress.setPercent(0);
        progress.setFinished(false);
        progress.setCompletedCount(0);
        progress.setMessage("数据生成任务已创建");
        progressMapper.createAP(progress);
        dataGenService.dataGen(projectId, progress);
        return progress;
    }

    @Override
    public AnalysisProgress createGenProgressTest(Integer projectId){
        AnalysisProgress existProgress = progressMapper.getApLatestByProjectId(projectId);
        if(existProgress != null && !existProgress.getFinished()){
            return existProgress;
        }
        AnalysisProgress progress = new AnalysisProgress();
        progress.setId(UUIDGenerator.generateUUID());
        progress.setProjectId(projectId);
        progress.setCreateTime(new Date());
        progress.setTotalCount(AnalysisType.size());
        progress.setPercent(0);
        progress.setFinished(false);
        progress.setCompletedCount(0);
        progress.setMessage("数据生成任务已创建");
        progressMapper.createAP(progress);
        dataGenService.dataTest(projectId, progress);
        return progress;
    }

    @Override
    public JsonNode getData(Integer projectId, String type) {
        AnalysisProgress progress = progressMapper.getApLatestByProjectId(projectId);
        if(progress == null || !progress.getFinished()){
            throw new ServiceException("数据暂未生成");
        }
        AnalysisType analysisType = AnalysisType.getByValue(type);
        if(analysisType == null){
            throw new ServiceException("不存在的图表类型");
        }
        AnalysisProgressData apd = progressMapper.getAPD(progress.getId(), analysisType.getValue());
        try {
            return objectMapper.readTree(apd.getData());
        } catch (NullPointerException e) {
            log.error("project:{}[{}]返回数据异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("[%s]数据未找到".formatted(analysisType.getDesc()));
        }catch (JsonProcessingException e){
            log.error("project:{}[{}]解析数据异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("[%s]解析数据异常".formatted(analysisType.getDesc()));
        }catch (Exception e){
            log.error("project:{}[{}] 靠北！意外的异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("靠北！[%s]意外的异常".formatted(analysisType.getDesc()));
        }
    }

    @Override
    public JsonNode getTaskData(Integer projectId, String type, Long ptId) {
        AnalysisProgress progress = progressMapper.getApLatestByProjectId(projectId);
        if(progress == null || !progress.getFinished()){
            throw new ServiceException("数据暂未生成");
        }
        AnalysisType analysisType = AnalysisType.getByValue(type);
        if(analysisType == null){
            throw new ServiceException("不存在的图表类型");
        }
        if(!Objects.equals(analysisType.getTerminal(), "task")){
            throw new ServiceException("非匹配路径");
        }
        AnalysisProgressData apd = progressMapper.getAPDWithPtId(progress.getId(), analysisType.getValue(), ptId);
        try {
            return objectMapper.readTree(apd.getData());
        } catch (NullPointerException e) {
            log.error("project:{}[{}]返回数据异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("[%s]数据未找到".formatted(analysisType.getDesc()));
        }catch (JsonProcessingException e){
            log.error("project:{}[{}]解析数据异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("[%s]解析数据异常".formatted(analysisType.getDesc()));
        }catch (Exception e){
            log.error("project:{}[{}] 靠北！意外的异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("靠北！[%s]意外的异常".formatted(analysisType.getDesc()));
        }
    }

    @Override
    public JsonNode getStuData(Integer projectId, String type, String studentId) {
        AnalysisProgress progress = progressMapper.getApLatestByProjectId(projectId);
        if(progress == null || !progress.getFinished()){
            throw new ServiceException("数据暂未生成");
        }
        AnalysisType analysisType = AnalysisType.getByValue(type);
        if(analysisType == null){
            throw new ServiceException("不存在的图表类型");
        }
        if(!Objects.equals(analysisType.getTerminal(), "student")){
            throw new ServiceException("非匹配路径");
        }
        AnalysisProgressData apd = progressMapper.getAPDWithStudentId(progress.getId(), analysisType.getValue(), studentId);
        try {
            return objectMapper.readTree(apd.getData());
        } catch (NullPointerException e) {
            log.error("project:{}[{}]返回数据异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("[%s]数据未找到".formatted(analysisType.getDesc()));
        }catch (JsonProcessingException e){
            log.error("project:{}[{}]解析数据异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("[%s]解析数据异常".formatted(analysisType.getDesc()));
        }catch (Exception e){
            log.error("project:{}[{}] 靠北！意外的异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("靠北！[%s]意外的异常".formatted(analysisType.getDesc()));
        }
    }

    @Override
    public JsonNode getPSTData(Integer projectId, String type, Long ptId, Long psId) {
        AnalysisProgress progress = progressMapper.getApLatestByProjectId(projectId);
        if(progress == null || !progress.getFinished()){
            throw new ServiceException("数据暂未生成");
        }
        AnalysisType analysisType = AnalysisType.getByValue(type);
        if(analysisType == null){
            throw new ServiceException("不存在的图表类型");
        }
        if(!Objects.equals(analysisType.getTerminal(), "pst")){
            throw new ServiceException("非匹配路径");
        }
        AnalysisProgressData apd = progressMapper.getAPDWithPtIdAndPsId(progress.getId(), analysisType.getValue(), ptId, psId);
        try {
            return objectMapper.readTree(apd.getData());
        } catch (NullPointerException e) {
            log.error("project:{}[{}]返回数据异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("[%s]数据未找到".formatted(analysisType.getDesc()));
        }catch (JsonProcessingException e){
            log.error("project:{}[{}]解析数据异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("[%s]解析数据异常".formatted(analysisType.getDesc()));
        }catch (Exception e){
            log.error("project:{}[{}] 靠北！意外的异常", projectId, analysisType.getDesc(), e);
            throw new ServiceException("靠北！[%s]意外的异常".formatted(analysisType.getDesc()));
        }
    }


}
