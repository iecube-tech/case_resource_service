package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.impl;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.mapper.EMDV4ProjectStudentTaskMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.qo.StepWeightingQo;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.EMDV4ProjectStudentTaskService;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.EMDV4ProjectStudentTaskSetWeightService;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.EMDV4StudentTaskBookService;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EMDV4ProjectStudentTaskServiceImpl implements EMDV4ProjectStudentTaskService {

    @Autowired
    private EMDV4ProjectStudentTaskMapper emdV4ProjectStudentTaskMapper;

    @Autowired
    private EMDV4StudentTaskBookService emdV4StudentTaskBookService;

    @Autowired
    private EMDV4ProjectStudentTaskSetWeightService emdv4ProjectStudentTaskSetWeightService;

    @Override
    public List<Integer> createProjectStudentTask(List<EMDV4ProjectTask> projectTaskList, List<BookLabCatalog> labProcList, List<EMDV4ProjectStudent> projectStudentList) {
        // 5. 根据学生人数 创建 EMDV4_student_task_book(实验指导书)和EMDV4_component(组件) // 分发实验指导书 一个学生一个学生的穿件 循环学生人数次
        // projectStudent+projectStudentTask->taskBook
        // List<EMDV4ProjectStudent>元素 和 List<EMDV4ProjectTask> 元素两两组合
        List<EMDV4ProjectStudentTask> studentTaskList = new ArrayList<>();
        List<Integer> taskTagNumList = new ArrayList<>(); // 第几个task的tag数量
        for(int i = 0; i < projectTaskList.size(); i++){
            taskTagNumList.add(0);
        }
        for(EMDV4ProjectStudent projectStudent : projectStudentList){
            for(int i=0; i<projectTaskList.size(); i++){
                EMDV4ProjectTask projectTask = projectTaskList.get(i);
                BookLabCatalog labCatalog = labProcList.get(i);
                EMDV4StudentTaskBook studentTaskBook = emdV4StudentTaskBookService.createStudentTaskBook(labCatalog);
                EMDV4ProjectStudentTask studentTask = this.newEMDV4ProjectStudentTask(projectStudent,projectTask,studentTaskBook);
                studentTask.setTotalNumOfTags(studentTaskBook.getTagList().size());  // 更新实验下的总tag数量
                taskTagNumList.set(i, studentTaskBook.getTagList().size());
                studentTaskList.add(studentTask);
            }
        }
        int res = emdV4ProjectStudentTaskMapper.batchInsert(studentTaskList);
        if(res!= studentTaskList.size()){
            throw new InsertException("分配学生实验异常");
        }
        return taskTagNumList;
    }

    private EMDV4ProjectStudentTask newEMDV4ProjectStudentTask(EMDV4ProjectStudent projectStudent, EMDV4ProjectTask projectTask, EMDV4StudentTaskBook studentTaskBook){
        EMDV4ProjectStudentTask projectStudentTask = new EMDV4ProjectStudentTask();
        projectStudentTask.setProjectStudent(projectStudent.getId());
        projectStudentTask.setProjectTask(projectTask.getId());
        projectStudentTask.setTaskBookId(studentTaskBook.getId());
        projectStudentTask.setScore(0.0);
        projectStudentTask.setStatus(0);
        projectStudentTask.setStartTime(null);
        projectStudentTask.setDoneTime(null);
        projectStudentTask.setUseTime(null);
        projectStudentTask.setTotalNumOfTags(0);
        projectStudentTask.setAchievedNumOfTags(0);
        projectStudentTask.setAverageErrorRate(0.0);
        projectStudentTask.setCheckScore(0.0);
        projectStudentTask.setHasChecked(false);
        projectStudentTask.setAiScoreTime(null);
        projectStudentTask.setAiScore(0.0);
        return projectStudentTask;
    }

    @Override
    public EMDV4ProjectStudentTask getByProjectTaskAndProjectStudent(Long projectTaskId, Long projectStudentId){
        EMDV4ProjectStudentTask res = emdV4ProjectStudentTaskMapper.getByPTIdAndPSId(projectTaskId, projectStudentId);
        EMDV4StudentTaskBook book = emdV4StudentTaskBookService.getByBookId(res.getTaskBookId());
        res.setStudentTaskBook(book);
        return res;
    }

    @Override
    public EMDV4ProjectStudentTask getByPSTid(Long pstId) {
        EMDV4ProjectStudentTask res = emdV4ProjectStudentTaskMapper.getById(pstId);
        EMDV4StudentTaskBook book = emdV4StudentTaskBookService.getByBookId(res.getTaskBookId());
        res.setStudentTaskBook(book);
        return res;
    }

    @Override
    public EMDV4ProjectStudentTask getByPS_idAndPT_num(Long projectStudentId, Integer projectTaskNum) {
        EMDV4ProjectStudentTask res = emdV4ProjectStudentTaskMapper.getByPSIdAndPTNum(projectStudentId, projectTaskNum);
        EMDV4StudentTaskBook book = emdV4StudentTaskBookService.getByBookId(res.getTaskBookId());
        res.setStudentTaskBook(book);
        return res;
    }

    @Override
    @Async
    public void computeAiScore(String blockLeafId) {
        EMDV4StudentTaskBook emdv4StudentTaskBook = emdV4StudentTaskBookService.computeAiScore(blockLeafId);
        // 更新PST的成绩
        emdV4ProjectStudentTaskMapper.updateAiScore(emdv4StudentTaskBook.getId(),emdv4StudentTaskBook.getScore());
    }

    /**
     * 教师批改过程中异步调用
     * @param blockLeafId 叶子节点的id
     */
    @Override
//    @Async
    public void computeCheckScore(String blockLeafId) {
//        System.out.println("计算成绩");
        EMDV4StudentTaskBook emdv4StudentTaskBook = emdV4StudentTaskBookService.computeCheckScore(blockLeafId);
//        System.out.println(emdv4StudentTaskBook);
        // 更新PST的成绩
        emdV4ProjectStudentTaskMapper.updateScore(emdv4StudentTaskBook.getId(),emdv4StudentTaskBook.getScore());
    }

    @Override
    public EMDV4ProjectStudentTask teacherChecked(Long id, Double score) {
        emdV4ProjectStudentTaskMapper.updateCheckScore(id,score);
        return emdV4ProjectStudentTaskMapper.getById(id);
    }

    @Override
    public EMDV4ProjectStudentTask checkedScoreWeighting(StepWeightingQo stepWeightingQo) {
        List<StepWeightingQo.StepWeighting> stepWeightings = stepWeightingQo.getStepWeightings();
        Map<String,Double> stepWeightingMap = new HashMap<String,Double>();
        List<EMDV4StudentTaskBook> emdv4StudentTaskBookList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        double totalWeight = 0.0;
        for (StepWeightingQo.StepWeighting stepWeighting : stepWeightings) {
            totalWeight+=stepWeighting.getWeighting();
            EMDV4StudentTaskBook emdv4StudentTaskBook = new EMDV4StudentTaskBook();
            emdv4StudentTaskBook.setId(stepWeighting.getBlockId());
            emdv4StudentTaskBook.setWeighting(stepWeighting.getWeighting());
            emdv4StudentTaskBookList.add(emdv4StudentTaskBook);
            idList.add(stepWeighting.getBlockId());
            stepWeightingMap.put(stepWeighting.getBlockId(),stepWeighting.getWeighting());
        }
        if(totalWeight!=100.0){
            throw new UpdateException("权重总和不为100，请重设");
        }
        // 校验是否有变化
        List<EMDV4StudentTaskBook> taskBookListOld = emdV4StudentTaskBookService.batchGetById(idList);
        Integer sameWeightingNum=0;
        for(EMDV4StudentTaskBook book:taskBookListOld){
            if(stepWeightingMap.containsKey(book.getId()) && stepWeightingMap.get(book.getId()).equals(book.getWeighting())){
                   sameWeightingNum++;
            }
        }
        if(sameWeightingNum.equals(stepWeightings.size())){
            throw new UpdateException("权重没有变化");
        }

        List<EMDV4StudentTaskBook> taskBookList = emdV4StudentTaskBookService.batchUpdateWeighting(emdv4StudentTaskBookList);
        if (taskBookList==null || taskBookList.isEmpty()){
            throw new UpdateException("成绩计算时未找到对象");
        }
        EMDV4StudentTaskBook res = emdV4StudentTaskBookService.computeTaskBookScore(taskBookList.get(0));
        emdV4ProjectStudentTaskMapper.updateScore(res.getId(),res.getScore());
        EMDV4ProjectStudentTask PST = this.getByPSTid(stepWeightingQo.getPstId());
        emdv4ProjectStudentTaskSetWeightService.checkProjectTaskWeighting(emdv4StudentTaskBookList, PST);
        return PST;
    }

}
