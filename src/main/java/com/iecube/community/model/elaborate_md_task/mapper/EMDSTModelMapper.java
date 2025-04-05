package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.entity.EMDSTModel;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskModelVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EMDSTModelMapper {
    int batchAdd( List<EMDSTModel> list);

    List<EMDTaskModelVo> getTaskModelVoByST(Integer taskId, Integer studentId);

    EMDTaskModelVo getTaskModelVoByModelId(Long id);

    int updateModelStatus(long id, int status, int currAskNum);
}
