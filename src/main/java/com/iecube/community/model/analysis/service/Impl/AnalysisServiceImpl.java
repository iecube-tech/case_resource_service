package com.iecube.community.model.analysis.service.Impl;

import com.iecube.community.model.analysis.dto.*;
import com.iecube.community.model.analysis.mapper.AnalysisMapper;
import com.iecube.community.model.analysis.service.AnalysisService;
import com.iecube.community.model.analysis.service.ex.NoneOfTheProjectsUnderTheCaseHaveBeenCompleted;
import com.iecube.community.model.analysis.vo.*;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.task.entity.StudentTaskDetailVo;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.util.ListArrayDeduplication;
import com.iecube.community.util.ListCounter;
import com.iecube.community.util.QuickSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AnalysisMapper analysisMapper;

    @Override
    public CurrentProjectData getCurrentProjectData(Integer projectId) {
        CurrentProjectData currentProjectData = new CurrentProjectData();
        //获取当前项目的人数
        Integer studentNumOfProject = projectMapper.studentNumOfCurrentProject(projectId);
        currentProjectData.setNumberOfParticipant(studentNumOfProject);

        List<ProjectStudentVo> students = projectMapper.findStudentsByProjectId(projectId);
        List<ProjectStudentVo> studentsOfComplete = new ArrayList<>();
        List<List<StudentTaskDetailVo>> tasksOfAllProjectStudents =  new ArrayList<>();
        for(ProjectStudentVo student : students){
           List<StudentTaskDetailVo> studentTasksDetail  = taskService.findStudentTaskByProjectId(projectId,student.getId());
           tasksOfAllProjectStudents.add(studentTasksDetail);
           int num=0;
           for(StudentTaskDetailVo studentTaskDetail : studentTasksDetail){
               if(studentTaskDetail.getTaskStatus()>=2){
                   num++;
               }
           }
           if(num>=studentTasksDetail.size()){
               studentsOfComplete.add(student);
           }
        }
        // 计算人员分布情况
        List<PersonnelDistribution> personnelDistributionList = new ArrayList<>();
        // 计算成绩
        List<TaskAverage> taskAverageList = new ArrayList<>();
        List<TaskMedian> taskMedianList = new ArrayList<>();
        List<ProjectTaskStudentsGrade> projectTaskStudentsGradeList = new ArrayList<>();
        List<ProjectTaskStudentsTags> projectTaskStudentsTagsList = new ArrayList<>();
        List<String> allTags = new ArrayList<>();
        for (int i=0; i<tasksOfAllProjectStudents.get(0).size(); i++){
            //第i个任务 的所有分数列表
            List<Double> grades = new ArrayList<>();
            List<String> tags = new ArrayList<>();
            int num = 0;
            for (int j=0; j<tasksOfAllProjectStudents.size(); j++){
                if(tasksOfAllProjectStudents.get(j).get(i).getTaskGrade()!=null){
                    grades.add(tasksOfAllProjectStudents.get(j).get(i).getTaskGrade());
                }else{
                    grades.add(0.0);
                }
                if( Objects.equals(tasksOfAllProjectStudents.get(j).get(i).getTaskStatus(),1)){
                    num++;
                }
                if(!tasksOfAllProjectStudents.get(j).get(i).getTaskTags().isEmpty()){
                    for(Tag tag: tasksOfAllProjectStudents.get(j).get(i).getTaskTags()){
                        tags.add(tag.getName());
                    }
                }
            }
            allTags.addAll(tags);
            ProjectTaskStudentsTags projectTaskStudentsTags = new ProjectTaskStudentsTags();
            projectTaskStudentsTags.setTaskNum(i+1);
            projectTaskStudentsTags.setTags(tags);
            projectTaskStudentsTagsList.add(projectTaskStudentsTags);
            //分布
            PersonnelDistribution personnelDistribution = new PersonnelDistribution();
            personnelDistribution.setTaskNum(i+1);
            personnelDistribution.setStudentNum(num);
            personnelDistributionList.add(personnelDistribution);
            //平均值
            TaskAverage taskAverage = new TaskAverage();
            taskAverage.setTaskNum(i+1);
            taskAverage.setAverageGrade(getAverage(grades));
            taskAverageList.add(taskAverage);
            //中位数
            if(grades!=null){
                QuickSort.quickSort(grades);
            }
            TaskMedian taskMedian = new TaskMedian();
            taskMedian.setTaskNum(i+1);
            taskMedian.setMedianGrade(getMedian(grades));
            taskMedianList.add(taskMedian);
            ProjectTaskStudentsGrade projectTaskStudentsGrade= new ProjectTaskStudentsGrade();
            projectTaskStudentsGrade.setTaskNum(i+1);
            projectTaskStudentsGrade.setGradeList(grades);
            projectTaskStudentsGradeList.add(projectTaskStudentsGrade);
        }
        List<ListCounter.Occurrence> tagsCount = new ArrayList<>();
        if(allTags.size()>0){
            tagsCount = ListCounter.countOccurrences(allTags);
        }
        List<Integer> projectGradeList = analysisMapper.getProjectStudentScoreList(projectId);
        currentProjectData.setProjectGradeList(projectGradeList);
        currentProjectData.setPersonnelDistributions(personnelDistributionList);
        currentProjectData.setTaskAverages(taskAverageList);
        currentProjectData.setTaskMedians(taskMedianList);
        currentProjectData.setProjectTaskStudentsGradeList(projectTaskStudentsGradeList);
        currentProjectData.setProjectTaskStudentsTagsList(projectTaskStudentsTagsList);
        currentProjectData.setNumberOfCompleter(studentsOfComplete.size());
        currentProjectData.setTagsCount(tagsCount);
        currentProjectData.setAverageGrade(this.currentProjectAverageGrade(projectId));
        return currentProjectData;
    }

    @Override
    public CaseHistoryData getCaseHistoryData(Integer projectId){
        Project thisProject = projectMapper.findById(projectId);
        List<Project> theSameCaseProjectList = projectMapper.findByCaseId(thisProject.getCaseId());
        List<Project> theSameCaseProjectIsExpire = new ArrayList<>();
        for(Project project:theSameCaseProjectList){
            if(project.getEndTime().before(new Date())){
                theSameCaseProjectIsExpire.add(project);
            }
        }
        List<CurrentProjectData> projectDataList = new ArrayList<>();
        for (Project project:theSameCaseProjectIsExpire){
            CurrentProjectData projectData =  this.getCurrentProjectData(project.getId());
            projectDataList.add(projectData);
        }
        Integer studentNumOfParticipant = 0;
        Integer studentNumOfCompleter = 0;
        List<TaskAverage> TaskAverages = new ArrayList<>();
        List<TaskMedian> TaskMedians = new ArrayList<>();
        List<TaskTagCount> taskTagCountList = new ArrayList<>();
        List<String> allTags = new ArrayList<>();
        if(projectDataList.size()>0){
            for (int i=0; i<projectDataList.get(0).getProjectTaskStudentsGradeList().size(); i++){
                //第i个任务
                //所有项目的第i个任务的成绩列表 tag点列表
                List<Double> grades = new ArrayList<>();
                List<String> tags=new ArrayList<>();
                for(int j=0; j<projectDataList.size();j++){
                    grades.addAll(projectDataList.get(j).getProjectTaskStudentsGradeList().get(i).getGradeList());
                    tags.addAll(projectDataList.get(j).getProjectTaskStudentsTagsList().get(i).getTags());
                    allTags.addAll(projectDataList.get(j).getProjectTaskStudentsTagsList().get(i).getTags());
                }
                QuickSort.quickSort(grades);
                double average = getAverage(grades);
                double  median = getMedian(grades);
                TaskAverage taskAverage = new TaskAverage();
                TaskMedian taskMedian = new TaskMedian();
                taskAverage.setTaskNum(i+1);
                taskAverage.setAverageGrade(average);
                TaskAverages.add(taskAverage);
                taskMedian.setTaskNum(i+1);
                taskMedian.setMedianGrade(median);
                TaskMedians.add(taskMedian);
                TaskTagCount taskTagCount = new TaskTagCount();
                taskTagCount.setTaskNum(i+1);
                taskTagCount.setTagsCount(ListCounter.countOccurrences(tags));
                taskTagCountList.add(taskTagCount);
            }
        }else{
            throw new NoneOfTheProjectsUnderTheCaseHaveBeenCompleted("该案例下没有已完成的项目");
        }
        for(CurrentProjectData projectData: projectDataList){
            studentNumOfParticipant=studentNumOfParticipant+projectData.getNumberOfParticipant();
            studentNumOfCompleter= studentNumOfCompleter+projectData.getNumberOfCompleter();
        }
        List<ListCounter.Occurrence> tagsCount = new ArrayList<>();
        if(allTags.size()>0){
            tagsCount = ListCounter.countOccurrences(allTags);
        }
        CaseHistoryData caseHistoryData = new CaseHistoryData();
        caseHistoryData.setNumberOfParticipant(studentNumOfParticipant);
        caseHistoryData.setNumberOfCompleter(studentNumOfCompleter);
        caseHistoryData.setTaskAverages(TaskAverages);
        caseHistoryData.setTaskMedians(TaskMedians);
        caseHistoryData.setTaskTagCountList(taskTagCountList);
        caseHistoryData.setTagsCount(tagsCount);
        caseHistoryData.setHistoryAverageGrade(this.sameCaseAllProjectsAverageGrade(theSameCaseProjectIsExpire));
        return caseHistoryData;
    }


    @Override
    public double currentProjectAverageGrade(Integer projectId){
        List<ProjectStudentVo> studentVos = projectMapper.findStudentsByProjectId(projectId);
        List<Double> grades = new ArrayList<>();
        for(ProjectStudentVo studentVo:studentVos){
            if(studentVo.getStudentGrade() == null){
                grades.add(0.0);
            }else{
                grades.add(studentVo.getStudentGrade());
            }
        }
        return getAverage(grades);
    }

    /**
     * 有多少project使用该case
     * @param caseId
     * @return
     */
    @Override
    public Integer projectNumByCase(Integer caseId) {
        Integer num =  analysisMapper.getProjectNumByCase(caseId);
        return num;
    }

    /**
     * 有多少学生参与了这个case
     * @param caseId
     * @return
     */
    @Override
    public Integer studentNumByCase(Integer caseId) {
        List <Integer> projectList = analysisMapper.getProjectIdListByCaseId(caseId);
        List<Integer> students = new ArrayList<>();
        //可能一个case老师创建了多个project并添加了相同的学生
        for(Integer id: projectList){
            List<Integer> student = analysisMapper.getStudentIdListByProjectId(id);
            students.addAll(student);
        }
        // 需要对学生ID列表去重
        List<Integer> s = ListArrayDeduplication.removeDuplicates(students);
        return s.size();
    }

    /**
     * 参与该case的所有人的成绩分布直方图
     * @param caseId
     * @return
     */
    @Override
    public ScoreDistributionHistogram ScoreDistributionHistogramOfCase(Integer caseId) {
        List <Integer> projectList = analysisMapper.getProjectIdListByCaseId(caseId);
        List<Integer> allScores = new ArrayList<>();
        for(Integer id: projectList){
            List<Integer> scoreList = analysisMapper.getProjectStudentScoreList(id);
            allScores.addAll(scoreList);
        }
        ScoreDistributionHistogram scoreDistributionHistogram =  this.GenerateScoreDistributionHistogram(allScores);
        return scoreDistributionHistogram;
    }

    /**
     * 生成成绩分布直方图
     * @param list 成绩列表
     * @return 直方图数据
     */
    private ScoreDistributionHistogram GenerateScoreDistributionHistogram(List<Integer> list){
        ScoreDistributionHistogram scoreDistributionHistogram = new ScoreDistributionHistogram();
        List<String> x = Arrays.asList("<50", "50-59", "60-69", "70-79", "80-89", ">90");
        scoreDistributionHistogram.setX(x);
        List<Integer> y = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            y.add(0); //将y中每个元素置0
        }
        list.removeIf(Objects::isNull);
