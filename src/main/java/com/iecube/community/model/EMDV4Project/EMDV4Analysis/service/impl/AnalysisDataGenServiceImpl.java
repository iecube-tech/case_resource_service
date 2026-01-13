package com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.EMDV4.TagLink.entity.TagLink;
import com.iecube.community.model.EMDV4.TagLink.mapper.TagLinkMapper;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.aiAgent.EvaluationAgent;
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
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.service.ProjectService;
import com.iecube.community.util.uuid.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@Slf4j
public class AnalysisDataGenServiceImpl implements AnalysisDataGenService {
    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private AnalysisProgressMapper progressMapper;

    @Autowired
    private EvaluationAgent evaluationAgent;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TagLinkMapper tagLinkMapper;


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
    private final ConcurrentHashMap<Long, List<PSTAIDto>> PSTAIGroupByPt = new ConcurrentHashMap<>();  // <studentId List<PSTAIDto>>

    private final List<CompTargetTagDto> ComponentList = new CopyOnWriteArrayList<>();

    private final List<CompTargetTagDto> CompTargetTagDtoList = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<Integer, List<CompTargetTagDto>> CompTargetTagGroupByTarget = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<CompTargetTagDto>> CompTargetTagGroupByStu = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<CompTargetTagDto>> CompTargetTagGroupByPT = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<CompTargetTagDto>> CompTargetTagGroupByPST = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, String> targetName = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long, List<TagLink>> tagLinkMap = new ConcurrentHashMap<>();

    //课程数据总览
    private final ObjectNode courseNode = objectMapper.createObjectNode();

    //学生数据总览
    private final ArrayNode studentsNode = objectMapper.createArrayNode();

    private JsonNode thematic=null;

    @Async("EvaDispatch")
    @Override
    public CompletableFuture<Void> dataGen(Integer projectId, AnalysisProgress progress){
        try{
            this.dataClean(projectId); //数据清洗
        }catch (Exception e){
            log.warn("[{}] 数据清洗失败", projectId, e);
            updateProgress(progress.getId(), 0,false, "[%s] 数据清洗失败:[%s]".formatted(projectId, e.getMessage()));
        }


        for(int i=0;i<Arrays.stream(AnalysisType.values()).toList().size();i++){
            AnalysisType type = AnalysisType.values()[i];
            try{
                this.genChildData(projectId, progress.getId(), type);
                log.info("[{}][{}] 数据已生成", projectId, type.getDesc());
                updateProgress(progress.getId(), i+1,i+1==Arrays.stream(AnalysisType.values()).toList().size(), "[%s] 数据已生成".formatted(i+1));
            }catch (AnalysisProgressGenChildDataException e){
                log.error("[{}][{}] 数据分析失败",projectId, type.getDesc() ,e);
                updateProgress(progress.getId(), i+1,i+1==Arrays.stream(AnalysisType.values()).toList().size(), "[%s] 数据生成失败，%s".formatted(i+1, e.getMessage()));
            }
        }
        log.info("[{}] 数据分析成功",projectId);
        clear(projectId);
        return CompletableFuture.completedFuture(null);
    }

    @Async("EvaDispatch")
    @Override
    public void dataTest(Integer projectId,  AnalysisProgress progress){
        this.dataClean(projectId); //数据清洗
        AnalysisType type = AnalysisType.PST_SUG;
        try{
            this.genChildData(projectId, progress.getId(), type);
            log.info("[{}][{}] 数据已生成", projectId, type.getDesc());
            updateProgress(progress.getId(), 1,true, "[%s] 数据已生成".formatted(type.getDesc()));
        }catch (AnalysisProgressGenChildDataException e){
            log.error("[{}][{}] 数据分析失败",projectId, type.getDesc() ,e);
            updateProgress(progress.getId(), 1,true, "[%s] 数据生成失败，%s".formatted(type.getDesc(), e.getMessage()));
        }finally {
            clear(projectId);
        }
    }

    private void clear(Integer projectId){
        log.info("[{}] 开始清理缓存数据",projectId);
        this.PSTWithStageList.clear();
        this.PSTDtoWithoutStage.clear();
        this.PSTGroupByPstId.clear();
        this.PSTGroupByStu.clear();
        this.PSTGroupByStuWithStage.clear();
        this.PSTGroupByTask.clear();
        this.PSTGroupByTaskWithStage.clear();
        this.PSTAIDtoList.clear();
        this.PSTAIGroupByChatId.clear();
        this.PSTAIGroupByStu.clear();
        this.PSTAIGroupByPt.clear();
        this.ComponentList.clear();
        this.CompTargetTagDtoList.clear();
        this.CompTargetTagGroupByTarget.clear();
        this.CompTargetTagGroupByStu.clear();
        this.CompTargetTagGroupByPT.clear();
        this.CompTargetTagGroupByPST.clear();
        this.tagLinkMap.clear();
        this.targetName.clear();
        this.courseNode.removeAll();
        this.studentsNode.removeAll();
        this.thematic=null;
        log.info("[{}] 缓存数据已清理",projectId);
    }

