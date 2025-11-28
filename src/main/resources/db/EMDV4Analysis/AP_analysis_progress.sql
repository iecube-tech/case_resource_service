
DROP TABLE IF EXISTS AP_data;

DROP TABLE IF EXISTS AP_analysis_progress;

CREATE TABLE AP_analysis_progress(
     `id` VARCHAR(32) PRIMARY KEY NOT NULL,
     `project_id` INT NOT NULL,
     `total_count` INT NULL,
     `completed_count` INT NULL,
     `finished` TINYINT NOT NULL DEFAULT 0,
     `percent` INT NOT NULL DEFAULT 0,
     `message` LONGTEXT NULL,
     `create_time` DATETIME NULL,
     `update_time` DATETIME NULL,
     CONSTRAINT AP_project_id
         FOREIGN KEY (project_id)
             REFERENCES project(id)
             ON DELETE CASCADE  -- 当父节点被删除时，将子节点的p_id设为NULL
             ON UPDATE CASCADE  -- 当父节点ID更新时，子节点的p_id也随之更新
) COMMENT "数据分析生成任务";

CREATE TABLE AP_data(
    `id` VARCHAR(32) PRIMARY KEY NOT NULL,
    `ap_id` VARCHAR(32) NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    `data` JSON NULL,
    CONSTRAINT AP_id
        FOREIGN KEY (ap_id)
            REFERENCES AP_analysis_progress(id)
            ON DELETE CASCADE  -- 当父节点被删除时，将子节点的p_id设为NULL
            ON UPDATE CASCADE  -- 当父节点ID更新时，子节点的p_id也随之更新
)COMMENT "数据分析任务生成的数据";