//        System.out.println(list);
        for (Integer num : list) {
            int index=0;
            if(num/10 >= 5) {
                index = ((num == 100) ? 9 : num / 10) - 4; // 确定在哪个范围内
            }
            y.set(index, y.get(index) + 1); // 更新对应范围的数量
        }
        scoreDistributionHistogram.setY(y);
        return scoreDistributionHistogram;
    }

    /**
     *  案例的每一个任务的所有参与人的成绩分布直方图
     * @param caseId
     * @return
     */
    @Override
    public List<ScoreDistributionHistogram> ScoreDistributionHistogramOfCaseEveryTask(Integer caseId) {
        // 该案例创建的所有的project
        List <Integer> projectList = analysisMapper.getProjectIdListByCaseId(caseId);
        // 该案例下的任务模板的数量 按顺序排列的任务编号
        List<Integer> taskNumListOfThisCase = analysisMapper.getTaskTemplateNumListByCase(caseId);
        List<List> allOfScoreList=new ArrayList<>();
        for(Integer num: taskNumListOfThisCase){
            // 任务1
            List<Integer> scoreListOfTaskNum = new ArrayList<>();
            for(Integer projectId: projectList){
                Integer taskId = analysisMapper.getTaskIdByProjectAndTaskNum(projectId,num);
                List<Integer> thisProjectTaskNumScoreList=analysisMapper.getATaskScoreListByTaskId(taskId);
                scoreListOfTaskNum.addAll(thisProjectTaskNumScoreList);
            }
            allOfScoreList.add(scoreListOfTaskNum);
        }
        List<ScoreDistributionHistogram> scoreDistributionHistogramList = new ArrayList<>();
        for(List<Integer> scoreList : allOfScoreList){
            ScoreDistributionHistogram scoreDistributionHistogram = this.GenerateScoreDistributionHistogram(scoreList);
            scoreDistributionHistogramList.add(scoreDistributionHistogram);
        }
        return scoreDistributionHistogramList;
    }

    /**
     * 案例下被评价给学生的所有的tag点的统计信息
     * @param caseId
     * @return
     */
    @Override
    public List<TagCountVo> tagCounterOfCase(Integer caseId) {
        List<Integer> projectList = analysisMapper.getProjectIdListByCaseId(caseId);
        List<Integer> tagListOfThisCase = new ArrayList<>();
        for(Integer projectId : projectList) {
            List<Integer> PSTIdList=analysisMapper.getPSTIdListByProject(projectId);
            for (Integer pstId : PSTIdList){
                List<Integer> tagListOfThisPST = analysisMapper.getTagIdListByPSTId(pstId);
                tagListOfThisCase.addAll(tagListOfThisPST);
            }
        }

        Map<Integer, Integer> countMap = new HashMap<>();

        for (Integer num : tagListOfThisCase) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        }
        List<Map.Entry<Integer, Integer>> countList = new ArrayList<>(countMap.entrySet());
        List<TagCountVo> tagCountVoList = new ArrayList<>();
        for (Map.Entry<Integer, Integer>  entry: countList ){
            TagCountVo tagCountVo = new TagCountVo();
            tagCountVo.setId(entry.getKey());
            tagCountVo.setTimes(entry.getValue());
            tagCountVo.setName(analysisMapper.getTagName(entry.getKey()));
            tagCountVo.setTaskNum(analysisMapper.getTaskNumByTag(entry.getKey()));
            tagCountVo.setSuggestion(analysisMapper.getTagSuggestion(entry.getKey()));
            tagCountVoList.add(tagCountVo);
        }
        return tagCountVoList;
    }

    /**
     * 直接获取案例的全部数据
     * @param caseId
     * @return
     */
    @Override
    public CaseAnalysis getCaseAnalysis(Integer caseId) {
        CaseAnalysis caseAnalysis = new CaseAnalysis();
        Integer usedNum = this.projectNumByCase(caseId);
        Integer studentNum = this.studentNumByCase(caseId);
        ScoreDistributionHistogram scoreDistributionHistogram = this.ScoreDistributionHistogramOfCase(caseId);
        List<ScoreDistributionHistogram> scoreDistributionHistogramList = this.ScoreDistributionHistogramOfCaseEveryTask(caseId);
        List<TagCountVo> tagCountVoList = this.tagCounterOfCase(caseId);
        caseAnalysis.setUsedTime(usedNum);
        caseAnalysis.setNumberOfParticipant(studentNum);
        caseAnalysis.setScoreDistributionHistogram(scoreDistributionHistogram);
        caseAnalysis.setCaseTaskScoreDistributionHistogram(scoreDistributionHistogramList);
        caseAnalysis.setTagCounterOfCase(tagCountVoList);
        return caseAnalysis;
    }

    @Override
    public CaseAnalysis getProjectAnalysis(Integer projectId) {
        CaseAnalysis projectAnalysis =  new CaseAnalysis();
        Integer studentInProject = analysisMapper.getProjectStudentNum(projectId);
        projectAnalysis.setNumberOfParticipant(studentInProject);
        List<Integer> projectScoreList = analysisMapper.getProjectStudentScoreList(projectId);
        ScoreDistributionHistogram scoreDistributionHistogram = GenerateScoreDistributionHistogram(projectScoreList);
        projectAnalysis.setScoreDistributionHistogram(scoreDistributionHistogram);
        List<ScoreDistributionHistogram> taskScoreDistributionHistogramList = new ArrayList<>();
        List<Integer> tasks = analysisMapper.getTaskListByProject(projectId);
        for(Integer task : tasks){
            List<Integer> scores = analysisMapper.getATaskScoreListByTaskId(task);
            ScoreDistributionHistogram taskScoreDistributionHistogram = GenerateScoreDistributionHistogram(scores);
            taskScoreDistributionHistogramList.add(taskScoreDistributionHistogram);
        }
        projectAnalysis.setCaseTaskScoreDistributionHistogram(taskScoreDistributionHistogramList);
        List<Integer> PSTIdList=analysisMapper.getPSTIdListByProject(projectId);
        List<Integer> thisProjectTags= new ArrayList<>();
        for (Integer pstId : PSTIdList){
            List<Integer> tagListOfThisPST = analysisMapper.getTagIdListByPSTId(pstId);
            thisProjectTags.addAll(tagListOfThisPST);
        }
        Map<Integer, Integer> countMap = new HashMap<>();

        for (Integer num : thisProjectTags) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        }
        List<Map.Entry<Integer, Integer>> countList = new ArrayList<>(countMap.entrySet());
        List<TagCountVo> tagCountVoList = new ArrayList<>();
        for (Map.Entry<Integer, Integer>  entry: countList ){
            TagCountVo tagCountVo = new TagCountVo();
            tagCountVo.setId(entry.getKey());
            tagCountVo.setTimes(entry.getValue());
            tagCountVo.setName(analysisMapper.getTagName(entry.getKey()));
            tagCountVo.setTaskNum(analysisMapper.getTaskNumByTag(entry.getKey()));
            tagCountVo.setSuggestion(analysisMapper.getTagSuggestion(entry.getKey()));
            tagCountVoList.add(tagCountVo);
        }
        projectAnalysis.setTagCounterOfCase(tagCountVoList);

        return projectAnalysis;
    }

    @Override
    public ProjectClassHour getProjectClassHour(Integer projectId) {
        ProjectClassHour projectClassHour = new ProjectClassHour();
        List<Double> taskClassHourList = analysisMapper.getClassHour(projectId);
        Double classHour = getAdd(taskClassHourList);
        projectClassHour.setClassHour(classHour);
        List<Double> totalClassHourList = analysisMapper.getTotalClassHour(projectId);
        Double totalClassHour = classHour*totalClassHourList.size()/taskClassHourList.size();
        projectClassHour.setTotalClassHour(totalClassHour);
        List<Double> completedClassHourList = analysisMapper.getCompletedClassHour(projectId);
        List<Double> redaOVerClassHourList = analysisMapper.getRedaOVerClassHour(projectId);
        Double completedClassHour = getAdd(completedClassHourList);
        Double redaOVerClassHour = getAdd(redaOVerClassHourList);
        projectClassHour.setCompletedClassHour(completedClassHour);
        projectClassHour.setRedaOVerClassHour(redaOVerClassHour);
        projectClassHour.setCompletedPercent((double)Math.round((completedClassHour/totalClassHour)*100));
        projectClassHour.setRedaOverPercent((double)Math.round((redaOVerClassHour/totalClassHour)*100));
        return projectClassHour;
    }

    public double sameCaseAllProjectsAverageGrade(List<Project> list){
        List<Double> allGrades = new ArrayList<>();
        for (Project project : list){
            List<ProjectStudentVo> studentVos = projectMapper.findStudentsByProjectId(project.getId());
            List<Double> grades = new ArrayList<>();
            for(ProjectStudentVo studentVo:studentVos){
                if(studentVo.getStudentGrade() == null){
                    grades.add(0.0);
                }else{
                    grades.add(studentVo.getStudentGrade());
                }
            }
            allGrades.addAll(grades);
        }
        return getAverage(allGrades);
    }

    public static double getMedian(List<Double> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }

        int size = list.size();
        if (size % 2 == 1) {
            // 奇数长度
            return list.get(size / 2);
        } else {
            // 偶数长度
            return (list.get((size - 1) / 2) + list.get(size / 2)) / 2.0;
        }
    }

    public static double getAverage(List<Double> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (double num : list) {
            sum += num;
        }
        double average =  sum / list.size();
        return Math.round(average * 100.0) / 100.0;
    }


    public static double getAdd(List<Double> list){
        if (list == null || list.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (double num : list) {
            sum += num;
        }
        return sum;
    }

}
