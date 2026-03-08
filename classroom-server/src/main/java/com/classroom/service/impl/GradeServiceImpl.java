package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.common.exception.BusinessException;
import com.classroom.entity.*;
import com.classroom.mapper.*;
import com.classroom.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    private final CourseMapper courseMapper;
    private final CheckinRecordMapper checkinRecordMapper;
    private final SubmissionMapper submissionMapper;
    private final CourseMemberMapper courseMemberMapper;

    @Override
    public void calculateAllGrades(Long courseId) {
        List<CourseMember> members = courseMemberMapper.selectList(
                new LambdaQueryWrapper<CourseMember>()
                        .eq(CourseMember::getCourseId, courseId)
                        .eq(CourseMember::getStatus, 1));
        for (CourseMember m : members) {
            calculateGrade(courseId, m.getStudentId());
        }
    }

    @Override
    public void calculateGrade(Long courseId, Long studentId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) throw new BusinessException("课程不存在");

        Grade grade = getOne(new LambdaQueryWrapper<Grade>()
                .eq(Grade::getCourseId, courseId).eq(Grade::getStudentId, studentId));
        if (grade == null) {
            grade = new Grade();
            grade.setCourseId(courseId);
            grade.setStudentId(studentId);
        }

        // 签到成绩：签到率 * 100
        long totalCheckin = checkinRecordMapper.selectCount(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getCourseId, courseId).eq(CheckinRecord::getStudentId, studentId));
        long checkedIn = checkinRecordMapper.selectCount(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getCourseId, courseId).eq(CheckinRecord::getStudentId, studentId)
                .in(CheckinRecord::getStatus, 1, 2));
        BigDecimal checkinScore = totalCheckin > 0
                ? BigDecimal.valueOf(checkedIn * 100.0 / totalCheckin).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        grade.setCheckinScore(checkinScore);

        // 作业成绩：已批改作业平均分
        List<Submission> submissions = submissionMapper.selectList(new LambdaQueryWrapper<Submission>()
                .eq(Submission::getCourseId, courseId).eq(Submission::getStudentId, studentId)
                .eq(Submission::getStatus, 2));
        BigDecimal assignmentScore = BigDecimal.ZERO;
        if (!submissions.isEmpty()) {
            BigDecimal totalScore = submissions.stream()
                    .map(Submission::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assignmentScore = totalScore.divide(BigDecimal.valueOf(submissions.size()), 1, RoundingMode.HALF_UP);
        }
        grade.setAssignmentScore(assignmentScore);

        // 总成绩 = 签到*权重 + 作业*权重 + 考试*权重
        BigDecimal cw = course.getCheckinWeight() != null ? course.getCheckinWeight() : BigDecimal.valueOf(20);
        BigDecimal aw = course.getAssignmentWeight() != null ? course.getAssignmentWeight() : BigDecimal.valueOf(40);
        BigDecimal ew = course.getExamWeight() != null ? course.getExamWeight() : BigDecimal.valueOf(40);
        BigDecimal examScore = grade.getExamScore() != null ? grade.getExamScore() : BigDecimal.ZERO;

        BigDecimal total = checkinScore.multiply(cw).add(assignmentScore.multiply(aw)).add(examScore.multiply(ew))
                .divide(BigDecimal.valueOf(100), 1, RoundingMode.HALF_UP);
        grade.setTotalScore(total);

        saveOrUpdate(grade);
    }

    @Override
    public void updateExamScore(Long courseId, Long studentId, BigDecimal examScore) {
        Grade grade = getOne(new LambdaQueryWrapper<Grade>()
                .eq(Grade::getCourseId, courseId).eq(Grade::getStudentId, studentId));
        if (grade == null) {
            grade = new Grade();
            grade.setCourseId(courseId);
            grade.setStudentId(studentId);
        }
        grade.setExamScore(examScore);
        saveOrUpdate(grade);
        calculateGrade(courseId, studentId);
    }

    @Override
    public IPage<Grade> pageList(int page, int size, Long courseId, Long studentId) {
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) wrapper.eq(Grade::getCourseId, courseId);
        if (studentId != null) wrapper.eq(Grade::getStudentId, studentId);
        wrapper.orderByDesc(Grade::getTotalScore);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Map<String, Object> getGradeStats(Long courseId) {
        Map<String, Object> stats = new HashMap<>();
        List<Grade> grades = list(new LambdaQueryWrapper<Grade>().eq(Grade::getCourseId, courseId));
        stats.put("count", grades.size());
        if (!grades.isEmpty()) {
            double avg = grades.stream().filter(g -> g.getTotalScore() != null)
                    .mapToDouble(g -> g.getTotalScore().doubleValue()).average().orElse(0);
            double max = grades.stream().filter(g -> g.getTotalScore() != null)
                    .mapToDouble(g -> g.getTotalScore().doubleValue()).max().orElse(0);
            double min = grades.stream().filter(g -> g.getTotalScore() != null)
                    .mapToDouble(g -> g.getTotalScore().doubleValue()).min().orElse(0);
            long pass = grades.stream().filter(g -> g.getTotalScore() != null && g.getTotalScore().doubleValue() >= 60).count();
            stats.put("avg", String.format("%.1f", avg));
            stats.put("max", max);
            stats.put("min", min);
            stats.put("passRate", String.format("%.1f", pass * 100.0 / grades.size()));
        }
        return stats;
    }
}
