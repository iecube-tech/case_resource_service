package com.iecube.community.model.exportProgress.service.impl;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.exportProgress.dto.PstReportCommentDto;
import com.iecube.community.model.exportProgress.dto.PstReportDTO;
import com.iecube.community.model.exportProgress.entity.ExportProgress;
import com.iecube.community.model.exportProgress.entity.ExportProgressChild;
import com.iecube.community.model.exportProgress.mapper.ExportProgressChildMapper;
import com.iecube.community.model.exportProgress.mapper.ExportProgressMapper;
import com.iecube.community.model.exportProgress.mapper.PstReportMapper;
import com.iecube.community.model.exportProgress.service.ExportService;
import com.iecube.community.model.exportProgress.util.FileCompressor;
import com.iecube.community.model.exportProgress.util.PdfGenerator;
import com.iecube.community.model.exportProgress.vo.ExportProgressVo;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.util.jwt.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ExportProgressMapper exportProgressMapper;

    @Autowired
    private ExportProgressChildMapper childFileMapper;

    @Autowired
    private PstReportMapper pstReportMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceService resourceService;

    @Value("${resource-location}/file")
    private String files;

    // 用于存储任务取消状态
    private final ConcurrentHashMap<String, Boolean> cancelFlags = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public ExportProgressVo createExportTask(Integer projectId, String type) {
        ExportProgress exits = exportProgressMapper.selectByProject(projectId,type);
        if(exits!=null){
            ExportProgressVo vo = new ExportProgressVo();
            BeanUtils.copyProperties(exits, vo);
            if(exits.getResultResource()!=null){
                vo.setResource(resourceMapper.getById(exits.getResultResource()));
            }
        }

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
            processGradeExportTask(progressId, projectId);
        }
        if(type.equals(ExportProgress.Types.PROJECT_REPORT_EXPORT.getValue())){
            processReportExportTask(progressId, projectId, pstReportDTOList);
        }

        ExportProgressVo vo = new ExportProgressVo();
        BeanUtils.copyProperties(progress, vo);
        return vo;
    }

    @Async("exportTaskExecutor")
    @Transactional
    public void processGradeExportTask(String progressId, Integer projectId) {

    }

