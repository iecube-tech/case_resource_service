package com.iecube.community.model.elaborate_md_task.service.impl;

import com.iecube.community.exception.NotFoundException;
import com.iecube.community.exception.ParameterException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.elaborate_md.block.entity.Block;
import com.iecube.community.model.elaborate_md.block.service.BlockService;
import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.elaborate_md.sectionalization.entity.Sectionalization;
import com.iecube.community.model.elaborate_md.sectionalization.service.SectionalizationService;
import com.iecube.community.model.elaborate_md_task.dto.EMDStuTaskDetailDto;
import com.iecube.community.model.elaborate_md_task.entity.EMDSTSAiRecord;
import com.iecube.community.model.elaborate_md_task.entity.EMDSTSBlock;
import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTask;
import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTaskSection;
import com.iecube.community.model.elaborate_md_task.mapper.EMDSTSBlockMapper;
import com.iecube.community.model.elaborate_md_task.mapper.EMDStudentTaskMapper;
import com.iecube.community.model.elaborate_md_task.mapper.EMDStudentTaskSectionMapper;
import com.iecube.community.model.elaborate_md_task.service.EMDTaskService;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskBlockVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskDetailVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskSectionVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskVo;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.task.entity.Task;
import com.iecube.community.model.task.entity.TaskVo;
import com.iecube.community.model.task.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Override
    /**
     * 发布emd实验的task。 studentList 和 taskList 生成的emdStudentTasks 批量添加到 emdStudentTasks
     *
     *
     */
    public void EMDTaskPublish(List<Student> studentList, List<Task> taskList) {
        Map<Integer,Long> taskProcMap = new HashMap<>(); // <taskId, labProcId>
        Map<Long,List<Sectionalization>> procSectionMap = new HashMap<>(); // 取到 task对应的实验指导书 的 section <labId, sectionList>
        Map<Long, List<Block>> sectionBlockMap = new HashMap<>(); // 取到 section 对应的 blockList <sectionId, blockList>
        for(Task task : taskList){ // task 中带着指导书的id ==> procId
            taskProcMap.put(task.getId(),task.getTaskEMdProc());
            List<Sectionalization> sectionList = sectionalizationService.getSectionalizationByLabProcId(task.getTaskEMdProc());
            procSectionMap.put(task.getTaskEMdProc(), sectionList);
        }
        procSectionMap.forEach((labId,sectionList)->{
            sectionList.forEach(section->{
               List<Block> blocks = blockService.getBlockListBySection(section.getId());
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
            List<Block> blockList  = sectionBlockMap.get(emdSTS.getSectionId());
            for(Block block : blockList){
                EMDSTSBlock emdSTSBlock = new EMDSTSBlock();
                emdSTSBlock.setSTSId(emdSTS.getId());
                emdSTSBlock.setBlockId(block.getId());
                emdSTSBlock.setResult(0);
                emdstsBlockList.add(emdSTSBlock);
            }
        }
        int re2 = emdSTSBlockMapper.BatchAdd(emdstsBlockList);
        if(re2 != emdstsBlockList.size()){
            throw new InsertException("新增数据异常");
        }
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
        List<EMDStuTaskDetailDto> emdStuTaskDetailDtoList = emdStudentTaskMapper.getStuTaskDetail(studentId,taskId);
        if(emdStuTaskDetailDtoList==null||emdStuTaskDetailDtoList.isEmpty()){
            throw new NotFoundException("学生实验列表为空");
        }
        Map<Long, List<EMDStuTaskDetailDto>> sectionMap = new HashMap<>();
        emdStuTaskDetailDtoList.forEach(std->{
            if(sectionMap.containsKey(std.getStsId())){
                sectionMap.get(std.getStsId()).add(std);
            }else {
                sectionMap.put(std.getStsId(),new ArrayList<>());
                sectionMap.get(std.getStsId()).add(std);
            }
        });
        EMDTaskDetailVo res = new EMDTaskDetailVo();
        List<EMDTaskSectionVo> EMDSectionVoList = new ArrayList<>();
        sectionMap.forEach((STSId,stdList)->{
            EMDTaskSectionVo EMDTaskSectionVo = new EMDTaskSectionVo();
            EMDTaskSectionVo.setSTSId(STSId);
            EMDTaskSectionVo.setStatus(stdList.get(0).getStsStatus());
            EMDTaskSectionVo.setSort(stdList.get(0).getStsSort());
            List<EMDTaskBlockVo> EMDTaskBlockVoList = new ArrayList<>();
            stdList.forEach(std->{
                EMDTaskBlockVo blockVo = new EMDTaskBlockVo();
                blockVo.setBlockId(std.getBlockId());
                blockVo.setType(std.getType());
                blockVo.setTitle(std.getTitle());
                blockVo.setContent(std.getContent());
                blockVo.setCatalogue(std.getCatalogue());
                blockVo.setConfData(std.getConfData());
                blockVo.setReferenceData(std.getReferenceData());
                blockVo.setDataTemplate(std.getDataTemplate());
                blockVo.setStuData(std.getStsBlockStuData()==null?std.getDataTemplate():std.getStsBlockStuData());
                blockVo.setResult(std.getStsBlockResult());
                EMDTaskBlockVoList.add(blockVo);
            });
            EMDTaskSectionVo.setBlockVoList(EMDTaskBlockVoList);
            EMDSectionVoList.add(EMDTaskSectionVo);
        });
        res.setSectionVoList(EMDSectionVoList);
        return res;
    }
}