    private void dataClean(Integer projectId){
        log.info("[{}] 正在清洗数据", projectId);
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
        PSTAIGroupByPt.putAll(PSTAIDtoList.stream().collect(Collectors.groupingBy(PSTAIDto::getPtId)));


        // 对CompTargetTag 分组聚合
        CompTargetTagGroupByTarget.putAll(CompTargetTagDtoList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTargetId)));
        CompTargetTagGroupByStu.putAll(CompTargetTagDtoList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getStudentId)));
        CompTargetTagGroupByPT.putAll(CompTargetTagDtoList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getPtId)));
        CompTargetTagGroupByPST.putAll(CompTargetTagDtoList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getPstId)));

        List<Long> tagList = CompTargetTagDtoList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTagId)).keySet().stream().toList();
        tagLinkMap.putAll(tagLinkMapper.getByTagIds(tagList).stream().collect(Collectors.groupingBy(TagLink::getTagId)));

        targetName.putAll(CompTargetTagDtoList.stream().collect(Collectors.toMap(CompTargetTagDto::getTargetId, CompTargetTagDto::getTargetName, (v1,v2)->v1)));

        //课程数据总览
        // 1.课程成绩分布[{name:"", value:""}]
        // 2.课程目标达成度 [{name:"", value:""}]
        // 3. AI：3.1AI互动总次数  3.2AI互动实验分布 3.3AI学生使用率 {times:"", tasks:[{ptId:"", name:"", 使用率:"", 使用人数:"" }] }
        // 4. 实验列表：[{ptId:"", name:"", 平均用时:"", 平均错误率:"", AI使用次数:"",AI交互记录:[], 能力标签成绩分布:[{标签:"", 分布:[{name:"", value:""}]], 实验得分分布:[{name:"", value:""}]  }]
        // 5. 课程实验难度 String
        ArrayNode courseGrades = objectMapper.createArrayNode();
        Map<String, Integer> gradeMap = new HashMap<>();
        PSTDtoWithoutStage.values().forEach(pst->{
            if(pst.getPsScore()<60){
                gradeMap.compute("<60",(k,v)->v==null?1:v+1);
            } else if (pst.getPsScore()>=60&&pst.getPsScore()<70) {
                gradeMap.compute("60-70",(k,v)->v==null?1:v+1);
            } else if (pst.getPsScore()>=70&&pst.getPsScore()<80) {
                gradeMap.compute("70-80",(k,v)->v==null?1:v+1);
            } else if (pst.getPsScore()>=80&&pst.getPsScore()<90) {
                gradeMap.compute("80-90",(k,v)->v==null?1:v+1);
            } else if (pst.getPsScore()>=90&&pst.getPsScore()<=100) {
                gradeMap.compute("90-100",(k,v)->v==null?1:v+1);
            }
        });
        gradeMap.forEach((k,v)->{
            ObjectNode gradeItem = objectMapper.createObjectNode();
            gradeItem.put("name", k);
            gradeItem.put("value", v);
            courseGrades.add(gradeItem);
        });
        courseNode.set("courseGrades", courseGrades); // 1.课程成绩分布[{name:"", value:""}]
        double tScore = CompTargetTagDtoList.stream()
                .filter(c->c.getCompScore()!=null)
                .mapToDouble(CompTargetTagDto::getCompScore)
                .sum();
        double tTotalScore = CompTargetTagDtoList.stream()
                .filter(c->c.getCompTotalScore()!=null)
                .mapToDouble(CompTargetTagDto::getCompTotalScore)
                .sum();
        double targetRage = numWith2Decimal(tScore*100/tTotalScore);
        courseNode.put("targetRage", targetRage+"%");
        ArrayNode courseTargets = objectMapper.createArrayNode();
        CompTargetTagGroupByTarget.forEach((targetId, list)->{
            String name = list.get(0).getTargetName();
            double totalScore = list.stream()
                    .filter(c->c.getCompTotalScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompTotalScore)
                    .sum();
            double score = list.stream()
                    .filter(c->c.getCompScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompScore)
                    .sum();
            double rage = numWith2Decimal(score*100/totalScore);
            ObjectNode targetItem = objectMapper.createObjectNode();
            targetItem.put("name", name);
            targetItem.put("value", rage+"%");
            courseTargets.add(targetItem);
        });
        courseNode.set("courseTargets", courseTargets); // 2.课程目标达成度 [{name:"", value:""}]
        ObjectNode aiNode = objectMapper.createObjectNode();
        ArrayNode aiTaskArray = objectMapper.createArrayNode();
        PSTAIGroupByPt.forEach((ptId, list)->{
            String taskName = list.get(0).getTaskName();
            int usedSize = list.stream().collect(Collectors.groupingBy(PSTAIDto::getStudentId)).size()/2;
            int stuTotalSize = PSTGroupByStu.size()/2;
            double rage = numWith2Decimal((double) (usedSize * 100) /stuTotalSize);
            ObjectNode aiTaskItem = objectMapper.createObjectNode();
            aiTaskItem.put("ptId", ptId);
            aiTaskItem.put("name", taskName);
            aiTaskItem.put("usedRage", rage+"%");
            aiTaskItem.put("usedNum", usedSize);
            aiTaskArray.add(aiTaskItem);
        });
        aiNode.put("times", PSTAIDtoList.size()/2);
        aiNode.set("tasks",aiTaskArray);  // 3. AI：3.1AI互动总次数  3.2AI互动实验分布 3.3AI学生使用率 {times:"", tasks:[{ptId:"", name:"", 使用率:"", 使用人数:"" }] }
        courseNode.set("ai", aiNode);
        ArrayNode tasks = objectMapper.createArrayNode();
        PSTGroupByTask.forEach((ptId, taskPstList)->{   //4. 实验列表：[{ptId:"", name:"", 平均用时:"", 平均错误率:"", AI使用次数:"",AI交互记录:[], 能力标签成绩分布:[{标签:"", 分布:[{name:"", value:""}]], 实验得分分布:[{name:"", value:""}]  }]
            String taskName = taskPstList.get(0).getTaskName();
            // 平均用时
            List<PSTDto> ptStuPSTList = PSTGroupByTaskWithStage.get(ptId)
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
            long millis = (long) Math.ceil(totalMillis/totalNum);  // 平均用时
            // 场景4：同时获取分钟和剩余秒数（如2分30秒）
            long minutes = (millis / 1000) / 60;
            long seconds = (millis / 1000) % 60;
            String avgMillis = "%s分%s秒".formatted(minutes, seconds);

            // 平均错误率
            List<CompTargetTagDto> ptStuComp = CompTargetTagGroupByPT.get(ptId)==null?new ArrayList<>():CompTargetTagGroupByPT.get(ptId);
            // 做错误的题目总数 == 成绩不为满分的题目
            List<CompTargetTagDto> errorPtStuComp = ptStuComp
                    .stream()
                    .filter(comp-> comp.getCompScore() < comp.getCompTotalScore())
                    .toList();
            double rageOfError = numWith2Decimal((double) errorPtStuComp.size()/taskPstList.size());

            // ai
            int aiUsedSize =PSTAIGroupByPt.get(ptId)==null?0:PSTAIGroupByPt.get(ptId).size()/2;

            //能力标签成绩分布
            ArrayNode tagsNode = objectMapper.createArrayNode();
            Map<Long, List<CompTargetTagDto>> taskTagMap = CompTargetTagGroupByPT.get(ptId)
                    .stream()
                    .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
            taskTagMap.forEach((tagId, compList)->{
                ObjectNode tagNode = objectMapper.createObjectNode();
                Map<Long, List<CompTargetTagDto>> taskStudentTagMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getPsId));
                List<Double> tagScoreList = new ArrayList<>();
                taskStudentTagMap.values().forEach(list->{
                    double score = list.stream().filter(c->c.getCompScore()!=null).mapToDouble(CompTargetTagDto::getCompScore).sum();
                    double totalScore = list.stream().filter(c->c.getCompScore()!=null).mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
                    double rage = numWith2Decimal((score * 100) /totalScore);
                    tagScoreList.add(rage);
                });
                ArrayNode distributeNode = objectMapper.createArrayNode();
                Map<String, Integer> tagScoreMap = new HashMap<>();
                tagScoreList.forEach(stuTagScore->{
                    if(stuTagScore<60){
                        tagScoreMap.compute("<60", (k,v)->v==null?1:v+1);
                    }else if(stuTagScore>=60&&stuTagScore<70){
                        tagScoreMap.compute("60-70", (k,v)->v==null?1:v+1);
                    }else if(stuTagScore>=70&&stuTagScore<80){
                        tagScoreMap.compute("70-80", (k,v)->v==null?1:v+1);
                    }else if(stuTagScore>=80&&stuTagScore<90){
                        tagScoreMap.compute("80-90", (k,v)->v==null?1:v+1);
                    }else if(stuTagScore>=90&&stuTagScore<=100){
                        tagScoreMap.compute("90-100", (k,v)->v==null?1:v+1);
                    }
                });
                tagScoreMap.forEach((k,v)->{
                    ObjectNode distNode = objectMapper.createObjectNode();
                    distNode.put("name", k);
                    distNode.put("value", v);
                    distributeNode.add(distNode);
                });
                tagNode.put("name", compList.get(0).getTagName());
                tagNode.set("distribute", distributeNode);
                tagsNode.add(tagNode);
            });

            // 实验得分分布
            ArrayNode scoreDistributeNode = objectMapper.createArrayNode();
            Map<String, Integer> scoreMap = new HashMap<>();
            taskPstList.forEach(pst->{
                if(pst.getTaskScore()/pst.getTaskTotalScore()<0.6){
                    scoreMap.compute("<60",(k,v)-> v==null?1:v+1);
                } else if (pst.getTaskScore()/pst.getTaskTotalScore()>=0.6 && pst.getTaskScore()/pst.getTaskTotalScore()<0.7) {
                    scoreMap.compute("60-70",(k,v)->v==null?1:v+1);
                } else if (pst.getTaskScore()/pst.getTaskTotalScore()>=0.7 && pst.getTaskScore()/pst.getTaskTotalScore()<0.8) {
                    scoreMap.compute("70-80",(k,v)->v==null?1:v+1);
                } else if (pst.getTaskScore()/pst.getTaskTotalScore()>=0.8 && pst.getTaskScore()/pst.getTaskTotalScore()<0.9) {
                    scoreMap.compute("80-90",(k,v)->v==null?1:v+1);
                } else if (pst.getTaskScore()/pst.getTaskTotalScore()>=0.9 && pst.getTaskScore()/pst.getTaskTotalScore()<=1) {
                    scoreMap.compute("90-100",(k,v)->v==null?1:v+1);
                }
            });
            scoreMap.forEach((k,v)->{
                ObjectNode distNode = objectMapper.createObjectNode();
                distNode.put("name", k);
                distNode.put("value", v);
                scoreDistributeNode.add(distNode);
            });

            // 平均得分
            OptionalDouble avgScore = taskPstList.stream().mapToDouble(PSTDto::getTaskScore).average();

            ObjectNode taskNode = objectMapper.createObjectNode();
            taskNode.put("ptId", ptId);
            taskNode.put("name", taskName);
            taskNode.put("avgScore", avgScore.isPresent()?numWith2Decimal(avgScore.getAsDouble()):0);
            taskNode.put("avgUsedTime", avgMillis);
            taskNode.put("avgRageOfError", rageOfError);
            taskNode.put("aiUsedTimes", aiUsedSize);
            taskNode.put("avgAiUsedTimes", numWith2Decimal((double)aiUsedSize/PSTGroupByStu.size()));
            taskNode.set("scoreDistribute", scoreDistributeNode);
            taskNode.set("tags", tagsNode);
            tasks.add(taskNode);
        });
        courseNode.set("tasks", tasks);
        //  courseNode.set(Difficulty) 课程实验难度  ai返回字符串
        StringBuilder sb = new StringBuilder();
        tasks.forEach(task->{
            String desc = "实验：%s,平均得分：%s分，平均问题错误率：%s/100, 平均用时：%s，平均AI互动次数：%s; \n"
                    .formatted(task.get("name").asText(),
                            task.get("avgScore").asText(),
                            task.get("avgRageOfError").asText(),
                            task.get("avgUsedTime").asText(),
                            task.get("avgAiUsedTimes").asText());
            sb.append(desc);
        });
        CompletableFuture<JsonNode> future = evaluationAgent.evaluate(sb.toString(), "experiment_comparison_analysis", projectId);
        JsonNode difficulty = future.join();
