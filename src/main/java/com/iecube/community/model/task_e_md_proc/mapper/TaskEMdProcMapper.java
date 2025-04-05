package com.iecube.community.model.task_e_md_proc.mapper;

import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskRefVo;
import com.iecube.community.model.task_e_md_proc.entity.TaskEMdProc;
import com.iecube.community.model.task_e_md_proc.entity.TaskTemplateEMdProc;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskEMdProcMapper {
    int taskTemplateAddProc(TaskTemplateEMdProc taskTemplateEMdProc);
    LabProc getLabProcByTaskTemplateId(int taskTemplateId);
    int deleteLabProcByTaskTemplateId(int taskTemplateId);

    int taskAddProc(TaskEMdProc taskEMdProc);
    LabProc getLabProcByTaskId(int taskId);
    int deleteLabProcByTaskId(int taskId);
    EMDTaskRefVo getTaskProcByTaskId(int taskId);
}
