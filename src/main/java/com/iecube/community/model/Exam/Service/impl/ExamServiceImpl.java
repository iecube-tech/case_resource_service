package com.iecube.community.model.Exam.Service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.Exam.Dto.ExamWithStudentDto;
import com.iecube.community.model.Exam.Dto.QuesType;
import com.iecube.community.model.Exam.Dto.QuestionDto;
import com.iecube.community.model.Exam.Service.ExamService;
import com.iecube.community.model.Exam.entity.*;
import com.iecube.community.model.Exam.exception.ExcelCellParseException;
import com.iecube.community.model.Exam.mapper.ExamMapper;
import com.iecube.community.model.Exam.qo.ExamSaveQo;
import com.iecube.community.model.Exam.vo.*;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.util.list.ListUtil;
import com.iecube.community.util.random.RandomUtil;
import com.iecube.community.util.uuid.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

    // 日期格式化器
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    // 存储单元格解析异常信息（key: sheet名称, value: 异常信息列表）
    private static final Map<String, List<String>> cellParseErrors = new HashMap<>();

    private static final Map<String, List<String>> quesParseErrors = new HashMap<>();

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String FLAG="EXAM_EXCEL_PARSE_";

    @Value("${resource-location}/file")
    private String fileLocation;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExamMapper examMapper;

    public ExamServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //解析EXCEL // 返回vo对象
    @Override
    public ExamParseVo parseExcel(Integer projectId, String filename){
        resetErrors();
        File excel = new File(fileLocation  + File.separator + filename);
        if(!excel.exists()){
            throw new ServiceException("文件不存在");
        }
        try (
                InputStream is = new FileInputStream(excel);
                Workbook workbook = WorkbookFactory.create(is)
        ) {
            ExamInfoEntity exam = null;
            List<QuestionDto> questionDtoList = new ArrayList<>();
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++){
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = sheet.getSheetName();
                if(sheetName.equals("考试信息")){
                    exam =parseExamInfoSheet(sheet);
                    exam.setProjectId(projectId);
                }else {
                    List<QuestionDto> questions = parseQuesSheet(sheet);
                    if (questions != null && !questions.isEmpty()) {
                        questionDtoList.addAll(questions);
                    }
                }
            }
//            System.out.println(exam);
//            questionDtoList.forEach(q->{
//                System.out.println(q.toString());
//            });
            checkCellParseErrors();
            // 需求中有多个题目随机出几道的场景
            // 整理为vo 处理总分 以及 随机规则
            Map<QuesType, List<ExamParseVo.QuesVo>> questions = new HashMap<>();
            questionDtoList.stream()
                    .collect(Collectors.groupingBy(QuestionDto::getType))
                    .forEach((type,list)->{
                        List<ExamParseVo.QuesVo> quesVoList = new ArrayList<>();
                        // 非随机题目
                        list.stream().filter(q->!q.getIsRandom()).forEach(q->{
                            String quesVoId = UUIDGenerator.generateUUID();
                            QuesContentEntity ques = new QuesContentEntity();
                            ques.setId(UUIDGenerator.generateUUID());
                            ques.setPId(quesVoId);
                            ques.setTitle(q.getQuestion());
                            ques.setOptions(q.getOptions());
                            ques.setAnswer(q.getAnswer());
                            ques.setKnowledge(q.getKnowledge());

                            ExamParseVo.QuesVo<QuesContentEntity> quesVo = new ExamParseVo.QuesVo<>();
                            quesVo.setId(quesVoId);
                            quesVo.setQuesType(type);
                            quesVo.setScore(q.getScore());
                            quesVo.setOrder(q.getOrder());
                            quesVo.setDifficulty(q.getDifficulty());
                            quesVo.setIsRandom(false);
                            quesVo.setRandomNum(1);
                            quesVo.setQuesContent(ques);

                            quesVoList.add(quesVo);
                        });

                        // 随机题目
                        Map<Integer, List<QuestionDto>> randomTypeMap = list.stream()
                                .filter(QuestionDto::getIsRandom)
                                .collect(Collectors.groupingBy(QuestionDto::getRandomType));
                        randomTypeMap.forEach((rType, rList)->{
                            OptionalInt maxRNum = rList.stream().mapToInt(QuestionDto::getRandomNumber).max(); //  题目数量 取最大值
                            OptionalDouble maxScore = rList.stream().mapToDouble(QuestionDto::getScore).max(); // 题目分数 取最大值
                            OptionalInt maxDifficulty = rList.stream().mapToInt(QuestionDto::getDifficulty).max(); // 题目难度 取最大值
                            // 检查分数
                            if(!ListUtil.isAllAttributesSame(rList, QuestionDto::getScore)){
                                // 同一个随机规则内的题目分数不相同
                                String errStr = "[%s]：随机规则[%s]中的题目分数不相等(%s)；".formatted(
                                        type.getDescription(),
                                        rType,
                                        Arrays.toString(rList.stream().mapToDouble(QuestionDto::getScore).toArray()));
                                quesParseErrors.computeIfAbsent(type.getDescription(), k->new ArrayList<>());
                                quesParseErrors.get(type.getDescription()).add(errStr);
                            }
                            if(!ListUtil.isAllAttributesSame(rList, QuestionDto::getDifficulty)){
                                // 同一个随机规则内的题目难度不相同
                                String errStr = "[%s]：随机规则[%s]中的题目难度不相等(%s)；".formatted(
                                        type.getDescription(),
                                        rType,
                                        Arrays.toString(rList.stream().mapToInt(QuestionDto::getDifficulty).toArray()));
                                quesParseErrors.computeIfAbsent(type.getDescription(), k->new ArrayList<>());
                                quesParseErrors.get(type.getDescription()).add(errStr);
                            }

                            String quesVoId = UUIDGenerator.generateUUID();
                            List<QuesContentEntity> quesList = new ArrayList<>();
                            rList.forEach(q->{
                                QuesContentEntity ques = new QuesContentEntity();
                                ques.setId(UUIDGenerator.generateUUID());
                                ques.setPId(quesVoId);
                                ques.setTitle(q.getQuestion());
                                ques.setOptions(q.getOptions());
                                ques.setAnswer(q.getAnswer());
                                ques.setKnowledge(q.getKnowledge());
                                quesList.add(ques);
                            });
                            ExamParseVo.QuesVo<List<QuesContentEntity>> quesVo = new ExamParseVo.QuesVo<>();
                            quesVo.setQuesType(type);
                            quesVo.setId(quesVoId);
                            quesVo.setScore(maxScore.isPresent()?maxScore.getAsDouble():0);
                            quesVo.setIsRandom(true);
                            quesVo.setRandomNum(maxRNum.isPresent()?maxRNum.getAsInt():0);
                            quesVo.setDifficulty(maxDifficulty.isPresent()?maxDifficulty.getAsInt():0);
                            quesVo.setQuesContent(quesList);
                            quesVo.setOrder(rList.get(0).getOrder());
                            quesVoList.add(quesVo);
                        });
                        questions.computeIfAbsent(type, k->new ArrayList<>());
                        questions.get(type).addAll(quesVoList);
//                        questions.get(type).sort(Comparator.comparingInt(ExamParseVo.QuesVo::getOrder));
                    });
            //计算总分
            AtomicReference<Double> totalScore = new AtomicReference<>((double) 0);
            questions.forEach((type, list)->{
                if(list!=null && !list.isEmpty()){
                    list.sort(Comparator.comparingInt(ExamParseVo.QuesVo::getOrder));
                    list.forEach(q->{
                        totalScore.updateAndGet(v ->v + q.getRandomNum() * q.getScore());
                    });
                }
            });
//            System.out.println(questions.toString());
            if (exam != null && !Objects.equals(totalScore.get(), exam.getTotalScore())) {
                // 题目总分和考试信息总分不匹配
                String errStr = "[总分]：考试信息中的设计总分[%s]与题目分数之和[%s]不相等；".formatted(exam.getTotalScore(), totalScore.get());
                quesParseErrors.computeIfAbsent("总分", k->new ArrayList<>());
                quesParseErrors.get("总分").add(errStr);
            }
            checkQuesParseErrors();
            ExamParseVo examParseVo = new ExamParseVo();
            examParseVo.setExam(exam);
            examParseVo.setQuestions(questions);
            examParseVo.setId(UUIDGenerator.generateUUID());
            redisTemplate.opsForValue().set(FLAG+examParseVo.getId(), examParseVo, 7, TimeUnit.DAYS);
            return examParseVo;
        } catch (IOException e) {
            log.error("解析EXCEL文件异常：{}",e.getMessage(),e);
            throw new ServiceException(e);
        }
    }

    @Override
    public Long savaExam(ExamSaveQo qo, Integer currentUser){
        Object data = redisTemplate.opsForValue().get(FLAG+qo.getParseId());
        if(data==null){
            throw new ServiceException("解析数据已过期或不存在，请重新上传并解析");
        }
        ExamParseVo examParseVo = (ExamParseVo) data;

        // 存储examInfoEntity 返回id
        ExamInfoEntity examInfoEntity = examParseVo.getExam();
        examInfoEntity.setProjectId(qo.getProjectId());
        examInfoEntity.setCreator(currentUser);
        examInfoEntity.setCreateTime(new Date());
        examInfoEntity.setLastModifiedTime(new Date());
        examInfoEntity.setLastModifiedUser(currentUser);
        examInfoEntity.setUseRandomOption(qo.isUseRandomOption());
        examInfoEntity.setUseRandomQuestion(qo.isUseRandomQuestion());
        examInfoEntity.setAiAutoCheck(qo.isAiAutoCheck());
        int res = examMapper.insertExamEntity(examInfoEntity);
        if(res!=1){
            throw new InsertException("保存数据异常：exam entity");
        }
        // 存储questions batchInsert
        List<QuestionEntity> questionEntityList = new ArrayList<>();
        List<QuesContentEntity> quesContentEntityList = new ArrayList<>();

        examParseVo.getQuestions().values().forEach(qL->{
            qL.forEach(q->{
                questionEntityList.add(QuesVoToQuestionEntity(q));
                if(q.getIsRandom()){
                    @SuppressWarnings("unchecked")
                    List<QuesContentEntity> quesContentList = (List<QuesContentEntity>) q.getQuesContent();
                    quesContentEntityList.addAll(quesContentList);
                }else {
                    QuesContentEntity quesContent = (QuesContentEntity) q.getQuesContent();
                    quesContentEntityList.add(quesContent);
                }
            });
        });

        if(!questionEntityList.isEmpty()){
            questionEntityList.forEach(q->{
                q.setPId(examInfoEntity.getId());
            });
            int res1 = examMapper.batchInsertQuestion(questionEntityList);
            if(res1 != questionEntityList.size()){
                throw new InsertException("保存数据异常：question entity");
            }
        }
        if(!quesContentEntityList.isEmpty()){
            int res2 = examMapper.batchInsertQuesContent(quesContentEntityList);
            if (res2!=quesContentEntityList.size()){
                throw new InsertException("保存数据异常：question content entity");
            }
        }
        // redisTemplate.delete(FLAG+qo.getParseId());

        return examInfoEntity.getId();
    }

    @Override
    public void publishExam(List<Integer> studentIdList, Long examId){
        ExamInfoEntity examInfoEntity = examMapper.selectExamWithQuestionsAndContent(examId);

        if(!studentIdList.isEmpty()){
            List<ExamStudent> examStudents = new ArrayList<>();
            studentIdList.forEach(s->{
                ExamStudent examStudent = new ExamStudent();
                examStudent.setStudentId(s);
                examStudent.setExamId(examId);
                examStudent.setScore(0.0);
                examStudents.add(examStudent);
            });
            try{
                int res = examMapper.batchInsertExamStudent(examStudents);
                if(res!=examStudents.size()){
                    throw new InsertException("保存数据异常: exam students 数量不匹配");
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
                examMapper.delExamById(examId);
                throw new InsertException("保存数据异常");
            }
            List<ExamPaper> examPapers = new ArrayList<>();
            examStudents.forEach(examStudent->{
                examInfoEntity.getExamQuestions().forEach(questionEntity -> {
                    if(questionEntity.getIsRandom()){
                        int randomNum = questionEntity.getRandomNum();
                        int qSize = questionEntity.getExamQuesContents().size();
                        List<Integer> randomIndex = new ArrayList<>();
                        if(examInfoEntity.getUseRandomQuestion()){
                            randomIndex.addAll(RandomUtil.generateUniqueRandomNumbers(0,qSize-1,randomNum));
                        }else{
                            for(int i=0;i<randomNum;i++){
                                randomIndex.add(i);
                            }
                        }
                        for(Integer index:randomIndex){
                            // 构建试卷
                            ExamPaper examPaper = new ExamPaper();
                            examPaper.setId(UUIDGenerator.generateUUID());
                            examPaper.setEsId(examStudent.getId());
                            examPaper.setQuesType(questionEntity.getQuesType());
                            examPaper.setOrder(questionEntity.getOrder()+index);
                            examPaper.setTotalScore(questionEntity.getScore());
                            examPaper.setScore(0.0);
                            examPaper.setTitle(questionEntity.getExamQuesContents().get(index).getTitle());
                            examPaper.setOptions(questionEntity.getExamQuesContents().get(index).getOptions()); // 随机处理
                            examPaper.setAnswer(questionEntity.getExamQuesContents().get(index).getAnswer());
                            examPaper.setKnowledge(questionEntity.getExamQuesContents().get(index).getKnowledge());
                            if(examInfoEntity.getUseRandomOption()){
                                examPapers.add(this.randomOptions(examPaper));
                            }else {
                                examPapers.add(examPaper);
                            }

                        }
                    }else{
                        ExamPaper examPaper = new ExamPaper();
                        examPaper.setId(UUIDGenerator.generateUUID());
                        examPaper.setEsId(examStudent.getId());
                        examPaper.setQuesType(questionEntity.getQuesType());
                        examPaper.setOrder(questionEntity.getOrder());
                        examPaper.setTotalScore(questionEntity.getScore());
                        examPaper.setScore(0.0);
                        examPaper.setTitle(questionEntity.getExamQuesContents().get(0).getTitle());
                        examPaper.setOptions(questionEntity.getExamQuesContents().get(0).getOptions()); // 随机处理
                        examPaper.setAnswer(questionEntity.getExamQuesContents().get(0).getAnswer());
                        examPaper.setKnowledge(questionEntity.getExamQuesContents().get(0).getKnowledge());
                        if(examInfoEntity.getUseRandomOption()){
                            examPapers.add(this.randomOptions(examPaper));
                        }else {
                            examPapers.add(examPaper);
                        }
                    }
                });
            });
            try{
                int res1 = examMapper.batchInsertExamPaper(examPapers);
                if(res1!=examPapers.size()){
                    throw new InsertException("保存数据异常：exam papers 数量不匹配");
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
                examMapper.delExamById(examId);
                throw new InsertException("保存数据异常");
            }
        }
    }

    @Override
    public void publishExamToProject(Integer projectId, Long examId){
        List<Integer> studentIdList = examMapper.selectProjectStudentId(projectId);
        this.publishExam(studentIdList, examId);
    }

    @Override
    public List<ExamCourseVo> getExamCourses(Integer creator) {
        return examMapper.selectTeacherExamCourse(creator);
    }

    @Override
    public Map<String, List<ExamInfoVo>> getCourseExamList(Integer projectId) {
        List<ExamWithStudentDto> examWithStudentDtoList = examMapper.selectExamWithStudentDto(projectId);
        Map<String, List<ExamInfoVo>> examInfoListMap = new HashMap<>();
        examInfoListMap.put("doing", new ArrayList<>());
        examInfoListMap.put("willStart", new ArrayList<>());
        examInfoListMap.put("done", new ArrayList<>());
        examWithStudentDtoList.forEach(examWithStudentDto -> {
            ExamInfoVo examInfoVo = genExamInfoVo(examWithStudentDto);
            if(examWithStudentDto.getStartTime()!=null
                    && examWithStudentDto.getEndTime()!=null
                    && examWithStudentDto.getStartTime().before(examWithStudentDto.getEndTime())){
                if(examWithStudentDto.getStartTime().after(new Date()) ){
                    // 未开始
                    examInfoListMap.get("willStart").add(examInfoVo);
                }
                if(examWithStudentDto.getStartTime().before(new Date()) && examWithStudentDto.getEndTime().after(new Date())){
                    // 进行中
                    examInfoListMap.get("doing").add(examInfoVo);
                }
                if(examWithStudentDto.getEndTime().before(new Date())){
                    // 已结束
                    examInfoListMap.get("done").add(examInfoVo);
                }
            }
        });
        return examInfoListMap;
    }

    private ExamInfoVo genExamInfoVo(ExamWithStudentDto examWithStudentDto){
        ExamInfoVo examInfoVo = new ExamInfoVo();
        examInfoVo.setExamId(examWithStudentDto.getId());
        examInfoVo.setProjectId(examWithStudentDto.getProjectId());
        examInfoVo.setProjectName(examWithStudentDto.getProjectName());
        examInfoVo.setSemester(examWithStudentDto.getSemester());
        examInfoVo.setExamName(examWithStudentDto.getName());
        examInfoVo.setDuration(examWithStudentDto.getDuration());
        examInfoVo.setTotalScore(examWithStudentDto.getTotalScore());
        examInfoVo.setPassScore(examWithStudentDto.getPassScore());
        examInfoVo.setStartTime(examWithStudentDto.getStartTime());
        examInfoVo.setEndTime(examWithStudentDto.getEndTime());
        examInfoVo.setStuNum(examWithStudentDto.getExamStudentList().size());
        Integer notStart = examWithStudentDto.getExamStudentList()
                .stream()
                .filter(examStudent -> examStudent.getStartTime()==null)
                .toList().size();
        Integer doing = examWithStudentDto.getExamStudentList()
                .stream()
                .filter(examStudent -> examStudent.getStartTime()!=null && examStudent.getEndTime()==null)
                .toList().size();
        Integer done = examWithStudentDto.getExamStudentList()
                .stream()
                .filter(examStudent -> examStudent.getEndTime()!=null)
                .toList().size();
        OptionalDouble max = examWithStudentDto.getExamStudentList()
                .stream()
                .mapToDouble(ExamStudent::getScore)
                .max();
        int passNum = examWithStudentDto.getExamStudentList()
                .stream()
                .filter(examStudent -> examStudent.getScore()>=examInfoVo.getPassScore())
                .toList()
                .size();
        examInfoVo.setDoingNum(doing);
        examInfoVo.setDoneNum(done);
        examInfoVo.setNotStartedNum(notStart);
        examInfoVo.setMaxScore(max.isPresent() ? max.getAsDouble() : 0.0);
        examInfoVo.setPassRate(numWith2Decimal((double)passNum * 100 /examWithStudentDto.getExamStudentList().size()));
        OptionalDouble avgScore = examWithStudentDto.getExamStudentList().stream().mapToDouble(ExamStudent::getScore).average();
        examInfoVo.setAvgScore(avgScore.isPresent()?numWith2Decimal(avgScore.getAsDouble()):0.0);
        return examInfoVo;
    }

    @Override
    public ExamInfoVo getExamInfo(Long examId) {
        ExamWithStudentDto examWithStudentDto = examMapper.selectExamWithStudentDtoById(examId);
        return genExamInfoVo(examWithStudentDto);
    }

    @Override
    public Map<String, List<ExamInfoVo>> delCourseExam(Integer projectId, Long examId) {
        // 删除未开始的考试
        ExamInfoEntity examInfoEntity = examMapper.selectExamWithQuestionsAndContent(examId);
        if(examInfoEntity == null){
            throw new DeleteException("未找到相关数据");
        }
        if(examInfoEntity.getStartTime().before(new Date())){
            throw new DeleteException("考试已开始，不允许删除");
        }
        int res = examMapper.deleteExamInfoByExamId(examId);
        if(res != 1){
            throw new DeleteException("删除数据时出错");
        }
        return getCourseExamList(projectId);
    }

    @Override
    public List<ExamStudentVo> getExamStudentList(Long examId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ExamStudentVo> examStudentVoList = examMapper.selectExamStudentVoByExamId(examId, offset, pageSize);
        examStudentVoList.forEach(this::fillExamStudentVo);
        return examStudentVoList;
    }

    private ExamStudentVo fillExamStudentVo(ExamStudentVo examStudentVo) {
        if(examStudentVo.getStartTime()==null){
            examStudentVo.setStatus("notStart");
        }
        if(examStudentVo.getStartTime()!=null && examStudentVo.getEndTime()==null){
            examStudentVo.setStatus("doing");
            examStudentVo.setTimeSpent(calculateMinuteDiff(examStudentVo.getStartTime(),new Date()));
        }
        if(examStudentVo.getEndTime()!=null){
            examStudentVo.setStatus("done");
            examStudentVo.setTimeSpent(calculateMinuteDiff(examStudentVo.getStartTime(),examStudentVo.getEndTime()));
        }
        return examStudentVo;
    }

    @Override
    public StuExamPaperVo getStudentExamPaperVo(Long esId) {
        StuExamPaperVo stuExamPaperVo = new StuExamPaperVo();
        List<ExamPaper> examPaperList = examMapper.selectExamStudentPaper(esId);
        ExamStudentVo examStudentVo = this.fillExamStudentVo(examMapper.selectExamStudentVoByEsId(esId));
        ExamInfoVo examInfoVo = this.getExamInfo(examStudentVo.getExamId());
        List<Long> esIdList = examMapper.selectEsIdByExamId(examStudentVo.getExamId());
        stuExamPaperVo.setExamInfo(examInfoVo);
        stuExamPaperVo.setExamStudent(examStudentVo);
        stuExamPaperVo.setExamPapers(examPaperList);
        stuExamPaperVo.setEsIdList(esIdList);
        return stuExamPaperVo;
    }

    @Override
    public void upQuesScore(Long esId, String quesId, Boolean upRemark, String remark, Double score) {
        System.out.println(esId);
        System.out.println(quesId);
        System.out.println(upRemark);
        System.out.println(remark);
        System.out.println(score);
        if(quesId!=null && score!=null){
            examMapper.updateQuesScore(quesId,score);
        }
        List<ExamPaper> examPaperList = examMapper.selectExamStudentPaper(esId);
        Double tScore = examPaperList.stream().mapToDouble(ExamPaper::getScore).sum();
        if(upRemark){
            examMapper.updateEsScoreAndRemark(esId, tScore, remark);
        }else {
            examMapper.updateEsScore(esId, score);
        }
    }

    private ExamPaper randomOptions(ExamPaper examPaper){
        if(examPaper.getQuesType()!=QuesType.CHOICE && examPaper.getQuesType()!=QuesType.MultipleCHOICE){
            return examPaper;
        }
        ArrayNode arrayNode = (ArrayNode) examPaper.getOptions();
        String answer = examPaper.getAnswer();
        List<String> answerList = answer==null? new ArrayList<>() : Arrays.stream(answer.split(","))
                .map(String::trim)
                .toList();
        // 建立「选项值 - 原始标签」的映射（如：热敏性 → A）
        Map<String, String> valueToOriginalLabel = new HashMap<>();
        for (JsonNode option : arrayNode) {
            valueToOriginalLabel.put(option.get("value").asText(), option.get("label").asText());
        }

        // 随机打乱选项列表（创建新列表，避免修改原对象）
        List<String> shuffledValues = new ArrayList<>(valueToOriginalLabel.keySet());
        Collections.shuffle(shuffledValues);

        List<String> labels = new ArrayList<>(valueToOriginalLabel.values());
        labels.sort(Comparator.naturalOrder());

        ArrayNode arrayNode1 = objectMapper.createArrayNode();
        List<String> answerList1 = new ArrayList<>();
        for(int i=0; i<labels.size(); i++){
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("label", labels.get(i));
            objectNode.put("value", shuffledValues.get(i));
            if(answerList.contains(valueToOriginalLabel.get(shuffledValues.get(i)))){
                answerList1.add(labels.get(i));
            }
            arrayNode1.add(objectNode);
        }
        StringBuilder stringBuilder = new StringBuilder();
        answerList1.forEach(l-> stringBuilder.append(l).append(","));
        examPaper.setOptions(arrayNode1);
        examPaper.setAnswer(stringBuilder.toString());
        return examPaper;
    }

    private QuestionEntity QuesVoToQuestionEntity(ExamParseVo.QuesVo quesVo){
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setId(quesVo.getId());
        questionEntity.setQuesType(quesVo.getQuesType());
        questionEntity.setOrder(quesVo.getOrder());
        questionEntity.setIsRandom(quesVo.getIsRandom());
        questionEntity.setRandomNum(quesVo.getRandomNum());
        questionEntity.setDifficulty(quesVo.getDifficulty());
        questionEntity.setScore(quesVo.getScore());
        return questionEntity;
    }

    private List<QuestionDto>  parseQuesSheet(Sheet sheet){
        QuesType quesType = QuesType.getByDesc(sheet.getSheetName());
        if(quesType!=null){
            if(quesType==QuesType.QA){
                return parseQASheet(sheet);
            }else {
                return parseChoiceSheet(sheet);
            }
        }
        return null;
    }

    private List<QuestionDto> parseChoiceSheet(Sheet sheet){
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for(int rowIndex=1; rowIndex<sheet.getLastRowNum(); rowIndex++){
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            QuestionDto questionDto = new QuestionDto();
            questionDto.setOrder(rowIndex);
            if(sheet.getSheetName().equals("单选题")){
                questionDto.setType(QuesType.CHOICE);
            }else if(sheet.getSheetName().equals("多选题")){
                questionDto.setType(QuesType.MultipleCHOICE);
            }
            questionDto.setQuestion(getCellValue(row.getCell(1),sheet.getSheetName(),rowIndex, 1,String.class));
            questionDto.setAnswer(getCellValue(row.getCell(6),sheet.getSheetName(),rowIndex, 6,String.class));
            questionDto.setScore(getCellValue(row.getCell(7),sheet.getSheetName(),rowIndex, 7,Double.class));
            questionDto.setDifficulty(getCellValue(row.getCell(8),sheet.getSheetName(),rowIndex, 8,Integer.class));
            String knowledge = getCellValue(row.getCell(9),sheet.getSheetName(),rowIndex, 9,String.class);
            questionDto.setKnowledge(splitString(knowledge, ",", true));
            if(row.getCell(10)==null){
                questionDto.setIsRandom(false);
            }else {
                questionDto.setRandomType(getCellValue(row.getCell(10), sheet.getSheetName(), rowIndex, 10, Integer.class));
                questionDto.setRandomNumber(getCellValue(row.getCell(11), sheet.getSheetName(), rowIndex, 11, Integer.class));
                questionDto.setIsRandom(questionDto.getRandomType()!=null);
            }
            List<String> labels = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for(int i=2; i<6; i++){
                if(row.getCell(i)!=null){
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.put("label", labels.get(i-2));
                    objectNode.put("value", getCellValue(row.getCell(i),sheet.getSheetName(),rowIndex, i, String.class));
                    arrayNode.add(objectNode);
                }
            }
            questionDto.setOptions(arrayNode);
            questionDtoList.add(questionDto);
        }
        return questionDtoList;
    }

    private List<QuestionDto> parseQASheet(Sheet sheet){
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for(int rowIndex=1; rowIndex<sheet.getLastRowNum(); rowIndex++){
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            QuestionDto questionDto = new QuestionDto();
            questionDto.setType(QuesType.QA);
            questionDto.setOrder(rowIndex);
            questionDto.setQuestion(getCellValue(row.getCell(1),sheet.getSheetName(),rowIndex, 1,String.class));
            questionDto.setAnswer(getCellValue(row.getCell(2),sheet.getSheetName(),rowIndex, 2,String.class));
            questionDto.setScore(getCellValue(row.getCell(3),sheet.getSheetName(),rowIndex, 3,Double.class));
            questionDto.setDifficulty(getCellValue(row.getCell(4),sheet.getSheetName(),rowIndex, 4,Integer.class));
            String knowledge = getCellValue(row.getCell(5),sheet.getSheetName(),rowIndex, 5,String.class);
            questionDto.setKnowledge(splitString(knowledge, ",", true));
            if(row.getCell(6)==null){
                questionDto.setIsRandom(false);
            }else {
                questionDto.setRandomType(getCellValue(row.getCell(6), sheet.getSheetName(), rowIndex, 6, Integer.class));
                questionDto.setRandomNumber(getCellValue(row.getCell(7), sheet.getSheetName(), rowIndex, 7, Integer.class));
                questionDto.setIsRandom(questionDto.getRandomType()!=null);
            }
            questionDtoList.add(questionDto);
        }
        return questionDtoList;
    }

    /**
     * 通用字符串拆分方法
     * @param originalStr 原始字符串（可为null）
     * @param separator 拆分符（如逗号、竖线等）
     * @param ignoreEmpty 是否忽略拆分后的空元素（true: 忽略，false: 保留）
     * @return 拆分后的字符串列表，若原始字符串为null/空则返回空列表
     */
    private static List<String> splitString(String originalStr, String separator, boolean ignoreEmpty) {
        List<String> result = new ArrayList<>();

        // 处理空值情况
        if (originalStr == null || originalStr.trim().isEmpty()) {
            return result;
        }

        // 拆分字符串
        String[] parts = originalStr.split(separator);

        // 处理拆分结果
        for (String part : parts) {
            String trimmedPart = part.trim();
            // 根据ignoreEmpty参数决定是否忽略空元素
            if (ignoreEmpty) {
                if (!trimmedPart.isEmpty()) {
                    result.add(trimmedPart);
                }
            } else {
                result.add(trimmedPart);
            }
        }

        return result;
    }

    private ExamInfoEntity parseExamInfoSheet(Sheet sheet){
        Row row = sheet.getRow(1);
        ExamInfoEntity examInfoEntity = new ExamInfoEntity();
        examInfoEntity.setName(getCellValue(row.getCell(0), sheet.getSheetName(), 1, 0,String.class));
        examInfoEntity.setDuration(getCellValue(row.getCell(1), sheet.getSheetName(), 1, 1, Integer.class));
        examInfoEntity.setTotalScore(getCellValue(row.getCell(2), sheet.getSheetName(), 1, 2, Double.class));
        examInfoEntity.setPassScore(getCellValue(row.getCell(3), sheet.getSheetName(), 1, 3, Double.class));
        examInfoEntity.setStartTime(getCellValue(row.getCell(4), sheet.getSheetName(), 1, 4, Date.class));
        examInfoEntity.setEndTime(getCellValue(row.getCell(5), sheet.getSheetName(), 1, 5, Date.class));
        return examInfoEntity;
    }

    /**
     * 优化后的单元格值获取方法
     * @param cell 单元格对象
     * @param sheetName sheet名称
     * @param rowNum 实际行号（用户视角，从1开始）
     * @param colNum 实际列号（用户视角，从1开始）
     * @param targetType 目标返回类型
     * @return 转换后的目标类型数据，转换失败返回null
     */
    private <T> T getCellValue(Cell cell, String sheetName, int rowNum, int colNum, Class<T> targetType) {
        // 空单元格直接记录异常并返回null
        if (cell == null) {
            String errorMsg = String.format("Sheet[%s] 第%d行第%d列：单元格为空，无法转换为%s类型",
                    sheetName, rowNum+1, colNum+1, targetType.getSimpleName());
            cellParseErrors.computeIfAbsent(sheetName, k -> new ArrayList<>());
            cellParseErrors.get(sheetName).add(errorMsg);
            return null;
        }

        try {
            Object cellValue = getRawCellValue(cell);
            // 转换为目标类型
            return convertToTargetType(cellValue, targetType);
        } catch (Exception e) {
            // 捕获转换异常，记录信息并返回null
            String errorMsg = String.format("Sheet[%s] 第%d行第%d列：%s，目标类型：%s",
                    sheetName, rowNum+1, colNum+1, e.getMessage(), targetType.getSimpleName());
            cellParseErrors.computeIfAbsent(sheetName, k -> new ArrayList<>());
            cellParseErrors.get(sheetName).add(errorMsg);
            return null;
        }
    }

    /**
     * 获取单元格原始值（不做类型转换）
     */
    private Object getRawCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue(); // 日期类型直接返回Date
                } else {
                    yield cell.getNumericCellValue(); // 数字返回Double
                }
            }
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> {
                // 公式单元格计算后获取值
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                yield getRawCellValue(evaluator.evaluateInCell(cell));
            }
            default -> null;
        };
    }

    /**
     * 将原始值转换为目标类型
     */
    private <T> T convertToTargetType(Object rawValue, Class<T> targetType) {
        if (rawValue == null) {
            throw new IllegalArgumentException("单元格值为空");
        }

        // 相同类型直接返回
        if (targetType.isInstance(rawValue)) {
            return targetType.cast(rawValue);
        }

        // 不同类型转换逻辑
        try {
            if (targetType == String.class) {
                return targetType.cast(rawValue.toString());
            } else if (targetType == Integer.class) {
                if (rawValue instanceof Number num) {
                    return targetType.cast(num.intValue());
                } else if (rawValue instanceof String str) {
                    return targetType.cast(Integer.parseInt(str));
                }
            } else if (targetType == Long.class) {
                if (rawValue instanceof Number num) {
                    return targetType.cast(num.longValue());
                } else if (rawValue instanceof String str) {
                    return targetType.cast(Long.parseLong(str));
                }
            } else if (targetType == Double.class) {
                if (rawValue instanceof Number num) {
                    return targetType.cast(num.doubleValue());
                } else if (rawValue instanceof String str) {
                    return targetType.cast(Double.parseDouble(str));
                }
            } else if (targetType == Date.class) {
                if (rawValue instanceof String str) {
                    // 字符串转日期
                    return targetType.cast(DATE_FORMAT.parse(str));
                }
            } else if (targetType == Boolean.class) {
                if (rawValue instanceof String str) {
                    return targetType.cast(Boolean.parseBoolean(str));
                }
            }
        } catch (NumberFormatException | ParseException e) {
            throw new IllegalArgumentException("类型转换失败：" + e.getMessage());
        }

        // 不支持的转换类型
        throw new IllegalArgumentException("不支持将" + rawValue.getClass().getSimpleName() + "转换为" + targetType.getSimpleName());
    }

    /**
     * 检查单元格解析异常，有异常则抛出自定义异常
     */
    private static void checkCellParseErrors() {
        // 收集所有sheet的异常信息
        List<String> allErrors = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : cellParseErrors.entrySet()) {
            allErrors.addAll(entry.getValue());
        }

        // 有异常则抛出
        if (!allErrors.isEmpty()) {
            throw new ExcelCellParseException("Excel单元格解析失败，共" + allErrors.size() + "处错误:"+allErrors);
        }
    }

    /**
     * 检查考试信息，考试题目合理性异常，有异常抛出自定义异常
     */
    private static void checkQuesParseErrors(){
        List<String> allErrors = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : quesParseErrors.entrySet()){
            allErrors.addAll(entry.getValue());
        }
        if(!allErrors.isEmpty()){
            throw new ExcelCellParseException("Excel单元格解析错误，共" + allErrors.size() + "处错误:"+allErrors);
        }
    }

    /**
     *重置异常列表（可选，防止多次解析时异常累积）
     */
    private static void resetErrors() {
        cellParseErrors.clear();
        quesParseErrors.clear();
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

    /**
     * 计算两个Date的时间差（分钟）
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间差（分钟）：end - start，正数=end在start之后，负数=end在start之前；若需绝对差则返回Math.abs(result)
     * @throws IllegalArgumentException 入参为null时抛出
     */
    public static long calculateMinuteDiff(Date start, Date end) {
        // 空值校验，避免NullPointerException
        if (start == null || end == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为null");
        }
        // 1. 获取两个Date的毫秒时间戳
        long startMs = start.getTime();
        long endMs = end.getTime();
        // 2. 计算毫秒差 → 转换为分钟（1分钟=60*1000=60000毫秒）
        return (endMs - startMs) / 60000L;
    }
}
