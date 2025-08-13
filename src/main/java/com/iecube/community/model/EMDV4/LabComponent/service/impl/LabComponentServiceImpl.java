package com.iecube.community.model.EMDV4.LabComponent.service.impl;

import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import com.iecube.community.model.EMDV4.LabComponent.mapper.LabComponentMapper;
import com.iecube.community.model.EMDV4.LabComponent.service.LabComponentService;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabComponentServiceImpl implements LabComponentService {

    @Autowired
    private LabComponentMapper labComponentMapper;


    @Override
    public LabComponent create(LabComponent labComponent) {
        int res = labComponentMapper.insert(labComponent);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return labComponent;
    }

    @Override
    public void delete(Long id) {
        LabComponent labComponent = labComponentMapper.getById(id);
        if(labComponent==null){
            throw new DeleteException("未找到相关数据");
        }
        int res = labComponentMapper.deleteById(id);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public LabComponent update(LabComponent labComponent) {
        LabComponent oldLabComponent = labComponentMapper.getById(labComponent.getId());
        if(oldLabComponent==null){
            throw new UpdateException("未找到相关数据");
        }
        int res = labComponentMapper.update(labComponent);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return  labComponentMapper.getById(labComponent.getId());
    }
}
