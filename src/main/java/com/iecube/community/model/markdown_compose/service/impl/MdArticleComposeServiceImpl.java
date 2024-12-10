package com.iecube.community.model.markdown_compose.service.impl;

import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.markdown_compose.entity.MdArticleCompose;
import com.iecube.community.model.markdown_compose.mapper.MdArticleComposeMapper;
import com.iecube.community.model.markdown_compose.service.MdArticleComposeService;
import com.iecube.community.model.markdown_compose.service.ex.UpdateComposeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MdArticleComposeServiceImpl implements MdArticleComposeService {
    @Autowired
    private MdArticleComposeMapper mdArticleComposeMapper;

    //更新mdArticle触发
    public List<MdArticleCompose> MdArticleUpdate(Integer articleId, List<MdArticleCompose> newMdArticleComposeList){
        //
        List<MdArticleCompose> oldMdArticleComposeList = mdArticleComposeMapper.getByArticle(articleId);
        List<Long> willDelete = new ArrayList<>();
        List<MdArticleCompose> willAdd = new ArrayList<>();

        if(oldMdArticleComposeList.size() == newMdArticleComposeList.size()){
            for(int i=0; i<oldMdArticleComposeList.size(); i++){
                if(!oldMdArticleComposeList.get(i).getName().equals(newMdArticleComposeList.get(i).getName())){
                    willDelete.add(oldMdArticleComposeList.get(i).getId());
                    willAdd.add(newMdArticleComposeList.get(i));
                }
            }
            if(!willDelete.isEmpty()){
                mdArticleComposeMapper.batchDelete(willDelete);
            }
            if(!willAdd.isEmpty()){
                mdArticleComposeMapper.batchAdd(willAdd);
            }
        }else {
            mdArticleComposeMapper.deleteByArticle(articleId);
            newMdArticleComposeList.forEach(mdArticleCompose -> {
                mdArticleCompose.setArticleId(articleId);
//                System.out.println(mdArticleCompose);
            });
            Integer res = mdArticleComposeMapper.batchAdd(newMdArticleComposeList);
            if(res != newMdArticleComposeList.size()){
                throw new UpdateComposeException("更新组件数据错误");
            }
        }

        List<MdArticleCompose> mdArticleComposeList = mdArticleComposeMapper.getByArticle(articleId);
        return mdArticleComposeList;
    }

    @Override
    public List<MdArticleCompose> getComposeByArticleId(Integer articleId) {
        List<MdArticleCompose> mdArticleComposeList = mdArticleComposeMapper.getByArticle(articleId);
        return mdArticleComposeList;
    }

    @Override
    public MdArticleCompose getCompose(Integer articleId, Integer index) {
        MdArticleCompose mdArticleCompose = mdArticleComposeMapper.getByArticleIndex(articleId, index);
        return mdArticleCompose;
    }

    @Override
    public MdArticleCompose mdComposerUpdateVal(Integer composeId, String val) {
        Integer res = mdArticleComposeMapper.updateValById(composeId, val);
        if(res!= 1){
            throw new UpdateException("更新数据异常");
        }
        MdArticleCompose mdArticleCompose = mdArticleComposeMapper.getById(composeId);
        return mdArticleCompose;
    }

    @Override
    public MdArticleCompose mdComposerUpdateAnswer(Integer composeId, String answer) {
        Integer res = mdArticleComposeMapper.updateAnswerById(composeId, answer);
        if(res!= 1){
            throw new UpdateException("更新数据异常");
        }
        MdArticleCompose mdArticleCompose = mdArticleComposeMapper.getById(composeId);
        return mdArticleCompose;
    }

    @Override
    public MdArticleCompose mdComposerUpdateScore(Integer composeId, Integer score) {
        Integer res = mdArticleComposeMapper.updateScoreById(composeId, score);
        if(res!= 1){
            throw new UpdateException("更新数据异常");
        }
        MdArticleCompose mdArticleCompose = mdArticleComposeMapper.getById(composeId);
        return mdArticleCompose;
    }

    @Override
    public MdArticleCompose mdComposerUpdateSubjective(Integer composeId, Boolean subjective) {
        Integer res = mdArticleComposeMapper.updateSubjectiveById(composeId, subjective);
        if(res!= 1){
            throw new UpdateException("更新数据异常");
        }
        MdArticleCompose mdArticleCompose = mdArticleComposeMapper.getById(composeId);
        return mdArticleCompose;
    }
}
