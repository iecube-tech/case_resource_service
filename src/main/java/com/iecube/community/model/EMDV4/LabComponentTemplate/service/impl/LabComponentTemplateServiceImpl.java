package com.iecube.community.model.EMDV4.LabComponentTemplate.service.impl;

import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import com.iecube.community.model.EMDV4.LabComponent.service.LabComponentService;
import com.iecube.community.model.EMDV4.LabComponentTemplate.entity.LabComponentTemplate;
import com.iecube.community.model.EMDV4.LabComponentTemplate.mapper.LabComponentTemplateMapper;
import com.iecube.community.model.EMDV4.LabComponentTemplate.service.LabComponentTemplateService;
import com.iecube.community.model.EMDV4.LabComponent.vo.LabComponentVo;
import com.iecube.community.model.auth.service.ex.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabComponentTemplateServiceImpl implements LabComponentTemplateService {

    @Autowired
    private LabComponentTemplateMapper BLCTemplateMapper;

    @Autowired
    private LabComponentService BLCService;


    @Override
    public List<LabComponentVo> getBLCTemplate(Long labId) {
        return BLCTemplateMapper.selectByLabId(labId);
    }

    @Override
    public List<LabComponentVo> getBLCTemplateByType(Long labId, String type) {
        return BLCTemplateMapper.selectByLabIdAndType(labId, type);
    }

    @Override
    public List<LabComponentVo> createBLCTemplate(Long labId, LabComponent labComponent) {
        LabComponent newBLC = BLCService.create(labComponent);
        LabComponentTemplate blct = new LabComponentTemplate();
        blct.setLabId(labId);
        blct.setComponent(newBLC.getId());
        int res = BLCTemplateMapper.insert(blct);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return this.getBLCTemplate(labId);
    }

    @Override
    public List<LabComponentVo> deleteBLCTemplate(Long labId, Long componentId) {
        BLCService.delete(componentId);
        BLCTemplateMapper.deleteByLabIdAndComponent(labId, componentId);
        return this.getBLCTemplate(labId);
    }

    @Override
    public LabComponentVo updateBLCTemplate(Long labId, LabComponent labComponent) {
        BLCService.update(labComponent);
        return BLCTemplateMapper.selectByLabIdAndComponent(labId, labComponent.getId());
    }
}
