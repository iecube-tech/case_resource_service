package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.service;

import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorInfoVo;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorOverview;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorStuVo;

public interface EMDV4MonitorService {

    MonitorInfoVo getMonitorInfo(Integer projectId);

    /**
     * 课程 学生监控信息分页查询
     * @param projectId projectId
     * @param page 页数
     * @param pageSize 每页个数
     * @return MonitorOverview
     */
    MonitorOverview getStuMonitorPaging(Integer projectId, Integer page, Integer pageSize);

    /**
     * 课程 学生监控信息【状态筛选后】分页查询
     * @param projectId  projectId
     * @param page page
     * @param pageSize pageSize
     * @param status status
     * @return MonitorOverview
     */
    MonitorOverview getStuMonitorPagingWithStatus(Integer projectId, Integer page, Integer pageSize, Integer status);

    /**
     * 实验 学生监控信息分页查询
     * @param projectId projectId
     * @param ptId ptId
     * @param page 页数
     * @param pageSize 每页个数
     * @return MonitorOverview
     */
    MonitorOverview getTaskStuMonitorPaging(Integer projectId, Long ptId, Integer page, Integer pageSize);

    /**
     * 实验 学生监控信息【状态筛选后】分页查询
     * @param projectId projectId
     * @param ptId ptId
     * @param page page
     * @param pageSize pageSize
     * @param status status
     * @return MonitorOverview
     */
    MonitorOverview getTaskStuMonitorPagingWithStatus(Integer projectId, Long ptId, Integer page, Integer pageSize, Integer status);

    /**
     * 根据关键字搜索学生模糊查询 如果ptId为空 返回课程内容， 如果不为空 则返回对应的实验内容 page pageSize
     * @param projectId projectId
     * @param ptId ptId
     * @param page page
     * @param pageSize pageSize
     * @param keyword keyword
     * @return MonitorOverview
     */
    MonitorOverview searchStudent(Integer projectId, Long ptId, Integer page, Integer pageSize, String keyword);
    /**
     * 课程 学生 详细信息查看, 如果学生不存在 返回空
     * @param projectId projectId
     * @param psId psId
     * @return MonitorStuVo
     */
    MonitorStuVo getStuTaskMonitor(Integer projectId, Long psId);
}