//        System.out.println(difficulty);
        courseNode.set("difficulty", difficulty);

        //学生数据总览[学生]
        // psId
        // 1. 课程目标详情 [{目标名称:a，达成度:a，监测点：[{name:""，value:""}]}]
        // 2. 实验列表[{ptId:"", 总分：a， 得分：a，AI互动次数：1，AI互动记录：[], 过程数据：[], 标签：[{name:""，value:""}]}]
        PSTGroupByStu.forEach((stuId,pstList)->{
            ObjectNode stuNode = objectMapper.createObjectNode();
            ArrayNode targetsNode = objectMapper.createArrayNode();
            Map<Integer, List<CompTargetTagDto>> stuTargetCompMap = CompTargetTagGroupByStu.get(stuId)
                    .stream()
                    .collect(Collectors.groupingBy(CompTargetTagDto::getTargetId));
            stuTargetCompMap.forEach((targetId, compList)->{
                ObjectNode targetNode = objectMapper.createObjectNode();
                double score = compList.stream().filter(c->c.getCompScore()!=null).mapToDouble(CompTargetTagDto::getCompScore).sum();
                double totalScore = compList.stream().filter(c->c.getCompTotalScore()!=null).mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
                double rage = numWith2Decimal(score*100/totalScore);

                ArrayNode targetTagNodes = objectMapper.createArrayNode();
                Map<Long, List<CompTargetTagDto>> stuTargetTagCompMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
                stuTargetTagCompMap.forEach((tagId, list)->{
                    ObjectNode tagNode = objectMapper.createObjectNode();
                    double tagScore = list.stream().filter(c->c.getCompScore()!=null).mapToDouble(CompTargetTagDto::getCompScore).sum();
                    double tagTotalScore = list.stream().filter(c->c.getCompTotalScore()!=null).mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
                    double tagRage = numWith2Decimal(tagScore*100/tagTotalScore);
                    tagNode.put("tagId", tagId);
                    tagNode.put("name", list.get(0).getTagName());
                    tagNode.put("value", tagRage);
                    targetTagNodes.add(tagNode);
                });
                targetNode.put("name",compList.get(0).getTargetName());
                targetNode.put("rage", rage+"%");
                targetNode.set("tags", targetTagNodes);
                targetsNode.add(targetNode);
            });
            ArrayNode stuTasks = objectMapper.createArrayNode();
            Map<Long, PSTDto> stuTaskMap = pstList.stream()
                    .collect(Collectors.toMap(
                            PSTDto::getPtId,
                            Function.identity(),
                            (existing, replacement) -> {
                                throw new IllegalStateException("Duplicate psId: " + existing.getPsId() + "pstId:"+existing.getPstId());
                            }));
            stuTaskMap.forEach((ptId, pst)->{
                double score = numWith2Decimal(pst.getTaskScore()*100/pst.getTaskTotalScore());
                int aiUsedTimes = PSTAIGroupByPt.get(ptId).stream().filter(a-> Objects.equals(a.getStudentId(), stuId)).toList().size();
                Map<Long, List<CompTargetTagDto>> pstTagMap = CompTargetTagGroupByPST.get(pst.getPstId())
                        .stream()
                        .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));

                ArrayNode tagsNode = objectMapper.createArrayNode();
                pstTagMap.forEach((tagId, list)->{
                    double pstTagScore = list.stream().filter(c->c.getCompScore()!=null).mapToDouble(CompTargetTagDto::getCompScore).sum();
                    double pstTagTotalScore = list.stream().filter(c->c.getCompTotalScore()!=null).mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
                    double pstTagRage = numWith2Decimal(pstTagScore*100/pstTagTotalScore);
                    ObjectNode tagNode = objectMapper.createObjectNode();
                    tagNode.put("tagId", tagId);
                    tagNode.put("name", list.get(0).getTagName());
                    tagNode.put("value", pstTagRage+"%");
                    tagsNode.add(tagNode);
                });
                ObjectNode stuTaskNode = objectMapper.createObjectNode();
                stuTaskNode.put("ptId", ptId);
                stuTaskNode.put("psId", pst.getPsId());
                stuTaskNode.put("pstId", pst.getPstId());
                stuTaskNode.put("name", pst.getTaskName());
                stuTaskNode.put("score",score);
                stuTaskNode.put("aiUsedTimes", aiUsedTimes);
                stuTaskNode.set("tags", tagsNode);
                stuTasks.add(stuTaskNode);
            });

            stuNode.put("psId", pstList.get(0).getPsId());
            stuNode.put("studentId", stuId);
            stuNode.set("targets", targetsNode);
            stuNode.set("tasks", stuTasks);
            studentsNode.add(stuNode);
        });

        StringBuilder aiB = new StringBuilder();
        aiB.append("所有学生的提问AI记录：\n");
        PSTAIDtoList.forEach(item->{
            String message = "";
            if(Objects.equals(item.getRole(), "user")){
                message = "Q："+item.getMessage()+"\n";
            }
//            else {
//                message = "A："+item.getMessage()+"\n";
//            }
            aiB.append(message);
        });
        CompletableFuture<JsonNode> thematicFuture = evaluationAgent.evaluate(aiB.toString(), "ai_assisted_teaching_analysis", projectId);
        thematic = thematicFuture.join();
        log.info("[{}] 数据清洗完成", projectId);
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
        jsonObject.set("lastSemester",null);
        Integer lastSemesterProjectId = this.getLastSemesterProject(projectId);
        if(lastSemesterProjectId!=null){
            AnalysisProgress progress = progressMapper.getApLatestSuccessByProjectId(lastSemesterProjectId);
            if(progress != null && progress.getFinished()){
                AnalysisType analysisType = AnalysisType.getByValue(type.getValue());
                if (analysisType != null) {
                    AnalysisProgressData apd = progressMapper.getAPD(progress.getId(), analysisType.getValue());
                    try {
                        jsonObject.set("lastSemester",objectMapper.readTree(apd.getData()));
                    } catch (Exception e) {
                        jsonObject.set("lastSemester",null);
                    }
                }
            }
        }
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
        aiUsedNode.put("label", "AI使用率");

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

        // TODO AI互动主题分析 DONE
        jsonObject.set("thematic", thematic);

        jsonObject.set("lastSemester",null);
        Integer lastSemesterProjectId = this.getLastSemesterProject(projectId);
        if(lastSemesterProjectId!=null){
            AnalysisProgress progress = progressMapper.getApLatestSuccessByProjectId(lastSemesterProjectId);
            if(progress != null && progress.getFinished()){
                AnalysisType analysisType = AnalysisType.getByValue(type.getValue());
                if (analysisType != null) {
                    AnalysisProgressData apd = progressMapper.getAPD(progress.getId(), analysisType.getValue());
                    try {
                        jsonObject.set("lastSemester",objectMapper.readTree(apd.getData()));
                    } catch (Exception e) {
                        jsonObject.set("lastSemester",null);
                    }
                }
            }
        }

        this.saveProgressData(type, progressId, jsonObject);
    }
    private void T_EA_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        ArrayNode jsonNode = objectMapper.createArrayNode();
        PSTGroupByTask.forEach((key,pstList)->{
            HashMap<Long, CompTargetTagDto> taskTagMap = CompTargetTagGroupByPT.get(key).stream()  // 收集 task 中的 tag 列表， tagId 取唯一 <tagId, CompTargetTagDto>
                    .collect(
                            Collectors.toMap(
                                    CompTargetTagDto::getTagId,
                                    com->com,
                                    (com1,com2)->com1,
                                    LinkedHashMap::new)
                    );
            Map<Long, CompTargetTagDto> sortedTaskTagMap = taskTagMap.entrySet().stream()  // 根据tagId 升序
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldVal, newVal) -> oldVal, // 键重复时取旧值
                            LinkedHashMap::new // 用LinkedHashMap存储，保证有序
                    ));
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
            sortedTaskTagMap.values().forEach(com->{
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
        PSTGroupByTask.forEach((ptId, pstList)->{
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
            List<PSTDto> ptStuPSTList = PSTGroupByTaskWithStage.get(ptId)
                    .stream()
                    .filter(Objects::nonNull)
                    // 过滤无效数据：startTime/endTime 不能为 null，且 endTime 不能早于 startTime
                    .filter(pstDto -> pstDto.getStartTime()!=null && pstDto.getEndTime()!=null && pstDto.getEndTime().after(pstDto.getStartTime()))
                    .toList();  // 保证有效值
                // 上述的列表中去除了空值，因此分子是会偏小，为确保结果更接近准确值， 要在分母中也去除空值的对象，由于ptStuPSTList中pst携带了stage的细分，所以分母表示的有效总人数就应该是 ptStuPSTList / stage种类
            Map<Integer, List<PSTDto>> groupByStage = ptStuPSTList.stream().collect(Collectors.groupingBy(PSTDto::getStage));
            // double totalNum = Math.floor(numWith2Decimal((double) ptStuPSTList.size() /groupByStage.size()));
            // 实验操作阶平均用时
            OptionalDouble averageOption = ptStuPSTList.stream()
                    .filter(pstDto->pstDto.getStage().equals(1))
                    // 计算单个对象的时间差（毫秒）：endTime.getTime() - startTime.getTime()
                    .mapToLong(dto -> dto.getEndTime().getTime() - dto.getStartTime().getTime())
                    .filter(res-> res <= 120*60*1000)
                    .average(); // 过滤大于120分钟的无效数据
            // long avgMillis = (long)totalMillisStream.average().getAsDouble();  // 平均用时

            // 平均错误率
            List<CompTargetTagDto> ptStuComp = CompTargetTagGroupByPT.get(ptId);
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
            overviewNode.put("avgMillis", averageOption.isPresent() ? (long) averageOption.getAsDouble() : 0);
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
            Map<Long, List<CompTargetTagDto>> ptTagMap = ptStuComp
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

            objectNode.put("ptId", ptId);
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

         //TODO AI 实验难度对比 DONE
        jsonNode.set("difficulty", courseNode.get("difficulty"));
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
        // 1. 把 ArrayNode 转成 List<ObjectNode> 集合
        List<ObjectNode> nodeList = new ArrayList<>();
        for (JsonNode jsonNode : arrayNode) {
            nodeList.add((ObjectNode) jsonNode);
        }
        // 2. 核心排序：按 ObjectNode 的 age 字段 升序
        nodeList.sort(new Comparator<ObjectNode>() {
            @Override
            public int compare(ObjectNode o1, ObjectNode o2) {
                // 取两个节点的指定字段值，做差值比较实现升序
                long age1 = o1.get("psId").asLong();
                long age2 = o2.get("psId").asLong();
                return (int) (age1 - age2); // 升序：前小后大
            }
        });
        ArrayNode sortedArrayNode = objectMapper.createArrayNode();
        nodeList.forEach(sortedArrayNode::add);
        this.saveProgressData(type, progressId, sortedArrayNode);
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

                    int usedSize = PSTAIGroupByStu.get(stuPst.getStudentId())
                            .stream()
                            .filter(pstaiDto -> pstaiDto.getPtId().equals(stuPst.getPtId()))
                            .toList()
                            .size()/2;
                    aiUsedTimesList.add(usedSize);
                    ug.add(usedSize);
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

            Map<Long, List<CompTargetTagDto>> abilitMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
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
            Map<Long, List<CompTargetTagDto>> abilitMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
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

            Map<Long, List<CompTargetTagDto>> abilitMap = compList.stream().collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
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
//                double rage = numWith2Decimal((double) v*100 /achievementList.size());
                object.put(k,v);
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
    private void T_TR_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        //TODO AI 1.报告生成 2. AI辅助教学分析 DONE
        ObjectNode jsonNode = objectMapper.createObjectNode();

        String desc = courseAgentDes();
        StringBuilder aiB = new StringBuilder();
        aiB.append("课程学生人数：")
                .append(PSTGroupByStu.size())
                .append("；课程实验数量：")
                .append(PSTGroupByTask.size())
                .append("；提供给学生进行AI对话窗口的数量为：")
                .append(PSTGroupByStu.size()*PSTGroupByTask.size());
        aiB.append("。\n所有学生的提问AI记录：\n");
        PSTAIDtoList.forEach(item->{
            String message = "";
            if(Objects.equals(item.getRole(), "user")){
                message = "Q："+item.getMessage()+"\n";
            }
//            else {
//                message = "A："+item.getMessage()+"\n";
//            }
            aiB.append(message);
        });
        CompletableFuture<JsonNode> reportF = evaluationAgent.evaluate(desc, "overall_teaching_effectiveness_report", projectId);
        CompletableFuture<JsonNode> aiAnalysisF = evaluationAgent.evaluate(aiB.toString(), "ai_teaching_effect_analysis", projectId);
        JsonNode report = reportF.join();
        JsonNode aiAnalysis = aiAnalysisF.join();
        jsonNode.set("report", report);
        jsonNode.set("aiAnalysis",aiAnalysis);
        this.saveProgressData(type, progressId, jsonNode);
    }
    private void T_TR_IS(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException{
        //TODO AI 教学改进建议 DONE
        ObjectNode jsonNode = objectMapper.createObjectNode();
        String desc = courseAgentDes()+"；实验难度："+courseNode.get("difficulty").asText();
        CompletableFuture<JsonNode> sugF = evaluationAgent.evaluate(desc, "teaching_improvement_recommendations", projectId);
        JsonNode sug = sugF.join();
        jsonNode.set("suggestion", sug);
        this.saveProgressData(type, progressId, jsonNode);
    }
    private String courseAgentDes(){
        StringBuilder reB = new StringBuilder();
        reB.append("实验得分分布：");
        courseNode.get("tasks").forEach(task->{
            StringBuilder taskDistDesc = new StringBuilder();
            task.get("scoreDistribute").forEach(d->{
                String distDesc = "%s分%s人".formatted(d.get("name").asText(), d.get("value").asText());
                taskDistDesc.append(distDesc);
            });
            String desc = "实验_%s:%s".formatted(task.get("name").asText(), taskDistDesc.toString());
            reB.append(desc);
        });
        reB.append("；AI助教互动总次数%s次,其中".formatted(courseNode.get("ai").get("times")));
        courseNode.get("ai").get("tasks").forEach(task->{
            String desc = "实验_%s:%s次，学生使用率%s".formatted(task.get("name").asText(),
                    task.get("usedNum").asText(),
                    task.get("usedRage").asText());
            reB.append(desc);
        });
        reB.append("；课程成绩分布：");
        courseNode.get("courseGrades").forEach(g->{
            String desc = "%s分%s人".formatted(g.get("name").asText(), g.get("value").asText());
            reB.append(desc);
        });
        reB.append("；课程目标达成度：");
        courseNode.get("courseTargets").forEach(t->{
            String desc = "课程目标：%s，达成度%s".formatted(t.get("name").asText(), t.get("value").asText());
            reB.append(desc);
        });
        reB.append("；整体达成度%s".formatted(courseNode.get("targetRage").asText()));
        return reB.toString();
    }
    private void TASK_D_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        List<AnalysisProgressData> list = new ArrayList<>();
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
                double score = cList.stream()
                        .filter(comp->comp.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .sum();
                double totalScore = cList.stream()
                        .filter(comp->comp.getCompTotalScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompTotalScore)
                        .sum();
                double avg = numWith2Decimal((score/cList.size())*100/(totalScore/cList.size()));
                node.put("stage",stage);
                node.put("label", "阶段%s".formatted(stage));
                node.put("value", avg);
                stageAvgScoreArrayNode.add(node);
            });

            // 实验下学生数据一览
            ArrayNode stuNodes = objectMapper.createArrayNode();
            Map<Long, List<CompTargetTagDto>> taskStuCompMap = ComponentList.stream()
                    .filter(comp->comp.getPtId().equals(ptId))
                    .collect(
                            Collectors.groupingBy(
                                    CompTargetTagDto::getPsId
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
                double stuTotalScore=sCompList.stream()
                        .filter(comp->comp.getCompTotalScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompTotalScore)
                        .sum();
                stuNode.put("score", numWith2Decimal(score*100 / stuTotalScore));
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

                stuNodes.add(stuNode);
            });

            taskNode.set("scoreDistribution", scoreArrayNode);
            taskNode.set("stageAvgScore", stageAvgScoreArrayNode);
            taskNode.set("students", stuNodes);
            try {
                list.add(genAPD(type, progressId, taskNode, ptId, null, null));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        progressMapper.batchCreatAPD(list);
    }
    private void TASK_D_ABILITY(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        List<AnalysisProgressData> list = new ArrayList<>();
        CompTargetTagGroupByPT.forEach((ptId, ptCompList)->{
            Map<Long, List<CompTargetTagDto>> tagCompMap = ptCompList.stream()
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
                        rageD.compute("80-90", (k,v)->v==null?1:v+1);
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
            try {
                list.add(genAPD(type, progressId, abilityAvgRage, ptId, null, null));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        progressMapper.batchCreatAPD(list);
    }
    private void TASK_D_QUES(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        List<AnalysisProgressData> qlist = new ArrayList<>();
        CompTargetTagGroupByPT.forEach((ptId, ptTagList)->{
            ObjectNode ptNode = objectMapper.createObjectNode();

            ArrayNode ptTagArrayNode = objectMapper.createArrayNode();
            int allTagSize = ptTagList.size();
            Map<Long, List<CompTargetTagDto>> tagMap = ptTagList.stream()
                    .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
            tagMap.forEach((tagId, list)->{
                ObjectNode item = objectMapper.createObjectNode();
                item.put("id", tagId);
                item.put("name", list.get(0).getTagName());
                item.put("value", list.size()/PSTGroupByTask.get(ptId).size());
//                item.put("rage", numWith2Decimal((double) list.size()*100/allTagSize));
                ptTagArrayNode.add(item);
            });

            Map<String, List<CompTargetTagDto>> quesMap =  ptTagList.stream()
                    .filter(comp-> !Objects.equals(comp.getCompType(), "TRACELINE") && !Objects.equals(comp.getCompType(), "TABLE"))
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
                item.put("blockLevel", qList.get(0).getBlockLevel());
                item.put("blockOrder", qList.get(0).getBlockOrder());
                item.put("compOrder", qList.get(0).getCompOrder());
                item.put("tagId", qList.get(0).getTagId());
                item.put("tagName", qList.get(0).getTagName());
                item.put("stage", qList.get(0).getCompStage());
                item.put("type", qList.get(0).getCompType());
                item.put("payload",qList.get(0).getCompPayload());
                quesDetailArray.add(item);
            });
            List<ObjectNode> nodeList = new ArrayList<>();
            for (JsonNode jsonNode : quesDetailArray) {
                nodeList.add((ObjectNode) jsonNode);
            }
            // 2. 核心：多字段组合排序（匿名内部类实现Comparator）
            nodeList.sort(new Comparator<ObjectNode>() {
                @Override
                public int compare(ObjectNode o1, ObjectNode o2) {
                    // 第1优先级：age 升序
                    int stage1 = o1.get("stage").asInt();
                    int stage2 = o2.get("stage").asInt();
                    if (stage1 != stage2) {
                        return stage1 - stage2; // 升序：前小后大
                    }

                    // age相同 → 第2优先级：level 降序
                    int level1 = o1.get("blockLevel").asInt();
                    int level2 = o2.get("blockLevel").asInt();
                    if (level1 != level2) {
                        return level1 - level2; //升序：前小后大
                    }

                    // level相同 → 第3优先级：name 升序
                    int blockO1 = o1.get("blockOrder").asInt();
                    int blockO2 = o2.get("blockOrder").asInt();
                    if (blockO1 != blockO2) {
                        return blockO1 - blockO2; //升序：前小后大
                    }

                    int compO1 = o1.get("compOrder").asInt();
                    int compO2 = o2.get("compOrder").asInt();
                    return compO1 - compO2;
                }
            });
            // 3. 排序后的List 转回 ArrayNode
            ArrayNode sortedArrayNode = objectMapper.createArrayNode();
            nodeList.forEach(sortedArrayNode::add);

            ptNode.set("tagOfQuesDistribution", ptTagArrayNode);
            ptNode.set("quesDetail", sortedArrayNode);
            try {
                qlist.add(genAPD(type, progressId, ptNode, ptId, null, null));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        progressMapper.batchCreatAPD(qlist);
    }
    private void TASK_D_COURSE(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        List<AnalysisProgressData> clist = new ArrayList<>();
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
            // TODO Ai辅助交互分析 DONE
            StringBuilder desc = new StringBuilder();
            desc.append("学生提问AI记录：\n");
            PSTAIGroupByPt.get(ptId).forEach(item->{
                String message = "";
                if(Objects.equals(item.getRole(), "user")){
                    message = "Q："+item.getMessage()+"\n";
                }
//                else {
//                    message = "A："+item.getMessage()+"\n";
//                }
                desc.append(message);
            });
            CompletableFuture<JsonNode> aiAnalysisFuture = evaluationAgent.evaluate(desc.toString(), "ai_interaction_evaluation", projectId);
            JsonNode aiAnalysis = aiAnalysisFuture.join();

            taskNode.set("stageTime", stageTime);
            taskNode.set("timeDistributionStage1",timeDistributionStage1);
            taskNode.set("aiAnalysis",aiAnalysis);
            try {
                clist.add(genAPD(type, progressId, taskNode, ptId, null, null));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        progressMapper.batchCreatAPD(clist);
    }
    private void TASK_D_SUG(AnalysisType type, String progressId, Integer projectId){
        //TODO AI 1. 主要表现及AI辅助影响 DONE
        // TODO 教学改进建议 DONE
        List<CompletableFuture<AnalysisProgressData>> singleDataFutureList = new ArrayList<>();
        courseNode.get("tasks").forEach(task->{
            long ptId = task.get("ptId").asLong();
            StringBuilder taskDesc = new StringBuilder();
            taskDesc.append("实验能力标签班级评分分布：");
            task.get("tags").forEach(tag->{
                StringBuilder tagB = new StringBuilder();
                tagB.append(tag.get("name").asText()).append("：");
                tag.get("distribute").forEach(d->{
                    tagB.append("%s分%s人".formatted(d.get("name").asText(), d.get("value").asText()));
                });
                taskDesc.append(tagB);
            });
            StringBuilder aiDesc = new StringBuilder();
            aiDesc.append("学生提问AI记录：");
            PSTAIGroupByPt.get(ptId).forEach(item->{
                String message = "";
                if(Objects.equals(item.getRole(), "user")){
                    message = "Q："+item.getMessage()+"\n";
                }
//                else {
//                    message = "A："+item.getMessage()+"\n";
//                }
                aiDesc.append(message);
            });
            taskDesc.append(aiDesc);

            StringBuilder scoreDesc = new StringBuilder();
            scoreDesc.append("学生实验得分分布：");
            task.get("scoreDistribute").forEach(d->{
                scoreDesc.append("%s分%s人".formatted(d.get("name").asText(), d.get("value").asText()));
            });
            taskDesc.append(scoreDesc);
            CompletableFuture<JsonNode> reportFuture= evaluationAgent.evaluate(taskDesc.toString(), "class_main_performance", projectId);
            CompletableFuture<JsonNode> sugFuture=evaluationAgent.evaluate(taskDesc.toString(), "teaching_improvement_suggestions", projectId);
            CompletableFuture<AnalysisProgressData> futureRes = CompletableFuture.allOf(reportFuture,sugFuture)
                    .thenApply(v->{
                        try{
                            // 获取异步结果
                            JsonNode report= reportFuture.join();
                            JsonNode sug = sugFuture.join();
                            ObjectNode jsonNode = objectMapper.createObjectNode();
                            jsonNode.set("report", report);
                            jsonNode.set("suggestion", sug);
                            return genAPD(type, progressId, jsonNode, ptId, null, null);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("[%s][%s]数据失败：".formatted(type.getDesc(), ptId));
                        }
                    })
                    .exceptionally(e->{ // 单个DTO处理异常兜底（不影响其他任务）
                        log.error("[{}][{}]数据失败：",type.getDesc(), ptId, e);
                        return null;
                    });
            singleDataFutureList.add(futureRes);
        });
        CompletableFuture<?>[] futuresArray = singleDataFutureList.toArray(new CompletableFuture[0]);
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futuresArray);
        try{
            allDone.get(300, TimeUnit.SECONDS);//超时时间 等待所有任务完成
        } catch ( InterruptedException | ExecutionException |TimeoutException e) {
            throw new RuntimeException("[%s]异步任务执行超时/异常".formatted(type.getDesc()), e);
        }

        List<AnalysisProgressData> finalDataList = new ArrayList<>();
        for(CompletableFuture<AnalysisProgressData> future : singleDataFutureList){
            AnalysisProgressData finalData = future.join();
            if (finalData != null) { // 过滤异常的空数据
                finalDataList.add(finalData);
            }
        }
        if (!finalDataList.isEmpty()) {
            progressMapper.batchCreatAPD(finalDataList);
        } else {
            log.error("[{}]无有效数据可保存", type.getDesc());
        }
    }
    private void STU_P_OVERVIEW(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        List<AnalysisProgressData> sList = new ArrayList<>();
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
            Map<Long, List<PSTDto>> stuPtMap = stuList.stream().filter(pst->pst.getStage()==2).collect(Collectors.groupingBy(PSTDto::getPtId));
            // 已完成的实验==》 各个实验的状态
            ArrayNode taskArray = objectMapper.createArrayNode();

            AtomicInteger hasDone = new AtomicInteger(0);  // 已完成的数量
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
            try {
                sList.add(genAPD(type, progressId, stuNode, null, null, stuId));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        progressMapper.batchCreatAPD(sList);
    }
    private void STU_P_TASK(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        List<AnalysisProgressData> sList = new ArrayList<>();
        PSTGroupByStu.forEach((stuId, pstList)->{
            ObjectNode stuNode = objectMapper.createObjectNode();
            ArrayNode taskNode  = objectMapper.createArrayNode();
            Map<Long, List<PSTDto>> stuTaskMap = pstList.stream().collect(Collectors.groupingBy(PSTDto::getPtId));
            stuTaskMap.forEach((ptId, list)->{
                ObjectNode task = objectMapper.createObjectNode();
                Map<Long, List<CompTargetTagDto>> tagMap = CompTargetTagGroupByPT.get(ptId).stream()
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
            try {
                sList.add(genAPD(type, progressId, stuNode, null, null, stuId));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        progressMapper.batchCreatAPD(sList);
    }
    private void STU_P_TARGET(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException {
        List<ObjectNode> sList = new ArrayList<>();

        ArrayNode targetEavNodes = objectMapper.createArrayNode();
        // 计算班级课程目标达成度
        Map<Integer, Double> targetRageMap = new HashMap<>();
        CompTargetTagGroupByTarget.forEach((targetId, compList)->{
            double score = compList.stream()
                    .filter(comp->comp.getCompScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompScore)
                    .sum();
            double totalScore = compList.stream()
                    .filter(comp->comp.getCompTotalScore()!=null)
                    .mapToDouble(CompTargetTagDto::getCompTotalScore)
                    .sum();
            double rage = numWith2Decimal(score*100/totalScore);
            targetRageMap.put(targetId, rage);
        });
        // 每个学生
        CompTargetTagGroupByStu.forEach((stuId, compList)->{
            ObjectNode stuNode = objectMapper.createObjectNode();
            ArrayNode targetRageArray = objectMapper.createArrayNode();
            ObjectNode trendObject= objectMapper.createObjectNode(); // 趋势分析

            Map<Integer, List<CompTargetTagDto>> stuTargetMap = compList.stream()
                    .filter(comp->comp.getTagId()!=null)
                    .collect(Collectors.groupingBy(CompTargetTagDto::getTargetId));

            stuTargetMap.forEach((targetId, list)->{
                ObjectNode targetNode = objectMapper.createObjectNode();
                double score =list.stream()
                        .filter(c->c.getCompScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompScore)
                        .sum();

                double totalScore =list.stream()
                        .filter(c->c.getCompTotalScore()!=null)
                        .mapToDouble(CompTargetTagDto::getCompTotalScore)
                        .sum();

                double rage = numWith2Decimal(score*100/totalScore);

                // 分析监测点
                Map<Long, List<CompTargetTagDto>> targetTagMap = list.stream()
                        .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));

                ArrayNode tagArray = objectMapper.createArrayNode();
                targetTagMap.forEach((tagId, l)->{
                    ObjectNode tagNode = objectMapper.createObjectNode();
                    double s = l.stream()
                            .filter(c->c.getCompScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompScore)
                            .sum();
                    double ts = l.stream()
                            .filter(c->c.getCompTotalScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompTotalScore)
                            .sum();
                    double tagRage = numWith2Decimal(s*100/ts);

                    tagNode.put("tagId",tagId);
                    tagNode.put("tagName",l.get(0).getTagName());
                    tagNode.put("rage", tagRage);
                    tagArray.add(tagNode);
                });
                targetNode.put("targetId", targetId);
                targetNode.put("targetName", list.get(0).getTargetName());
                targetNode.put("rage", rage);
                targetNode.put("classRage", targetRageMap.get(targetId));
                targetNode.set("tag", tagArray);
                StringBuilder targetDesc = new StringBuilder();
                targetDesc.append("课程目标1达成度").append(rage).append("%,对应监测点及其评分为:");
                tagArray.forEach(tagNode->{
                    targetDesc.append(tagNode.get("tagName").asText()).append(tagNode.get("rage").asText()).append("%");
                });
                targetRageArray.add(targetNode);
                ObjectNode targetEvaNode = objectMapper.createObjectNode();
                targetEvaNode.put("stuId",stuId);
                targetEvaNode.put("targetId",targetId);
                targetEvaNode.put("desc",targetDesc.toString());
                targetEavNodes.add(targetEvaNode);
                // 目标达成度趋势分析
                ArrayNode trendNode = objectMapper.createArrayNode();
                List<Long> ptIdList = list.stream()
                        .map(CompTargetTagDto::getPtId)
                        .distinct()
                        .toList();
                List<Long> ptIdList2 = new ArrayList<>();
                ObjectNode object0 = objectMapper.createObjectNode();
                object0.put("id",0);
                object0.put("label","学期初");
                object0.put("name","学期初");
                object0.put("value",0);
                trendNode.add(object0);
                for(int i=0;i<ptIdList.size(); i++){
                    // 实验数量递增
                    ptIdList2.add(ptIdList.get(i));
                    // 计算达成度
                    List<CompTargetTagDto> comps = list.stream()  // target 的所有 comp
                            .filter(comp-> ptIdList2.contains(comp.getPtId()))
                            .filter(comp->comp.getCompScore()!=null)
                            .toList();
                    // 获取第i个实验的实验名称
                    int finalI = i;
                    List<CompTargetTagDto> comps2 = list.stream()
                            .filter(comp-> Objects.equals(comp.getPtId(), ptIdList.get(finalI)))
                            .toList();
                    String taskName = comps2.get(0).getPtName();
                    double curScore = comps.stream().mapToDouble(CompTargetTagDto::getCompScore).sum();
                    double trend = numWith2Decimal(curScore*100 / totalScore);
                    ObjectNode object = objectMapper.createObjectNode();
                    object.put("id",i+1);
                    object.put("label","第%s次实验".formatted(i+1));
                    object.put("name",taskName);
                    object.put("value",trend);
                    trendNode.add(object);
                }
                double trend = numWith2Decimal(score*100 / totalScore);
                ObjectNode object1 = objectMapper.createObjectNode();
                object1.put("id",ptIdList.size()+1);
                object1.put("label","当前");
                object1.put("name","当前");
                object1.put("value",trend);
                trendNode.add(object1);
                trendObject.set(targetId.toString(), trendNode);
            });

            stuNode.put("stuId",stuId);
            stuNode.set("target", targetRageArray);
            stuNode.set("trend", trendObject);
            sList.add(stuNode);
//                sList.add(genAPD(type, progressId, stuNode, null, null, stuId));

        });
        // TODO 课程目标表现详情概述
        List<CompletableFuture<ObjectNode>> futureList = new ArrayList<>();
        targetEavNodes.forEach(stuTarget->{
            CompletableFuture<JsonNode> futureStr = evaluationAgent.evaluate(stuTarget.get("desc").asText(), "student_course_objective_analysis", projectId);
            CompletableFuture<ObjectNode> singleFinalFuture = CompletableFuture.allOf(futureStr)
                    // 结果返回后，执行组装（异步执行）
                    .thenApply(v -> {
                        try {
                            // 获取异步结果（join不抛检查异常）
                            JsonNode res = futureStr.join();
                            // 组装当前DTO的最终数据
                            ObjectNode jsonNode = objectMapper.createObjectNode();
                            jsonNode.put("stuId", stuTarget.get("stuId").asText());
                            jsonNode.put("targetId", stuTarget.get("targetId").asText());
                            jsonNode.set("res", res);
                            return jsonNode;
                        } catch (Exception e) {
                            throw new RuntimeException("[%s][student:%s][target:%s]数据失败："
                                    .formatted(type.getDesc(), stuTarget.get("stuId").asText(), stuTarget.get("targetId").asText()), e);
                        }
                    })
                    // 单个DTO处理异常兜底（不影响其他任务）
                    .exceptionally(e -> {
                        log.error("[{}}][{}}]数据失败：",type.getDesc(), stuTarget.get("stuId").asText(), e);
                        // 可返回空值/默认值，或抛异常终止整体流程
                        return null;
                    });
            futureList.add(singleFinalFuture);
        });
        CompletableFuture<?>[] futuresArray = futureList.toArray(new CompletableFuture[0]);
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futuresArray);
        try {
            // 超时时间根据业务调整（比如总耗时不超过10秒）等待任务结束
            allDone.get(600, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
            throw new RuntimeException("[%s]异步任务执行超时/异常".formatted(type.getDesc()), e);
        }
        List<ObjectNode> finalDataList = new ArrayList<>();
        for (CompletableFuture<ObjectNode> future : futureList){
            if(future!=null){
                finalDataList.add(future.join());
            }
        }
        for (ObjectNode targetNode : finalDataList) {
            if(targetNode!=null){
                for(ObjectNode stuNode : sList){
                    ArrayNode stuTargetNodes = (ArrayNode) stuNode.get("target");
                    for(JsonNode stuTargetNode:stuTargetNodes){
                        if(Objects.equals(targetNode.get("stuId").asText(), stuNode.get("stuId").asText())
                                && targetNode.get("targetId").asInt() == stuTargetNode.get("targetId").asInt() ){
                            if (stuTargetNode instanceof ObjectNode) {
                                // 本身已是 ObjectNode，直接强转
                                ((ObjectNode) stuTargetNode).put("desc",targetNode.get("res").asText());
                            }
                        }
                    }
                }
            }
        }

        List<AnalysisProgressData> finalList = new ArrayList<>();
        sList.forEach(node->{
            try {
                finalList.add( genAPD(type,progressId, node, null, null, node.get("stuId").asText()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        progressMapper.batchCreatAPD(finalList);



    }
    private void STU_P_SUG(AnalysisType type, String progressId, Integer projectId){
        //TODO AI 课程学习反馈 DONE
        // TODO AI 能力提升计划 DONE
        List<CompletableFuture<AnalysisProgressData>> singleDataFutureList = new ArrayList<>();
        studentsNode.forEach(student->{
            StringBuilder stuDesc = new StringBuilder();
            stuDesc.append("实验得分与AI互动次数：");
            student.get("tasks").forEach(task->{
                stuDesc.append("实验_%s %s分 AI互动%s次".formatted(task.get("name").asText(),
                        task.get("score").asText(),
                        task.get("aiUsedTimes").asText()));
            });

            stuDesc.append("课程目标达成度：");
            student.get("targets").forEach(target->{
                stuDesc.append("课程目标：%s 达成度%s".formatted(target.get("name").asText(), target.get("rage").asText()));
            });
            // 2.1 并行发起两次异步调用（同一函数，不同参数）
            CompletableFuture<JsonNode> res1Future = evaluationAgent.evaluate(stuDesc.toString(),"student_course_feedback", projectId);
            CompletableFuture<JsonNode> res2Future = evaluationAgent.evaluate(stuDesc.toString(),"student_course_plan",projectId);
            // 2.2 等待当前DTO的两个异步结果返回后，组装数据（异步组装，不阻塞循环）
            CompletableFuture<AnalysisProgressData> singleFinalFuture = CompletableFuture.allOf(res1Future, res2Future)
                    // 两个结果都返回后，执行组装（异步执行）
                    .thenApply(v -> {
                        try {
                            // 获取两个异步结果（join不抛检查异常）
                            JsonNode res1 = res1Future.join();
                            JsonNode res2 = res2Future.join();
                            // 组装当前DTO的最终数据
                            ObjectNode jsonNode = objectMapper.createObjectNode();
                            jsonNode.set("report", res1);
                            jsonNode.set("suggestion", res2);
                            // todo 取tag达成度低于80的
                            Map<Long, Double> stuTagGrade = new HashMap<>();
                            CompTargetTagGroupByStu.get(student.get("studentId").asText()).stream()
                                    .filter(comp -> comp.getTagId() != null && comp.getCompStatus().equals(1))
                                    .collect(Collectors.groupingBy(CompTargetTagDto::getTagId))
                                    .forEach((tageId, list)->{
                                        double total = list.stream().mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
                                        double score  = list.stream().mapToDouble(CompTargetTagDto::getCompScore).sum();
                                        stuTagGrade.put(tageId, numWith2Decimal(score*100/total));
                                    });
                            List<Long> sortedTagByTagGrade = stuTagGrade.entrySet()
                                    .stream()
                                    // 按value升序排序（若需降序，替换为 comparingDouble(Entry::getValue).reversed()）
                                    .sorted(Comparator.comparing(Map.Entry::getValue))
                                    // 提取排序后的key
                                    .map(Map.Entry::getKey)
                                    // 转换为List
                                    .toList();
                            ArrayNode arrayNode = objectMapper.createArrayNode();
                            for(int i=0; i<sortedTagByTagGrade.size(); i++){
                                if(stuTagGrade.get(sortedTagByTagGrade.get(i))<80){
                                    JsonNode linkNode = objectMapper.valueToTree(tagLinkMap.get(sortedTagByTagGrade.get(i)));

                                    arrayNode.add(linkNode);
                                }else {
                                    if(arrayNode.size()<2){
                                        JsonNode linkNode = objectMapper.valueToTree(tagLinkMap.get(sortedTagByTagGrade.get(i)));
                                        arrayNode.add(linkNode);
                                    }
                                }
                            }
                            jsonNode.set("learn", arrayNode);

                            return genAPD(type, progressId, jsonNode, null, null, student.get("studentId").asText());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("[%s][%s]数据失败：".formatted(type.getDesc(), student.get("studentId").asText()), e);
                        }
                    })
                    // 单个DTO处理异常兜底（不影响其他任务）
                    .exceptionally(e -> {
                        log.error("[{}}][{}}]数据失败：",type.getDesc(), student.get("studentId").asText(), e);
                        // 可返回空值/默认值，或抛异常终止整体流程
                        return null;
                    });
            // 2.3 收集当前DTO的最终组装结果Future
            singleDataFutureList.add(singleFinalFuture);
        });

        // 3. 等待所有DTO的异步处理完成（批量等待，非逐个阻塞）
        // 3.1 转换为数组，方便allOf处理
        CompletableFuture<?>[] futuresArray = singleDataFutureList.toArray(new CompletableFuture[0]);
        // 3.2 等待所有异步任务完成（设置超时，避免无限等待）
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futuresArray);
        try {
            // 超时时间根据业务调整（比如总耗时不超过10秒）
            allDone.get(600, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
            throw new RuntimeException("[%s]异步任务执行超时/异常".formatted(type.getDesc()), e);
        }

        // 4. 收集所有组装完成的有效数据
        List<AnalysisProgressData> finalDataList = new ArrayList<>();
        for (CompletableFuture<AnalysisProgressData> future : singleDataFutureList) {
            AnalysisProgressData finalData = future.join();
            if (finalData != null) { // 过滤异常的空数据
                finalDataList.add(finalData);
            }
        }
        // 5. 批量保存（核心：只执行一次批量操作）
        if (!finalDataList.isEmpty()) {
            progressMapper.batchCreatAPD(finalDataList);
        } else {
            log.error("[{}]无有效数据可保存", type.getDesc());
        }
    }
    private void PST_DETAIL(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException, IllegalStateException {
        List<AnalysisProgressData> sList = new ArrayList<>();
        PSTGroupByTask.forEach((ptId, taskPstList)->{
//            ObjectNode taskNode = objectMapper.createObjectNode();
            Map<Long, PSTDto> taskStuPstMap = taskPstList.stream()
                    .collect(Collectors.toMap(
                            PSTDto::getPsId,
                            Function.identity(),
                            (existing, replacement) -> {
                                throw new IllegalStateException("Duplicate psId: " + existing.getPsId() + "pstId:"+existing.getPstId());
                    }));

            taskStuPstMap.forEach((psId, pst)->{
                ObjectNode stuNode = objectMapper.createObjectNode();
                List<CompTargetTagDto> pstCompList = CompTargetTagGroupByPST.get(pst.getPstId());
                Map<Long, List<CompTargetTagDto>> pstTagMap = pstCompList.stream()
                        .filter(c->c.getTagId()!=null)
                        .collect(Collectors.groupingBy(CompTargetTagDto::getTagId));
                Map<Integer, List<CompTargetTagDto>> pstStageMap = pstCompList.stream()
                        .filter(c->c.getCompStage()!=null)
                        .collect(Collectors.groupingBy(CompTargetTagDto::getCompStage));

                ArrayNode tagArray = objectMapper.createArrayNode();
                pstTagMap.forEach((tagId, cList)->{
                    ObjectNode tagNode = objectMapper.createObjectNode();
                    double score = cList.stream()
                            .filter(c->c.getCompScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompScore)
                            .sum();
                    double totalScore = cList.stream()
                            .filter(c->c.getCompTotalScore()!=null)
                            .mapToDouble(CompTargetTagDto::getCompTotalScore)
                            .sum();
                    double rage=numWith2Decimal(score*100/totalScore);

                    tagNode.put("tagId",tagId);
                    tagNode.put("tagName", cList.get(0).getTagName());
                    tagNode.put("rage", rage);
                    tagNode.put("size", cList.size());
                    tagArray.add(tagNode);
                });
                ArrayNode stageArray = objectMapper.createArrayNode();
                pstStageMap.forEach((stage, cList)->{
                    ObjectNode stageNode = objectMapper.createObjectNode();
                    int errorSize = cList.stream()
                            .filter(c->c.getCompScore()==null||c.getCompScore()<c.getCompTotalScore())
                            .toList()
                            .size();

                    stageNode.put("stage", stage);
                    stageNode.put("quesSize", cList.size());
                    stageNode.put("quesErrorSize", errorSize);
                    stageNode.put("quesCorrectSize", cList.size()-errorSize);
                    List<CompTargetTagDto> sortedComp = cList.stream()
                            .filter(comp-> !Objects.equals(comp.getCompType(), "TRACELINE") && !Objects.equals(comp.getCompType(), "TABLE"))
                            .sorted(Comparator.comparing(CompTargetTagDto::getCompStage)
                                    .thenComparing(CompTargetTagDto::getBlockLevel)
                                    .thenComparing(CompTargetTagDto::getBlockOrder)
                                    .thenComparing(CompTargetTagDto::getCompOrder)
                            ).collect(Collectors.toList());
                    stageNode.set("quesList", objectMapper.valueToTree(sortedComp));
                    stageArray.add(stageNode);
                });
                stuNode.set("tag",tagArray);
                stuNode.set("stage", stageArray);
//                taskNode.set(psId.toString(), stuNode);
                try {
                    sList.add(genAPD(type, progressId, stuNode, ptId, psId, null));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        progressMapper.batchCreatAPD(sList);
    }
    private void PST_SUG(AnalysisType type, String progressId, Integer projectId) throws JsonProcessingException{
        //TODO AI pst改进建议 DONE
        List<CompletableFuture<AnalysisProgressData>> futureList = new ArrayList<>();
        studentsNode.forEach(student->{
            long psId = student.get("psId").asLong();
            student.get("tasks").forEach(stuTask->{
                long ptId = stuTask.get("ptId").asLong();
                StringBuilder pstDesc = new StringBuilder();
                pstDesc.append("实验能力标签评分：");
                stuTask.get("tags").forEach(tag->{
                    pstDesc.append("%s%s".formatted(tag.get("name").asText(), tag.get("value").asText()));
                });
                pstDesc.append("学生提问AI记录：");
                List<PSTAIDto> stuAiRecord = PSTAIGroupByStu.get(student.get("studentId").asText());
                if(stuAiRecord!=null){
                    List<PSTAIDto> taskAiRecord = stuAiRecord.stream().filter(r->r.getPtId().equals(ptId)).toList();
                    if(!taskAiRecord.isEmpty()){
                        taskAiRecord.forEach(pstaiDto->{
                            if(Objects.equals(pstaiDto.getRole(), "user")){
                                pstDesc.append("Q:").append(pstaiDto.getMessage());
                            }
//                            else{
//                                pstDesc.append("A:").append(pstaiDto.getMessage());
//                            }
                        });
                    }else {
                        pstDesc.append("无");
                    }
                }else{
                    pstDesc.append("无");
                }
                CompletableFuture<JsonNode> strFuture = evaluationAgent.evaluate(pstDesc.toString(), "student_single",projectId);
                CompletableFuture<AnalysisProgressData> singleFinalFuture = CompletableFuture.allOf(strFuture)
                        // 两个结果都返回后，执行组装（异步执行）
                        .thenApply(v -> {
                            try {
                                // 获取异步结果（join不抛检查异常）
                                JsonNode res1 = strFuture.join();
                                // 组装当前DTO的最终数据
                                ObjectNode jsonNode = objectMapper.createObjectNode();
                                jsonNode.set("suggestion", res1);

                                Map<Long, Double> stuTagGrade = new HashMap<>();
                                CompTargetTagGroupByStu.get(student.get("studentId").asText()).stream()
                                        .filter(comp->comp.getPtId()==ptId)
                                        .filter(comp -> comp.getTagId() != null && comp.getCompStatus().equals(1))
                                        .collect(Collectors.groupingBy(CompTargetTagDto::getTagId))
                                        .forEach((tageId, list)->{
                                            double total = list.stream().mapToDouble(CompTargetTagDto::getCompTotalScore).sum();
                                            double score  = list.stream().mapToDouble(CompTargetTagDto::getCompScore).sum();
                                            stuTagGrade.put(tageId, numWith2Decimal(score*100/total));
                                        });
                                List<Long> sortedTagByTagGrade = stuTagGrade.entrySet()
                                        .stream()
                                        // 按value升序排序（若需降序，替换为 comparingDouble(Entry::getValue).reversed()）
                                        .sorted(Comparator.comparing(Map.Entry::getValue))
                                        // 提取排序后的key
                                        .map(Map.Entry::getKey)
                                        // 转换为List
                                        .toList();
                                ArrayNode arrayNode = objectMapper.createArrayNode();
                                for(int i=0; i<sortedTagByTagGrade.size(); i++){
                                    if(stuTagGrade.get(sortedTagByTagGrade.get(i))<80){
                                        JsonNode linkNode = objectMapper.valueToTree(tagLinkMap.get(sortedTagByTagGrade.get(i)));
                                        arrayNode.add(linkNode);
                                    }else {
                                        if(arrayNode.size()<2){
                                            JsonNode linkNode = objectMapper.valueToTree(tagLinkMap.get(sortedTagByTagGrade.get(i)));
                                            arrayNode.add(linkNode);
                                        }
                                    }
                                }
                                jsonNode.set("learn", arrayNode);


                                return genAPD(type, progressId, jsonNode, ptId, psId, null);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException("[%s][student:%s][task:%s]数据失败："
                                        .formatted(type.getDesc(), student.get("studentId").asText(), stuTask.get("name").asText()), e);
                            }
                        })
                        // 单个DTO处理异常兜底（不影响其他任务）
                        .exceptionally(e -> {
                            log.error("[{}}][{}}]数据失败：",type.getDesc(), student.get("studentId"), e);
                            // 可返回空值/默认值，或抛异常终止整体流程
                            return null;
                        });
                futureList.add(singleFinalFuture);
            });
        });
        CompletableFuture<?>[] futuresArray = futureList.toArray(new CompletableFuture[0]);
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futuresArray);
        try {
            // 超时时间根据业务调整（比如总耗时不超过10秒）
            allDone.get(600, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
            throw new RuntimeException("[%s]异步任务执行超时/异常".formatted(type.getDesc()), e);
        }

        // 4. 收集所有组装完成的有效数据
        List<AnalysisProgressData> finalDataList = new ArrayList<>();
        for (CompletableFuture<AnalysisProgressData> future : futureList) {
            AnalysisProgressData finalData = future.join();
            if (finalData != null) { // 过滤异常的空数据
                finalDataList.add(finalData);
            }
        }
        // 5. 批量保存（核心：只执行一次批量操作）
        if (!finalDataList.isEmpty()) {
            progressMapper.batchCreatAPD(finalDataList);
        } else {
            log.error("[{}]无有效数据可保存", type.getDesc());
        }
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
                case TASK_D_OVERVIEW -> {
                    this.TASK_D_OVERVIEW(analysisType, progressId, progetId);
                }
                case TASK_D_ABILITY -> {
                    this.TASK_D_ABILITY(analysisType, progressId, progetId);
                }
                case TASK_D_QUES -> {
                    this.TASK_D_QUES(analysisType, progressId, progetId);
                }
                case TASK_D_COURSE -> {
                    this.TASK_D_COURSE(analysisType, progressId, progetId);
                }
                case TASK_D_SUG -> {
                    this.TASK_D_SUG(analysisType, progressId, progetId);
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
                case PST_DETAIL -> {
                    this.PST_DETAIL(analysisType, progressId, progetId);
                }
                case PST_SUG -> {
                    this.PST_SUG(analysisType, progressId, progetId);
                }
                default -> throw new AnalysisProgressGenChildDataException("没有对应方法");
            }
        }catch(Exception e){
            log.error("[{}] 任务异常", analysisType.getDesc());
            throw new AnalysisProgressGenChildDataException(e.getMessage());
        }
    }
    private Integer getLastSemesterProject(Integer projectId){
        Project project = projectService.findProjectById(projectId);
        try{
            long semester = Long.parseLong(project.getSemester());
            long lastSemester;
            if(semester%2==0){ //偶数
                lastSemester = semester-1;
            }else{
                lastSemester= semester-100;
            }
            return dataSourceMapper.getLastSemesterProjectId(project.getCaseId(), project.getCreator(), String.valueOf(lastSemester));
        } catch (NumberFormatException e) {
            return null;
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

    private void saveProgressData(AnalysisType type, String progressId, JsonNode data)
            throws JsonProcessingException {
        AnalysisProgressData apd = new AnalysisProgressData();
        apd.setId(UUIDGenerator.generateUUID());
        apd.setApId(progressId);
        apd.setType(type.getValue());
        apd.setData(objectMapper.writeValueAsString(data));
        progressMapper.createAPD(apd);
    }

    private AnalysisProgressData genAPD(AnalysisType type, String progressId, JsonNode data,Long ptId, Long psId, String studentId)
            throws JsonProcessingException{
        AnalysisProgressData apd = new AnalysisProgressData();
        apd.setId(UUIDGenerator.generateUUID());
        apd.setApId(progressId);
        apd.setType(type.getValue());
        apd.setData(objectMapper.writeValueAsString(data));
        apd.setPtId(ptId);
        apd.setPsId(psId);
        apd.setStudentId(studentId);
        return apd;
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
