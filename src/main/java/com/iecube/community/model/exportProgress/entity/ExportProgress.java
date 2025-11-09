package com.iecube.community.model.exportProgress.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ExportProgress {
    private String id;
    private Integer projectId;
    private String type; //
    private Integer totalCount;
    private Integer completedCount;
    private Integer percent;
    private Boolean finished;
    private String message;
    private String errorMsg;
    private Integer resultResource;
    private Date createTime;
    private Date updateTime;

    public enum Types{
        PROJECT_GRADE_EXPORT("project_grade_export", "学生成绩导出"),
        PROJECT_REPORT_EXPORT("project_report_export", "学生报告导出");

        private final String value; // 存储到数据库的标识（小写+下划线，符合数据库字段规范）
        private final String desc;  // 中文描述（用于前端展示/日志打印）

        Types(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        // 获取数据库存储值
        public String getValue() {
            return value;
        }
        // 获取中文描述
        public String getDesc() {
            return desc;
        }

        /**
         * 根据value反向查询枚举（用于数据库查询结果转枚举）
         */
        public static Types getByValue(String value) {
            for (Types t : Types.values()) {
                if (t.value.equals(value)) {
                    return t;
                }
            }
            return null; // 或抛出 IllegalArgumentException 非法参数异常
        }
    }
}
