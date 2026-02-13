package com.iecube.community.model.Exam_timing.controller;

import com.iecube.community.model.Exam.vo.StuExamInfoVo;
import com.iecube.community.model.Exam_timing.Dto.TimingTask;
import com.iecube.community.model.Exam_timing.Service.TimingTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 考试计时任务接口（启动/中断/监控）
 */
@RestController
@RequestMapping("/exam/timing")
@RequiredArgsConstructor
public class TimingTaskController {

    private final TimingTaskService timingTaskService;

    /**
     * 启动考试计时任务
     */
    @PostMapping("/start")
    public ResponseEntity<String> startExamTask(@RequestBody StuExamInfoVo stuExamInfoVo) {
        timingTaskService.startExamTask(stuExamInfoVo);
        return ResponseEntity.ok("考试计时任务启动成功");
    }

    /**
     * 中断考试计时任务
     */
    @PostMapping("/interrupt/{esId}")
    public ResponseEntity<String> interruptExamTask(@PathVariable Long esId) {
        timingTaskService.interruptExamTask(esId);
        return ResponseEntity.ok("考试已提交");
    }

    /**
     * 监控接口：根据esId查询任务状态
     */
    @GetMapping("/status/{esId}")
    public ResponseEntity<Map<String, Object>> getTaskStatus(@PathVariable Long esId) {
        TimingTask task = timingTaskService.getTaskByEsId(esId);
        Map<String, Object> result = new HashMap<>();
        if (task == null) {
            result.put("code", 404);
            result.put("message", "任务不存在");
            return ResponseEntity.ok(result);
        }

        Long remainingMinutes = timingTaskService.getRemainingMinutes(esId);
        result.put("code", 200);
        result.put("esId", esId);
        result.put("examId", task.getExamId());
        result.put("studentId", task.getStudentId());
        result.put("status", task.getStatus().name());
        result.put("startTime", task.getStartTime());
        result.put("durationMinutes", task.getDurationMinutes());
        result.put("remainingMinutes", remainingMinutes);
        return ResponseEntity.ok(result);
    }

    /**
     * 监控接口：根据学生ID和考试ID查询任务
     */
    @GetMapping("/status/student/{studentId}/exam/{examId}")
    public ResponseEntity<Map<String, Object>> getTaskByStudentAndExam(
            @PathVariable Integer studentId,
            @PathVariable Long examId) {
        TimingTask task = timingTaskService.getTaskByStudentAndExam(studentId, examId);
        Map<String, Object> result = new HashMap<>();
        if (task == null) {
            result.put("code", 404);
            result.put("message", "该学生的该考试无计时任务");
            return ResponseEntity.ok(result);
        }

        Long remainingMinutes = timingTaskService.getRemainingMinutes(task.getEsId());
        result.put("code", 200);
        result.put("esId", task.getEsId());
        result.put("examId", task.getExamId());
        result.put("studentId", task.getStudentId());
        result.put("status", task.getStatus().name());
        result.put("remainingMinutes", remainingMinutes);
        return ResponseEntity.ok(result);
    }
}