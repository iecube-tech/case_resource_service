package com.iecube.community.model.EMDV4Project.EMDV4_component.service.impl;

import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import com.iecube.community.model.EMDV4Project.EMDV4_component.mapper.EMDV4ComponentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_component.service.EMDV4ComponentService;
import com.iecube.community.model.auth.service.ex.UpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EMDV4ComponentServiceImpl implements EMDV4ComponentService {
    @Autowired
    private EMDV4ComponentMapper emdV4ComponentMapper;


    @Override
    public EMDV4Component updateStatus(String id, int status) {
        int res = emdV4ComponentMapper.updateStatus(id, status);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return emdV4ComponentMapper.getById(id);
    }

    @Override
    public EMDV4Component updatePayload(String id, String payload) {
        int res = emdV4ComponentMapper.updatePayload(id, payload);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return emdV4ComponentMapper.getById(id);
    }

    @Override
    public EMDV4Component updateScore(String id, double score) {
        EMDV4Component oldComponent = emdV4ComponentMapper.getById(id);
        Double totalScore = oldComponent.getTotalScore();
        double per =0.0;
        if(totalScore==null || totalScore==0){
            score=0;
        }else {
            if(score>totalScore){
                throw new UpdateException("成绩大于总成绩");
            }
            per = (score / totalScore) *100;
        }
        int res = emdV4ComponentMapper.updateScore(id,score,per);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return emdV4ComponentMapper.getById(id);
    }
}
