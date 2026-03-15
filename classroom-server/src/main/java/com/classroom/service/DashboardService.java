package com.classroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classroom.entity.*;
import com.classroom.mapper.*;
import com.classroom.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SysUserMapper sysUserMapper;
    private final CourseMapper courseMapper;
    private final CourseMemberMapper courseMemberMapper;
    private final CheckinMapper checkinMapper;
    private final CheckinRecordMapper checkinRecordMapper;
    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;
    private final AuditLogMapper auditLogMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DashboardVO getStats() {
        DashboardVO vo = new DashboardVO();

        // ===== 1. 顶部四卡片 =====
        long totalStudents = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "STUDENT"));
        long totalTeachers = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "TEACHER"));
        long totalCourses = courseMapper.selectCount(new LambdaQueryWrapper<>());
        long totalCheckins = checkinMapper.selectCount(new LambdaQueryWrapper<>());

        vo.setTotalStudents(totalStudents);
        vo.setTotalTeachers(totalTeachers);
        vo.setTotalCourses(totalCourses);
        vo.setTotalCheckins(totalCheckins);

        // 辅助统计
        vo.setTotalUsers(sysUserMapper.selectCount(new LambdaQueryWrapper<>()));
        vo.setActiveCourses(courseMapper.selectCount(
                new LambdaQueryWrapper<Course>().eq(Course::getStatus, 1)));
        vo.setTotalAssignments(assignmentMapper.selectCount(new LambdaQueryWrapper<>()));

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        vo.setTodayCheckins(checkinMapper.selectCount(
                new LambdaQueryWrapper<Checkin>().ge(Checkin::getCreateTime, todayStart)));

        long totalSubmissions = submissionMapper.selectCount(new LambdaQueryWrapper<>());
        long gradedSubmissions = submissionMapper.selectCount(
                new LambdaQueryWrapper<Submission>().eq(Submission::getStatus, 2));
        vo.setTotalSubmissions(totalSubmissions);
        vo.setGradedSubmissions(gradedSubmissions);

        // ===== 2. 柱状图：Top 10 课程的学生人数和作业数 =====
        List<Course> courses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>().orderByDesc(Course::getCreateTime).last("LIMIT 10"));

        List<String> courseNames = new ArrayList<>();
        List<Long> courseStudentCounts = new ArrayList<>();
        List<Long> courseAssignmentCounts = new ArrayList<>();

        for (Course course : courses) {
            courseNames.add(course.getCourseName());
            courseStudentCounts.add(courseMemberMapper.selectCount(
                    new LambdaQueryWrapper<CourseMember>().eq(CourseMember::getCourseId, course.getId())));
            courseAssignmentCounts.add(assignmentMapper.selectCount(
                    new LambdaQueryWrapper<Assignment>().eq(Assignment::getCourseId, course.getId())));
        }
        vo.setCourseNames(courseNames);
        vo.setCourseStudentCounts(courseStudentCounts);
        vo.setCourseAssignmentCounts(courseAssignmentCounts);

        // ===== 3. 进度条：各项完成率 =====
        // 签到率 = 已签到记录 / 全部签到记录
        long totalRecords = checkinRecordMapper.selectCount(new LambdaQueryWrapper<>());
        long checkedRecords = checkinRecordMapper.selectCount(
                new LambdaQueryWrapper<CheckinRecord>().eq(CheckinRecord::getStatus, 1));
        vo.setCheckinRate(totalRecords > 0 ? (int) (checkedRecords * 100 / totalRecords) : 0);

        // 作业提交率
        long totalAssignmentSlots = getTotalAssignmentSlots();
        vo.setSubmissionRate(totalAssignmentSlots > 0 ? (int) (totalSubmissions * 100 / totalAssignmentSlots) : 0);

        // 批改率 = 已批改 / 已提交
        vo.setGradingRate(totalSubmissions > 0 ? (int) (gradedSubmissions * 100 / totalSubmissions) : 0);

        // ===== 4. 最近课程表格 =====
        List<Course> recentCourses = courseMapper.selectList(
                new LambdaQueryWrapper<Course>().orderByDesc(Course::getCreateTime).last("LIMIT 20"));
        List<DashboardVO.CourseItem> courseItems = new ArrayList<>();
        for (Course c : recentCourses) {
            DashboardVO.CourseItem item = new DashboardVO.CourseItem();
            item.setId(c.getId());
            item.setCourseName(c.getCourseName());
            // 查教师姓名
            SysUser teacher = sysUserMapper.selectById(c.getTeacherId());
            item.setTeacherName(teacher != null ? teacher.getRealName() : "未知");
            item.setStudentCount(courseMemberMapper.selectCount(
                    new LambdaQueryWrapper<CourseMember>().eq(CourseMember::getCourseId, c.getId())));
            item.setStatus(c.getStatus());
            item.setCreateTime(c.getCreateTime() != null ? c.getCreateTime().format(FMT) : "");
            courseItems.add(item);
        }
        vo.setRecentCourses(courseItems);

        // ===== 5. 最新动态：审计日志 =====
        List<AuditLog> logs = auditLogMapper.selectList(
                new LambdaQueryWrapper<AuditLog>().orderByDesc(AuditLog::getCreateTime).last("LIMIT 14"));
        List<DashboardVO.ActivityItem> activities = new ArrayList<>();
        for (AuditLog log : logs) {
            DashboardVO.ActivityItem a = new DashboardVO.ActivityItem();
            a.setUsername(log.getUsername() != null ? log.getUsername() : "系统");
            a.setModule(log.getModule() != null ? log.getModule() : "");
            a.setOperation(log.getOperation() != null ? log.getOperation() : "");
            a.setCreateTime(log.getCreateTime() != null ? log.getCreateTime().format(FMT) : "");
            activities.add(a);
        }
        vo.setRecentActivities(activities);

        return vo;
    }

    /**
     * 计算作业提交率的分母：所有作业 × 对应课程的学生人数
     */
    private long getTotalAssignmentSlots() {
        List<Assignment> assignments = assignmentMapper.selectList(new LambdaQueryWrapper<>());
        long total = 0;
        for (Assignment a : assignments) {
            total += courseMemberMapper.selectCount(
                    new LambdaQueryWrapper<CourseMember>().eq(CourseMember::getCourseId, a.getCourseId()));
        }
        return total;
    }
}
