package com.iecube.community.model.EMDV4.LabStageBlockComponent.service.impl;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4.BookLab.mapper.BookLabMapper;
import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import com.iecube.community.model.EMDV4.LabComponent.mapper.LabComponentMapper;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.entity.LabStageBlockComp;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.mapper.LabStageBlockCompMapper;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.service.LabStageBlockCompService;
import com.iecube.community.model.EMDV4.LabStageBlockComponent.vo.LabStageBlockCompVo;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabStageBlockCompServiceImpl implements LabStageBlockCompService {

    @Autowired
    private LabStageBlockCompMapper labStageBlockCompMapper;

    @Autowired
    private BookLabMapper bookLabMapper;

    @Autowired
    private LabComponentMapper labComponentMapper;

    @Override
    public List<LabStageBlockCompVo> getBlockComponents(Long blockId) {
        return labStageBlockCompMapper.getBlockCompVos(blockId);
    }

    @Override
    public List<LabStageBlockCompVo> blockAddComponent(Long blockId, Long componentId) {
        BookLabCatalog block = bookLabMapper.getById(blockId);
        LabComponent labComponent = labComponentMapper.getById(componentId);
        if(!block.getStage().equals(labComponent.getStage())){
            throw new InsertException("节点阶段和组件阶段不匹配");
        }
        List<LabStageBlockCompVo> bclist = labStageBlockCompMapper.getBlockCompVos(blockId);
        System.out.println(bclist);
        LabStageBlockComp labStageBlockComp = new LabStageBlockComp();
        labStageBlockComp.setBlockId(blockId);
        labStageBlockComp.setComponent(componentId);
        labStageBlockComp.setOrder(bclist.isEmpty()?1:(bclist.get(bclist.size()-1).getOrder())+1);
        int res = labStageBlockCompMapper.insert(labStageBlockComp);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return labStageBlockCompMapper.getBlockCompVos(blockId);
    }

    @Override
    public List<LabStageBlockCompVo> blockDelComponent(Long labStageBlockCompId) {
        LabStageBlockCompVo labStageBlockCompVo = labStageBlockCompMapper.getById(labStageBlockCompId);
        if(labStageBlockCompVo==null){
            throw new DeleteException("未找到相关数据");
        }
        int res = labStageBlockCompMapper.delete(labStageBlockCompId);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return labStageBlockCompMapper.getBlockCompVos(labStageBlockCompVo.getBlockId());
    }

    @Override
    public List<LabStageBlockCompVo> updateOrderBatch(List<LabStageBlockComp> list) {
        if(list.isEmpty()){
            throw new UpdateException("参数错误");
        }
        list.forEach(lsbc->{
            if(lsbc.getBlockId()==null || lsbc.getComponent()==null){
                throw new UpdateException("参数错误,blockId 或 componentId 为空");
            }
        });
        int res = labStageBlockCompMapper.updateOrderBatch(list);
        if(res!=list.size()){
            throw new UpdateException("更新数据异常");
        }
        return labStageBlockCompMapper.getBlockCompVos(list.get(0).getBlockId());
    }
}
