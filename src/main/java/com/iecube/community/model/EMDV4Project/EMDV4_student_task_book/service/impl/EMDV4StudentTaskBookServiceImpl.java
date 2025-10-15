package com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.impl;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.event.BlockCurrentChildChanged;
import com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.event.BlockScoreChanged;
import com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.event.BlockStatusChanged;
import com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.event.BlockTimeChanged;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity.EMDV4TaskGroup;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.service.EMDV4TaskGroupService;
import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import com.iecube.community.model.EMDV4Project.EMDV4_component.mapper.EMDV4ComponentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.mapper.EMDV4ProjectStudentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.mapper.EMDV4ProjectStudentTaskMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.mapper.EMDV4StudentTaskBookMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.EMDV4StudentTaskBookService;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.util.uuid.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EMDV4StudentTaskBookServiceImpl implements EMDV4StudentTaskBookService {

    @Autowired
    private EMDV4StudentTaskBookMapper emdV4StudentTaskBookMapper;

    @Autowired
    private EMDV4ComponentMapper emdv4ComponentMapper;

    @Autowired
    private EMDV4ProjectStudentTaskMapper emdv4ProjectStudentTaskMapper;

    @Autowired
    private EMDV4ProjectStudentMapper emdv4ProjectStudentMapper;

    @Autowired
    private EMDV4TaskGroupService emdv4TaskGroupService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public EMDV4StudentTaskBook createStudentTaskBook(EMDV4ProjectTask projectTask, BookLabCatalog labProc) {
        // 存储原ID与新ID的映射关系（避免递归中找不到父节点新ID）
        Map<Long, String> booklabIdMap = new HashMap<>(); // 原BookLabCatalog.id -> 新id
        List<EMDV4StudentTaskBook> allNodes = new ArrayList<>();
        List<EMDV4Component> allComponents = new ArrayList<>();
        EMDV4StudentTaskBook res = this.copyFromBookLabCatalog(projectTask, labProc, allNodes, allComponents, booklabIdMap);
        // 入库 allNodes  allComponents
        if(!allNodes.isEmpty()){
            int re1 = emdV4StudentTaskBookMapper.batchInsert(allNodes);
            if(re1!= allNodes.size()){
                throw new InsertException("分配学生实验指导书异常");
            }
        }
        if(!allComponents.isEmpty()){
            int re2 = emdv4ComponentMapper.batchInsert(allComponents);
            if(re2!= allComponents.size()){
                throw new InsertException("分配学生实验指导书组件异常");
            }
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

    @Override
    public EMDV4StudentTaskBook getByBookId(String taskBookId) {
        EMDV4StudentTaskBook res = emdV4StudentTaskBookMapper.getById(taskBookId);
        return this.buildTreeWithComponents(res);
    }

    @Override
    public EMDV4StudentTaskBook updateStatus(String taskBookId, Integer status) {
        int res = emdV4StudentTaskBookMapper.updateStatus(taskBookId, status);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        EMDV4StudentTaskBook block= emdV4StudentTaskBookMapper.getById(taskBookId); // 更新状态的block
//        this.batchUpdateStatus(block); // 同步小组状态
        eventPublisher.publishEvent(new BlockStatusChanged(this,block));
        EMDV4StudentTaskBook wholeParentBlock=this.getByBookId(block.getPId()); // 更新currentChild的block
        List<EMDV4StudentTaskBook> brothBlockList = wholeParentBlock.getChildren();
        int parentBlockCurrentChild = wholeParentBlock.getCurrentChild();
        if(parentBlockCurrentChild < (brothBlockList.size()-1)){
            emdV4StudentTaskBookMapper.updateCurrentChild(wholeParentBlock.getId(), parentBlockCurrentChild+1);
            EMDV4StudentTaskBook currentChildChanged = emdV4StudentTaskBookMapper.getById(wholeParentBlock.getId());
//            this.batchUpdateCurrentChild(currentChildChanged); // 同步小组currentChild
            eventPublisher.publishEvent(new BlockCurrentChildChanged(this,currentChildChanged));
        }
        // 判断是不根节点的最后一个节点
        if(block.getLevel().equals(2) && status.equals(1)){
            List<EMDV4StudentTaskBook> level2Blocks = this.getChildrenByPid(block.getPId());
            if(block.getOrder()>=level2Blocks.size()-1){
                // 是最后一步 更新pst的Status
                EMDV4StudentTaskBook rootBlock = this.getRootTaskBook(block.getId());
                EMDV4ProjectStudentTask PST = emdv4ProjectStudentTaskMapper.getByTaskBookId(rootBlock.getId());
                emdv4ProjectStudentTaskMapper.updateStatus(PST.getId(), 1);
            }
        }
        return this.getByBookId(taskBookId);
    }

    @Override
    public EMDV4StudentTaskBook updateScore(String taskBookId, Double score) {
        EMDV4StudentTaskBook taskBook = emdV4StudentTaskBookMapper.getById(taskBookId);
        try{
            if(taskBook.getPassScore()==null){
                taskBook.setPassScore(0.0);
            }
            int res = emdV4StudentTaskBookMapper.updateScore(taskBookId, score, score>taskBook.getPassScore());
            if(res!=1){
                throw new UpdateException();
            }
        }catch(Exception e){
            throw new UpdateException("更新数据异常，"+e.getCause());
        }
        EMDV4StudentTaskBook changed = emdV4StudentTaskBookMapper.getById(taskBookId);
//        this.batchUpdateScore(changed);  // 同步小组状态
        eventPublisher.publishEvent(new BlockScoreChanged(this,changed));
        return changed;
    }

    @Override
    public EMDV4StudentTaskBook getRootTaskBook(String taskBookLeafId) {
        return emdV4StudentTaskBookMapper.getRootByLeaf(taskBookLeafId);
    }

    @Override
    public EMDV4StudentTaskBook computeAiScore(String taskBookLeafId) {
        // 1. 更新叶子节点的成绩
        List<EMDV4Component> componentList = emdv4ComponentMapper.getByBlockId(taskBookLeafId);
        double score = 0.0;
        double totalScore = 0.0;
        for (EMDV4Component comp : componentList) {
            score += comp.getAiScore();
            totalScore += comp.getTotalScore();
        }
        emdV4StudentTaskBookMapper.updateScoreOnly(taskBookLeafId, score, totalScore);
        EMDV4StudentTaskBook taskBook = emdV4StudentTaskBookMapper.getById(taskBookLeafId);
        // 2. 递归计算父节点的成绩 最后返回根节点
        return computeTaskBookScore(taskBook);
    }

    @Override
    public EMDV4StudentTaskBook computeCheckScore(String taskBookLeafId) {
        // 1. 更新叶子节点的成绩
        List<EMDV4Component> componentList = emdv4ComponentMapper.getByBlockId(taskBookLeafId);
        double score = 0.0;
        double totalScore = 0.0;
        for (EMDV4Component comp : componentList) {
            score += comp.getTScore();
            totalScore += comp.getTotalScore();
        }
        emdV4StudentTaskBookMapper.updateScoreOnly(taskBookLeafId, score, totalScore);
        EMDV4StudentTaskBook taskBook = emdV4StudentTaskBookMapper.getById(taskBookLeafId);
        // 2. 递归计算父节点的成绩 最后返回根节点
        return computeTaskBookScore(taskBook);
    }


    @Override
    public List<EMDV4StudentTaskBook> batchGetById(List<String> idList){
        return emdV4StudentTaskBookMapper.batchGetById(idList);
    }

    @Override
    public List<EMDV4StudentTaskBook> batchUpdateWeighting(List<EMDV4StudentTaskBook> list) {
        int res = emdV4StudentTaskBookMapper.batchUpdateWeight(list);
        if(res != list.size()){
            throw new UpdateException("更新数据异常");
        }
        List<String> idList = new ArrayList<>();
        for(EMDV4StudentTaskBook book : list){
            idList.add(book.getId());
        }
        return emdV4StudentTaskBookMapper.batchGetById(idList);
    }

    /**
     * 根据子节点递归计算根节点的成绩
     * @param node 子节点
     * @return  根节点
     */
    @Override
    public EMDV4StudentTaskBook computeTaskBookScore(EMDV4StudentTaskBook node){
//        System.out.println("计算成绩");
//        System.out.println("计算成绩"+node.getPId());
        if(node.getPId()!=null){
            // 计算父节点的成绩
            List<EMDV4StudentTaskBook> taskBookList = emdV4StudentTaskBookMapper.getByPId(node.getPId());
            double score = 0.0;
            double totalScore = 0.0;
            for (EMDV4StudentTaskBook child : taskBookList) {
                if(node.getLevel().equals(2)){
                    totalScore += child.getWeighting()==null?0.0:child.getWeighting();
                    if(child.getWeighting()!=null&&child.getWeighting()!=0){
                        if(child.getTotalScore()==null || child.getTotalScore().equals(0.0)){
                            score += 0.0;
                        }
                        else {
                            score += (child.getWeighting() / 100) * ((child.getScore() * 100) / child.getTotalScore());// 归一化后的总分
                        }
                    }
                }else {
                    score += child.getScore();
                    totalScore += child.getTotalScore()==null?0.0:child.getTotalScore();
                }
            }
            emdV4StudentTaskBookMapper.updateScoreOnly(node.getPId(), score, totalScore);
            EMDV4StudentTaskBook parent = emdV4StudentTaskBookMapper.getById(node.getPId());
//            System.out.println("parent");
//            System.out.println(parent);
            node = computeTaskBookScore(parent);
        }
//        System.out.println("计算成绩完成");
        return node;
    }

    @Override
    public List<EMDV4StudentTaskBook> getProjectTaskBlockList(Long projectTaskId) {
        return emdV4StudentTaskBookMapper.getProjectTaskBlockList(projectTaskId);
    }

    @Override
    public List<EMDV4StudentTaskBook> getAllLeafNodes(EMDV4StudentTaskBook rootNode) {
        List<EMDV4StudentTaskBook> leafNodes = new ArrayList<>();
        // 处理空树的情况
        if (rootNode == null) {
            return leafNodes;
        }
        // 使用递归方法收集叶子节点
        collectLeafNodes(rootNode, leafNodes);
        return leafNodes;
    }

    @Override
    public EMDV4StudentTaskBook updateBlockTime(String blockId, Boolean startTime, Boolean endTime) {
        if(startTime!=null && startTime){
            List<String> idList = new ArrayList<>();
            idList.add(blockId);
            EMDV4StudentTaskBook rootBlock = this.getRootTaskBook(blockId);
            if(rootBlock.getStartTime()==null){
                idList.add(rootBlock.getId());
            }
            emdV4StudentTaskBookMapper.batchUpdateStartTime(new Date(), idList);
            // 更新PST的startTime
            EMDV4ProjectStudentTask PST = emdv4ProjectStudentTaskMapper.getByTaskBookId(rootBlock.getId());
            if(PST.getStartTime()==null){
                List<Long> pstIdList = new ArrayList<>();
                pstIdList.add(PST.getId());
                emdv4ProjectStudentTaskMapper.batchUpdateStartTime(pstIdList, new Date());
            }
        }
        if(endTime!=null && endTime){
            List<String> idList = new ArrayList<>();
            idList.add(blockId);
            EMDV4StudentTaskBook rootBlock = this.getRootTaskBook(blockId);
            idList.add(rootBlock.getId());
            emdV4StudentTaskBookMapper.batchUpdateEndTime(new Date(), idList);
            // 更新PST的endTime
            EMDV4ProjectStudentTask PST = emdv4ProjectStudentTaskMapper.getByTaskBookId(rootBlock.getId());
            List<Long> pstIdList = new ArrayList<>();
            pstIdList.add(PST.getId());
            emdv4ProjectStudentTaskMapper.batchUpdateDoneTime(pstIdList, new Date());
        }
        EMDV4StudentTaskBook block = emdV4StudentTaskBookMapper.getById(blockId);
        eventPublisher.publishEvent(new BlockTimeChanged(this, block));
        return block;
    }

    /**
     * 同步小组数据
     * @param changed 更改的节点
     */
    @Override
    public void batchUpdateBlockStatus(EMDV4StudentTaskBook changed){
        List<EMDV4StudentTaskBook> willUpdate = this.willBatchUpdate(changed);
        if(willUpdate!=null){
            willUpdate.forEach(block->{
                block.setStatus(changed.getStatus());
            });
            //  batchUpdate
            emdV4StudentTaskBookMapper.batchUpdateStatus(willUpdate);

            if(changed.getLevel().equals(2) && changed.getStatus().equals(1)){
                willUpdate.forEach(block->{
                    List<EMDV4StudentTaskBook> level2Blocks = this.getChildrenByPid(block.getPId());
                    if(block.getOrder()>=level2Blocks.size()-1){
                        // 是最后一步 更新pst的Status
                        EMDV4StudentTaskBook rootBlock = this.getRootTaskBook(block.getId());
                        EMDV4ProjectStudentTask PST = emdv4ProjectStudentTaskMapper.getByTaskBookId(rootBlock.getId());
                        emdv4ProjectStudentTaskMapper.updateStatus(PST.getId(), 1);
                    }
                });
            }
        }
    }

    /**
     * 同步小组数据
     * @param changed 更改的节点
     */
    @Override
    public void batchUpdateBlockCurrentChild(EMDV4StudentTaskBook changed){
        List<EMDV4StudentTaskBook> willUpdate = this.willBatchUpdate(changed);
        if(willUpdate!=null){
            willUpdate.forEach(block->{
                block.setCurrentChild(changed.getCurrentChild());
            });
            //  batchUpdate
            emdV4StudentTaskBookMapper.batchUpdateCurrentChild(willUpdate);
        }
    }

    /**
     * 同步小组数据
     * @param changed 更改的节点
     */
    @Override
    public void batchUpdateBlockScore(EMDV4StudentTaskBook changed){
        List<EMDV4StudentTaskBook> willUpdate = this.willBatchUpdate(changed);
        if(willUpdate!=null){
            willUpdate.forEach(block->{
                block.setScore(changed.getScore());
                block.setPassStatus(changed.getPassStatus());
            });
            // batchUpdate
            emdV4StudentTaskBookMapper.batchUpdateScore(willUpdate);
        }
    }

    /**
     * 同步小组数据
     * @param changed 更改的节点
     */
    @Override
    public void batchUpdateBlockTime(EMDV4StudentTaskBook changed){
        List<String> idList = new ArrayList<>();
        List<EMDV4StudentTaskBook> willUpdate = this.willBatchUpdate(changed); // 同步小组时间
        if(willUpdate!=null){
            willUpdate.forEach(b->{
                idList.add(b.getId());
            });
            if(changed.getStartTime()!=null){
                emdV4StudentTaskBookMapper.batchUpdateStartTime(new Date(), idList);
            }
            if(changed.getEndTime()!=null){
                emdV4StudentTaskBookMapper.batchUpdateEndTime(new Date(), idList);
            }
        }
    }

    @Override
    public void handleUpdateLevel2StartTime(EMDV4Component component) {
        EMDV4StudentTaskBook level2TaskBook = emdV4StudentTaskBookMapper.getLevel2ByComponentId(component.getId());
        if(level2TaskBook!=null){
            if (level2TaskBook.getStartTime()==null) {
                this.updateBlockTime(level2TaskBook.getId(), true, false);
            }
        }
    }

    /**
     * 根据已更改的节点获取需要同步的小组数据
     * @param changedBlock 已更改的节点
     * @return 需要同步的节点
     */
    private List<EMDV4StudentTaskBook> willBatchUpdate(EMDV4StudentTaskBook changedBlock){
        EMDV4StudentTaskBook rootBlock = this.getRootTaskBook(changedBlock.getId());
        EMDV4ProjectStudentTask PST = emdv4ProjectStudentTaskMapper.getByTaskBookId(rootBlock.getId());
        EMDV4ProjectStudent PS = emdv4ProjectStudentMapper.getById(PST.getProjectStudent());

        // 3. 查询PST的分组
        EMDV4TaskGroup studentGroup = emdv4TaskGroupService.getTaskStudentGroup(PST.getProjectTask(), PS.getStudentId());
        if(studentGroup==null){
            return null;
        }
        List<Integer> stuIdList = new ArrayList<>();
        studentGroup.getStudentList().forEach(student -> {
            stuIdList.add(student.getId());
        });
        // 判断小组人数，需不需要同步
        if(stuIdList.size()<=1){
            return null;
        }

        List<String> rootBlockIdList = new ArrayList<>();

        List<EMDV4ProjectStudentTask> groupPSTList = emdv4ProjectStudentTaskMapper.getByPTAndStuIdList(PST.getProjectTask(), stuIdList);
        groupPSTList.forEach(pst->{
            rootBlockIdList.add(pst.getTaskBookId());
        });

        // 根据根节点id获取所有的节点
        Map<String, List<EMDV4StudentTaskBook>> rootBlockNodeMap = new HashMap<>(); // 根节点id==>对应的整棵树中的所有节点
        for(String rootNodeId: rootBlockIdList){
            EMDV4StudentTaskBook taskBook = emdV4StudentTaskBookMapper.getById(rootNodeId);
            EMDV4StudentTaskBook tree = this.buildTreeWithoutComponents(taskBook);
            List<EMDV4StudentTaskBook> allNode = this.getAllNodes(tree);
            rootBlockNodeMap.put(rootNodeId, allNode);
        }
        // 从这些节点列表中找出需要更改的节点
        List<EMDV4StudentTaskBook> willUpdateNodes = new ArrayList<>();
        for(String rootId: rootBlockIdList){
            if(rootId.equals(PST.getTaskBookId())){
                continue;
            }
            for(EMDV4StudentTaskBook node: rootBlockNodeMap.get(rootId)){
                if(node.getLevel().equals(changedBlock.getLevel())
                        && node.getStage().equals(changedBlock.getStage())
                        && node.getOrder().equals(changedBlock.getOrder())
                        && node.getName().equals(changedBlock.getName())){
                    willUpdateNodes.add(node);
                }
            }
        }
        return willUpdateNodes;
    }

    /**
     * 递归收集叶子节点的辅助方法
     * @param node 当前节点
     * @param leafNodes 用于存储叶子节点的列表
     */
    private void collectLeafNodes(EMDV4StudentTaskBook node, List<EMDV4StudentTaskBook> leafNodes) {
        // 如果当前节点没有子节点，说明是叶子节点
        if (!node.getHasChildren()) {
            leafNodes.add(node);
            return;
        }

        // 如果有子节点，递归处理每个子节点
        List<EMDV4StudentTaskBook> children = node.getChildren();
        if (children != null) {
            for (EMDV4StudentTaskBook child : children) {
                collectLeafNodes(child, leafNodes);
            }
        }
    }

    /**
     * 获取节点的子节点
     * @param pId 节点id
     * @return 子节点列表
     */
    private List<EMDV4StudentTaskBook> getChildrenByPid(String pId){
        if (pId == null || pId.isEmpty()) {
            throw new IllegalArgumentException("父节点ID不能为空");
        }

        List<EMDV4StudentTaskBook> children = emdV4StudentTaskBookMapper.getByPId(pId);
        children.forEach(child->{
            child.setChildren(null);
        });
        return children;
    }

    /**
     *  从 模版复制必要字段
     * @param projectTask 当前知道书对应的任务
     * @param labProc EMDV4 的labProc
     * @param allNodes 所有节点
     * @param allComponents 所有的component
     * @param booklabIdMap map
     * @return 构建好的EMDV4StudentTaskBook
     */
    private EMDV4StudentTaskBook copyFromBookLabCatalog(EMDV4ProjectTask projectTask,
                                                        BookLabCatalog labProc,
                                                        List<EMDV4StudentTaskBook> allNodes,
                                                        List<EMDV4Component> allComponents,
                                                        Map<Long, String> booklabIdMap) {
        // 1. 复制当前节点基本信息（生成新ID，重置父子关系相关字段）
        EMDV4StudentTaskBook taskBook = new EMDV4StudentTaskBook();
        this.copyTaskBookFields(taskBook, labProc); // 复制非ID、非关系字段
        taskBook.setId(UUIDGenerator.generateUUID()); // 生成新ID（根据实际需求实现，如UUID或自增）
        taskBook.setChildren(null); // 先清空子节点，后续递归处理
        if(taskBook.getLevel().equals(2) && taskBook.getStage().equals(0)){
            taskBook.setNeedPassScore(projectTask.getStep1NeedPassScore() != null && projectTask.getStep1NeedPassScore());
            taskBook.setPassScore(projectTask.getStep1PassScore()==null?0: projectTask.getStep1PassScore());
        }

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
                    component.setBlockId(booklabIdMap.get(labProc.getId()));
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
                copyFromBookLabCatalog(projectTask, block, allNodes, allComponents, booklabIdMap);
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
        taskBook.setType(labProc.getType());
        taskBook.setDescription(labProc.getDescription());
        taskBook.setOrder(labProc.getOrder());
        taskBook.setSectionPrefix(labProc.getSectionPrefix());
        taskBook.setDeviceType(labProc.getDeviceType());
        taskBook.setIcon(labProc.getIcon());
        taskBook.setStepByStep(labProc.getStepByStep());
        taskBook.setStyle(labProc.getStyle());
        taskBook.setConfig(labProc.getConfig());
        taskBook.setPayload(labProc.getPayload());
        taskBook.setScore(0.0);
        taskBook.setPassStatus(labProc.getPassScore()==null||labProc.getPassScore()<=0);
        taskBook.setStatus(0);
        taskBook.setCurrentChild(0);
        taskBook.setWeighting(labProc.getWeighting());
        taskBook.setTotalScore(0.0);
        taskBook.setNeedPassScore(labProc.getNeedPassScore());
        taskBook.setPassScore(labProc.getPassScore()==null?0.0:labProc.getPassScore());
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
        target.setAiScore(0.0);
        target.setTScore(0.0);
    }

    private EMDV4StudentTaskBook buildTreeWithComponents(EMDV4StudentTaskBook node){
        if (node == null) {
            return null;
        }
        List<EMDV4Component> componentList = emdv4ComponentMapper.getByBlockId(node.getId());
        if(componentList!=null){
            node.setComponents(componentList.isEmpty()?null:componentList);
        }else {
            node.setComponents(null);
        }
        List<EMDV4StudentTaskBook> children = this.getChildrenByPid(node.getId());
        // 递归处理子节点
        List<EMDV4StudentTaskBook> processedChildren = children.stream().map(this::buildTreeWithComponents).toList();
        node.setChildren(processedChildren);
        node.setHasChildren(!processedChildren.isEmpty());
        return node;
    }

    /**
     * 构建没有component的taskBook树
     * @param node 节点
     * @return 构建好的整棵树
     */
    private EMDV4StudentTaskBook buildTreeWithoutComponents(EMDV4StudentTaskBook node){
        if (node == null) {
            return null;
        }
        List<EMDV4StudentTaskBook> children = this.getChildrenByPid(node.getId());
        // 递归处理子节点
        List<EMDV4StudentTaskBook> processedChildren = children.stream().map(this::buildTreeWithoutComponents).toList();
        node.setChildren(processedChildren);
        node.setHasChildren(!processedChildren.isEmpty());
        return node;
    }

    /**
     * 获取树中的所有节点
     * @param root 根节点
     * @return 所有节点列表
     */
    private List<EMDV4StudentTaskBook> getAllNodes(EMDV4StudentTaskBook root) {
        List<EMDV4StudentTaskBook> nodeList = new ArrayList<>();
        // 根节点为空时直接返回空列表
        if (root == null) {
            return nodeList;
        }
        // 使用递归方式遍历树
        this.traverseTree(root, nodeList);
        return nodeList;
    }

    /**
     * 递归遍历树，收集所有节点
     * @param node 当前节点
     * @param nodeList 用于存储节点的列表
     */
    private void traverseTree(EMDV4StudentTaskBook node, List<EMDV4StudentTaskBook> nodeList) {
        // 将当前节点添加到列表
        nodeList.add(node);
        // 如果有子节点，则递归遍历所有子节点
        if (node.getHasChildren() && node.getChildren() != null) {
            for (EMDV4StudentTaskBook child : node.getChildren()) {
                traverseTree(child, nodeList);
            }
        }
    }
}
