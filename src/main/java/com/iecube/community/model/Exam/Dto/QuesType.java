package com.iecube.community.model.Exam.Dto;

import com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto.AnalysisType;
import lombok.Getter;

@Getter
public enum QuesType {
    CHOICE(1, "单选题"),
    MultipleCHOICE(2, "多选题"),
    QA(3, "简答题");

    private final Integer value;
    private final String description;
    QuesType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据value反向查询枚举（用于数据库查询结果转枚举）
     */
    public static QuesType getByDesc(String description) {
        for (QuesType q : QuesType.values()) {
            if (q.description.equals(description)) {
                return q;
            }
        }
        return null; // 或抛出 IllegalArgumentException 非法参数异常
    }
}
