package com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.impl;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import com.iecube.community.model.EMDV4Project.EMDV4_component.mapper.EMDV4ComponentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.mapper.EMDV4StudentTaskBookMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.EMDV4StudentTaskBookService;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.util.uuid.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EMDV4StudentTaskBookServiceImpl implements EMDV4StudentTaskBookService {

    @Autowired
    private EMDV4StudentTaskBookMapper emdV4StudentTaskBookMapper;

    @Autowired
    private EMDV4ComponentMapper emdv4ComponentMapper;

    @Override
    public EMDV4StudentTaskBook createStudentTaskBook(BookLabCatalog labProc) {
        // 存储原ID与新ID的映射关系（避免递归中找不到父节点新ID）
        Map<Long, String> booklabIdMap = new HashMap<>(); // 原BookLabCatalog.id -> 新id
        List<EMDV4StudentTaskBook> allNodes = new ArrayList<>();
        List<EMDV4Component> allComponents = new ArrayList<>();
        EMDV4StudentTaskBook res = this.copyFromBookLabCatalog(labProc, allNodes,allComponents, booklabIdMap);
        //todo 入库 allNodes  allComponents
        int re1 = emdV4StudentTaskBookMapper.batchInsert(allNodes);
        if(re1!= allNodes.size()){
            throw new InsertException("分配学生实验指导书异常");
        }
        int re2 = emdv4ComponentMapper.batchInsert(allComponents);
        if(re2!= allComponents.size()){
            throw new InsertException("分配学生实验指导书组件异常");
        }
        List<Long> taskBookTagList=new ArrayList<>();
        allComponents.forEach(c->{
            if(c.getTag()!=null){
                taskBookTagList.add(c.getTag());
            }
        });
        res.setTagList(taskBookTagList);
        return res;
    }

    private EMDV4StudentTaskBook copyFromBookLabCatalog(BookLabCatalog labProc,
                                                        List<EMDV4StudentTaskBook> allNodes,
                                                        List<EMDV4Component> allComponents,
                                                        Map<Long, String> booklabIdMap) {
        // 1. 复制当前节点基本信息（生成新ID，重置父子关系相关字段）
        EMDV4StudentTaskBook taskBook = new EMDV4StudentTaskBook();
        this.copyTaskBookFields(taskBook, labProc); // 复制非ID、非关系字段
        taskBook.setId(UUIDGenerator.generateUUID()); // 生成新ID（根据实际需求实现，如UUID或自增）
        taskBook.setChildren(null); // 先清空子节点，后续递归处理

        // 2. 存储原ID与新ID的映射（供子节点设置pId使用）
        booklabIdMap.put(labProc.getId(), taskBook.getId());

        // 3 设置pId
        if(labProc.getPId()!=null){
            if(booklabIdMap.get(labProc.getPId())==null){
                taskBook.setPId(null);
            }else {
                taskBook.setPId(booklabIdMap.get(labProc.getPId()));
            }
        }else {
            taskBook.setPId(null);
        }

        // 4. 复制关联的组件
        if(labProc.getComponentList()!=null && !labProc.getComponentList().isEmpty()){
            List<LabComponent> blockLabComponents = labProc.getComponentList();
            blockLabComponents.forEach(labComponent -> {
                EMDV4Component component = new EMDV4Component();
                this.copyComponentFields(component, labComponent);
                component.setId(UUIDGenerator.generateUUID());
                if(booklabIdMap.get(labProc.getId())!=null){
                    component.setBlockId(booklabIdMap.get(labProc.getPId()));
                }else {
                    component.setBlockId(null);
                }
                allComponents.add(component);
            });
        }

        // 5 存储taskBook
        allNodes.add(taskBook);

        // 6. 递归复制子节点
        if(labProc.getChildren()!=null && !labProc.getChildren().isEmpty()) {
            // 如果子节点不为空
            labProc.getChildren().forEach(block->{
                copyFromBookLabCatalog(block, allNodes, allComponents, booklabIdMap);
            });
        }
        return taskBook;
    }

    private void copyTaskBookFields(EMDV4StudentTaskBook taskBook, BookLabCatalog labProc) {
        // 复制非ID、非关系字段
        taskBook.setSourceId(labProc.getId());
        taskBook.setStage(labProc.getStage());
        taskBook.setLevel(labProc.getLevel());
        taskBook.setVersion(labProc.getVersion());
        taskBook.setName(labProc.getName());
        taskBook.setOrder(labProc.getOrder());
        taskBook.setSectionPrefix(labProc.getSectionPrefix());
        taskBook.setDeviceType(labProc.getDeviceType());
        taskBook.setIcon(labProc.getIcon());
        taskBook.setStepByStep(labProc.getStepByStep());
        taskBook.setStyle(labProc.getStyle());
        taskBook.setConfig(labProc.getConfig());
        taskBook.setPayload(labProc.getPayload());
        taskBook.setStatus(0);
        taskBook.setCurrentChild(0);
    }

    private void copyComponentFields(EMDV4Component target, LabComponent source) {
        target.setTag(source.getTag());
        target.setStage(source.getStage());
        target.setName(source.getName());
        target.setIcon(source.getIcon());
        target.setType(source.getType());
        target.setNeedCalculate(source.getNeedCalculate());
        target.setTotalScore(source.getTotalScore());
        target.setScore(0.0);
        target.setScoreProportion(0.0);
        target.setStyle(source.getStyle());
        target.setConfig(source.getConfig());
        target.setPayload(source.getPayload());
        target.setStatus(0);
        target.setOrder(source.getOrder());
    }
}
