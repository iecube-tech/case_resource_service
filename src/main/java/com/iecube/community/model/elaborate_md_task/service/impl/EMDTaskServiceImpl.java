package com.iecube.community.model.elaborate_md_task.service.impl;

import com.iecube.community.exception.ParameterException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.elaborate_md.block.service.BlockService;
import com.iecube.community.model.elaborate_md.block.vo.BlockVo;
import com.iecube.community.model.elaborate_md.lab_model.entity.LabModel;
import com.iecube.community.model.elaborate_md.lab_model.service.LabModelService;
import com.iecube.community.model.elaborate_md.sectionalization.entity.Sectionalization;
import com.iecube.community.model.elaborate_md.sectionalization.service.SectionalizationService;
import com.iecube.community.model.elaborate_md_task.entity.*;
import com.iecube.community.model.elaborate_md_task.mapper.*;
import com.iecube.community.model.elaborate_md_task.service.EMDTaskService;
import com.iecube.community.model.elaborate_md_task.vo.*;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.task.entity.Task;
import com.iecube.community.model.task.entity.TaskVo;
import com.iecube.community.model.task.mapper.TaskMapper;
import com.iecube.community.model.task_e_md_proc.mapper.TaskEMdProcMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class EMDTaskServiceImpl implements EMDTaskService {

    @Autowired
    private SectionalizationService sectionalizationService;

    @Autowired
    private LabModelService labModelService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private EMDStudentTaskMapper emdStudentTaskMapper;

    @Autowired
    private EMDSTModelMapper emdstModelMapper;

    @Autowired
    private EMDSTMSectionMapper EMDSTMSectionMapper;

    @Autowired
    private EMDSTMSBlockMapper emdSTSBlockMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskEMdProcMapper taskEMdProcMapper;

    @Autowired
    private EMDTaskRecordMapper emdTaskRecordMapper;

    @Autowired
    private ResourceService resourceService;


    @Override
    /*
     * 发布emd实验的task。 studentList 和 taskList 生成的emdStudentTasks 批量添加到 emdStudentTasks
     */
    public void EMDTaskPublish(List<Student> studentList, List<Task> taskList) {
        Map<Integer,Long> taskProcMap = new HashMap<>(); // <taskId, labProcId>

        Map<Long, List<LabModel>> procModelMap = new HashMap<>();  // 取到 指导书的 model <labId, labModelList>

        Map<Long,List<Sectionalization>> modelSectionMap = new HashMap<>(); // 取到 task对应的实验指导书 的 section <modelId, sectionList>

        Map<Long, List<BlockVo>> sectionBlockMap = new HashMap<>(); // 取到 section 对应的 blockList <sectionId, blockList>

        for(Task task : taskList){ // task 中带着指导书的id ==> procId
            taskProcMap.put(task.getId(),task.getTaskEMdProc());
            List<LabModel> labModelList = labModelService.getByLabProc(task.getTaskEMdProc());
            procModelMap.put(task.getTaskEMdProc(), labModelList);
        }

        procModelMap.forEach((labId, labModelList) -> {
            labModelList.forEach(labModel -> {
                List<Sectionalization> sectinList = sectionalizationService.getSectionalizationByLabModeId(labModel.getId());
                modelSectionMap.put(labModel.getId(), sectinList);
            });
        });

        modelSectionMap.forEach((modelId, sectionList)->{
            sectionList.forEach(section -> {
                List<BlockVo> blockVos = blockService.getBlockVoListBySection(section.getId());
                sectionBlockMap.put(section.getId(), blockVos);
            });
        });

        // student-task 批量添加
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

        // stModel 批量增加
        List<EMDSTModel> EMDSTModelList = new ArrayList<>();
        // 创建 studentTask -- model
        for(EMDStudentTask emdStudentTask: EMDStudentTaskList){
            List<LabModel> labModelList = procModelMap.get(taskProcMap.get(emdStudentTask.getTaskId()));
            for(LabModel labModel: labModelList){
                EMDSTModel emdSTModel = getEmdSTModel(emdStudentTask, labModel);
                EMDSTModelList.add(emdSTModel);
            }
        }
        int STMre = emdstModelMapper.batchAdd(EMDSTModelList);
        if(STMre!=EMDSTModelList.size()){
            throw new InsertException("新增数据异常");
        }

        List<EMDSTMSection> EMDSTMSectionList = new ArrayList<>();
        // 创建 student-task -- section
        for(EMDSTModel emdstModel : EMDSTModelList){
            List<Sectionalization> sectionList = modelSectionMap.get(emdstModel.getModelId());
            for(Sectionalization section : sectionList){
                EMDSTMSection emdstmSection = new EMDSTMSection();
                emdstmSection.setStmId(emdstModel.getId());
                emdstmSection.setSectionId(section.getId());
                emdstmSection.setSort(section.getSort());
                emdstmSection.setStatus(0);
                EMDSTMSectionList.add(emdstmSection);
            }
        }
        int re1 = EMDSTMSectionMapper.BatchAdd(EMDSTMSectionList);
        if(re1 != EMDSTMSectionList.size()){
            throw new InsertException("新增数据异常");
        }

        List<EMDSTMSBlock> EMDSTMSBlockList = new ArrayList<>();
        for(EMDSTMSection emdstmSection : EMDSTMSectionList){
            List<BlockVo> blockList  = sectionBlockMap.get(emdstmSection.getSectionId());
            for(BlockVo blockVo : blockList){
                EMDSTMSBlock emdSTSBlock = getEmdSTSBlock(emdstmSection, blockVo);
                EMDSTMSBlockList.add(emdSTSBlock);
            }
        }
        int re2 = emdSTSBlockMapper.BatchAdd(EMDSTMSBlockList);
        if(re2 != EMDSTMSBlockList.size()){
            throw new InsertException("新增数据异常");
        }
    }

    private static @NotNull EMDSTModel getEmdSTModel(EMDStudentTask emdStudentTask, LabModel labModel) {
        EMDSTModel emdSTModel = new EMDSTModel();
        emdSTModel.setModelId(labModel.getId());
        emdSTModel.setStId(emdStudentTask.getId());
        emdSTModel.setName(labModel.getName());
        emdSTModel.setIcon(labModel.getIcon());
        emdSTModel.setSort(labModel.getSort());
        emdSTModel.setIsNeedAiAsk(labModel.getIsNeedAiAsk());
        emdSTModel.setAskNum(labModel.getAskNum());
        emdSTModel.setCurrAskNum(0);
        emdSTModel.setSectionPrefix(labModel.getSectionPrefix());
        emdSTModel.setStage(labModel.getStage());
        emdSTModel.setStatus(0);
        return emdSTModel;
    }

    private static @NotNull EMDSTMSBlock getEmdSTSBlock(EMDSTMSection STModelSec, BlockVo blockVo) {
        EMDSTMSBlock emdSTSBlock = new EMDSTMSBlock();
        emdSTSBlock.setSTMSId(STModelSec.getId());
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
        if(taskId == null || studentId==null){
            throw new ParameterException("请求参数不能为空");
        }
        // studentId taskId ==> STId ===> List<EMDSTModel> ==> forEach List<EMDSTMSection> ==> forEach List<EMDSTMSBlock>
        HashMap<Long, List<EMDTaskSectionVo>> modelSectionsMap = new HashMap<>();   // <modelId, List<EMDTaskSectionVo>>

        // 获取 modelList
        List<EMDTaskModelVo> emdTaskModelVoList = emdstModelMapper.getTaskModelVoByST(taskId, studentId);
        for (EMDTaskModelVo emdTaskModelVo : emdTaskModelVoList) {
            // 获取sectionList
            List<EMDTaskSectionVo> emdTaskSectionVoList = EMDSTMSectionMapper.getBySTM(emdTaskModelVo.getId());
            modelSectionsMap.put(emdTaskModelVo.getId(), emdTaskSectionVoList);
        }

        modelSectionsMap.forEach((modelVoId, sectionVoList) -> {
            // 获取blockList
            HashMap<Long, Integer> sectionIdIndexMap = new HashMap<>();
            List<Long> sectionIdList = new ArrayList<>();
            for(int i=0; i< sectionVoList.size(); i++){
                sectionIdList.add(sectionVoList.get(i).getId());
                sectionIdIndexMap.put(sectionVoList.get(i).getId(), i);
            }
            List<EMDTaskBlockVo> emdTaskBlockVoList = emdSTSBlockMapper.batchGetBySTMSId(sectionIdList);
            emdTaskBlockVoList.forEach(emdTaskBlockVo -> {
                int index = sectionIdIndexMap.get(emdTaskBlockVo.getSTMSId());
                if(sectionVoList.get(index).getBlockVoList()==null){
                    List<EMDTaskBlockVo> list = new ArrayList<>();
                    list.add(emdTaskBlockVo);
                    sectionVoList.get(index).setBlockVoList(list);
                }else{
                    sectionVoList.get(index).getBlockVoList().add(emdTaskBlockVo);
                }
            });

            for(EMDTaskModelVo modelVo: emdTaskModelVoList){
                if(modelVoId.equals(modelVo.getId())){
                    modelVo.setSectionVoList(sectionVoList);
                }
            }
        });
        EMDTaskDetailVo emdTaskDetailVo = new EMDTaskDetailVo();
        emdTaskDetailVo.setTaskId(taskId);
        emdTaskDetailVo.setLabModelVoList(emdTaskModelVoList);

        // 记录
        EMDTaskRecord record = new EMDTaskRecord();
        record.setStudentId(studentId);
        record.setTaskId(taskId);
        record.setType("GET");
        this.stsRecord(record);

        return emdTaskDetailVo;
    }

    @Override
    public EMDTaskRefVo getTaskEMDProc(Integer taskId) {
        return taskEMdProcMapper.getTaskProcByTaskId(taskId);
    }

    @Override
    @Async
    public void stsRecord(EMDTaskRecord record) {
        record.setTime(new Date());
        emdTaskRecordMapper.insert(record);
    }

    @Override
    public void updateEMDSSTSBlockPayload(EMDSTMSBlock block, String cellId, Integer taskId, Integer studentId) {
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

    @Override
    public void uploadDeviceLog(Integer studentId, Integer taskId, MultipartFile file) {
        try{
            Resource resource = resourceService.UploadFile(file, studentId);
            EMDTaskRecord record = new EMDTaskRecord();
            record.setTaskId(taskId);
            record.setStudentId(studentId);
            record.setType("DLOG");
            record.setResourceId(resource.getId());
            this.stsRecord(record);
        }catch (Exception e){
            throw new UpdateException("上传数据异常"+e.getMessage());
        }
    }

    @Override
    public Boolean toNextSection(Long STMSId) {
//        EMDTaskSectionVo emdTaskSectionVo = EMDSTMSectionMapper.getBySTSId(STSId);
        // todo emdTaskSectionVo.setBlockVoList();
        // 结果校验
        // 设置status
        int res = EMDSTMSectionMapper.upStatus(STMSId, 1);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
        return true;
    }

    @Override
    public EMDTaskModelVo upModelStatus(Long modelId, int status, int currAskNum) {
        int res = emdstModelMapper.updateModelStatus(modelId, status, currAskNum);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
        return emdstModelMapper.getTaskModelVoByModelId(modelId);
    }
}
