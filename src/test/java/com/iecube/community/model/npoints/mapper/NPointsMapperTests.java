package com.iecube.community.model.npoints.mapper;

import com.iecube.community.model.npoints.entity.NPoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@SpringBootTest 表示标注当前的类是一个测试类， 不会随同项目一块打包发送
@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class NPointsMapperTests {

    @Autowired
    private NPointsMapper nPointsMapper;

    @Test
    public void getByProjectId(){
        System.out.println(nPointsMapper.getByProjectId(2));
        List<NPoints> childList = nPointsMapper.getByProjectId(2);
        List treeList = new ArrayList<>();
        Map<String, String> map = new HashMap<String, String>();
        for(NPoints point: childList){
            System.out.println(point);
            treeList.add(point.getProjectId());
            map.put("name",point.getProjectName());
            map.put("children",point.getModuleName());
            System.out.println(treeList);
            System.out.println(map);

        }
    }

    @Test
    public void getCaseByCaseId(){
        System.out.println(nPointsMapper.getCaseByCaseId(1));
    }

    @Test
    public void getModuleByCaseId(){
        System.out.println(nPointsMapper.getModuleByCaseId(2));
    }

    @Test
    public void getConceptByCaseId(){
        System.out.println(nPointsMapper.getConceptByCaseId(1));
    }

    @Test
    public void getTargetByCaseId(){
        System.out.println(nPointsMapper.getTargetByCaseId(1));
    }

    @Test
    public void getTargetByModuleId(){
        System.out.println(nPointsMapper.getTargetByModuleId(1));
    }

    @Test
    public void getCasesByModuleId(){
        System.out.println(nPointsMapper.getCasesByModuleId(1));
    }


    @Test
    public void getModulesByConceptId(){
        System.out.println(nPointsMapper.getModulesByConceptId(1));
    }

    @Test
    public void deleteCaseModule(){
        System.out.println(nPointsMapper.deleteCaseModule(9,9));
    }

    @Test
    public void addCaseModule(){
        System.out.println(nPointsMapper.addCaseModule(9,9));
    }
}
