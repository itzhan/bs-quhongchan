package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.common.exception.BusinessException;
import com.classroom.entity.Assignment;
import com.classroom.entity.Submission;
import com.classroom.mapper.AssignmentMapper;
import com.classroom.mapper.SubmissionMapper;
import com.classroom.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {

    private final AssignmentMapper assignmentMapper;

    @Override
    public void submit(Submission submission) {
        Assignment assignment = assignmentMapper.selectById(submission.getAssignmentId());
        if (assignment == null) throw new BusinessException("作业不存在");

        Long existing = count(new LambdaQueryWrapper<Submission>()
                .eq(Submission::getAssignmentId, submission.getAssignmentId())
                .eq(Submission::getStudentId, submission.getStudentId()));
        if (existing > 0) throw new BusinessException("已提交过该作业");

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(assignment.getDeadline()) && assignment.getAllowLate() != 1) {
            throw new BusinessException("作业已截止且不允许迟交");
        }
        submission.setSubmitTime(now);
        submission.setIsLate(now.isAfter(assignment.getDeadline()) ? 1 : 0);
        submission.setCourseId(assignment.getCourseId());
        submission.setStatus(1);
        save(submission);
    }

    @Override
    public void grade(Long id, BigDecimal score, String feedback) {
        Submission submission = getById(id);
        if (submission == null) throw new BusinessException("提交记录不存在");
        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setGradeTime(LocalDateTime.now());
        submission.setStatus(2);
        updateById(submission);
    }

    @Override
    public IPage<Submission> pageList(int page, int size, Long assignmentId, Long studentId, Integer status) {
        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<>();
        if (assignmentId != null) wrapper.eq(Submission::getAssignmentId, assignmentId);
        if (studentId != null) wrapper.eq(Submission::getStudentId, studentId);
        if (status != null) wrapper.eq(Submission::getStatus, status);
        wrapper.orderByDesc(Submission::getSubmitTime);
        return page(new Page<>(page, size), wrapper);
    }
}
