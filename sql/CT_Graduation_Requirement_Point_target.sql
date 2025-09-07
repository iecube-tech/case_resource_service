/*
 Navicat Premium Dump SQL

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80026 (8.0.26)
 Source Host           : 192.168.1.252:3308
 Source Schema         : community

 Target Server Type    : MySQL
 Target Server Version : 80026 (8.0.26)
 File Encoding         : 65001

 Date: 05/09/2025 14:01:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for CT_Graduation_Requirement_Point_target
-- ----------------------------
DROP TABLE IF EXISTS `CT_Graduation_Requirement_Point_target`;
CREATE TABLE `CT_Graduation_Requirement_Point_target`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '节点ID，主键',
  `MF` bigint NULL DEFAULT NULL COMMENT '顶级节点父节点专业方向，顶级节点不为NULL',
  `p_id` bigint NULL DEFAULT NULL COMMENT '父节点ID，顶级节点为NULL',
  `level` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `order` int NULL DEFAULT NULL,
  `style` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `config` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `payload` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `CT_MF_parent`(`MF` ASC) USING BTREE,
  INDEX `CT_CTGR`(`p_id` ASC) USING BTREE,
  CONSTRAINT `CT_CTGR` FOREIGN KEY (`p_id`) REFERENCES `CT_Graduation_Requirement_Point_target` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `CT_MF_parent` FOREIGN KEY (`MF`) REFERENCES `TOP_Major_Field` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '课程目标树形结构' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of CT_Graduation_Requirement_Point_target
-- ----------------------------
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (1, 2, NULL, 0, '工程知识', '毕业要求', NULL, NULL, NULL, NULL);
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (2, NULL, 1, 1, '系统掌握微电子科学与工程专业的基础理论知识，包括半导体物理、半导体器件、集成电路原理、集成电路设计等核心知识，能够将这些知识用于分析和设计微电子系统和器件', '1.2系统掌握微电子科学与工程专业的基础理论知识，包括半导体物理、半导体器件、集成电路原理、集成电路设计等核心知识，能够将这些知识用于分析和设计微电子系统和器件', NULL, NULL, NULL, NULL);
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (3, NULL, 2, 2, '使学生深入理解半导体器件的基本物理原理和工作机制，掌握半导体器件物理实验的基本技能和方法，能够熟练操作和调试实验仪器，准确测量和分析半导体材料和器件的特性参数，通过物理原理理解半导体器件的工作机制，培养学生的实践动手能力和科学研究素养。', '课程目标1：使学生深入理解半导体器件的基本物理原理和工作机制，掌握半导体器件物理实验的基本技能和方法，能够熟练操作和调试实验仪器，准确测量和分析半导体材料和器件的特性参数，通过物理原理理解半导体器件的工作机制，培养学生的实践动手能力和科学研究素养。', NULL, NULL, NULL, NULL);
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (4, 2, NULL, 0, '研究', '毕业要求', NULL, NULL, NULL, NULL);
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (5, NULL, 4, 1, '能够对实验数据进行收集、整理、分析和解释，运用统计学方法和专业知识得出可靠的研究结论，并能够撰写规范的学术论文或研究报告，展示研究成果和创新点。', '4.2能够对实验数据进行收集、整理、分析和解释，运用统计学方法和专业知识得出可靠的研究结论，并能够撰写规范的学术论文或研究报告，展示研究成果和创新点。', NULL, NULL, NULL, NULL);
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (6, NULL, 5, 2, '通过实验项目的设计、实施和结果分析，培养学生解决复杂工程问题的能力，包括能够针对给定的半导体材料和器件问题，制定合理的实验方案，选择合适的实验设备和技术，对实验数据进行有效的计算处理和解释，并根据实验结果提出改进和优化建议，提高学生的工程实践能力和创新思维。', '课程目标2：通过实验项目的设计、实施和结果分析，培养学生解决复杂工程问题的能力，包括能够针对给定的半导体材料和器件问题，制定合理的实验方案，选择合适的实验设备和技术，对实验数据进行有效的计算处理和解释，并根据实验结果提出改进和优化建议，提高学生的工程实践能力和创新思维。', NULL, NULL, NULL, NULL);
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (7, 2, NULL, 0, '沟通', '毕业要求', NULL, NULL, NULL, NULL);
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (8, NULL, 7, 1, '能够清晰、准确地表达自己的专业观点和技术方案，包括撰写技术报告、设计文档、学术论文等书面材料，以及进行口头汇报、项目演示等面对面交流活动，能够使不同专业背景的人员理解自己的工作内容和成果', '10.1能够清晰、准确地表达自己的专业观点和技术方案，包括撰写技术报告、设计文档、学术论文等书面材料，以及进行口头汇报、项目演示等面对面交流活动，能够使不同专业背景的人员理解自己的工作内容和成果', NULL, NULL, NULL, NULL);
INSERT INTO `CT_Graduation_Requirement_Point_target` VALUES (9, NULL, 8, 2, '在实验教学过程中，注重培养学生的团队协作精神、沟通能力和职业责任感，使学生具备良好的工程职业道德和素养，能够在未来的半导体器件相关工程领域中，有效地与团队成员合作，遵守工程规范和安全标准，承担起相应的社会责任和义务。', '课程目标3：在实验教学过程中，注重培养学生的团队协作精神、沟通能力和职业责任感，使学生具备良好的工程职业道德和素养，能够在未来的半导体器件相关工程领域中，有效地与团队成员合作，遵守工程规范和安全标准，承担起相应的社会责任和义务。', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Triggers structure for table CT_Graduation_Requirement_Point_target
-- ----------------------------
DROP TRIGGER IF EXISTS `CT_before_tree_node_insert`;
delimiter ;;
CREATE TRIGGER `CT_before_tree_node_insert` BEFORE INSERT ON `CT_Graduation_Requirement_Point_target` FOR EACH ROW BEGIN
    IF NEW.p_id IS NULL THEN
        SET NEW.level = 0;
    ELSE
        SET NEW.level = (SELECT level FROM CT_Graduation_Requirement_Point_target WHERE id = NEW.p_id) + 1;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table CT_Graduation_Requirement_Point_target
-- ----------------------------
DROP TRIGGER IF EXISTS `CT_after_tree_node_update`;
delimiter ;;
CREATE TRIGGER `CT_after_tree_node_update` AFTER UPDATE ON `CT_Graduation_Requirement_Point_target` FOR EACH ROW BEGIN
    IF OLD.level != NEW.level THEN
        UPDATE BL_book_lab 
        SET level = level + (NEW.level - OLD.level) 
        WHERE p_id = OLD.id;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