//    @Async("exportTaskExecutor")
    @Transactional
    @Async
    public void processReportExportTask(String progressId, int projectId, List<PstReportDTO> pstReportDTOList) {
        log.info("开始处理导出任务: taskId={}, projectId={}", progressId, projectId);
        cancelFlags.put(progressId, false); // 初始化取消标志
        try {
            ExportProgress progress = exportProgressMapper.selectById(progressId);// 获取任务信息
            if (progress == null) {
                getError(progressId);
                return;
            }

            int totalCount = progress.getTotalCount();
            List<File> pdfFiles = new ArrayList<>();

            // 模拟生成300个PDF文件
            for (int i = 0; i < pstReportDTOList.size(); i++) {
                // 检查是否需要取消任务
                if (cancelFlags.getOrDefault(progressId, false)) {
                    log.info("任务已取消: progressId={}", progressId);
                    updateProgress(progressId, i, "任务已取消", true);
                    return;
                }
                PstReportDTO pstReportDTO = pstReportDTOList.get(i);
                String fileName = pstReportDTO.getGcName() + "_" +
                        pstReportDTO.getStudentId() + "_" +
                        pstReportDTO.getStudentName() + "_" +
                        pstReportDTO.getTaskName() + "_" +
                        pstReportDTO.getTaskScore().toString() + "_" +
                        pstReportDTO.getPstId().toString()+ ".pdf";
                try {
                    // 生成PDF文件 班级_学号_姓名_实验_成绩_pstId.pdf
//                    String content = "这是第" + (i + 1) + "个PDF文件，项目ID: " + projectId;
                    List<PstReportCommentDto> PstReportCommentDtoList = pstReportMapper.getSTComListByTaskBookId(pstReportDTO.getTaskBookId());
                    File pdfFile = PdfGenerator.generatePdf("D:\\work\\gentest", fileName, PstReportCommentDtoList);
                    pdfFiles.add(pdfFile);
                    // 创建子任务记录
                    createChildTask(progressId, true,fileName+"已生成", i + 1);
                    // 更新进度
                    updateProgress(progressId, i + 1, "已生成第" + (i + 1) + "个PDF", false);
                    // 模拟处理时间
//                    Thread.sleep(100);
                } catch (IOException e) {
                    log.error("生成PDF文件失败", e);
                    createChildTask(progressId, false,fileName+"生成失败", i + 1);
                    updateProgress(progressId, i, "生成第" + (i + 1) + "个PDF时失败: " + e.getMessage(), true);
                    return;
                }
            }

            // 所有PDF生成完成，开始压缩
            updateProgress(progressId, totalCount, "PDF生成完成，开始压缩", false);
            try {
                // 压缩PDF文件
                String zipFileName = this.files + UUID.randomUUID().toString().replace("-", "") + ".tar.gz";
                FileCompressor.compressToTarGz(pdfFiles, zipFileName);
                Resource resource = resourceService.buildResourceDTO("报告导出", zipFileName, "TAR.GZ");
                Resource result = resourceService.addResource(resource, AuthUtils.getCurrentUserId());
                // 更新任务状态为完成
                updateProgress(progressId, totalCount, "任务完成，压缩包已生成", true, result.getId());
                // todo 清理临时文件
//                for (File file : pdfFiles) {
//                    if (file.delete()) {
//                        log.info("临时文件已删除: {}", file.getAbsolutePath());
//                    }
//                }
            } catch (IOException e) {
                log.error("压缩文件失败", e);
                updateProgress(progressId, totalCount, "压缩文件失败: " + e.getMessage(), true);
            }
        } finally {
            // 移除取消标志
            cancelFlags.remove(progressId);
        }
    }

    private static void getError(String taskId) {
        log.error("任务不存在: taskId={}", taskId);
    }


    public ExportProgressVo getExportProgress(String taskId) {
        ExportProgress progress = exportProgressMapper.selectById(taskId);
        if (progress == null) {
            return null;
        }
        ExportProgressVo response = new ExportProgressVo();
        BeanUtils.copyProperties(progress, response);
        response.setResource(resourceMapper.getById(response.getResultResource()));
        return response;
    }

    @Transactional
    public void cancelExportTask(String taskId) {
        cancelFlags.put(taskId, true);
    }

    @Transactional
    public void updateProgress(String taskId, int completedCount, String message, boolean finished) {
        ExportProgress progress = exportProgressMapper.selectById(taskId);
        if (progress == null) {
            getError(taskId);
            return;
        }
        int totalCount = progress.getTotalCount();
        int percent = (int) (((float) completedCount / totalCount) * 100);
        progress.setCompletedCount(completedCount);
        progress.setPercent(percent);
        progress.setMessage(message);
        progress.setFinished(finished);
        exportProgressMapper.updateById(progress);
        log.info("任务进度更新: taskId={}, percent={}%, message={}", taskId, percent, message);
    }

    @Transactional
    public void updateProgress(String taskId, int completedCount, String message, boolean finished, Integer resourceId) {
        ExportProgress progress = exportProgressMapper.selectById(taskId);
        if (progress == null) {
            getError(taskId);
            return;
        }
        int totalCount = progress.getTotalCount();
        int percent = (int) (((float) completedCount / totalCount) * 100);
        progress.setCompletedCount(completedCount);
        progress.setPercent(percent);
        progress.setMessage(message);
        progress.setFinished(finished);
        progress.setResultResource(resourceId);
        exportProgressMapper.updateById(progress);
        log.info("任务进度更新: taskId={}, percent={}%, message={}", taskId, percent, message);
    }

    @Transactional
    public void createChildTask(String taskId, Boolean success, String message, int order) {
        ExportProgressChild childFile = new ExportProgressChild();
        childFile.setId(UUID.randomUUID().toString().replace("-", ""));
        childFile.setExportProgressId(taskId);
        childFile.setOrder(order);
        childFile.setFinished(success);
        childFile.setMessage(message);
        childFile.setCreateTime(new Date());
        childFileMapper.insert(childFile);
    }


}
