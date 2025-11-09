package com.iecube.community.model.exportProgress.service.impl;

import com.iecube.community.model.exportProgress.dto.PstReportCommentDto;
import com.iecube.community.model.exportProgress.dto.PstReportDTO;
import com.iecube.community.model.exportProgress.entity.ExportProgress;
import com.iecube.community.model.exportProgress.entity.ExportProgressChild;
import com.iecube.community.model.exportProgress.mapper.ExportProgressChildMapper;
import com.iecube.community.model.exportProgress.mapper.ExportProgressMapper;
import com.iecube.community.model.exportProgress.mapper.PstReportMapper;
import com.iecube.community.model.exportProgress.service.ExportChildService;
import com.iecube.community.model.exportProgress.util.FileCompressor;
import com.iecube.community.model.exportProgress.util.PdfGenerator;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import com.iecube.community.model.resource.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class ExportChildServiceImpl implements ExportChildService {

    ConcurrentHashMap<String, Boolean> cancelFlags;

    private final ExportProgressMapper exportProgressMapper;

    private final ExportProgressChildMapper childFileMapper;

    private final PstReportMapper pstReportMapper;

    private final ResourceService resourceService;

    private ThreadPoolTaskExecutor exportTaskExecutor;


    @Value("${resource-location}/file")
    private String files;

    @Value("${generated-report}")
    private String genFileDir;

    @Autowired
    public ExportChildServiceImpl(ConcurrentHashMap<String, Boolean> cancelFlags,
                                  ExportProgressMapper exportProgressMapper,
                                  ExportProgressChildMapper childFileMapper,
                                  PstReportMapper pstReportMapper,
                                  ResourceService resourceService,
                                  ThreadPoolTaskExecutor exportTaskExecutor){
        this.cancelFlags=cancelFlags;
        this.exportProgressMapper = exportProgressMapper;
        this.childFileMapper = childFileMapper;
        this.pstReportMapper = pstReportMapper;
        this.resourceService = resourceService;
        this.exportTaskExecutor = exportTaskExecutor;
    }

    @Async("exportTaskExecutor")
    @Transactional
    public void processGradeExportTask(String progressId, Integer projectId) {

    }

    @Async("exportTaskExecutor")
    @Transactional
    public void processReportExportTask(String progressId, int projectId, List<PstReportDTO> pstReportDTOList) {
//        String threadName = Thread.currentThread().getName();
//        System.out.println("当前异步线程：" + threadName);
//        if (exportTaskExecutor != null) {
//            System.out.printf(
//                    "线程池状态：核心线程数=%d, 活跃线程数=%d, 队列任务数=%d%n",
//                    exportTaskExecutor.getCorePoolSize(),
//                    exportTaskExecutor.getActiveCount(),
//                    exportTaskExecutor.getQueueSize()
//            );
//        }
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
                    File pdfFile = PdfGenerator.generatePdf(this.genFileDir+"/"+projectId, fileName, PstReportCommentDtoList);
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
                String zipFileName = this.files +"/"+ UUID.randomUUID().toString().replace("-", "") + ".tar.gz";
                FileCompressor.compressToTarGz(pdfFiles, zipFileName);
                Resource resource = resourceService.buildResourceDTO("报告导出", zipFileName, "TAR.GZ");
//                Resource result = resourceService.addResource(resource, AuthUtils.getCurrentUserId()); // TODO: 2025/11/9
                Resource result = resourceService.addResource(resource, 6); // TODO: 2025/11/9
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


    private static void getError(String taskId) {
        log.error("任务不存在: taskId={}", taskId);
    }

}
