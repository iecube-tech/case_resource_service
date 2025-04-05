package com.iecube.community.model.elaborate_md_task.service;

import com.iecube.community.model.elaborate_md_task.entity.EMDSTMSBlock;
import com.iecube.community.model.elaborate_md_task.entity.EMDTaskRecord;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskDetailVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskModelVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskRefVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskVo;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.task.entity.Task;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EMDTaskService {

    void EMDTaskPublish(List<Student> studentList, List<Task> taskList);

    List<EMDTaskVo> getEMDTaskVoList(Integer projectId);

    EMDTaskDetailVo getTaskDetailVo(Integer taskId, Integer studentId);

    EMDTaskRefVo getTaskEMDProc(Integer taskId);

    void stsRecord(EMDTaskRecord record);

    void updateEMDSSTSBlockPayload(EMDSTMSBlock block, String cellId, Integer taskId, Integer studentId);

    void uploadDeviceLog(Integer studentId, Integer taskId, MultipartFile file);

    Boolean toNextSection(Long STMSId);

    EMDTaskModelVo upModelStatus(Long modelId, int status, int currAskNum);
}
