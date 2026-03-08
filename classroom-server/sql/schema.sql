-- ============================================================
-- 基于Spring Boot的学生课堂管理系统 - 数据库初始化脚本
-- Database: MySQL 8.0+
-- Charset: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `classroom_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `classroom_db`;

-- ============================================================
-- 1. 用户表 (sys_user)
-- ============================================================
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名/学号/工号',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: STUDENT/TEACHER/ADMIN',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`, `deleted`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 课程/班课表 (course)
-- ============================================================
CREATE TABLE `course` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `course_code` VARCHAR(20) NOT NULL COMMENT '班课码(6位随机)',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '课程封面',
    `description` TEXT DEFAULT NULL COMMENT '课程描述',
    `teacher_id` BIGINT NOT NULL COMMENT '授课教师ID',
    `semester` VARCHAR(20) DEFAULT NULL COMMENT '学期: 如2025-2026-1',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已结课 1-进行中 2-未开始',
    `max_students` INT DEFAULT 200 COMMENT '最大学生数',
    `checkin_weight` DECIMAL(5,2) DEFAULT 20.00 COMMENT '签到成绩权重(%)',
    `assignment_weight` DECIMAL(5,2) DEFAULT 40.00 COMMENT '作业成绩权重(%)',
    `exam_weight` DECIMAL(5,2) DEFAULT 40.00 COMMENT '考试成绩权重(%)',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_code` (`course_code`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_semester` (`semester`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程/班课表';

-- ============================================================
-- 3. 名册/课程成员表 (course_member)
-- ============================================================
CREATE TABLE `course_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已退出 1-正常',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_student` (`course_id`, `student_id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程成员/名册表';

-- ============================================================
-- 4. 签到活动表 (checkin)
-- ============================================================
CREATE TABLE `checkin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(100) DEFAULT NULL COMMENT '签到标题',
    `checkin_type` TINYINT DEFAULT 1 COMMENT '签到类型: 1-普通签到 2-定位签到 3-手势签到',
    `gesture_code` VARCHAR(20) DEFAULT NULL COMMENT '手势码(手势签到时使用)',
    `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '签到纬度(定位签到)',
    `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '签到经度(定位签到)',
    `location_range` INT DEFAULT 200 COMMENT '定位范围(米)',
    `start_time` DATETIME NOT NULL COMMENT '签到开始时间',
    `end_time` DATETIME NOT NULL COMMENT '签到截止时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已结束 1-进行中',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_status_time` (`status`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到活动表';

-- ============================================================
-- 5. 签到记录表 (checkin_record)
-- ============================================================
CREATE TABLE `checkin_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `checkin_id` BIGINT NOT NULL COMMENT '签到活动ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `checkin_time` DATETIME DEFAULT NULL COMMENT '签到时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-未签到 1-已签到 2-迟到 3-请假',
    `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '学生签到纬度',
    `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '学生签到经度',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_checkin_student` (`checkin_id`, `student_id`),
    KEY `idx_student_course` (`student_id`, `course_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到记录表';

-- ============================================================
-- 6. 作业表 (assignment)
-- ============================================================
CREATE TABLE `assignment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '作业标题',
    `content` TEXT DEFAULT NULL COMMENT '作业内容/要求',
    `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL(多个用逗号分隔)',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `deadline` DATETIME NOT NULL COMMENT '截止时间',
    `max_score` DECIMAL(5,1) DEFAULT 100.0 COMMENT '满分分值',
    `allow_late` TINYINT DEFAULT 0 COMMENT '是否允许迟交: 0-否 1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已关闭 1-进行中 2-未开始',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_deadline` (`deadline`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业表';

-- ============================================================
-- 7. 作业提交表 (submission)
-- ============================================================
CREATE TABLE `submission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `assignment_id` BIGINT NOT NULL COMMENT '作业ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `content` TEXT DEFAULT NULL COMMENT '提交内容',
    `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL(多个用逗号分隔)',
    `submit_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `is_late` TINYINT DEFAULT 0 COMMENT '是否迟交: 0-否 1-是',
    `score` DECIMAL(5,1) DEFAULT NULL COMMENT '得分',
    `feedback` TEXT DEFAULT NULL COMMENT '教师批语',
    `grade_time` DATETIME DEFAULT NULL COMMENT '批改时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-退回 1-已提交 2-已批改',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_assignment_student` (`assignment_id`, `student_id`),
    KEY `idx_student_course` (`student_id`, `course_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业提交表';

-- ============================================================
-- 8. 成绩表 (grade)
-- ============================================================
CREATE TABLE `grade` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `checkin_score` DECIMAL(5,1) DEFAULT NULL COMMENT '签到成绩(百分制)',
    `assignment_score` DECIMAL(5,1) DEFAULT NULL COMMENT '作业成绩(百分制)',
    `exam_score` DECIMAL(5,1) DEFAULT NULL COMMENT '考试成绩(百分制)',
    `total_score` DECIMAL(5,1) DEFAULT NULL COMMENT '总成绩(加权计算)',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_student` (`course_id`, `student_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_total_score` (`total_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- ============================================================
-- 9. 公告表 (announcement)
-- ============================================================
CREATE TABLE `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `type` TINYINT DEFAULT 1 COMMENT '类型: 1-课程公告 2-系统公告',
    `course_id` BIGINT DEFAULT NULL COMMENT '课程ID(课程公告时)',
    `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶: 0-否 1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-草稿 1-已发布',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_type_status` (`type`, `status`),
    KEY `idx_publisher_id` (`publisher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ============================================================
-- 10. 资料表 (material)
-- ============================================================
CREATE TABLE `material` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '资料标题',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '资料描述',
    `file_url` VARCHAR(500) DEFAULT '' COMMENT '文件URL',
    `file_name` VARCHAR(200) DEFAULT '' COMMENT '原始文件名',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型(MIME)',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `uploader_id` BIGINT NOT NULL COMMENT '上传人ID',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_uploader_id` (`uploader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程资料表';

-- ============================================================
-- 11. 留言表 (message)
-- ============================================================
CREATE TABLE `message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `sender_id` BIGINT NOT NULL COMMENT '发送人ID',
    `content` TEXT NOT NULL COMMENT '留言内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父留言ID(回复时)',
    `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名: 0-否 1-是',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='留言/提问表';

-- ============================================================
-- 12. 审计日志表 (audit_log)
-- ============================================================
CREATE TABLE `audit_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    `operation` VARCHAR(100) DEFAULT NULL COMMENT '操作描述',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT 'HTTP方法',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
    `response_code` INT DEFAULT NULL COMMENT '响应状态码',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT 'User-Agent',
    `execution_time` BIGINT DEFAULT NULL COMMENT '执行耗时(ms)',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_module` (`module`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- ============================================================
-- 初始数据: 管理员账户
-- 所有账户密码统一为: admin123 (BCrypt加密)
-- ============================================================
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('admin', '$2a$10$uCSy6p57U7dBAD8L9fmz9eBdW8UZo5AhsQ/r4mQheRY8UgyJVV6UK', '系统管理员', 'ADMIN', 1);

-- 测试教师账户 密码: admin123
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('teacher001', '$2a$10$uCSy6p57U7dBAD8L9fmz9eBdW8UZo5AhsQ/r4mQheRY8UgyJVV6UK', '张老师', 'TEACHER', 1);

-- 测试学生账户 密码: admin123
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('2022102310001', '$2a$10$uCSy6p57U7dBAD8L9fmz9eBdW8UZo5AhsQ/r4mQheRY8UgyJVV6UK', '李明', 'STUDENT', 1);
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('2022102310002', '$2a$10$uCSy6p57U7dBAD8L9fmz9eBdW8UZo5AhsQ/r4mQheRY8UgyJVV6UK', '王芳', 'STUDENT', 1);
