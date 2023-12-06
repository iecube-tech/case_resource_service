package com.iecube.community.model.duplicate_checking.mapper;

import com.iecube.community.model.duplicate_checking.dto.TaskStudentPDFFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DuplicateCheckingMapper {
    Integer getTaskIdByPSTId(Integer pstId);

    List<Integer> getPSTIdsByTaskId(Integer taskId);

    List<TaskStudentPDFFile> getStudentFileListByPSTId(Integer pstId);
}
