package com.iecube.community.model.elaborate_md.lab_proc.service.impl;

import com.iecube.community.exception.ParameterException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProcRef;
import com.iecube.community.model.elaborate_md.lab_proc.mapper.LabProcMapper;
import com.iecube.community.model.elaborate_md.lab_proc.mapper.LabProcRefMapper;
import com.iecube.community.model.elaborate_md.lab_proc.qo.LabProcQo;
import com.iecube.community.model.elaborate_md.lab_proc.service.LabProcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabProcServiceImpl implements LabProcService {
    @Autowired
    private LabProcMapper labProcMapper;

    @Autowired
    private LabProcRefMapper labProcRefMapper;

    @Override
    public List<LabProc> getByCourse(long courseId) {
        return labProcMapper.getLabProcByCourse(courseId);
    }

    @Override
    public List<LabProc> createLabProc(LabProcQo labProcQo) {
        if(labProcQo==null||labProcQo.getCourseId()==null){
            throw new InsertException("缺少必要参数");
        }
        List<LabProc> labProcList = labProcMapper.getLabProcByCourse(labProcQo.getCourseId());
        LabProc labProc = new LabProc();
        labProc.setName(labProcQo.getName());
        labProc.setSectionPrefix(labProcQo.getSectionPrefix());
        labProc.setParentId(labProcQo.getCourseId());
        labProc.setSort(labProcList.isEmpty()?1:labProcList.get(labProcList.size()-1).getSort()+1);
        int res = labProcMapper.createLabProc(labProc);
        if(res != 1){
            throw new InsertException("新增数据异常");
        }
        return labProcMapper.getLabProcByCourse(labProcQo.getCourseId());
    }

    @Override
    public List<LabProc> batchUpdateSort(List<LabProc> labProcList) {
        if(labProcList == null || labProcList.isEmpty()){
            throw new UpdateException("更新数据参数错误");
        }
        int res = labProcMapper.batchUpdateSort(labProcList);
        if(res != labProcList.size()){
            throw new UpdateException("更新数据异常，参数长度与结果长度不符");
        }
        return labProcMapper.getLabProcByCourse(labProcList.get(0).getParentId());
    }

    @Override
    public List<LabProc> updateLabProc(LabProcQo labProcQo) {
        LabProc labProc = labProcMapper.getLabProcById(labProcQo.getId());
        labProc.setSectionPrefix(labProcQo.getSectionPrefix());
        labProc.setName(labProcQo.getName());
        int res = labProcMapper.updateLabProc(labProc);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
        return labProcMapper.getLabProcByCourse(labProc.getParentId());
    }

    @Override
    public List<LabProc> deleteLabProc(LabProcQo labProcQo) {
        LabProc labProc = labProcMapper.getLabProcById(labProcQo.getId());
        if(labProc == null){
            throw new DeleteException("数据不存在");
        }
        int res = labProcMapper.deleteLabProc(labProcQo.getId());
        if(res != 1){
            throw new DeleteException("删除数据异常");
        }
        return labProcMapper.getLabProcByCourse(labProc.getParentId());
    }

    @Override
    public LabProcRef getLabProcRef(long labId) {
        LabProcRef labProcRef = labProcRefMapper.getByLabId(labId);
        if(labProcRef == null){
            LabProcRef labProcRef1 = new LabProcRef();
            labProcRef1.setLabProcId(labId);
            labProcRef1.setReference("");
            int res = labProcRefMapper.insert(labProcRef1);
            if(res != 1){
                throw new InsertException("新增数据异常");
            }
            labProcRef=labProcRef1;
        }
        return labProcRef;
    }

    @Override
    public LabProcRef updateLabProcRef(LabProcRef labProcRef) {
        if(labProcRef == null || labProcRef.getId()==null){
            throw new ParameterException("参数异常");
        }
        int res = labProcRefMapper.updateByLabId(labProcRef);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
        return labProcRefMapper.getByLabId(labProcRef.getId());
    }
}
