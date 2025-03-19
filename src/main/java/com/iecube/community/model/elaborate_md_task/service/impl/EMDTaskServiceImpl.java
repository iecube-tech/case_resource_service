package com.iecube.community.model.elaborate_md_task.service.impl;

import com.iecube.community.exception.ParameterException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.elaborate_md.block.service.BlockService;
import com.iecube.community.model.elaborate_md.block.vo.BlockVo;
import com.iecube.community.model.elaborate_md.sectionalization.entity.Sectionalization;
import com.iecube.community.model.elaborate_md.sectionalization.service.SectionalizationService;
import com.iecube.community.model.elaborate_md_task.entity.EMDSTSBlock;
import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTask;
import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTaskSection;
import com.iecube.community.model.elaborate_md_task.entity.EMDTaskRecord;
import com.iecube.community.model.elaborate_md_task.mapper.EMDSTSBlockMapper;
import com.iecube.community.model.elaborate_md_task.mapper.EMDStudentTaskMapper;
import com.iecube.community.model.elaborate_md_task.mapper.EMDStudentTaskSectionMapper;
import com.iecube.community.model.elaborate_md_task.mapper.EMDTaskRecordMapper;
import com.iecube.community.model.elaborate_md_task.service.EMDTaskService;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskBlockVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskDetailVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskSectionVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskVo;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.task.entity.Task;
import com.iecube.community.model.task.entity.TaskVo;
import com.iecube.community.model.task.mapper.TaskMapper;
import com.iecube.community.model.task_e_md_proc.mapper.TaskEMdProcMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EMDTaskServiceImpl implements EMDTaskService {

    @Autowired
    private SectionalizationService sectionalizationService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private EMDStudentTaskMapper emdStudentTaskMapper;

    @Autowired
    private EMDStudentTaskSectionMapper emdStudentTaskSectionMapper;

    @Autowired
    private EMDSTSBlockMapper emdSTSBlockMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskEMdProcMapper taskEMdProcMapper;

    @Autowired
    private EMDTaskRecordMapper emdTaskRecordMapper;


    @Override
    /**
     * 发布emd实验的task。 studentList 和 taskList 生成的emdStudentTasks 批量添加到 emdStudentTasks
     *
     *
     */
    public void EMDTaskPublish(List<Student> studentList, List<Task> taskList) {
        Map<Integer,Long> taskProcMap = new HashMap<>(); // <taskId, labProcId>
        Map<Long,List<Sectionalization>> procSectionMap = new HashMap<>(); // 取到 task对应的实验指导书 的 section <labId, sectionList>
        Map<Long, List<BlockVo>> sectionBlockMap = new HashMap<>(); // 取到 section 对应的 blockList <sectionId, blockList>
        for(Task task : taskList){ // task 中带着指导书的id ==> procId
            taskProcMap.put(task.getId(),task.getTaskEMdProc());
            List<Sectionalization> sectionList = sectionalizationService.getSectionalizationByLabProcId(task.getTaskEMdProc());
            procSectionMap.put(task.getTaskEMdProc(), sectionList);
        }
        procSectionMap.forEach((labId,sectionList)->{
            sectionList.forEach(section->{
               List<BlockVo> blocks = blockService.getBlockVoListBySection(section.getId());
                sectionBlockMap.put(section.getId(),blocks);
            });
        });

        List<EMDStudentTask> EMDStudentTaskList = new ArrayList<>();
        for(Student student : studentList){
            for(Task task: taskList){
                // 创建 student--task
                EMDStudentTask eMDStudentTask = new EMDStudentTask();
                eMDStudentTask.setStudentId(student.getId());
                eMDStudentTask.setTaskId(task.getId());
                eMDStudentTask.setStatus(0);
                EMDStudentTaskList.add(eMDStudentTask);
            }
        }
        int re = emdStudentTaskMapper.BatchAdd(EMDStudentTaskList);
        if(re!=EMDStudentTaskList.size()){
            throw new InsertException("新增数据异常");
        }

        List<EMDStudentTaskSection> emdStudentTaskSectionList = new ArrayList<>();
        // 创建 student-task -- section
        for(EMDStudentTask eMDStudentTask : EMDStudentTaskList){
            List<Sectionalization> sectionList = procSectionMap.get(taskProcMap.get(eMDStudentTask.getTaskId()));
            for(Sectionalization section : sectionList){
                EMDStudentTaskSection eMDStudentTaskSection = new EMDStudentTaskSection();
                eMDStudentTaskSection.setStudentTaskId(eMDStudentTask.getId());
                eMDStudentTaskSection.setSectionId(section.getId());
                eMDStudentTaskSection.setSort(section.getSort());
                eMDStudentTaskSection.setStatus(0);
                emdStudentTaskSectionList.add(eMDStudentTaskSection);
            }
        }
        int re1 = emdStudentTaskSectionMapper.BatchAdd(emdStudentTaskSectionList);
        if(re1 != emdStudentTaskSectionList.size()){
            throw new InsertException("新增数据异常");
        }

        List<EMDSTSBlock> emdstsBlockList = new ArrayList<>();
        for(EMDStudentTaskSection emdSTS : emdStudentTaskSectionList){
            List<BlockVo> blockList  = sectionBlockMap.get(emdSTS.getSectionId());
            for(BlockVo blockVo : blockList){
                EMDSTSBlock emdSTSBlock = getEmdSTSBlock(emdSTS, blockVo);
                emdstsBlockList.add(emdSTSBlock);
            }
        }
        int re2 = emdSTSBlockMapper.BatchAdd(emdstsBlockList);
        if(re2 != emdstsBlockList.size()){
            throw new InsertException("新增数据异常");
        }
    }

    private static @NotNull EMDSTSBlock getEmdSTSBlock(EMDStudentTaskSection emdSTS, BlockVo blockVo) {
        EMDSTSBlock emdSTSBlock = new EMDSTSBlock();
        emdSTSBlock.setSTSId(emdSTS.getId());
        emdSTSBlock.setBlockId(blockVo.getBlockId());
        emdSTSBlock.setStatus(0);
        emdSTSBlock.setSort(blockVo.getSort());
        emdSTSBlock.setType(blockVo.getType());
        emdSTSBlock.setTitle(blockVo.getTitle());
        emdSTSBlock.setContent(blockVo.getContent());
        emdSTSBlock.setCatalogue(blockVo.getCatalogue());
        emdSTSBlock.setPayload(blockVo.getPayload());
        return emdSTSBlock;
    }

    @Override
    public List<EMDTaskVo> getEMDTaskVoList(Integer projectId) {
        List<TaskVo> taskVoList = taskMapper.findByProjectId(projectId);
        List<EMDTaskVo> result = new ArrayList<>();
        taskVoList.forEach(taskVo->{
            EMDTaskVo eMDTaskVo = new EMDTaskVo();
            eMDTaskVo.setTaskName(taskVo.getTaskName());
            eMDTaskVo.setTaskId(taskVo.getId());
            result.add(eMDTaskVo);
        });
        return result;
    }

    @Override
    public EMDTaskDetailVo getTaskDetailVo(Integer taskId, Integer studentId) {
        if(taskId == null){
            throw new ParameterException("请求参数不能为空");
        }
        // studentId taskId ==> STId ==> List<EMDStudentTaskSection> ==> forEach List<EMDSTSBlock>
        List<EMDTaskSectionVo> emdTaskSectionVoList = emdStudentTaskSectionMapper.getByST(studentId,taskId);
        HashMap<Long, Integer> stsIdIndexMap = new HashMap<>();  // <stsId, emdTaskSectionVoList.Index>
        List<Long> stsIdList = new ArrayList<>();
        for(int i=0;i<emdTaskSectionVoList.size();i++){
            stsIdList.add(emdTaskSectionVoList.get(i).getSTSId());
            stsIdIndexMap.put(emdTaskSectionVoList.get(i).getSTSId(), i);
        }
        List<EMDTaskBlockVo> emdTaskBlockVoList = emdSTSBlockMapper.batchGetBySTSId(stsIdList);
        emdTaskBlockVoList.forEach(emdTaskBlockVo->{
            int index = stsIdIndexMap.get(emdTaskBlockVo.getSTSId());
            if(emdTaskSectionVoList.get(index).getBlockVoList()==null){
                List<EMDTaskBlockVo> list = new ArrayList<>();
                list.add(emdTaskBlockVo);
                emdTaskSectionVoList.get(index).setBlockVoList(list);
            }else{
                emdTaskSectionVoList.get(index).getBlockVoList().add(emdTaskBlockVo);
            }
        });
        EMDTaskDetailVo emdTaskDetailVo = new EMDTaskDetailVo();
        emdTaskDetailVo.setTaskId(taskId);
        emdTaskDetailVo.setSectionVoList(emdTaskSectionVoList);

        EMDTaskRecord record = new EMDTaskRecord();
        record.setStudentId(studentId);
        record.setTaskId(taskId);
        record.setType("GET");
        this.stsRecord(record);
        return emdTaskDetailVo;
    }

    @Override
    public String getTaskEMDProc(Integer taskId) {
        return taskEMdProcMapper.getTaskProcByTaskId(taskId);
    }

    @Override
    @Async
    public void stsRecord(EMDTaskRecord record) {
        record.setTime(new Date());
        emdTaskRecordMapper.insert(record);
    }

    @Override
    public void updateEMDSSTSBlockPayload(EMDSTSBlock block, String cellId, Integer taskId, Integer studentId) {
        int res = emdSTSBlockMapper.updatePayload(block);
        if(res!= 1){
            throw new UpdateException("保存数据出错了");
        }

        EMDTaskRecord record = new EMDTaskRecord();
        record.setTaskId(taskId);
        record.setStudentId(studentId);
        record.setType("UPDATE");
        record.setBlockId(block.getBlockId());
        record.setBlockSort(block.getSort());
        record.setPayload(block.getPayload());
        record.setCellId(cellId);
        this.stsRecord(record);
    }
}
