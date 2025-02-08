package com.iecube.community.model.elaborate_md.lab_proc.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.elaborate_md.lab_proc.entity.LabProc;
import com.iecube.community.model.elaborate_md.lab_proc.mapper.LabProcMapper;
import com.iecube.community.model.elaborate_md.lab_proc.qo.LabProcQo;
import com.iecube.community.model.elaborate_md.lab_proc.service.LabProcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabProcServiceImpl implements LabProcService {
    @Autowired
    private LabProcMapper labProcMapper;

    @Override
    public List<LabProc> getByCourse(long courseId) {
        return labProcMapper.getLabProcByCourse(courseId);
    }

    @Override
    public List<LabProc> createLabProc(LabProcQo labProcQo) {
        if(labProcQo==null||labProcQo.getCourseId()==null){
            throw new InsertException("缺少必要参数");
        }
        LabProc labProc = new LabProc();
        labProc.setName(labProcQo.getName());
        labProc.setParentId(labProcQo.getCourseId());
        labProc.setSort(labProcQo.getSort());
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
    public LabProc updateLabProc(LabProc labProc) {
        int res = labProcMapper.updateLabProc(labProc);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
        return labProc;
    }

    @Override
    public List<LabProc> deleteLabProc(long id) {
        LabProc labProc = labProcMapper.getLabProcById(id);
        if(labProc == null){
            throw new DeleteException("数据不存在");
        }
        int res = labProcMapper.deleteLabProc(id);
        if(res != 1){
            throw new DeleteException("删除数据异常");
        }
        return labProcMapper.getLabProcByCourse(labProc.getParentId());
    }
}
