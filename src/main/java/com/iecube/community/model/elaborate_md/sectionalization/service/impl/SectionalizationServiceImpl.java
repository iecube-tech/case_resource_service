package com.iecube.community.model.elaborate_md.sectionalization.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.elaborate_md.block.service.BlockService;
import com.iecube.community.model.elaborate_md.sectionalization.entity.Sectionalization;
import com.iecube.community.model.elaborate_md.sectionalization.mapper.SectionalizationMapper;
import com.iecube.community.model.elaborate_md.sectionalization.qo.SectionalizationQo;
import com.iecube.community.model.elaborate_md.sectionalization.service.SectionalizationService;
import com.iecube.community.model.elaborate_md.sectionalization.vo.SectionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SectionalizationServiceImpl implements SectionalizationService {

    @Autowired
    private SectionalizationMapper sectionalizationMapper;

    @Autowired
    private BlockService blockService;


    @Override
    public void createSectionalization(SectionalizationQo sectionalizationQo) {
        Sectionalization sectionalization = new Sectionalization();
        List<Sectionalization> sectionalizationList = sectionalizationMapper.getByLabModelId(sectionalizationQo.getLabModelId());
        sectionalization.setParentId(sectionalizationQo.getLabModelId());
        sectionalization.setSort(sectionalizationList.isEmpty()?1:sectionalizationList.get(sectionalizationList.size()-1).getSort()+1);
        int res = sectionalizationMapper.createSectionalization(sectionalization);
        if(res != 1){
            throw new InsertException("添加数据异常");
        }
    }

    @Override
    public void deleteSectionalization(SectionalizationQo sectionalizationQo) {
        Sectionalization sectionalization = sectionalizationMapper.getById(sectionalizationQo.getId());
        if(sectionalization==null){
            throw new DeleteException("数据不存在，删除异常");
        }
        int res = sectionalizationMapper.deleteSectionalization(sectionalizationQo.getId());
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public void updateSectionalizationSort(List<Sectionalization> list) {
        if(list==null || list.isEmpty()){
            throw new UpdateException("参数错误");
        }
        int res = sectionalizationMapper.batchUpdateSort(list);
        if(res != list.size()){
            throw new UpdateException("更新数据异常，参数长度与结果长度不符");
        }
    }

    @Override
    public List<Sectionalization> getSectionalizationByLabModeId(long labModelId) {
        return sectionalizationMapper.getByLabModelId(labModelId);
    }

    @Override
    public List<SectionVo> getSectionVoByLabModelId(long labModelId) {
        List<SectionVo> sectionVoList = new ArrayList<>();
        List<Sectionalization> sectionList = this.getSectionalizationByLabModeId(labModelId);
        sectionList.forEach(section->{
            SectionVo sectionVo = new SectionVo();
            sectionVo.setId(section.getId());
            sectionVo.setParentId(section.getParentId());
            sectionVo.setSort(section.getSort());
            sectionVo.setHasChildren(section.isHasChildren());
            sectionVo.setName(section.getName());
            sectionVo.setLevel(section.getLevel());
            sectionVo.setBlockList(blockService.getBlockListBySection(section.getId()));
            sectionVoList.add(sectionVo);
        });
        return sectionVoList;
    }
}
