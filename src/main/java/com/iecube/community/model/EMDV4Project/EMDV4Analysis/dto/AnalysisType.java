package com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto;

import com.iecube.community.model.exportProgress.entity.ExportProgress;
import lombok.Getter;

@Getter
public enum AnalysisType {
    T_OVERVIEW_OVERVIEW("t_overview_overview", "教师_课程概览_概览", "teacher"),
    T_OVERVIEW_RATE("t_overview_rate", "教师_课程概览_课程进度概览", "teacher"),
    T_OVERVIEW_GA("t_overview_ga", "教师_课程概览_目标达成分析", "teacher"),
    T_OVERVIEW_DOCG("t_overview_docg", "教师_课程概览_班级成绩分布", "teacher"),
    T_OVERVIEW_ES("t_overview_es", "教师_课程概览_实验得分情况", "teacher"),
    T_OVERVIEW_CWLS("t_overview_cwls", "教师_课程概览_对比上学期", "teacher"),
    T_OVERVIEW_AI_VIEW("t_overview_ai_view", "教师_课程概览_AI辅助教学分析", "teacher"),
    T_EA_OVERVIEW("t_ea_overview", "教师_实验分析_课程概览", "teacher"),
    T_EA_ED("t_ea_ed", "教师_实验分析_实验详情", "teacher"),
    T_EA_ECA("t_ea_eca", "教师_实验分析_实验比较分析", "teacher"),
    T_STU_OVERVIEW("t_stu_overview", "教师_学生分析_学生总览", "teacher"),
    T_STU_BEHAVIOUR("t_stu_behaviour", "教师_学生分析_学生行为分析", "teacher"),
    T_CT_OAS("t_ct_oas", "教师_课程目标_目标达成度", "teacher"), // objective achievement scale
    T_CT_CR("t_ct_cr", "教师_课程目标_课程目标与能力标签支撑关系", "teacher"), // corresponding relation
    T_CT_TA("t_ct_ta", "教师_课程目标_课程目标分析", "teacher"),
    T_CT_OAS_TREND("t_ct_oas_trend", "教师_课程目标_课程目标达成度趋势", "teacher"),
    T_TR_OVERVIEW("t_tr_overview","教师_教学报告_整体教学效果报告", "teacher"),
    T_TR_IS("t_tr_is","教师_教学报告_教学改进建议", "teacher"), //Improvement suggestions
    T_TASK_D_OVERVIEW("t_task_d_overview", "教师_实验分析_实验详情_实验概览", "teacher"),
    T_TASK_D_ABILITY("t_task_d_ability", "教师_实验分析_实验详情_能力分析", "teacher"),
    T_TASK_D_QUES("t_task_d_ques", "教师_实验分析_实验详情_题目分析", "teacher"),
    T_TASK_D_COURSE("t_task_d_course", "教师_实验分析_实验详情_过程分析", "teacher"),
    T_TASK_D_SUG("t_task_d_sug", "教师_实验分析_实验详情_教学建议", "teacher"),
    STU_P_OVERVIEW("stu_p_overview", "学生_课程概览", "student"),
    STU_P_TASK("stu_p_task", "学生_实验列表", "student"),
    STU_P_TARGET("stu_p_target", "学生_课程目标", "student"),
    STU_P_SUG("stu_p_sug", "学生_学习建议", "student"),
    PST_DETAIL("pst_detail", "实验_学生_详情", "teacher"),
    PST_SUG("pst_sug", "实验_学生_改进建议", "teacher");


    // 获取数据库存储值
    private final String value; // 存储到数据库的标识（小写+下划线，符合数据库字段规范）
    // 获取中文描述
    private final String desc;  // 中文描述（用于前端展示/日志打印）

    private final String terminal;

    AnalysisType(String value, String desc, String terminal) {
        this.value = value;
        this.desc = desc;
        this.terminal = terminal;
    }

    /**
     * 根据value反向查询枚举（用于数据库查询结果转枚举）
     */
    public static AnalysisType getByValue(String value) {
        for (AnalysisType t : AnalysisType.values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        return null; // 或抛出 IllegalArgumentException 非法参数异常
    }

    public static Integer size() {
        return AnalysisType.values().length;
    }
}
