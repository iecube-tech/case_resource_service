package com.iecube.community.model.exportProgress.service.impl;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.exportProgress.dto.PstReportDTO;
import com.iecube.community.model.exportProgress.entity.ExportProgress;
import com.iecube.community.model.exportProgress.mapper.ExportProgressMapper;
import com.iecube.community.model.exportProgress.mapper.PstReportMapper;
import com.iecube.community.model.exportProgress.service.ExportChildService;
import com.iecube.community.model.exportProgress.service.ExportService;
import com.iecube.community.model.exportProgress.vo.ExportProgressVo;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ExportServiceImpl implements ExportService {

    private final ExportProgressMapper exportProgressMapper;
    private final PstReportMapper pstReportMapper;

    private final ProjectMapper projectMapper;

    private final ResourceMapper resourceMapper;

    private final ExportChildService exportChildService;


    private final ConcurrentHashMap<String, Boolean> cancelFlags;

    @Autowired
    public ExportServiceImpl(ConcurrentHashMap<String, Boolean> cancelFlags,
                             ExportProgressMapper exportProgressMapper,
                             PstReportMapper pstReportMapper,
                             ProjectMapper projectMapper,
                             ResourceMapper resourceMapper,
                             ExportChildService exportChildService){
        this.cancelFlags=cancelFlags;
        this.exportProgressMapper = exportProgressMapper;
        this.pstReportMapper = pstReportMapper;
        this.projectMapper = projectMapper;
        this.resourceMapper = resourceMapper;
        this.exportChildService = exportChildService;
    }


    /**
     * 生成项目的导出报告文件压缩包
     * @param projectId projectId
     * @param type ExportProgress.Types.PROJECT_REPORT_EXPORT.getValue()
     * @param currentUser currentUser
     * @return ExportProgressVo 如果存在 则返回存在的信息， 如果不存在则生成 再返回信息
     */
    @Override
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ExportProgressVo create(Integer projectId, String type, Integer currentUser){
        List<ExportProgress> exits = exportProgressMapper.selectByProject(projectId);
        if(exits!=null&& !exits.isEmpty()){
            for (ExportProgress progress: exits){
                if(progress.getType().equals(type)){
                    ExportProgressVo vo = new ExportProgressVo();
                    BeanUtils.copyProperties(progress, vo);
                    if(vo.getResultResource()!=null){
                        vo.setResource(resourceMapper.getById(vo.getResultResource()));
                    }
                    return vo;
                }
            }
        }
        return createExportTask(projectId, type, currentUser);
    }

    /**
     * 重新生成导出文件 会删除 覆盖已有文件
     * @param projectId projectId
     * @param type ExportProgress. Types. PROJECT_REPORT_EXPORT. getValue()
     * @param currentUser currentUser
     * @return ExportProgressVo 生成后的信息
     */
    @Override
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ExportProgressVo reCreate(Integer projectId, String type, Integer currentUser){
        List<ExportProgress> exits = exportProgressMapper.selectByProject(projectId);
        if(exits!=null&& !exits.isEmpty()){
            for (ExportProgress progress: exits){
                if(progress.getType().equals(type)){
                    exportProgressMapper.delById(progress.getId());
                }
            }
        }
        return createExportTask(projectId, type, currentUser);
    }

//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ExportProgressVo createExportTask(Integer projectId, String type, Integer currentUser) {
        Project project = projectMapper.findById(projectId);
        if(project == null) {
            throw new ServiceException("课程/项目不存在");
        }
        List<Integer> SupportedVersions = List.of(4);
        if(project.getVersion() == null || !SupportedVersions.contains(project.getVersion())) {
            throw new ServiceException("暂不不支持该版本的数据导出");
        }
        List<PstReportDTO> pstReportDTOList = pstReportMapper.getPSTReportListByProjectId(projectId);
        // 生成任务ID
        String progressId = UUID.randomUUID().toString().replace("-", "");
        // 创建任务记录
        ExportProgress progress = new ExportProgress();
        progress.setId(progressId);
        progress.setProjectId(projectId);
        progress.setType(type);
        progress.setTotalCount(pstReportDTOList.size()); //根据Project的信息查询 pdf任务或excel任务
        progress.setCompletedCount(0);
        progress.setPercent(0);
        progress.setFinished(false);
        progress.setMessage("任务已创建，等待处理");
        progress.setCreateTime(new Date());
        int res = exportProgressMapper.insert(progress);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }

        // 启动异步任务处理PDF生成
        if(type.equals(ExportProgress.Types.PROJECT_GRADE_EXPORT.getValue())){
            exportChildService.processGradeExportTask(progressId, project, pstReportDTOList, currentUser);
        }
        if(type.equals(ExportProgress.Types.PROJECT_REPORT_EXPORT.getValue())){
            exportChildService.processReportExportTask(progressId, project, pstReportDTOList, currentUser);
        }

        ExportProgressVo vo = new ExportProgressVo();
        BeanUtils.copyProperties(progress, vo);
        return vo;
    }

    @Override
    public ExportProgressVo getExportProgress(String taskId) {
        ExportProgress progress = exportProgressMapper.selectById(taskId);
        if (progress == null) {
            return null;
        }
        ExportProgressVo response = new ExportProgressVo();
        BeanUtils.copyProperties(progress, response);
        if(progress.getResultResource()!=null){
            response.setResource(resourceMapper.getById(response.getResultResource()));
        }
        return response;
    }

    @Override
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public ExportProgressVo cancelExportTask(String taskId) {
        ExportProgress progress = exportProgressMapper.selectById(taskId);
        if (progress == null) {
            return null;
        }
        if(!progress.getFinished() && progress.getType().equals(ExportProgress.Types.PROJECT_REPORT_EXPORT.getValue())){
            cancelFlags.put(taskId, true);
        }
        ExportProgressVo response = new ExportProgressVo();
        BeanUtils.copyProperties(progress, response);
        if(progress.getResultResource()!=null){
            response.setResource(resourceMapper.getById(response.getResultResource()));
        }
        return response;
    }

}
