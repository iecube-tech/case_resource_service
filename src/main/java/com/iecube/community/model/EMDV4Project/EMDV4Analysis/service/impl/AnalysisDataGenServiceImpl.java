package com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.AnalysisType;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.CompTargetTagDto;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.PSTAIDto;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.PSTDto;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgressData;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.exception.AnalysisProgressGenChildDataException;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.mapper.AnalysisProgressMapper;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.mapper.DataSourceMapper;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.AnalysisDataGenService;
import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.util.uuid.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalysisDataGenServiceImpl implements AnalysisDataGenService {
    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private AnalysisProgressMapper progressMapper;


    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<PSTDto> PSTWithStageList = new CopyOnWriteArrayList<>(); // project下的所有pst及课前课后状态 区分stage
    private final ConcurrentHashMap<Long, PSTDto> PSTDtoWithoutStage= new ConcurrentHashMap<>();  // <pstId, PSTDto>
    private final ConcurrentHashMap<Long,List<PSTDto>> PSTGroupByPstId = new ConcurrentHashMap<>(); // pst 根据pstId分组聚合 不区分stage <pstId, List<PSTDto>>
    private final ConcurrentHashMap<String,List<PSTDto>> PSTGroupByStu = new ConcurrentHashMap<>();  // pst 根据学生分组聚合 不区分stage <studentId, List<PSTDto>>
    private final ConcurrentHashMap<String,List<PSTDto>> PSTGroupByStuWithStage = new ConcurrentHashMap<>();  // pst 根据学生分组聚合 不区分stage <studentId, List<PSTDto>>
    private final ConcurrentHashMap<Long,List<PSTDto>> PSTGroupByTask = new ConcurrentHashMap<>();  // pst 根据实验分组聚合 不区分stage <ptId, List<PSTDto>>
    private final ConcurrentHashMap<Long,List<PSTDto>> PSTGroupByTaskWithStage = new ConcurrentHashMap<>();  // pst 根据实验分组聚合 不区分stage <ptId, List<PSTDto>>

    private final List<PSTAIDto> PSTAIDtoList = new CopyOnWriteArrayList<>();  // project 下的所有 ai对话信息
    private final ConcurrentHashMap<String, List<PSTAIDto>> PSTAIGroupByChatId = new ConcurrentHashMap<>(); // <chatId List<PSTAIDto>>
    private final ConcurrentHashMap<String, List<PSTAIDto>> PSTAIGroupByStu = new ConcurrentHashMap<>();  // <studentId List<PSTAIDto>>

    private final List<CompTargetTagDto> ComponentList = new CopyOnWriteArrayList<>();

    private final List<CompTargetTagDto> CompTargetTagDtoList = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<Integer, List<CompTargetTagDto>> CompTargetTagGroupByTarget = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<CompTargetTagDto>> CompTargetTagGroupByStu = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<CompTargetTagDto>> CompTargetTagGroupByPT = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, String> targetName = new ConcurrentHashMap<>();

    @Async
    @Override
    public void dataGen(Integer projectId, AnalysisProgress progress){
        this.dataClean(projectId); //数据清洗

        for(int i=0;i<Arrays.stream(AnalysisType.values()).toList().size();i++){
            AnalysisType type = AnalysisType.values()[i];
            try{
                this.genChildData(projectId, progress.getId(), type);
                log.info("[{}] 数据已生成", type.getDesc());
                updateProgress(progress.getId(), i+1,i+1==Arrays.stream(AnalysisType.values()).toList().size(), "[%s] 数据已生成".formatted(i+1));
            }catch (AnalysisProgressGenChildDataException e){
                log.error("[{}] 数据分析失败",type.getDesc() ,e);
                updateProgress(progress.getId(), i+1,true, "[%s] 数据生成失败，%s".formatted(i+1, e.getMessage()));
            }
        }
        this.PSTWithStageList.clear();
    }

    private void dataClean(Integer projectId){
        log.info("正在清洗数据");
        this.PSTWithStageList.addAll(dataSourceMapper.getProjectPSTWithStage(projectId));
        this.PSTAIDtoList.addAll(dataSourceMapper.getProjectPSTAIDTO(projectId));
        this.ComponentList.addAll(dataSourceMapper.getCompTargetTagDtoByProject(projectId));
        this.CompTargetTagDtoList.addAll(ComponentList.stream().filter(comp->comp.getTagId()!=null).toList());

        // 对PST 分组聚合
        PSTGroupByTaskWithStage.putAll(PSTWithStageList.stream().collect(Collectors.groupingBy(PSTDto::getPtId)));
        PSTGroupByStuWithStage.putAll(PSTWithStageList.stream().collect(Collectors.groupingBy(PSTDto::getStudentId)));
        PSTDtoWithoutStage.putAll(PSTWithStageList.stream().collect(Collectors.toMap(PSTDto::getPstId, p -> p, (p1,p2)->p1)));
        PSTGroupByPstId.putAll(PSTDtoWithoutStage.values().stream().collect(Collectors.groupingBy(PSTDto::getPstId)));
        PSTGroupByStu.putAll(PSTDtoWithoutStage.values().stream().collect(Collectors.groupingBy(PSTDto::getStudentId)));
        PSTGroupByTask.putAll(PSTDtoWithoutStage.values().stream().collect(Collectors.groupingBy(PSTDto::getPtId)));

        // 对PSTAi 分组聚合
        PSTAIGroupByChatId.putAll(PSTAIDtoList.stream().collect(Collectors.groupingBy(PSTAIDto::getChatId)));
        PSTAIGroupByStu.putAll(PSTAIDtoList.stream().collect(Collectors.groupingBy(PSTAIDto::getStudentId)));

        // 对CompTargetTag 分组聚合
        CompTargetTagGroupByTarget.putAll(CompTargetTagDtoList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTargetId)));
        CompTargetTagGroupByStu.putAll(CompTargetTagDtoList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getStudentId)));
        CompTargetTagGroupByPT.putAll(CompTargetTagDtoList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getPtId)));

        targetName.putAll(CompTargetTagDtoList.stream().collect(Collectors.toMap(CompTargetTagDto::getTargetId, CompTargetTagDto::getTargetName, (v1,v2)->v1)));
        log.info("数据清洗完成");
    }

    private void T_OVERVIEW_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException{
        ObjectNode jsonObject = objectMapper.createObjectNode();

        AtomicReference<Double> courseGrade = new AtomicReference<>(0.0);
        PSTDtoWithoutStage.values().forEach(pst->{
            courseGrade.updateAndGet(v -> v + pst.getPsScore());
        });
        Double avgGrade = numWith2Decimal(courseGrade.get()/PSTDtoWithoutStage.values().size()) ;  // 课程平均成绩
        Integer stuNum = PSTGroupByStu.size();  // 学生人数
        Integer aiUsedNum = PSTAIDtoList.size()/2; // ai使用数 一问一答为一次
        AtomicReference<Integer> doneNum = new AtomicReference<>(0);  // 实验完成数， 提交率大于等于95%视为已完成
        PSTGroupByTask.values().forEach(taskPSTList->{
            int doneSize = taskPSTList.stream().filter(Objects::nonNull).filter(pst->pst.getStatus().equals(1)).toList().size();
            if(doneSize*100/taskPSTList.size()>=95){
                doneNum.getAndSet(doneNum.get() + 1);
            }
        });
        ObjectNode rateOfCourse = objectMapper.createObjectNode();
        rateOfCourse.put("done", doneNum.get());
        rateOfCourse.put("total", PSTGroupByTask.size());

        jsonObject.put("avgGrade", avgGrade);
        jsonObject.put("stuNum", stuNum);
        jsonObject.put("aiUsedNum", aiUsedNum);
        jsonObject.set("rateOfCourse", rateOfCourse);
        this.saveProgressData(type, progressId, jsonObject);
    }
    private void T_OVERVIEW_RATE(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        if(CompTargetTagDtoList.isEmpty()){
            throw new AnalysisProgressGenChildDataException("没有有效的课程目标数据");
        }
        ObjectNode jsonObject = objectMapper.createObjectNode();

        // 课程目标达成度 = 课程目标相关题目的总得分 / 课程目标相关题目的总分
        double targetScore = CompTargetTagDtoList.stream().filter(Objects::nonNull).mapToDouble(CompTargetTagDto::getCompScore).sum();
        double targetTotalScore = CompTargetTagDtoList.stream().filter(Objects::nonNull).mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
        double rageOfTarget = numWith2Decimal(targetScore*100/targetTotalScore);

        // 知识点掌握率 = 已做的所有题目 / 题目总数
        int doneCompNum = CompTargetTagDtoList.stream().filter(Objects::nonNull).filter(c -> c.getCompStatus().equals(1)).toList().size();
        double rageOfKnowledgePoints = numWith2Decimal((double) (doneCompNum * 100) / CompTargetTagDtoList.size());

        // 实验完成率 = 学生完成的实验个数除 / 学生实验总个数
        int donePSTNum = PSTDtoWithoutStage.values().stream().filter(Objects::nonNull).filter(pstDto -> pstDto.getStatus().equals(1)).toList().size();
        double rageOfPSTDone = numWith2Decimal((double) (donePSTNum * 100) / PSTDtoWithoutStage.size());

        // 课程整体进度 = 已完成的实验个数（单个实验完成 >95%）/ 总实验个数
        AtomicReference<Integer> doneTaskNum = new AtomicReference<>(0);  // 实验完成数， 提交率大于等于95%视为已完成
        PSTGroupByTask.values().forEach(taskPSTList->{
            int doneSize = taskPSTList.stream().filter(Objects::nonNull).filter(pst->pst.getStatus().equals(1)).toList().size();
            if(doneSize*100/taskPSTList.size()>=95){
                doneTaskNum.getAndSet(doneTaskNum.get() + 1);
            }
        });
        double rageOfCourse = numWith2Decimal((double) (doneTaskNum.get() * 100) / PSTGroupByTask.size());

        jsonObject.put("rageOfTarget", rageOfTarget);
        jsonObject.put("rageOfKnowledgePoints", rageOfKnowledgePoints);
        jsonObject.put("rageOfPSTDone", rageOfPSTDone);
        jsonObject.put("rageOfCourse", rageOfCourse);
        this.saveProgressData(type, progressId, jsonObject);
    }
    private void T_OVERVIEW_GA(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        if(CompTargetTagGroupByTarget.isEmpty()){
            throw new AnalysisProgressGenChildDataException("没有有效的课程目标数据");
        }

        ArrayNode targetNodeList = objectMapper.createArrayNode();
        // 目标达成分析  根据 课程目标分组聚合 分别计算达成度
        // 计算各个学生的各目标达成度 Map<studentId, List[Map<课程目标， 达成度>]> // map<课程目标id， List[Map<studentId, 达成度>]>

        Map<Integer, List<Map<String, Double>>> stuRageOfTarget = new HashMap<>(); // map<课程目标id， List[Map<studentId, 达成度>]>
        CompTargetTagGroupByTarget.forEach((targetId, compList) -> {
            Map<String, List<CompTargetTagDto>> thisTargetCompGroupByStu =
                    new HashMap<>(compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getStudentId)));
            List<Map<String, Double>> targetStuRageList = new ArrayList<>();
            thisTargetCompGroupByStu.forEach((studentId, stuCompList)->{
                double score = stuCompList.stream().mapToDouble(CompTargetTagDto::getCompScore).sum();
                double totalScore = stuCompList.stream().mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
                double rage = numWith2Decimal(score*100/totalScore);
                Map<String, Double> targetStuRage = new HashMap<>();
                targetStuRage.put(studentId, rage);
                targetStuRageList.add(targetStuRage);
            });
            stuRageOfTarget.put(targetId, targetStuRageList);
        });
        stuRageOfTarget.forEach((targetId, mapList)->{
            List<Double> targetStuRageList = mapList.stream().flatMap(map->map.values().stream()).toList();
            Double max = targetStuRageList.stream().filter(Objects::nonNull).max(Double::compare).orElse(null);
            Double min = targetStuRageList.stream().filter(Objects::nonNull).min(Double::compare).orElse(null);
            OptionalDouble avg = targetStuRageList.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).average();

            ObjectNode jsonObject = objectMapper.createObjectNode();
            jsonObject.put("targetId", targetId);
            jsonObject.put("targetName", targetName.get(targetId));
            jsonObject.put("avgRage", avg.isPresent() ? numWith2Decimal(avg.getAsDouble()) : null);
            jsonObject.put("maxRage", max);
            jsonObject.put("minRage", min);
            targetNodeList.add(jsonObject);
        });
        this.saveProgressData(type, progressId, targetNodeList);
    }
    private void T_OVERVIEW_DOCG(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode jsonObject = objectMapper.createArrayNode();
        Map<String, Integer> scoreMap = new HashMap<>();
        scoreMap.put("<60", 0);
        scoreMap.put("60-70", 0);
        scoreMap.put("70-80", 0);
        scoreMap.put("80-90", 0);
        scoreMap.put("90-100", 0);
        PSTGroupByStu.values().forEach(pstList->{
            if(pstList.get(0).getPsScore()<60){
                scoreMap.compute("<60",(k,v)-> v==null?1:v+1);
            } else if (pstList.get(0).getPsScore()>=60 && pstList.get(0).getPsScore()<70) {
                scoreMap.compute("60-70",(k,v)->v==null?1:v+1);
            } else if (pstList.get(0).getPsScore()>=70 && pstList.get(0).getPsScore()<80) {
                scoreMap.compute("70-80",(k,v)->v==null?1:v+1);
            } else if (pstList.get(0).getPsScore()>=80 && pstList.get(0).getPsScore()<90) {
                scoreMap.compute("80-90",(k,v)->v==null?1:v+1);
            } else if (pstList.get(0).getPsScore()>=90 && pstList.get(0).getPsScore()<=100) {
                scoreMap.compute("90-100",(k,v)->v==null?1:v+1);
            }
        });
        scoreMap.forEach((k,v)->{
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put(k,v);
            jsonObject.add(jsonNode);
        });
        this.saveProgressData(type, progressId, jsonObject);
    }
    private void T_OVERVIEW_ES(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode jsonObject = objectMapper.createArrayNode();
        PSTGroupByTask.forEach((k, pstList)->{
            OptionalDouble max = pstList.stream().mapToDouble(PSTDto::getTaskScore).max();
            OptionalDouble min = pstList.stream().mapToDouble(PSTDto::getTaskScore).min();
            OptionalDouble avg = pstList.stream().mapToDouble(PSTDto::getTaskScore).average();
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("projectTaskId", k);
            jsonNode.put("projectTaskName", pstList.get(0).getTaskName());
            jsonNode.put("maxScore", max.isPresent() ? numWith2Decimal(max.getAsDouble()) : 0);
            jsonNode.put("minScore", min.isPresent() ? numWith2Decimal(min.getAsDouble()) : 0);
            jsonNode.put("avgScore", avg.isPresent() ? numWith2Decimal(avg.getAsDouble()) : 0);
            jsonObject.add(jsonNode);
        });
        this.saveProgressData(type, progressId, jsonObject);
    }
    private void T_OVERVIEW_CWLS(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        OptionalDouble avg = PSTDtoWithoutStage.values().stream().mapToDouble(PSTDto::getPsScore).average();
        ArrayNode jsonNode = objectMapper.createArrayNode();
        ObjectNode avgNode = objectMapper.createObjectNode();
        avgNode.put("value", avg.isPresent() ? numWith2Decimal(avg.getAsDouble()) : null);
        avgNode.put("label", "平均分");

        long passNum = PSTGroupByStu.values().stream().filter(Objects::nonNull)
                        .filter(pstDtoList -> pstDtoList.get(0).getPsScore()!=null)
                        .filter(pstDtoList -> pstDtoList.get(0).getPsScore()>=60)
                        .count();
        Double rageOfPass = numWith2Decimal((double) (passNum * 100) / PSTGroupByStu.size());
        ObjectNode passNode = objectMapper.createObjectNode();
        passNode.put("value", rageOfPass);
        passNode.put("label", "通过率");

        // ai 使用率 = 使用ai交互的 pst 数量 / 总pst数量
        Double rageOfAiUsed = numWith2Decimal((double) (PSTAIGroupByChatId.size() * 100) / PSTGroupByPstId.size());
        ObjectNode aiUsedNode = objectMapper.createObjectNode();
        aiUsedNode.put("value", rageOfAiUsed);
        aiUsedNode.put("label", "Ai使用率");

        // 课程目标达成度 = 课程目标相关题目的总得分 / 课程目标相关题目的总分
        double targetScore = CompTargetTagDtoList.stream().filter(Objects::nonNull).mapToDouble(CompTargetTagDto::getCompScore).sum();
        double targetTotalScore = CompTargetTagDtoList.stream().filter(Objects::nonNull).mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
        double rageOfTarget = numWith2Decimal(targetScore*100/targetTotalScore);
        ObjectNode targetNode = objectMapper.createObjectNode();
        targetNode.put("value", rageOfTarget);
        targetNode.put("label", "课程目标达成度");

        jsonNode.add(avgNode);
        jsonNode.add(passNode);
        jsonNode.add(aiUsedNode);
        jsonNode.add(targetNode);
        jsonObject.set("semester", jsonNode);
        jsonObject.set("lastSemester", objectMapper.createArrayNode());
        this.saveProgressData(type, progressId, jsonObject);
    }
    private void T_OVERVIEW_AI_VIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        // 学生使用率 = 使用ai的学生数 / 学生总数
        AtomicInteger stuNumOfAiUsed = new AtomicInteger();
        PSTGroupByStu.forEach((k,v)->{
            if(PSTAIGroupByStu.containsKey(k)){
                stuNumOfAiUsed.getAndIncrement();
            }
        });
        double rageOfUsed = numWith2Decimal((double) (stuNumOfAiUsed.get() * 100) /PSTGroupByStu.size());

        // 互动频次 = 互动总次数 / 学生总数
        int aiUsedNum = PSTAIDtoList.size()/2; // ai使用数 一问一答为一次
        double frequency = numWith2Decimal((double) aiUsedNum /PSTGroupByStu.size());


        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("rageOfUsed", rageOfUsed);
        dataNode.put("frequency", frequency);
        dataNode.put("totalUsed", aiUsedNum);

        jsonObject.set("data", dataNode);
        jsonObject.set("thematic", objectMapper.createObjectNode());
        this.saveProgressData(type, progressId, jsonObject);
    }
    private void T_EA_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode jsonNode = objectMapper.createArrayNode();
        PSTGroupByTask.forEach((key,pstList)->{
            HashMap<Integer, CompTargetTagDto> taskTagMap = CompTargetTagGroupByPT.get(key).stream()  // 收集 task 中的 tag 列表， tagId 取唯一 <tagId, CompTargetTagDto>
                    .collect(
                            Collectors.toMap(
                                    CompTargetTagDto::getTagId,
                                    com->com,
                                    (com1,com2)->com1,
                                    LinkedHashMap::new)
                    );
            PSTDto lastDonePst = pstList.stream().filter(pstDto -> pstDto.getEndTime()!=null).max(Comparator.comparing(PSTDto::getEndTime)).orElse(null);
            List<PSTDto> statusHasDoneList = pstList.stream().filter(Objects::nonNull).filter(pstdto->pstdto.getStatus().equals(1)).toList();
            String taskName = pstList.get(0).getTaskName();
            String taskIcon = pstList.get(0).getBookIcon();
            double rageOfDone = numWith2Decimal((double) (statusHasDoneList.size() * 100) /pstList.size());
            int taskStatus = rageOfDone>=95?2:1;
            taskStatus = rageOfDone==0?0:taskStatus;
            Date doneTime = lastDonePst==null?null:lastDonePst.getEndTime();
            OptionalDouble avgScore = pstList.stream().mapToDouble(PSTDto::getTaskScore).average();

            ObjectNode overviewNode = objectMapper.createObjectNode();
            overviewNode.put("ptId",key);
            overviewNode.put("ptName", taskName);
            overviewNode.put("ptIcon", taskIcon);
            overviewNode.put("status", taskStatus);
            overviewNode.put("rageOfDone", rageOfDone);
            if (doneTime != null) {
                overviewNode.put("doneTime", doneTime.toString());
            }else {
                overviewNode.put("doneTime", "");
            }
            overviewNode.put("avgScore", avgScore.isPresent() ? numWith2Decimal(avgScore.getAsDouble()) : 0);
            ArrayNode arrayNode = objectMapper.createArrayNode();
            taskTagMap.values().forEach(com->{
                if(com.isKeyNode()){
                    arrayNode.add(com.getTagName());
                }
            });
            overviewNode.set("keyTag", arrayNode);
            jsonNode.add(overviewNode);
        });

        this.saveProgressData(type, progressId, jsonNode);
    }
    private void T_EA_ED(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode jsonNode = objectMapper.createArrayNode();
        PSTGroupByTask.forEach((key, pstList)->{
            ObjectNode objectNode = objectMapper.createObjectNode();

            String taskName = pstList.get(0).getTaskName();
            // 实验概览  完成人数  平均得分  平均用时 平均错误率
            ObjectNode overviewNode = objectMapper.createObjectNode();
            // 完成人数
            List<PSTDto> statusHasDoneList = pstList.stream().filter(Objects::nonNull).filter(pstdto->pstdto.getStatus().equals(1)).toList();
            int stuNumOfDone = statusHasDoneList.size();
            int stuNumOfTotal = pstList.size();
            // 平均得分
            OptionalDouble avgScore = pstList.stream().mapToDouble(PSTDto::getTaskScore).average();
            // 实验总分
            double totalScore = pstList.get(0).getTaskTotalScore();
            // 平均用时
            List<PSTDto> ptStuPSTList = PSTGroupByTaskWithStage.get(key)
                    .stream()
                    .filter(Objects::nonNull)
                    // 过滤无效数据：startTime/endTime 不能为 null，且 endTime 不能早于 startTime
                    .filter(pstDto -> pstDto.getStartTime()!=null && pstDto.getEndTime()!=null && pstDto.getEndTime().after(pstDto.getStartTime()))
                    .toList();  // 保证有效值
                // 上述的列表中去除了空值，因此分子是会偏小，为确保结果更接近准确值， 要在分母中也去除空值的对象，由于ptStuPSTList中pst携带了stage的细分，所以分母表示的有效总人数就应该是 ptStuPSTList / stage种类
            Map<Integer, List<PSTDto>> groupByStage = ptStuPSTList.stream().collect(Collectors.groupingBy(PSTDto::getStage));
            double totalNum = Math.floor(numWith2Decimal((double) ptStuPSTList.size() /groupByStage.size()));
                // 总用时
            long totalMillis = ptStuPSTList.stream()
                    // 计算单个对象的时间差（毫秒）：endTime.getTime() - startTime.getTime()
                    .mapToLong(dto -> dto.getEndTime().getTime() - dto.getStartTime().getTime())
                    // 累加总和
                    .sum();
            long avgMillis = (long) Math.ceil(totalMillis/totalNum);  // 平均用时

            // 平均错误率
            List<CompTargetTagDto> ptStuComp = CompTargetTagGroupByPT.get(key);
            // 做错误的题目总数 == 成绩不为满分的题目
            List<CompTargetTagDto> errorPtStuComp = ptStuComp
                    .stream()
                    .filter(comp-> comp.getCompScore() < comp.getCompTotalScore())
                    .toList();
            double rageOfError = numWith2Decimal((double) errorPtStuComp.size()/pstList.size());
            overviewNode.put("stuNumOfDone", stuNumOfDone);
            overviewNode.put("stuNumOfTotal", stuNumOfTotal);
            overviewNode.put("avgScore", avgScore.isPresent() ? numWith2Decimal(avgScore.getAsDouble()) : 0);
            overviewNode.put("totalScore", totalScore);
            overviewNode.put("avgMillis", avgMillis);
            overviewNode.put("rageOfError", rageOfError);

            // 成绩分布 distributionOfGrade
            ArrayNode distributionNode = objectMapper.createArrayNode();
            Map<String, Integer> scoreMap = new HashMap<>();
            scoreMap.put("<60", 0);
            scoreMap.put("60-70", 0);
            scoreMap.put("70-80", 0);
            scoreMap.put("80-90", 0);
            scoreMap.put("90-100", 0);
            pstList.forEach(pst->{
                if(pst.getTaskScore()<60){
                    scoreMap.compute("<60",(k,v)-> v==null?1:v+1);
                } else if (pst.getTaskScore()>=60 && pst.getTaskScore()<70) {
                    scoreMap.compute("60-70",(k,v)->v==null?1:v+1);
                } else if (pst.getTaskScore()>=70 && pst.getTaskScore()<80) {
                    scoreMap.compute("70-80",(k,v)->v==null?1:v+1);
                } else if (pst.getTaskScore()>=80 && pst.getTaskScore()<90) {
                    scoreMap.compute("80-90",(k,v)->v==null?1:v+1);
                } else if (pst.getTaskScore()>=90 && pst.getTaskScore()<=100) {
                    scoreMap.compute("90-100",(k,v)->v==null?1:v+1);
                }
            });
            scoreMap.forEach((k,v)->{
                ObjectNode item = objectMapper.createObjectNode();
                item.put(k,v);
                distributionNode.add(item);
            });

            // 能力评价 ability
            ArrayNode abilityNode = objectMapper.createArrayNode();
            Map<Integer, List<CompTargetTagDto>> ptTagMap = ptStuComp
                    .stream()
                    .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
            ptTagMap.forEach( (tagId,compList)->{
                ObjectNode item = objectMapper.createObjectNode();
                int rightSize = compList
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(comp-> comp.getCompScore()!=null && comp.getCompTotalScore()!=null)
                        .filter(comp-> Objects.equals(comp.getCompScore(), comp.getCompTotalScore()))
                        .toList()
                        .size();
                item.put("tagId", tagId);
                item.put("tagName", compList.get(0).getTagName());
                item.put("value", numWith2Decimal((double) (rightSize * 100) /compList.size()));
                abilityNode.add(item);
            });

            objectNode.put("ptId", key);
            objectNode.put("ptName", taskName);
            objectNode.set("overview", overviewNode);
            objectNode.set("distributionOfGrade", distributionNode);
            objectNode.set("ability", abilityNode);
            jsonNode.add(objectNode);
        });
        this.saveProgressData(type, progressId, jsonNode);
    }
    private void T_EA_ECA(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        // 实验难度对比
        ArrayNode difficultyNode= objectMapper.createArrayNode();
        //TODO

        // 实验成绩对比
        ArrayNode gradeNode = objectMapper.createArrayNode();
        PSTGroupByTask.forEach((ptId, pstList)->{
            String taskName = pstList.get(0).getTaskName();
            double totalScore =  pstList.get(0).getTaskTotalScore();
            OptionalDouble avg = pstList.stream().mapToDouble(PSTDto::getTaskScore).average();
            ObjectNode item = objectMapper.createObjectNode();
            item.put("ptName", taskName);
            item.put("ptId", ptId);
            item.put("avgScore", avg.isPresent() ? numWith2Decimal(avg.getAsDouble()) : 0);
            item.put("totalScore", totalScore);
            gradeNode.add(item);
        });

        jsonNode.set("difficulty", difficultyNode);
        jsonNode.set("grade", gradeNode);
        this.saveProgressData(type, progressId, jsonNode);
    }
    private void T_STU_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        PSTGroupByStuWithStage.forEach((studentId, pstDtoList)->{
            ObjectNode jsonNode = objectMapper.createObjectNode();
            // 学生信息
            Long psId = pstDtoList.get(0).getPsId();
            String stuName = pstDtoList.get(0).getStudentName();
            // 课程进度
            int numOfDone =  pstDtoList.stream().filter(Objects::nonNull).filter(pst->pst.getStatus().equals(1)).toList().size();
            double rageOfCourse = numWith2Decimal((double) numOfDone*100/pstDtoList.size());
            //实验完成情况
            Map<Long, List<PSTDto>> stuTaskMap = pstDtoList.stream().collect(Collectors.groupingBy(PSTDto::getPtId));
            int numOfTotalTask = stuTaskMap.size();
            AtomicInteger numOfDoneTask = new AtomicInteger();
            stuTaskMap.values().forEach(tList->{
                // 学生没有完成的步骤
                int thisTaskStageNotDoneSize = tList.stream().filter(Objects::nonNull).filter(pst->pst.getStatus()==0).toList().size();
                if(thisTaskStageNotDoneSize==0){
                    numOfDoneTask.getAndIncrement();
                }
            });

            // 平均分
            OptionalDouble avg = pstDtoList.stream().mapToDouble(PSTDto::getTaskScore).average();

            // 实验根据成绩排序
            ArrayNode orderNode = objectMapper.createArrayNode();
            List<PSTDto> stuTaskOrderByScore = new ArrayList<>();
            stuTaskMap.forEach((ptId, pstList)->{
                stuTaskOrderByScore.add(pstList.get(0));
            });
            stuTaskOrderByScore.sort((pst1, pst2) -> {
                double score1 = pst1.getTaskScore() == null ? 0 : pst1.getTaskScore();
                double score2 = pst2.getTaskScore() == null ? 0 : pst2.getTaskScore();
                return Double.compare(score1, score2);
            });
            stuTaskOrderByScore.forEach(pst->{
                ObjectNode item = objectMapper.createObjectNode();
                item.put("ptId", pst.getPtId());
                item.put("ptName", pst.getTaskName());
                item.put("score", pst.getTaskScore());
                orderNode.add(item);
            });
            jsonNode.put("psId", psId);
            jsonNode.put("studentName", stuName);
            jsonNode.put("studentId", studentId);
            jsonNode.put("rageOfCourse", rageOfCourse);
            jsonNode.put("numOfTotalTask", numOfTotalTask);
            jsonNode.put("numOfDoneTask", numOfDoneTask.get());
            jsonNode.put("avgScore", avg.isPresent()?numWith2Decimal(avg.getAsDouble()):0);
            jsonNode.set("orderByScore", orderNode);
            arrayNode.add(jsonNode);
        });
        this.saveProgressData(type, progressId, arrayNode);
    }
    private void T_STU_BEHAVIOUR(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        PSTGroupByTaskWithStage.forEach((ptId, pstList)->{
            ObjectNode jsonNode = objectMapper.createObjectNode();
            String ptName = pstList.get(0).getTaskName();
            // 课前预习时间分布 与成绩关系
            List<PSTDto> taskStage0List = pstList.stream().filter(Objects::nonNull).filter(pst->pst.getStage()==0).toList();
            List<Long> stage1TimeList = new ArrayList<>();
            ArrayNode btgNode = objectMapper.createArrayNode();
            taskStage0List.forEach(pst->{
                ObjectNode stu = objectMapper.createObjectNode();
                stu.put("studentId", pst.getStudentId());
                stu.put("studentName", pst.getStudentName());
                ArrayNode tg = objectMapper.createArrayNode();
                if(pst.getEndTime()!=null && pst.getStartTime()!=null && pst.getEndTime().after(pst.getStartTime())){
                    stage1TimeList.add(pst.getEndTime().getTime()-pst.getStartTime().getTime());
                    tg.add(pst.getEndTime().getTime()-pst.getStartTime().getTime());
                }else {
                    stage1TimeList.add(0L);
                    tg.add(0L);
                }
                tg.add(pst.getTaskScore());
                stu.set("data", tg);
                btgNode.add(stu);
            });
            ArrayNode distributionNode = objectMapper.createArrayNode();
            Map<String, Integer> timeMap = new HashMap<>();
            timeMap.put("<0.5小时", 0);
            timeMap.put("0.5-1小时", 0);
            timeMap.put("1-1.5小时", 0);
            timeMap.put("1.5-2小时", 0);
            timeMap.put("2-2.5小时", 0);
            timeMap.put("2.5-3小时", 0);
            timeMap.put(">3小时", 0);
            stage1TimeList.forEach(time->{
                if(time<1800000){
                    timeMap.compute("<0.5小时",(k,v)-> v==null?1:v+1);
                } else if (time>=1800000 && time<3600000) {
                    timeMap.compute("0.5-1小时",(k,v)->v==null?1:v+1);
                } else if (time>=3600000 && time<5400000) {
                    timeMap.compute("1-1.5小时",(k,v)->v==null?1:v+1);
                } else if (time>=5400000 && time<7200000) {
                    timeMap.compute("1.5-2小时",(k,v)->v==null?1:v+1);
                } else if (time>=7200000 && time<9000000) {
                    timeMap.compute("2-2.5小时",(k,v)->v==null?1:v+1);
                } else if (time>=9000000 && time<10800000) {
                    timeMap.compute("2.5-3小时",(k,v)->v==null?1:v+1);
                } else {
                    timeMap.compute(">3小时",(k,v)->v==null?1:v+1);
                }
            });
            timeMap.forEach((k,v)->{
                ObjectNode item = objectMapper.createObjectNode();
                item.put(k,v);
                distributionNode.add(item);
            });

            // ai 使用次数分布  与成绩关系
            ArrayNode aiUsedDistributionNode = objectMapper.createArrayNode();
            ArrayNode ugNode = objectMapper.createArrayNode();
            List<Integer> aiUsedTimesList = new ArrayList<>();
            taskStage0List.forEach(stuPst->{
                ObjectNode stu = objectMapper.createObjectNode();
                stu.put("studentId", stuPst.getStudentId());
                stu.put("studentName", stuPst.getStudentName());
                ArrayNode ug = objectMapper.createArrayNode();
                if (PSTAIGroupByStu.containsKey(stuPst.getStudentId())){
                    // 统计使用了几次
                    aiUsedTimesList.add(PSTAIGroupByStu.get(stuPst.getStudentId()).size()/2);
                    ug.add(PSTAIGroupByStu.get(stuPst.getStudentId()).size()/2);
                }
                else {
                    // 使用了0次
                    aiUsedTimesList.add(0);
                    ug.add(0);
                }
                ug.add(stuPst.getTaskScore());
                stu.set("data", ug);
                ugNode.add(stu);
            });

            Map<String, Integer> aiUsedTimesMap = new HashMap<>();
            aiUsedTimesMap.put("0次",0);
            aiUsedTimesMap.put("1-2次",0);
            aiUsedTimesMap.put("3-4次",0);
            aiUsedTimesMap.put("5-6次",0);
            aiUsedTimesMap.put("7-8次",0);
            aiUsedTimesMap.put("9-10次",0);
            aiUsedTimesMap.put(">10次",0);
            aiUsedTimesList.forEach(t->{
                if(t<=0){
                    aiUsedTimesMap.compute("0次",(k,v)-> v==null?1:v+1);
                }else if (t>=1 && t<=2){
                    aiUsedTimesMap.compute("1-2次",(k,v)-> v==null?1:v+1);
                }else if (t>=3 && t<=4){
                    aiUsedTimesMap.compute("3-4次",(k,v)-> v==null?1:v+1);
                }else if (t>=5 && t<=6){
                    aiUsedTimesMap.compute("5-6次",(k,v)-> v==null?1:v+1);
                }else if (t>=7 && t<=8){
                    aiUsedTimesMap.compute("7-8次",(k,v)-> v==null?1:v+1);
                }else if (t>=9 && t<=10){
                    aiUsedTimesMap.compute("9-10次",(k,v)-> v==null?1:v+1);
                }else {
                    aiUsedTimesMap.compute(">10次",(k,v)-> v==null?1:v+1);
                }
            });
            aiUsedTimesMap.forEach((k,v)->{
                ObjectNode item = objectMapper.createObjectNode();
                item.put(k,v);
                aiUsedDistributionNode.add(item);
            });


            jsonNode.put("ptId", ptId);
            jsonNode.put("ptName", ptName);
            jsonNode.set("timeDistribution", distributionNode);
            jsonNode.set("aiUsedDistribution", aiUsedDistributionNode);
            // 课前预习时间与成绩
            jsonNode.set("btg", btgNode);
            // AI辅助使用与成绩关系
            jsonNode.set("ug", ugNode);
            arrayNode.add(jsonNode);
        });
        this.saveProgressData(type, progressId, arrayNode);
    }
    private void T_CT_OAS(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        if(CompTargetTagGroupByTarget.isEmpty()){
            throw new AnalysisProgressGenChildDataException("没有有效的课程目标数据");
        }

        ArrayNode targetNodeList = objectMapper.createArrayNode();
        // 目标达成分析  根据 课程目标分组聚合 分别计算达成度
        // 计算各个学生的各目标达成度 Map<studentId, List[Map<课程目标， 达成度>]> // map<课程目标id， List[Map<studentId, 达成度>]>

        Map<Integer, List<Map<String, Double>>> stuRageOfTarget = new HashMap<>(); // map<课程目标id， List[Map<studentId, 达成度>]>
        CompTargetTagGroupByTarget.forEach((targetId, compList) -> {
            Map<String, List<CompTargetTagDto>> thisTargetCompGroupByStu =
                    new HashMap<>(compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getStudentId)));
            List<Map<String, Double>> targetStuRageList = new ArrayList<>();
            thisTargetCompGroupByStu.forEach((studentId, stuCompList)->{
                double score = stuCompList.stream().mapToDouble(CompTargetTagDto::getCompScore).sum();
                double totalScore = stuCompList.stream().mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
                double rage = numWith2Decimal(score*100/totalScore);
                Map<String, Double> targetStuRage = new HashMap<>();
                targetStuRage.put(studentId, rage);
                targetStuRageList.add(targetStuRage);
            });
            stuRageOfTarget.put(targetId, targetStuRageList);
        });
        stuRageOfTarget.forEach((targetId, mapList)->{
            List<Double> targetStuRageList = mapList.stream().flatMap(map->map.values().stream()).toList();
            Double max = targetStuRageList.stream().filter(Objects::nonNull).max(Double::compare).orElse(null);
            Double min = targetStuRageList.stream().filter(Objects::nonNull).min(Double::compare).orElse(null);
            OptionalDouble avg = targetStuRageList.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).average();

            ObjectNode jsonObject = objectMapper.createObjectNode();
            jsonObject.put("targetId", targetId);
            jsonObject.put("targetName", targetName.get(targetId));
            jsonObject.put("avgRage", avg.isPresent() ? numWith2Decimal(avg.getAsDouble()) : null);
            jsonObject.put("maxRage", max);
            jsonObject.put("minRage", min);
            targetNodeList.add(jsonObject);
        });
        this.saveProgressData(type, progressId, targetNodeList);
    }
    private void T_CT_CR(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();

        ArrayNode courseTargets = objectMapper.createArrayNode();
        CompTargetTagGroupByTarget.forEach((targetId, compList) -> {
            ObjectNode target = objectMapper.createObjectNode();
            double achievementScore = compList.stream()
                    .filter(Objects::nonNull)
                    .filter(comp->comp.getCompScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompScore)
                    .sum();
            double totalScore = compList.stream()
                    .filter(Objects::nonNull)
                    .filter(comp->comp.getCompTotalScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompTotalScore)
                    .sum();
            double achievement = numWith2Decimal(achievementScore*100 / totalScore);

            Map<Integer, List<CompTargetTagDto>> abilitMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
            ArrayNode abilityNode = objectMapper.createArrayNode();
            abilitMap.forEach((tagId, list)->{
                ObjectNode ability = objectMapper.createObjectNode();
                double tagAchievementScore = list.stream()
                        .filter(comp->comp.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .sum();
                double tagTotalScore = list.stream()
                        .filter(comp->comp.getCompTotalScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompTotalScore)
                        .sum();
                double tagAchievement = numWith2Decimal(tagAchievementScore*100 / tagTotalScore);
                ability.put("tagId", tagId);
                ability.put("tagName", list.get(0).getTagName());
                ability.put("achievement", tagAchievement);
                abilityNode.add(ability);
            });

            target.put("targetId", targetId);
            target.put("targetName", targetName.get(targetId));
            target.put("description", compList.get(0).getTargetDesc());
            target.put("category", compList.get(0).getTargetRName());
            target.put("achievement", achievement);
            target.set("abilities", abilityNode);

            courseTargets.add(target);
        });

        ArrayNode experiments = objectMapper.createArrayNode();
        CompTargetTagGroupByPT.forEach((ptId, compList)->{
            ObjectNode experiment = objectMapper.createObjectNode();
            Map<Integer, List<CompTargetTagDto>> abilitMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
            ArrayNode abilityNode = objectMapper.createArrayNode();
            abilitMap.forEach((tagId, list)->{
                ObjectNode ability = objectMapper.createObjectNode();
                double tagAchievementScore = list.stream()
                        .filter(comp->comp.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .sum();
                double tagTotalScore = list.stream()
                        .filter(comp->comp.getCompTotalScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompTotalScore)
                        .sum();
                double tagAchievement = numWith2Decimal(tagAchievementScore*100 / tagTotalScore);
                ability.put("tagId", tagId);
                ability.put("tagName", list.get(0).getTagName());
                ability.put("achievement", tagAchievement);
                ability.put("targetId", list.get(0).getTargetId());
                abilityNode.add(ability);
            });
            experiment.put("name", compList.get(0).getPtName());
            experiment.set("abilities",abilityNode);
            experiments.add(experiment);
        });

        objectNode.set("courseTargets", courseTargets);
        objectNode.set("experiments", experiments);
        this.saveProgressData(type, progressId, objectNode);
    }
    private void T_CT_TA(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode courseTargets = objectMapper.createArrayNode();
        CompTargetTagGroupByTarget.forEach((targetId, compList) -> {
            ObjectNode target = objectMapper.createObjectNode();
            double achievementScore = compList.stream()
                    .filter(Objects::nonNull)
                    .filter(comp->comp.getCompScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompScore)
                    .sum();
            double totalScore = compList.stream()
                    .filter(Objects::nonNull)
                    .filter(comp->comp.getCompTotalScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompTotalScore)
                    .sum();
            double achievement = numWith2Decimal(achievementScore*100 / totalScore);

            Map<Integer, List<CompTargetTagDto>> abilitMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
            ArrayNode abilityNode = objectMapper.createArrayNode();
            abilitMap.forEach((tagId, list)->{
                ObjectNode ability = objectMapper.createObjectNode();
                double tagAchievementScore = list.stream()
                        .filter(comp->comp.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .sum();
                double tagTotalScore = list.stream()
                        .filter(comp->comp.getCompTotalScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompTotalScore)
                        .sum();
                double tagAchievement = numWith2Decimal(tagAchievementScore*100 / tagTotalScore);
                ability.put("tagId", tagId);
                ability.put("tagName", list.get(0).getTagName());
                ability.put("achievement", tagAchievement);
                abilityNode.add(ability);
            });

            Map<String,List<CompTargetTagDto>> stuMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getStudentId));
            List<Double> achievementList = new ArrayList<>();
            stuMap.forEach((studentId, list)->{
                double stuScore = list.stream()
                        .filter(comp->comp.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .sum();
                double stuTotalScore = list.stream()
                        .filter(comp->comp.getCompTotalScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompTotalScore)
                        .sum();
                double achieve = numWith2Decimal(stuScore*100 / stuTotalScore);
                achievementList.add(achieve);
            });

            Map<String, Integer> stuAchievementMap = new HashMap<>();
            stuAchievementMap.put("优秀(90-100)", 0);
            stuAchievementMap.put("良好(80-90)", 0);
            stuAchievementMap.put("中等(70-80)", 0);
            stuAchievementMap.put("及格(60-70)", 0);
            stuAchievementMap.put("不及格(<60)", 0);
            achievementList.forEach(a->{
                if(a<60){
                    stuAchievementMap.compute("不及格(<60)", (k,v)-> v==null?1:v+1);
                }else if(a>=60 && a <70){
                    stuAchievementMap.compute("及格(60-70)", (k,v)-> v==null?1:v+1);
                }else if(a>=70 && a <80){
                    stuAchievementMap.compute("中等(70-80)", (k,v)-> v==null?1:v+1);
                }else if(a>=80 && a <90){
                    stuAchievementMap.compute("良好(80-90)", (k,v)-> v==null?1:v+1);
                }else if(a>=90 && a <=100){
                    stuAchievementMap.compute("优秀(90-100)", (k,v)-> v==null?1:v+1);
                }
            });
            ArrayNode achievementDistribution = objectMapper.createArrayNode();
            stuAchievementMap.forEach((k,v)->{
                ObjectNode object = objectMapper.createObjectNode();
                double rage = numWith2Decimal((double) v*100 /achievementList.size());
                object.put(k,rage);
                achievementDistribution.add(object);
            });

            target.put("targetId", targetId);
            target.put("targetName", targetName.get(targetId));
            target.put("description", compList.get(0).getTargetDesc());
            target.put("category", compList.get(0).getTargetRName());
            target.put("achievement", achievement);
            target.set("abilities", abilityNode);
            target.set("achievementDistribution", achievementDistribution);
            courseTargets.add(target);
        });
        this.saveProgressData(type, progressId, courseTargets);
    }
    private void T_CT_OAS_TREND(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        CompTargetTagGroupByTarget.forEach((targetId, compList)->{
            ObjectNode jsonNode = objectMapper.createObjectNode();

            List<Long> ptIdList = compList.stream()
                    .map(CompTargetTagDto::getPtId)
                    .distinct()
                    .toList();
            List<Long> ptIdList2 = new ArrayList<>();
            double totalScore = compList.stream()
                    .mapToDouble(CompTargetTagDto::getCompTotalScore)
                    .sum();

            ArrayNode trendNode = objectMapper.createArrayNode();

            ObjectNode object0 = objectMapper.createObjectNode();
            object0.put("id",0);
            object0.put("label","学期初");
            object0.put("name","学期初");
            object0.put("value",0);
            trendNode.add(object0);
            for(int i=0; i<ptIdList.size(); i++){
                // 实验数量递增
                ptIdList2.add(ptIdList.get(i));
                // 计算达成度
                List<CompTargetTagDto> comps = compList.stream()
                        .filter(comp-> ptIdList2.contains(comp.getPtId()))
                        .filter(comp->comp.getCompScore()!=null)
                        .toList();
                // 获取第i个实验的实验名称
                int finalI = i;
                List<CompTargetTagDto> comps2 = compList.stream()
                        .filter(comp-> Objects.equals(comp.getPtId(), ptIdList.get(finalI)))
                        .toList();
                String taskName = comps2.get(0).getPtName();
                double score = comps.stream().mapToDouble(CompTargetTagDto::getCompScore).sum();
                double trend = numWith2Decimal(score*100 / totalScore);
                ObjectNode object = objectMapper.createObjectNode();
                object.put("id",i+1);
                object.put("label","第%s次实验".formatted(i+1));
                object.put("name",taskName);
                object.put("value",trend);
                trendNode.add(object);
            }
            double score = compList.stream()
                    .mapToDouble(CompTargetTagDto::getCompScore)
                    .sum();
            double trend = numWith2Decimal(score*100 / totalScore);
            ObjectNode object1 = objectMapper.createObjectNode();
            object1.put("id",ptIdList.size()+1);
            object1.put("label","当前");
            object1.put("name","当前");
            object1.put("value",trend);
            trendNode.add(object1);

            jsonNode.put("targetId", targetId);
            jsonNode.put("targetName", compList.get(0).getTargetName());
            jsonNode.set("trend", trendNode);

            arrayNode.add(jsonNode);
        });
        this.saveProgressData(type, progressId, arrayNode);
    }
    private void T_TR_OVERVIEW(AnalysisType type, String progressId, Integer projectId){

    }
    private void T_TR_IS(AnalysisType type, String progressId, Integer projectId){

    }
    private void T_TASK_D_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode jsonNodes = objectMapper.createObjectNode();
        PSTGroupByTask.forEach((ptId, taskPstList)->{
            ObjectNode taskNode = objectMapper.createObjectNode();
            // 班级实验成绩分布
            List<Double> ptScoreList = taskPstList.stream()
                    .map(pst->pst.getTaskScore()==null?0.0:pst.getTaskScore())
                    .toList();
            Map<String, Integer> scoreDMap = new HashMap<>();
            scoreDMap.put("<60",0);
            scoreDMap.put("60-70",0);
            scoreDMap.put("70-80",0);
            scoreDMap.put("80-90",0);
            scoreDMap.put("90-100",0);
            ptScoreList.forEach(score->{
                if(score<60){
                    scoreDMap.compute("<60",(k,v)->v==null?1:v+1);
                } else if (score>=60 && score<70) {
                    scoreDMap.compute("60-70",(k,v)->v==null?1:v+1);
                } else if (score>=70 && score<80) {
                    scoreDMap.compute("70-80",(k,v)->v==null?1:v+1);
                } else if (score>=80 && score<90) {
                    scoreDMap.compute("80-90",(k,v)->v==null?1:v+1);
                } else if (score>=90 && score<=100) {
                    scoreDMap.compute("90-100",(k,v)->v==null?1:v+1);
                }
            });
            ArrayNode scoreArrayNode = objectMapper.createArrayNode();
            scoreDMap.forEach((k,v)->{
                ObjectNode object = objectMapper.createObjectNode();
                object.put(k,v);
                scoreArrayNode.add(object);
            });

            // 实验阶段平均得分
            ArrayNode stageAvgScoreArrayNode = objectMapper.createArrayNode();
            Map<Integer, List<CompTargetTagDto>> taskStageCompMap = ComponentList
                    .stream()
                    .filter(comp->comp.getPtId().equals(ptId))
                    .collect(
                            Collectors.groupingBy(
                                    CompTargetTagDto::getCompStage
                            )
                    );
            taskStageCompMap.forEach((stage, cList)->{
                ObjectNode node = objectMapper.createObjectNode();
                OptionalDouble avg = cList.stream()
                        .filter(comp->comp.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .average();
                node.put("stage",stage);
                node.put("label", "阶段%s".formatted(stage));
                node.put("value", avg.isPresent()?numWith2Decimal(avg.getAsDouble()):0);
                stageAvgScoreArrayNode.add(node);
            });

            // 实验下学生数据一览
            ObjectNode stuNodes = objectMapper.createObjectNode();
            Map<Long, List<CompTargetTagDto>> taskStuCompMap = ComponentList.stream()
                    .filter(comp->comp.getPtId().equals(ptId))
                    .collect(
                            Collectors.groupingBy(
                                    CompTargetTagDto::getPtId
                            )
                    );
            taskStuCompMap.forEach((psId, sCompList)->{
                ObjectNode stuNode = objectMapper.createObjectNode();
                stuNode.put("psId", psId);
                stuNode.put("ptId", ptId);
                stuNode.put("studentName", sCompList.get(0).getStudentName());
                stuNode.put("studentId", sCompList.get(0).getStudentId());
                double score=sCompList.stream()
                        .filter(comp->comp.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .sum();
                stuNode.put("score", score);
                Map<Integer, List<CompTargetTagDto>> pstStageMap = sCompList.stream()
                        .collect(Collectors.groupingBy(CompTargetTagDto::getCompStage));
                ObjectNode stageRageNode = objectMapper.createObjectNode();
                pstStageMap.forEach((stage, pstList)->{
                    double totalScore = pstList.stream()
                            .filter(comp->comp.getCompTotalScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompTotalScore)
                            .sum();
                    double stageScore = pstList.stream()
                            .filter(comp->comp.getCompScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompScore)
                            .sum();
                    double stageRage = numWith2Decimal(stageScore*100 / totalScore);
                    stageRageNode.put(stage.toString(),stageRage);
                });
                stuNode.set("stageRage", stageRageNode);

                stuNodes.set(psId.toString(),stuNode);
            });

            taskNode.set("scoreDistribution", scoreArrayNode);
            taskNode.set("stageAvgScore", stageAvgScoreArrayNode);
            jsonNodes.set(String.valueOf(ptId),taskNode);
        });
        this.saveProgressData(type,progressId, jsonNodes);
    }
    private void T_TASK_D_ABILITY(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        CompTargetTagGroupByPT.forEach((ptId, ptCompList)->{
            Map<Integer, List<CompTargetTagDto>> tagCompMap = ptCompList.stream()
                    .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
            //能力掌握分布

            ArrayNode abilityAvgRage = objectMapper.createArrayNode();
            tagCompMap.forEach((tagId, tagCompList)->{
                ObjectNode abilityRage = objectMapper.createObjectNode();
                Map<Long, List<CompTargetTagDto>> stuTagCompMap = tagCompList.stream()
                        .collect(Collectors.groupingBy(CompTargetTagDto::getPsId));
                List<Double> rageList = new ArrayList<>();

                stuTagCompMap.forEach((psId, stuTagCompList)->{
                    double score = stuTagCompList.stream()
                            .filter(comp->comp.getCompScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompScore)
                            .sum();
                    double totalScore = stuTagCompList.stream()
                            .filter(comp->comp.getCompTotalScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompTotalScore)
                            .sum();
                    double rage = numWith2Decimal(score*100 / totalScore);
                    rageList.add(rage);
                });

                OptionalDouble avgRage = rageList.stream().mapToDouble(Double::doubleValue).average();
                abilityRage.put("tagId", tagId);
                abilityRage.put("tagName", tagCompList.get(0).getTagName());
                abilityRage.put("avgRage", avgRage.isPresent()?numWith2Decimal(avgRage.getAsDouble()):0);

                // 掌握程度分布
                ArrayNode rageDArray = objectMapper.createArrayNode();
                Map<String, Integer> rageD = new HashMap<>();
                rageD.put("<60",0);
                rageD.put("60-70",0);
                rageD.put("70-80",0);
                rageD.put("80-90",0);
                rageD.put("90-100",0);
                rageList.forEach(rage->{
                    if(rage<60){
                        rageD.compute("<60", (k,v)->v==null?1:v+1);
                    } else if (rage>=60 && rage<70) {
                        rageD.compute("60-70", (k,v)->v==null?1:v+1);
                    } else if (rage>=70 && rage<80) {
                        rageD.compute("70-80", (k,v)->v==null?1:v+1);
                    } else if (rage>=80 && rage<90) {
                        rageD.compute("<80-90", (k,v)->v==null?1:v+1);
                    } else if (rage>=90 && rage<=100) {
                        rageD.compute("90-100", (k,v)->v==null?1:v+1);
                    }
                });
                rageD.forEach((k,v)->{
                    ObjectNode item = objectMapper.createObjectNode();
                    item.put(k,v);
                    rageDArray.add(item);
                });
                abilityRage.set("abilityDistribution", rageDArray);

                //问题掌握情况
                ArrayNode quesAchievedRage = objectMapper.createArrayNode();

                Map<String,List<CompTargetTagDto>> quesTagCompMap = tagCompList.stream()
                        .filter(comp->comp.getCompName()!=null)
                        .collect(Collectors.groupingBy(CompTargetTagDto::getCompName));
                quesTagCompMap.forEach((ques, quesList)->{
                    ObjectNode item = objectMapper.createObjectNode();
                    double score = quesList.stream()
                            .filter(comp->comp.getCompScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompScore)
                            .sum();
                    double totalScore = quesList.stream()
                            .filter(comp->comp.getCompTotalScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompTotalScore)
                            .sum();
                    double rage = numWith2Decimal(score*100/totalScore);
                    item.put("name", ques);
                    item.put("scoringRage", rage);
                    item.put("type", quesList.get(0).getCompType());
                    item.put("payload", quesList.get(0).getCompPayload());
                    quesAchievedRage.add(item);
                });
                abilityRage.set("quesAchievedRage", quesAchievedRage);

                abilityAvgRage.add(abilityRage);
            });
            objectNode.set(ptId.toString(), abilityAvgRage);
        });
        this.saveProgressData(type, progressId, objectNode);
    }
    private void T_TASK_D_QUES(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {

        ObjectNode objectNode = objectMapper.createObjectNode();
        CompTargetTagGroupByPT.forEach((ptId, ptTagList)->{
            ObjectNode ptNode = objectMapper.createObjectNode();

            ArrayNode ptTagArrayNode = objectMapper.createArrayNode();
            int allTagSize = ptTagList.size();
            Map<Integer, List<CompTargetTagDto>> tagMap = ptTagList.stream()
                    .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
            tagMap.forEach((tagId, list)->{
                ObjectNode item = objectMapper.createObjectNode();
                item.put("id", tagId);
                item.put("name", list.get(0).getTagName());
                item.put("value", list.size());
                item.put("rage", numWith2Decimal((double) list.size()*100/allTagSize));
                ptTagArrayNode.add(item);
            });

            Map<String, List<CompTargetTagDto>> quesMap =  ptTagList.stream()
                    .collect(Collectors.groupingBy(CompTargetTagDto::getCompName));

            ArrayNode quesDetailArray = objectMapper.createArrayNode();
            quesMap.forEach((name, qList)->{
                ObjectNode item = objectMapper.createObjectNode();
                List<CompTargetTagDto> notDone = qList.stream().filter(comp->comp.getCompStatus().equals(0)).toList();
                List<CompTargetTagDto> rightDone = qList.stream()
                        .filter(comp->comp.getCompStatus()>0)
                        .filter(comp-> Objects.equals(comp.getCompTotalScore(), comp.getCompScore()))
                        .toList();
                List<CompTargetTagDto> errorDone = qList.stream()
                        .filter(comp->comp.getCompStatus()>0)
                        .filter(comp->comp.getCompTotalScore()>comp.getCompScore())
                        .toList();

                double errorRate = numWith2Decimal((double) (errorDone.size() * 100) / qList.size());
                double accuracyRate = numWith2Decimal((double) (rightDone.size() * 100) / qList.size());

                item.put("name", name);
                item.put("accuracyRate", accuracyRate);
                item.put("errorRate", errorRate);
                item.put("errorNum", errorDone.size());
                item.put("rightNum", rightDone.size());
                item.put("notDoneNum", notDone.size());
                item.put("tagId", qList.get(0).getTagId());
                item.put("tagName", qList.get(0).getTagName());
                item.put("stage", qList.get(0).getCompStage());
                item.put("type", qList.get(0).getCompType());
                item.put("payload",qList.get(0).getCompPayload());
                quesDetailArray.add(item);
            });
            ptNode.set("tagOfQuesDistribution", ptTagArrayNode);
            ptNode.set("quesDetail", quesDetailArray);

            objectNode.set(ptId.toString(), ptNode);
        });
        this.saveProgressData(type, progressId, objectNode);
    }
    private void T_TASK_D_COURSE(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        PSTGroupByTaskWithStage.forEach((ptId, pstList)->{
            ObjectNode taskNode = objectMapper.createObjectNode();
            // 实验 -> 学生操作列表
            ArrayNode stageTime = objectMapper.createArrayNode();
            ArrayNode timeDistributionStage1 = objectMapper.createArrayNode();
            Map<Integer, List<PSTDto>> stagePstMap = pstList.stream().collect(Collectors.groupingBy(PSTDto::getStage));
            stagePstMap.forEach((stage, list)->{
                ObjectNode timeNode = objectMapper.createObjectNode();

                // 计算平均时间
                List<Long> stageTimeList = list.stream()
                        .filter(pst->pst.getStartTime()!=null & pst.getEndTime()!=null)
                        .filter(pst->pst.getEndTime().after(pst.getStartTime()))
                        .map(pst-> pst.getEndTime().getTime()-pst.getStartTime().getTime())
                        .toList();
                OptionalDouble avg  = stageTimeList.stream().mapToLong(Long::longValue).average();
                long avgTime = avg.isPresent()?(long)Math.ceil(avg.getAsDouble()):0;
                OptionalLong maxTime = stageTimeList.stream().mapToLong(Long::longValue).max();
                OptionalLong minTime = stageTimeList.stream().mapToLong(Long::longValue).min();

                timeNode.put("avg", avgTime);
                timeNode.put("max", maxTime.isPresent()?maxTime.getAsLong():0);
                timeNode.put("min", minTime.isPresent()?minTime.getAsLong():0);
                stageTime.add(timeNode);
                if(stage==1){
                    // 计算时间分布
                    Map<String, Integer> timeMap = new HashMap<>();
                    timeMap.put("<45", 0);
                    timeMap.put("45-80", 0);
                    timeMap.put(">80", 0);
                    stageTimeList.forEach(t->{
                        if(t<2700000){
                            timeMap.compute("<45",(k,v)->v==null?1:v+1);
                        }else if(t>=2700000 && t<=4800000){
                            timeMap.compute("45-80",(k,v)->v==null?1:v+1);
                        } else{
                            timeMap.compute(">80",(k,v)->v==null?1:v+1);
                        }
                    });
                    timeMap.forEach((k,v)->{
                        ObjectNode itemD = objectMapper.createObjectNode();
                        itemD.put(k, v);
                        timeDistributionStage1.add(itemD);
                    });
                }

            });
            taskNode.set("stageTime", stageTime);
            taskNode.set("timeDistributionStage1",timeDistributionStage1);
            objectNode.set(ptId.toString(), taskNode);
        });
        this.saveProgressData(type, progressId, objectNode);
    }
    private void T_TASK_D_SUG(AnalysisType type, String progressId, Integer projectId){

    }
    private void STU_P_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();

        Map<Long, Double> taskAvgScoreMap = new HashMap<>(); // 存放各个实验的平均成绩
        // 计算班级各个实验平均成绩
        PSTGroupByTask.forEach((ptId, list)->{
            OptionalDouble avg = list.stream().filter(pst->pst.getTaskScore()!=null).mapToDouble(PSTDto::getTaskScore).average();
            taskAvgScoreMap.put(ptId, avg.isPresent()?numWith2Decimal(avg.getAsDouble()):0);
        });

        Map<Integer, Double> targetRageMap = new HashMap<>();
        CompTargetTagGroupByTarget.forEach((targetId, list)->{
            double totalScore = list.stream()
                    .filter(comp->comp.getCompTotalScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompTotalScore)
                    .sum();
            double score = list.stream()
                    .filter(comp->comp.getCompScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompScore)
                    .sum();
            double rage = numWith2Decimal(score*100/totalScore);
            targetRageMap.put(targetId, rage);
        });

        PSTGroupByStuWithStage.forEach((stuId, stuList)->{
            ObjectNode stuNode = objectMapper.createObjectNode();
            // 课程整体完成进度
            int doneSize = stuList.stream().filter(pst->pst.getStatus()>=1).toList().size();
            double projectRage = numWith2Decimal((double) doneSize*100/stuList.size());
            Map<Long, List<PSTDto>> stuPtMap = stuList.stream().filter(pst->pst.getStage()==0).collect(Collectors.groupingBy(PSTDto::getPtId));
            // 已完成的实验==》 各个实验的状态
            ArrayNode taskArray = objectMapper.createArrayNode();

            AtomicInteger hasDone = new AtomicInteger(1);  // 已完成的数量
            stuPtMap.forEach((ptId, list)->{
                ObjectNode taskNode = objectMapper.createObjectNode();
                taskNode.put("ptId", ptId);
                taskNode.put("ptName", list.get(0).getTaskName());
                taskNode.put("ptScore", list.get(0).getTaskScore());
                taskNode.put("ptAvgScore", taskAvgScoreMap.get(ptId));
                if(list.stream().filter(pst->pst.getStatus()<=0).toList().isEmpty()){  // 没有status为0的，表示该实验已经已经完成
                    taskNode.put("status", 1);
                    hasDone.getAndIncrement();
                }else {
                    taskNode.put("status", 0);
                }
                taskArray.add(taskNode);
            });

            // 知识点掌握
            List<CompTargetTagDto> stuComp = CompTargetTagGroupByStu.get(stuId);
            List<CompTargetTagDto> compHasDone = stuComp.stream().filter(comp->comp.getCompStatus()>0).toList();

            Map<Integer, List<CompTargetTagDto>> stuTargetCompMap = stuComp.stream()
                    .collect(Collectors.groupingBy(CompTargetTagDto::getTargetId));

            ArrayNode targetNode = objectMapper.createArrayNode();
            stuTargetCompMap.forEach((targetId, list)->{
                ObjectNode targetItem = objectMapper.createObjectNode();

                double score = list.stream()
                        .filter(comp->comp.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .sum();
                double totalScore = list.stream()
                        .filter(comp->comp.getCompTotalScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompTotalScore)
                        .sum();
                double rage = numWith2Decimal(score*100/totalScore);

                targetItem.put("targetId", targetId);
                targetItem.put("targetName", list.get(0).getTargetName());
                targetItem.put("stuRage", rage);
                targetItem.put("classRage", targetRageMap.get(targetId));
                targetItem.put("difference", rage- targetRageMap.get(targetId));
                targetNode.add(targetItem);
            });


            stuNode.put("projectRage", projectRage);
            stuNode.set("taskList", taskArray);
            stuNode.put("hadDoneSize", hasDone.get());
            stuNode.put("tagSize", stuComp.size());
            stuNode.put("tagDoneSize", compHasDone.size());
            stuNode.put("tagHasNotDone", numWith2Decimal(stuComp.size()-compHasDone.size()));
            stuNode.set("target", targetNode);
            objectNode.set(stuId, stuNode);
        });
        this.saveProgressData(type, progressId, objectNode);
    }
    private void STU_P_TASK(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        PSTGroupByStu.forEach((stuId, pstList)->{
            ObjectNode stuNode = objectMapper.createObjectNode();
            ArrayNode taskNode  = objectMapper.createArrayNode();
            Map<Long, List<PSTDto>> stuTaskMap = pstList.stream().collect(Collectors.groupingBy(PSTDto::getPtId));
            stuTaskMap.forEach((ptId, list)->{
                ObjectNode task = objectMapper.createObjectNode();
                Map<Integer, List<CompTargetTagDto>> tagMap = CompTargetTagGroupByPT.get(ptId).stream()
                        .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));

                ArrayNode tagArray = objectMapper.createArrayNode();
                tagMap.forEach((tagId, compList)->{
                    if(compList.get(0).isKeyNode()){
                        ObjectNode tagNode = objectMapper.createObjectNode();
                        tagNode.put("tagId",tagId);
                        tagNode.put("tagName", compList.get(0).getTagName());
                        tagArray.add(tagNode);
                    }
                });
                task.put("ptId", ptId);
                task.put("ptName",list.get(0).getTaskName());
                task.put("psId",list.get(0).getPsId());
                task.put("doneTime",list.get(0).getEndTime()==null?null:list.get(0).getEndTime().toString());
                task.put("score",list.get(0).getTaskScore()==null?0:list.get(0).getTaskScore());
                task.set("tagList",tagArray);
                task.put("status",list.get(0).getStatus());
                taskNode.add(task);
            });

            stuNode.set("task",taskNode);
            objectNode.set(stuId, stuNode);
        });
        this.saveProgressData(type, progressId, objectNode);
    }
    private void STU_P_TARGET(AnalysisType type, String progressId, Integer projectId){

    }
    private void STU_P_SUG(AnalysisType type, String progressId, Integer projectId){

    }


    private void genChildData(Integer progetId, String progressId, AnalysisType analysisType) throws AnalysisProgressGenChildDataException{
        try{
            switch (analysisType){
                case T_OVERVIEW_OVERVIEW -> {
                    this.T_OVERVIEW_OVERVIEW(analysisType, progressId, progetId);
                }
                case T_OVERVIEW_RATE -> {
                    this.T_OVERVIEW_RATE(analysisType, progressId, progetId);
                }
                case T_OVERVIEW_GA -> {
                    this.T_OVERVIEW_GA(analysisType, progressId, progetId);
                }
                case T_OVERVIEW_DOCG -> {
                    this.T_OVERVIEW_DOCG(analysisType, progressId, progetId);
                }
                case T_OVERVIEW_ES -> {
                    this.T_OVERVIEW_ES(analysisType, progressId, progetId);
                }
                case T_OVERVIEW_CWLS -> {
                    this.T_OVERVIEW_CWLS(analysisType, progressId, progetId);
                }
                case T_OVERVIEW_AI_VIEW -> {
                    this.T_OVERVIEW_AI_VIEW(analysisType, progressId, progetId);
                }
                case T_EA_OVERVIEW -> {
                    this.T_EA_OVERVIEW(analysisType, progressId, progetId);
                }
                case T_EA_ED -> {
                    this.T_EA_ED(analysisType, progressId, progetId);
                }
                case T_EA_ECA -> {
                    this.T_EA_ECA(analysisType, progressId, progetId);
                }
                case T_STU_OVERVIEW -> {
                    this.T_STU_OVERVIEW(analysisType, progressId, progetId);
                }
                case T_STU_BEHAVIOUR -> {
                    this.T_STU_BEHAVIOUR(analysisType, progressId, progetId);
                }
                case T_CT_OAS -> {
                    this.T_CT_OAS(analysisType, progressId, progetId);
                }
                case T_CT_CR -> {
                    this.T_CT_CR(analysisType, progressId, progetId);
                }
                case T_CT_TA -> {
                    this.T_CT_TA(analysisType, progressId, progetId);
                }
                case T_CT_OAS_TREND -> {
                    this.T_CT_OAS_TREND(analysisType, progressId, progetId);
                }
                case T_TR_OVERVIEW -> {
                    this.T_TR_OVERVIEW(analysisType, progressId, progetId);
                }
                case T_TR_IS -> {
                    this.T_TR_IS(analysisType, progressId, progetId);
                }
                case T_TASK_D_OVERVIEW -> {
                    this.T_TASK_D_OVERVIEW(analysisType, progressId, progetId);
                }
                case T_TASK_D_ABILITY -> {
                    this.T_TASK_D_ABILITY(analysisType, progressId, progetId);
                }
                case T_TASK_D_QUES -> {
                    this.T_TASK_D_QUES(analysisType, progressId, progetId);
                }
                case T_TASK_D_COURSE -> {
                    this.T_TASK_D_COURSE(analysisType, progressId, progetId);
                }
                case T_TASK_D_SUG -> {
                    this.T_TASK_D_SUG(analysisType, progressId, progetId);
                }
                case STU_P_OVERVIEW -> {
                    this.STU_P_OVERVIEW(analysisType, progressId, progetId);
                }
                case STU_P_TASK -> {
                    this.STU_P_TASK(analysisType, progressId, progetId);
                }
                case STU_P_TARGET -> {
                    this.STU_P_TARGET(analysisType, progressId, progetId);
                }
                case STU_P_SUG -> {
                    this.STU_P_SUG(analysisType, progressId, progetId);
                }
                default -> throw new AnalysisProgressGenChildDataException("没有对应方法");
            }
        }catch(Exception e){
            log.error("[{}] 任务异常", analysisType.getDesc());
            throw new AnalysisProgressGenChildDataException(e.getMessage());
        }
    }

    private void updateProgress(String progressId, Integer completedCount, Boolean finished, String msg){
        AnalysisProgress progress = progressMapper.getAPById(progressId);
        progress.setCompletedCount(completedCount);
        progress.setPercent(completedCount*100/progress.getTotalCount());
        progress.setFinished(finished);
        String message = progress.getMessage();
        progress.setMessage(message.isEmpty()?msg:message+"\n"+msg);
        progress.setUpdateTime(new Date());
        progressMapper.updateAPById(progress);
    }

    private void saveProgressData(AnalysisType type, String progressId, JsonNode data) throws JsonProcessingException {
        AnalysisProgressData apd = new AnalysisProgressData();
        apd.setId(UUIDGenerator.generateUUID());
        apd.setApId(progressId);
        apd.setType(type.getValue());
        apd.setData(objectMapper.writeValueAsString(data));
        progressMapper.createAPD(apd);
    }

    /**
     * 传入一个带小数的数字
     * @param num 数字
     * @return 保留两位小数的值
     */
    private static double numWith2Decimal(double num){
        return BigDecimal.valueOf(num)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
