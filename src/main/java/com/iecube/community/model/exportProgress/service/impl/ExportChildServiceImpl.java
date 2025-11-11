package com.iecube.community.model.exportProgress.service.impl;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.exportProgress.dto.PstReportCommentDto;
import com.iecube.community.model.exportProgress.dto.PstReportDTO;
import com.iecube.community.model.exportProgress.entity.ExportProgress;
import com.iecube.community.model.exportProgress.entity.ExportProgressChild;
import com.iecube.community.model.exportProgress.mapper.ExportProgressChildMapper;
import com.iecube.community.model.exportProgress.mapper.ExportProgressMapper;
import com.iecube.community.model.exportProgress.mapper.PstReportMapper;
import com.iecube.community.model.exportProgress.service.ExportChildService;
import com.iecube.community.model.exportProgress.util.FileCompressor;
import com.iecube.community.model.exportProgress.util.GradeExcelGenEmdV4;
import com.iecube.community.model.exportProgress.util.PdfGeneratorEmdV4;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.util.thread.IOThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportChildServiceImpl implements ExportChildService {

    ConcurrentHashMap<String, Boolean> cancelFlags;

    private final ExportProgressMapper exportProgressMapper;

    private final ExportProgressChildMapper childFileMapper;

    private final PstReportMapper pstReportMapper;

    private final ResourceService resourceService;

    private final PdfGeneratorEmdV4 pdfGeneratorEmdV4;

    private final GradeExcelGenEmdV4 gradeExcelGenEmdV4;

    private final ConcurrentHashMap<String, Integer> progressRate;


    @Value("${resource-location}/file")
    private String files;

    @Value("${generated-report}")
    private String genFileDir;

    @Autowired
    public ExportChildServiceImpl(ConcurrentHashMap<String, Boolean> cancelFlags,
                                  ConcurrentHashMap<String, Integer> progressRate,
                                  ExportProgressMapper exportProgressMapper,
                                  ExportProgressChildMapper childFileMapper,
                                  PstReportMapper pstReportMapper,
                                  ResourceService resourceService,
                                  PdfGeneratorEmdV4 pdfGeneratorEmdV4,
                                  GradeExcelGenEmdV4 gradeExcelGenEmdV4){
        this.cancelFlags=cancelFlags;
        this.progressRate=progressRate;
        this.exportProgressMapper = exportProgressMapper;
        this.childFileMapper = childFileMapper;
        this.pstReportMapper = pstReportMapper;
        this.resourceService = resourceService;
        this.pdfGeneratorEmdV4 = pdfGeneratorEmdV4;
        this.gradeExcelGenEmdV4 = gradeExcelGenEmdV4;
    }

    @Async("exportTaskExecutor")
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void processGradeExportTask(String progressId, Project project, List<PstReportDTO> pstReportDTOList, Integer currentUser) {
        String xlsxDir = this.files+"/";
        String xlsxName = UUID.randomUUID().toString().replace("-", "") + ".xlsx";
        log.info("开始处理导出成绩任务: taskId={}, projectId={}, file={}", progressId, project.getId(), xlsxName);
        cancelFlags.put(progressId, false); // 初始化取消标志
        try(
            Workbook workbook = new XSSFWorkbook();
            FileOutputStream fos = new FileOutputStream(xlsxDir+xlsxName);
        ){
            ExportProgress progress = exportProgressMapper.selectById(progressId);// 获取任务信息
            if (progress == null) {
                getError(progressId);
                return;
            }
            // <实验一:[PstReportDTO], 实验二:[PstReportDTO], 实验三:[PstReportDTO], 实验四:[PstReportDTO]....>
            Map<Long, List<PstReportDTO>> pstReportDTOListGroupByPT = pstReportDTOList.stream()
                    .collect(
                            Collectors.groupingBy(
                                    PstReportDTO::getPtId,
                                    Collectors.mapping(pst->pst, Collectors.collectingAndThen(
                                            Collectors.toList(), // 先收集到 List
                                            list -> {
                                                // 对 List 按学号排序
                                                list.sort(Comparator.comparing(PstReportDTO::getStudentId));
                                                return list;
                                            }
                                    ))
                            )
                    );
            // <学号1: [PstReportDTO], 学号2: [PstReportDTO], 学号3: [PstReportDTO] ......>
            Map<String, List<PstReportDTO>> pstReportDTOGroupByStudentId = pstReportDTOList.stream()
                    .collect(Collectors.groupingBy(PstReportDTO::getStudentId));
            if(pstReportDTOListGroupByPT.isEmpty()){
                log.error("没有成绩数据");
                return;
            }
            log.info("PST数据整理完毕，开始生成课程成绩概览");
            gradeExcelGenEmdV4.genSheetOverview(workbook, pstReportDTOListGroupByPT, pstReportDTOGroupByStudentId);
            log.info("开始生成各个实验成绩概览");
            AtomicInteger completedCount = new AtomicInteger(1);
            pstReportDTOListGroupByPT.forEach((key, value) -> {
                if (cancelFlags.getOrDefault(progressId, false)) {
                    log.info("任务已取消: progressId={}", progressId);
                    updateProgress(progressId, 1, "任务已取消", true);
                    return;
                }
                Map<Long,List<PstReportCommentDto>> pstComponentsMap = new HashMap<>();
                value.forEach(pstReportDTO -> {
                    List<PstReportCommentDto> components = pstReportMapper.getSTComListByTaskBookId(pstReportDTO.getTaskBookId());
                    pstComponentsMap.put(pstReportDTO.getPstId(), components);
                });
                // 生成taskView
                updateProgress(progressId, completedCount.get(), "正在写入各个实验成绩概览，已写入"+completedCount.get()+"条数据",false);
                log.info("正在写入各个实验成绩概览，已写入{}条数据", completedCount.get());
                gradeExcelGenEmdV4.genTaskView(workbook, value.get(0).getTaskName(),value, pstComponentsMap);
                completedCount.addAndGet(value.size());
            });
            workbook.write(fos);
            Resource resource = resourceService.buildResourceDTO(project.getProjectName()+"_课程成绩.xlsx", xlsxName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            Resource result = resourceService.addResource(resource, currentUser);
            updateProgress(progressId, 1, "任务完成，文件已生成", true, result.getId());
            log.info("成绩导出完成");
        } catch (IOException e) {
            log.error("成绩导出报告错误", e);
            updateProgress(progressId, 0, "生成文件失败: " + e.getMessage(), true);
        }
    }

    @Async("exportTaskExecutor")
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public void processReportExportTask(String progressId, Project project, List<PstReportDTO> pstReportDTOList, Integer currentUser){

        String zipDir = this.files +"/" ;
        String zipName = UUID.randomUUID().toString().replace("-", "") + ".tar.gz";
        log.info("开始处理导出报告任务: taskId={}, projectId={}, filename={}", progressId, project.getId(), zipName);
        cancelFlags.put(progressId, false); // 初始化取消标志
        try {
            ExportProgress progress = exportProgressMapper.selectById(progressId);// 获取任务信息
            if (progress == null) {
                getError(progressId);
                return;
            }

            int totalCount = progress.getTotalCount();
            List<File> pdfFiles = new ArrayList<>();

            File genDirectory = new File(this.genFileDir+"/"+project.getId());
            if (!genDirectory.exists()) {
                // 不存在则创建目录（mkdirs() 可创建多级目录，mkdir() 只能创建单级目录）
                boolean isCreated = genDirectory.mkdirs();
                if (!isCreated) {
                    throw new ServiceException("创建PDF导出目录失败");
                }
            }

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
                    File pdfFile = pdfGeneratorEmdV4.generatePdf(genDirectory.getPath(), fileName, PstReportCommentDtoList);
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
                FileCompressor.compressToTarGz(pdfFiles, zipDir+zipName);
                Resource resource = resourceService.buildResourceDTO(project.getProjectName()+"_学生报告.tar.gz", zipName, "TAR.GZ");
                Resource result = resourceService.addResource(resource, currentUser);
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


    @Async("exportTaskExecutor")
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void processReportExport(String progressId, int projectId, List<PstReportDTO> pstReportDTOList, Integer currentUser) {
        log.info("开始处理导出任务: taskId={}, projectId={}", progressId, projectId);
        cancelFlags.put(progressId, false); // 初始化取消标志
        progressRate.put(progressId, 0);
        File genDirectory = new File(this.genFileDir+"/"+projectId);
        if (!genDirectory.exists()) {
            // 不存在则创建目录（mkdirs() 可创建多级目录，mkdir() 只能创建单级目录）
            boolean isCreated = genDirectory.mkdirs();
            if (!isCreated) {
                throw new ServiceException("创建PDF导出目录失败");
            }
        }
        try {
            ExportProgress progress = exportProgressMapper.selectById(progressId);// 获取任务信息
            if (progress == null) {
                getError(progressId);
                return;
            }

            int totalCount = progress.getTotalCount();
            // 2. 配置线程池（IO密集型任务，线程数设为 2*CPU核心数+1）
            ExecutorService executor = IOThreadFactory.getExecutor();

            // 3. 关键：创建「线程安全的 List」用于收集处理后的 File
            // 原因：多个异步任务会同时向 List 添加元素，普通 ArrayList 非线程安全，会导致数据错乱
            AtomicReference<List<File>> resultFileListRef = new AtomicReference<>(new ArrayList<>());
            // 4. 存储所有异步任务的 CompletableFuture，用于后续等待全部完成
            List<CompletableFuture<File>> futureList = new ArrayList<>();
            // 5. 异步处理每个 File（核心循环）
            for(int i= 0; i < pstReportDTOList.size(); i++){
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
                // 创建子任务记录
                String progressChildId = UUID.randomUUID().toString().replace("-", "");
                createChildTask(progressChildId,progressId, false,fileName+"开始生成", i + 1);

                CompletableFuture<File> future = CompletableFuture.supplyAsync(()->{
                    List<PstReportCommentDto> PstReportCommentDtoList = pstReportMapper.getSTComListByTaskBookId(pstReportDTO.getTaskBookId());
                    try{
                        File pdf = pdfGeneratorEmdV4.generatePdf(genDirectory.getPath(), fileName, PstReportCommentDtoList);
                        synchronized (resultFileListRef) { // 加锁保证并发安全
                            resultFileListRef.get().add(pdf);
                        }
                        // 更新进度
                        progressRate.compute(progressId, (k, rate) -> rate==null?0:rate + 1);
                        return pdf;
                    }catch (IOException e){
                        Thread.currentThread().interrupt();
                        throw new ServiceException(e);
                    }
                },executor).thenApply(genedFile->{
                    updateChildTask(progressChildId,true, fileName+"生成成功");
                    return genedFile;
                }).exceptionally( // 捕获异常
                    throwable -> {
                        log.error("生成PDF文件失败", throwable);
                        updateChildTask(progressChildId,false, fileName+"生成失败："+throwable.getMessage());
                        updateProgress(progressId, progressRate.get(progressId), "生成第" + progressRate.get(progressId) + "个PDF时失败: " + throwable.getMessage(), true);
                        return null; // 异常时返回默认值
                    }
                );
                futureList.add(future);
            }

            // 6. 等待所有异步任务全部完成（核心步骤）
            // 先将 futureList 转为数组，适配 allOf 的可变参数要求
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futureList.toArray(new CompletableFuture[0])
            );

            // 7. 所有任务完成后，统一处理收集到的 File 列表（非阻塞回调）
            allFutures.thenRun(() -> {
                List<File> finalResultList = resultFileListRef.get();
                // 所有PDF生成完成，开始压缩
                updateProgress(progressId, totalCount, "PDF生成完成，开始压缩", false);
                progressRate.remove(progressId);
                try {
                    // 压缩PDF文件
                    String zipFileName = this.files +"/"+ UUID.randomUUID().toString().replace("-", "") + ".tar.gz";
                    FileCompressor.compressToTarGz(finalResultList, zipFileName);
                    Resource resource = resourceService.buildResourceDTO("报告导出", zipFileName, "TAR.GZ");
                    Resource result = resourceService.addResource(resource, currentUser);
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
            });
            // 8. 关闭线程池，阻塞主线程等待所有任务完成（避免程序提前退出）
            executor.shutdown();
            boolean isAllDone = executor.awaitTermination(5, TimeUnit.SECONDS);
            log.info("主线程等待结束，所有任务是否完成：{}" , isAllDone);
            // 模拟生成300个PDF文件
//            for (int i = 0; i < pstReportDTOList.size(); i++) {
//                // 检查是否需要取消任务
//                if (cancelFlags.getOrDefault(progressId, false)) {
//                    log.info("任务已取消: progressId={}", progressId);
//                    updateProgress(progressId, i, "任务已取消", true);
//                    return;
//                }
//                PstReportDTO pstReportDTO = pstReportDTOList.get(i);
//                String fileName = pstReportDTO.getGcName() + "_" +
//                        pstReportDTO.getStudentId() + "_" +
//                        pstReportDTO.getStudentName() + "_" +
//                        pstReportDTO.getTaskName() + "_" +
//                        pstReportDTO.getTaskScore().toString() + "_" +
//                        pstReportDTO.getPstId().toString()+ ".pdf";
//                try {
//                    // 生成PDF文件 班级_学号_姓名_实验_成绩_pstId.pdf
////                    String content = "这是第" + (i + 1) + "个PDF文件，项目ID: " + projectId;
//                    List<PstReportCommentDto> PstReportCommentDtoList = pstReportMapper.getSTComListByTaskBookId(pstReportDTO.getTaskBookId());
//                    File pdfFile = pdfGenerator.generatePdf(this.genFileDir+"/"+projectId, fileName, PstReportCommentDtoList);
//                    pdfFiles.add(pdfFile);
//                    // 创建子任务记录
//                    createChildTask(progressId, true,fileName+"已生成", i + 1);
//                    // 更新进度
//                    updateProgress(progressId, i + 1, "已生成第" + (i + 1) + "个PDF", false);
//                    // 模拟处理时间
////                    Thread.sleep(100);
//                } catch (IOException e) {
//                    log.error("生成PDF文件失败", e);
//                    createChildTask(progressId, false,fileName+"生成失败", i + 1);
//                    updateProgress(progressId, i, "生成第" + (i + 1) + "个PDF时失败: " + e.getMessage(), true);
//                    return;
//                }
//            }
        } catch (InterruptedException e) {
            log.error("文件处理异常", e);
        } finally {
            // 移除取消标志
            cancelFlags.remove(progressId);
        }
    }

//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
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

//    @Transactional
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

//    @Transactional
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

//    @Transactional
    public void createChildTask(String id, String taskId, Boolean success, String message, int order) {
        ExportProgressChild childFile = new ExportProgressChild();
        childFile.setId(id);
        childFile.setExportProgressId(taskId);
        childFile.setOrder(order);
        childFile.setFinished(success);
        childFile.setMessage(message);
        childFile.setCreateTime(new Date());
        childFileMapper.insert(childFile);
    }

//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void updateChildTask(String id ,Boolean success, String message){
        ExportProgressChild child = childFileMapper.getById(id);
        child.setMessage(message);
        child.setFinished(success);
        childFileMapper.update(child);
    }

    private static void getError(String taskId) {
        log.error("任务不存在: taskId={}", taskId);
    }

}
