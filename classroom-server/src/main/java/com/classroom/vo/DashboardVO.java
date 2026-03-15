package com.classroom.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    // ===== 顶部四卡片核心指标 =====
    private long totalStudents;
    private long totalTeachers;
    private long totalCourses;
    private long totalCheckins;

    // ===== 辅助统计 =====
    private long totalUsers;
    private long activeCourses;        // 进行中的课程
    private long totalAssignments;
    private long todayCheckins;        // 今日签到活动数
    private long totalSubmissions;     // 作业提交总数
    private long gradedSubmissions;    // 已批改提交数

    // ===== 柱状图：Top 课程的学生人数和作业数 =====
    private List<String> courseNames;
    private List<Long> courseStudentCounts;
    private List<Long> courseAssignmentCounts;

    // ===== 进度条：各项完成率 =====
    private int checkinRate;           // 签到率 (%)
    private int submissionRate;        // 作业提交率 (%)
    private int gradingRate;           // 批改率 (%)

    // ===== 最近课程表格数据 =====
    private List<CourseItem> recentCourses;

    // ===== 最新动态：审计日志 =====
    private List<ActivityItem> recentActivities;

    @Data
    public static class CourseItem {
        private Long id;
        private String courseName;
        private String teacherName;
        private long studentCount;
        private Integer status;
        private String createTime;
    }

    @Data
    public static class ActivityItem {
        private String username;
        private String module;
        private String operation;
        private String createTime;
    }
}
