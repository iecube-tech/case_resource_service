package com.iecube.community.model.exportProgress.service.impl;

import com.iecube.community.baseservice.ex.ServiceException;
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
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
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


    @Override
    @Transactional
    public ExportProgressVo create(Integer projectId, String type){
        List<ExportProgress> exits = exportProgressMapper.selectByProject(projectId);
        if(exits!=null&& !exits.isEmpty()){
            for (ExportProgress progress: exits){
                if(progress.getType().equals(type)){
                    ExportProgressVo vo = new ExportProgressVo();
                    BeanUtils.copyProperties(progress, vo);
                    return vo;
                }
            }
        }
        return createExportTask(projectId, type);
    }

    @Override
    @Transactional
    public ExportProgressVo reCreate(Integer projectId, String type){
        List<ExportProgress> exits = exportProgressMapper.selectByProject(projectId);
        if(exits!=null&& !exits.isEmpty()){
            for (ExportProgress progress: exits){
                if(progress.getType().equals(type)){
                    exportProgressMapper.delById(progress.getId());
                }
            }
        }
        return createExportTask(projectId, type);
    }

    @Transactional
    public ExportProgressVo createExportTask(Integer projectId, String type) {
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
        progress.setTotalCount(pstReportDTOList.size()); // 假设每次导出300个PDF // todo 根据Project的信息查询 pdf任务或excel任务
        progress.setCompletedCount(0);
        progress.setPercent(0);
        progress.setFinished(false);
        progress.setMessage("任务已创建，等待处理");
        exportProgressMapper.insert(progress);

        // 启动异步任务处理PDF生成
        if(type.equals(ExportProgress.Types.PROJECT_GRADE_EXPORT.getValue())){
            exportChildService.processGradeExportTask(progressId, projectId);
        }
        if(type.equals(ExportProgress.Types.PROJECT_REPORT_EXPORT.getValue())){
            exportChildService.processReportExportTask(progressId, projectId, pstReportDTOList);
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
    @Transactional
    public ExportProgressVo cancelExportTask(String taskId) {
        cancelFlags.put(taskId, true);
        return getExportProgress(taskId);
    }

}
