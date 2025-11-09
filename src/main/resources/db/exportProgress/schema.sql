-- 批量生成任务进度表
DROP TABLE IF EXISTS `EP_export_progress`;
DROP TABLE IF EXISTS `EP_export_progerss_child_file`;

CREATE TABLE IF NOT EXISTS EP_export_progress (
                                                  `id` BINARY(32) NOT NULL COMMENT '任务ID（UUID）',
    `project_id` INT NOT NULL COMMENT 'projectId',
    `type` varchar(255) NOT NULL COMMENT '生成类型 报告 (report)， 成绩(grade)',
    `total_count` INT NOT NULL COMMENT '总任务数（目标数量）',
    `completed_count` INT NOT NULL DEFAULT 0 COMMENT '已完成数量',
    `percent` INT NOT NULL DEFAULT 0 COMMENT '进度百分比（0-100）',
    `finished` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否全部完成（生成+压缩）',
    `message` VARCHAR(255) DEFAULT '' COMMENT '状态描述（如：生成第10个PDF）',
    `error_msg` VARCHAR(255) DEFAULT NULL COMMENT '错误信息（失败时非空）',
    `result_resource` INT NULL COMMENT '文件信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '进度更新时间',
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批量导出文件生成任务进度表';

-- 批量生成任务子任务详情
CREATE TABLE IF NOT EXISTS EP_export_progerss_child_file(
                                                            `id` BINARY(32) NOT NULL COMMENT 'id_uuid',
    `export_progress_id` BINARY(32) NOT NULL COMMENT '任务ID（UUID）',
    `resource` INT NOT NULL COMMENT '文件信息',
    `order` INT NULL COMMENT '生成的时候位于第多少个任务',
    `finished` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否正常完成',
    `message`  VARCHAR(255) DEFAULT '' COMMENT '状态描述（如：生成第10个PDF）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '任务执行时间',
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批量导出文件子任务进度细节';