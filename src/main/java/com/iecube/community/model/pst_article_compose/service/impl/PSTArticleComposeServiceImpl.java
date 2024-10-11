package com.iecube.community.model.pst_article_compose.service.impl;

import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.pst_article_compose.entity.PSTArticleCompose;
import com.iecube.community.model.pst_article_compose.mapper.PSTArticleComposeMapper;
import com.iecube.community.model.pst_article_compose.service.PSTArticleComposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PSTArticleComposeServiceImpl implements PSTArticleComposeService {
    @Autowired
    private PSTArticleComposeMapper pstArticleComposeMapper;

    @Override
    public PSTArticleCompose getCompose(Integer articleId, Integer index){
        PSTArticleCompose pstArticleCompose = pstArticleComposeMapper.getByArticleIndex(articleId, index);
        return pstArticleCompose;
    }

    @Override
    public PSTArticleCompose composeUpdateVal(Integer composeId, String val) {
        Integer res = pstArticleComposeMapper.composeUpdateVal(composeId, val);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        PSTArticleCompose pstArticleCompose = pstArticleComposeMapper.getById(composeId);
        return pstArticleCompose;
    }

    @Override
    public PSTArticleCompose composeUpdateResult(Integer composeId, Double result) {
        Integer res  = pstArticleComposeMapper.composeUpdateResult(composeId, result);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        PSTArticleCompose pstArticleCompose = pstArticleComposeMapper.getById(composeId);
        return pstArticleCompose;
    }

    @Override
    public PSTArticleCompose composeUpdateResultStatus(Integer composeId, Double result) {
        Integer res  = pstArticleComposeMapper.composeUpdateResultStatus(composeId, result);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        PSTArticleCompose pstArticleCompose = pstArticleComposeMapper.getById(composeId);
        return pstArticleCompose;
    }

    @Override
    public List<PSTArticleCompose> composeListByPstId(Integer pstId) {
        List<PSTArticleCompose> composeList = pstArticleComposeMapper.composeListByPstId(pstId);
        return composeList;
    }
}
