package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.Grade;

import java.util.Map;

public interface GradeService extends IService<Grade> {
    void calculateGrade(Long courseId, Long studentId);
    void calculateAllGrades(Long courseId);
    void updateExamScore(Long courseId, Long studentId, java.math.BigDecimal examScore);
    IPage<Grade> pageList(int page, int size, Long courseId, Long studentId);
    Map<String, Object> getGradeStats(Long courseId);
}
