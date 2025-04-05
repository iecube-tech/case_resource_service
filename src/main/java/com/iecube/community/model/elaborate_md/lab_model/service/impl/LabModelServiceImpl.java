package com.iecube.community.model.elaborate_md.lab_model.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.elaborate_md.lab_model.entity.LabModel;
import com.iecube.community.model.elaborate_md.lab_model.mapper.LabModelMapper;
import com.iecube.community.model.elaborate_md.lab_model.qo.LabModelQo;
import com.iecube.community.model.elaborate_md.lab_model.service.LabModelService;
import com.iecube.community.model.elaborate_md.lab_model.vo.LabModelVo;
import com.iecube.community.model.elaborate_md.sectionalization.service.SectionalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LabModelServiceImpl implements LabModelService {

    @Autowired
    private LabModelMapper labModelMapper;

    @Autowired
    private SectionalizationService sectionService;


    @Override
    public List<LabModel> getByLabProc(Long labProcId) {
        return labModelMapper.getLabModelsByLabId(labProcId);
    }

    @Override
    public List<LabModel> createLabModel(LabModelQo labModelQo) {
        if(labModelQo == null ||labModelQo.getLabProcId() == null){
            throw new InsertException("缺少必要参数");
        }
        List<LabModel> labModelList = labModelMapper.getLabModelsByLabId(labModelQo.getLabProcId());
        LabModel labModel = new LabModel();
        labModel.setName(labModelQo.getName());
        labModel.setIcon(labModelQo.getIcon());
        labModel.setIsNeedAiAsk(labModelQo.getIsNeedAiAsk());
        labModel.setAskNum(labModelQo.getAskNum());
        labModel.setSectionPrefix(labModelQo.getSectionPrefix());
        labModel.setStage(labModelQo.getStage());
        labModel.setParentId(labModelQo.getLabProcId());
        labModel.setSort(labModelList.isEmpty()?1:labModelList.get(labModelList.size()-1).getSort()+1);
        int res = labModelMapper.createLabModel(labModel);
        if(res != 1){
            throw new InsertException("新增数据异常");
        }
        return labModelMapper.getLabModelsByLabId(labModelQo.getLabProcId());
    }

    @Override
    public List<LabModel> batchUpdateSort(List<LabModel> labModelList) {
        if(labModelList == null || labModelList.isEmpty()){
            throw new UpdateException("更新数据参数错误");
        }
        int res = labModelMapper.batchUpdateSort(labModelList);
        if(res != labModelList.size()){
            throw new UpdateException("更新数据异常，参数长度与结果长度不符");
        }
        return labModelMapper.getLabModelsByLabId(labModelList.get(0).getParentId());
    }

    @Override
    public List<LabModel> updateLabModel(LabModelQo labModelQo) {
        LabModel labModel = labModelMapper.getLabModelById(labModelQo.getId());
        labModel.setName(labModelQo.getName());
        labModel.setIcon(labModelQo.getIcon());
        labModel.setIsNeedAiAsk(labModelQo.getIsNeedAiAsk());
        labModel.setAskNum(labModelQo.getAskNum());
        labModel.setSectionPrefix(labModelQo.getSectionPrefix());
        labModel.setStage(labModelQo.getStage());
        int res = labModelMapper.updateLabModel(labModel);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
        return labModelMapper.getLabModelsByLabId(labModel.getParentId());
    }

    @Override
    public List<LabModel> deleteLabModel(LabModelQo labModelQo) {
        LabModel labModel = labModelMapper.getLabModelById(labModelQo.getId());
        if(labModel == null){
            throw new DeleteException("数据不存在");
        }
        int res = labModelMapper.deleteLabModel(labModel);
        if (res != 1){
            throw new DeleteException("删除数据异常");
        }
        return labModelMapper.getLabModelsByLabId(labModel.getParentId());
    }

    @Override
    public List<LabModelVo> getLabModelVoList(Long labProcId) {
        List<LabModelVo> labModelVoList = new ArrayList<>();
        List<LabModel> labModelList = this.getByLabProc(labProcId);
        labModelList.forEach(labModel -> {
            LabModelVo labModelVo = new LabModelVo();
            labModelVo.setId(labModel.getId());
            labModelVo.setName(labModel.getName());
            labModelVo.setIcon(labModel.getIcon());
            labModelVo.setParentId(labModel.getParentId());
            labModelVo.setSort(labModel.getSort());
            labModelVo.setHasChildren(labModel.isHasChildren());
            labModelVo.setSectionVoList(sectionService.getSectionVoByLabModelId(labModel.getId()));
            labModelVoList.add(labModelVo);
        });
        return labModelVoList;
    }
}
