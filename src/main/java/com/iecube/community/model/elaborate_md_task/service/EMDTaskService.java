package com.iecube.community.model.elaborate_md_task.service;

import com.iecube.community.model.elaborate_md_task.entity.EMDSTSBlock;
import com.iecube.community.model.elaborate_md_task.entity.EMDTaskRecord;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskDetailVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskVo;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.task.entity.Task;

import java.util.List;

public interface EMDTaskService {

    void EMDTaskPublish(List<Student> studentList, List<Task> taskList);

    List<EMDTaskVo> getEMDTaskVoList(Integer projectId);

    EMDTaskDetailVo getTaskDetailVo(Integer taskId, Integer studentId);

    String getTaskEMDProc(Integer taskId);

    void stsRecord(EMDTaskRecord record);

    void updateEMDSSTSBlockPayload(EMDSTSBlock block, String cellId, Integer taskId,  Integer studentId);
}
