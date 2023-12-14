package com.iecube.community.model.duplicate_checking.mapper;

import com.iecube.community.model.duplicate_checking.dto.TaskStudentPDFFile;
import com.iecube.community.model.duplicate_checking.entity.RepetitiveRate;
import com.iecube.community.model.duplicate_checking.vo.RepetitiveRateVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DuplicateCheckingMapper {
    Integer getTaskIdByPSTId(Integer pstId);

    List<Integer> getPSTIdsByTaskId(Integer taskId);

    List<TaskStudentPDFFile> getStudentFileListByPSTId(Integer pstId);

    Integer insertRepetitiveRate(RepetitiveRate repetitiveRate);

    List<RepetitiveRateVo> getRepetitiveRateVoByTaskId(Integer taskId);

    Integer deleteRepetitiveRate(Integer taskId);
    Integer deleteRepetitiveRateByPstId(Integer pstId);
    List<RepetitiveRateVo> getRepetitiveRateVoByPstId(Integer pstId);
}
