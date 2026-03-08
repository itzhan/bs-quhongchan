package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.classroom.common.result.R;
import com.classroom.entity.Grade;
import com.classroom.service.GradeService;
import com.classroom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping("/page")
    public R<IPage<Grade>> page(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) Long courseId,
                                 @RequestParam(required = false) Long studentId) {
        return R.ok(gradeService.pageList(page, size, courseId, studentId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public R<IPage<Grade>> myGrades(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) Long courseId) {
        return R.ok(gradeService.pageList(page, size, courseId, SecurityUtil.getCurrentUserId()));
    }

    @PostMapping("/{courseId}/calculate/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> calculate(@PathVariable Long courseId, @PathVariable Long studentId) {
        gradeService.calculateGrade(courseId, studentId);
        return R.ok();
    }

    @PostMapping("/{courseId}/calculate-all")
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> calculateAll(@PathVariable Long courseId) {
        gradeService.calculateAllGrades(courseId);
        return R.ok();
    }

    @PutMapping("/{courseId}/exam/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> updateExamScore(@PathVariable Long courseId, @PathVariable Long studentId,
                                    @RequestParam BigDecimal examScore) {
        gradeService.updateExamScore(courseId, studentId, examScore);
        return R.ok();
    }

    @GetMapping("/stats/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public R<Map<String, Object>> stats(@PathVariable Long courseId) {
        return R.ok(gradeService.getGradeStats(courseId));
    }
}
