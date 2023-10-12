package com.iecube.community.model.analysis.service.Impl;

import com.iecube.community.model.analysis.dto.*;
import com.iecube.community.model.analysis.service.AnalysisService;
import com.iecube.community.model.analysis.service.ex.NoneOfTheProjectsUnderTheCaseHaveBeenCompleted;
import com.iecube.community.model.analysis.vo.CaseHistoryData;
import com.iecube.community.model.analysis.vo.CurrentProjectData;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.tag.entity.Tag;
import com.iecube.community.model.task.entity.StudentTaskDetailVo;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.util.ListCounter;
import com.iecube.community.util.QuickSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskService taskService;

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
            List<Integer> grades = new ArrayList<>();
            List<String> tags = new ArrayList<>();
            int num = 0;
            for (int j=0; j<tasksOfAllProjectStudents.size(); j++){
                if(tasksOfAllProjectStudents.get(j).get(i).getTaskGrade()!=null){
                    grades.add(tasksOfAllProjectStudents.get(j).get(i).getTaskGrade());
                }else{
                    grades.add(0);
                }
                if(tasksOfAllProjectStudents.get(j).get(i).getTaskStatus()==1){
                    num++;
                }
                if(tasksOfAllProjectStudents.get(j).get(i).getTaskTags().size() > 0){
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
                List<Integer> grades = new ArrayList<>();
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
        List<Integer> grades = new ArrayList<>();
        for(ProjectStudentVo studentVo:studentVos){
            if(studentVo.getStudentGrade() == null){
                grades.add(0);
            }else{
                grades.add(studentVo.getStudentGrade());
            }
        }
        return getAverage(grades);
    }

    public double sameCaseAllProjectsAverageGrade(List<Project> list){
        List<Integer> allGrades = new ArrayList<>();
        for (Project project : list){
            List<ProjectStudentVo> studentVos = projectMapper.findStudentsByProjectId(project.getId());
            List<Integer> grades = new ArrayList<>();
            for(ProjectStudentVo studentVo:studentVos){
                if(studentVo.getStudentGrade() == null){
                    grades.add(0);
                }else{
                    grades.add(studentVo.getStudentGrade());
                }
            }
            allGrades.addAll(grades);
        }
        return getAverage(allGrades);
    }

    public static double getMedian(List<Integer> list) {
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

    public static double getAverage(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (int num : list) {
            sum += num;
        }
        double average =  sum / list.size();
        return Math.round(average * 100.0) / 100.0;
    }


}
