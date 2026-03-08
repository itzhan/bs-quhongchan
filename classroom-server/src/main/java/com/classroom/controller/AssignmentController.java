package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.classroom.common.result.R;
import com.classroom.entity.Assignment;
import com.classroom.entity.Submission;
import com.classroom.service.AssignmentService;
import com.classroom.service.SubmissionService;
import com.classroom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/assignment")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> create(@RequestBody Assignment assignment) {
        assignmentService.save(assignment);
        return R.ok();
    }

    @PutMapping
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> update(@RequestBody Assignment assignment) {
        assignmentService.updateById(assignment);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public R<Void> delete(@PathVariable Long id) {
        assignmentService.removeById(id);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<Assignment> getById(@PathVariable Long id) {
        return R.ok(assignmentService.getById(id));
    }

    @GetMapping("/page")
    public R<IPage<Assignment>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) Long courseId,
                                      @RequestParam(required = false) Integer status) {
        return R.ok(assignmentService.pageList(page, size, courseId, status));
    }

    @PostMapping("/{assignmentId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public R<Void> submit(@PathVariable Long assignmentId, @RequestBody Submission submission) {
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(SecurityUtil.getCurrentUserId());
        submissionService.submit(submission);
        return R.ok();
    }

    @PutMapping("/submission/{id}/grade")
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> grade(@PathVariable Long id, @RequestParam BigDecimal score,
                          @RequestParam(required = false) String feedback) {
        submissionService.grade(id, score, feedback);
        return R.ok();
    }

    @GetMapping("/submission/page")
    public R<IPage<Submission>> submissionPage(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) Long assignmentId,
                                                @RequestParam(required = false) Long studentId,
                                                @RequestParam(required = false) Integer status) {
        return R.ok(submissionService.pageList(page, size, assignmentId, studentId, status));
    }
}
