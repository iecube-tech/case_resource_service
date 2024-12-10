package com.iecube.community.model.classification.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.PermissionDeniedException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.classification.entity.Classification;
import com.iecube.community.model.classification.mapper.ClassificationMapper;
import com.iecube.community.model.classification.service.ClassificationService;
import com.iecube.community.model.classification.service.ex.ClassificationDuplicateException;
import com.iecube.community.model.classification.service.ex.ClassificationNotFoundException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ClassificationServiceImpl implements ClassificationService {

    @Autowired
    private ClassificationMapper classificationMapper;

    @Override
    public void insert(Classification classification, Integer lastModifiedUser) {
        /**判断是否为管理员操作 userType=1**/
        //todo
        /**更新日志字段**/
        classification.setCreator(lastModifiedUser);
        classification.setCreateTime(new Date());
        classification.setLastModifiedTime(new Date());
        classification.setLastModifiedUser(lastModifiedUser);
        /**判断产品方向下是否已经存在该类别, 如果存在抛出异常 ClassificationDuplicateException ，不存在则添加**/
        Classification exitsClassification = classificationMapper.findNameWithParenId(classification.getParentId(),classification.getName());
        if (exitsClassification != null){
            throw new ClassificationDuplicateException("产品方向下该类别已存在");
        }
        Integer rows = classificationMapper.insert(classification);
        if (rows != 1){
            throw new InsertException("插入数据异常");
        }
    }

    @Override
    public void update(Classification classification, Integer lastModifiedUser) {
        /**判断是否为管理员操作 userType=1**/
        //todo
        classification.setLastModifiedTime(new Date());
        classification.setLastModifiedUser(lastModifiedUser);
        /**判断修改的类别是否存在 抛出 ClassificationNotFoundException 异常**/
        Classification oldClassification = classificationMapper.findById(classification.getId());
        if (oldClassification == null){
            throw new ClassificationNotFoundException("未找到该类别");
        }
        /**判断更新后是否有冲突**/
        Classification exitsClassification = classificationMapper.findNameWithParenId(classification.getParentId(),classification.getName());
        if (exitsClassification != null){
            throw new ClassificationDuplicateException("产品方向下该类别已存在，修改无效");
        }
        /**更新**/
        Integer rows = classificationMapper.update(classification);
        if (rows != 1){
            throw new UpdateException("更新数据异常");
        }

    }

    @Override
    public void delete(Integer id, Integer lastModifiedUser) {
        /**判断是否为管理员操作 userType=1**/
        //todo
        /**判断是否存在**/
        Classification exitsClassification = classificationMapper.findById(id);
        if (exitsClassification == null){
            throw new ClassificationNotFoundException("未找到该类别");
        }
        /**删除**/
        Integer rows = classificationMapper.delete(id);
        if (rows != 1){
            throw new DeleteException("删除数据异常");
        }
        /**删除后删除对应的内容 调用内容删除的方法**/

    }

    @Override
    public Classification findById(Integer id) {
        Classification classification = classificationMapper.findById(id);
        if (classification  == null){
            throw new ClassificationNotFoundException("产品分类未找到");
        }
        return classification;
    }

    @Override
    public List<Classification> findByParentId(Integer parentId) {
        List<Classification> classifications = classificationMapper.findByParentId(parentId);
        if (classifications.size() == 0){
            throw new ClassificationNotFoundException("产品方向下产品分类未找到");
        }
        return classifications;
    }
}
