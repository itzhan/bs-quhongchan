package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.Submission;

public interface SubmissionService extends IService<Submission> {
    void submit(Submission submission);
    void grade(Long id, java.math.BigDecimal score, String feedback);
    IPage<Submission> pageList(int page, int size, Long assignmentId, Long studentId, Integer status);
}
