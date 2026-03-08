package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.classroom.common.result.R;
import com.classroom.dto.CourseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.CourseMember;
import com.classroom.service.CourseMemberService;
import com.classroom.service.CourseService;
import com.classroom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseMemberService courseMemberService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public R<Course> create(@Valid @RequestBody CourseDTO dto) {
        return R.ok(courseService.createCourse(dto, SecurityUtil.getCurrentUserId()));
    }

    @PutMapping
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> update(@Valid @RequestBody CourseDTO dto) {
        courseService.updateCourse(dto, SecurityUtil.getCurrentUserId());
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<Course> getById(@PathVariable Long id) {
        return R.ok(courseService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public R<Void> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return R.ok();
    }

    @GetMapping("/page")
    public R<IPage<Course>> page(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Long teacherId,
                                  @RequestParam(required = false) Integer status) {
        return R.ok(courseService.pageList(page, size, keyword, teacherId, status));
    }

    @GetMapping("/my")
    public R<?> myCourses() {
        if (SecurityUtil.isTeacher()) {
            return R.ok(courseService.pageList(1, 100, null, SecurityUtil.getCurrentUserId(), null));
        }
        return R.ok(courseService.getStudentCourses(SecurityUtil.getCurrentUserId()));
    }

    @PostMapping("/join")
    @PreAuthorize("hasRole('STUDENT')")
    public R<Course> joinCourse(@RequestParam String courseCode) {
        return R.ok(courseService.joinCourse(courseCode, SecurityUtil.getCurrentUserId()));
    }

    @PostMapping("/{courseId}/quit")
    @PreAuthorize("hasRole('STUDENT')")
    public R<Void> quitCourse(@PathVariable Long courseId) {
        courseService.quitCourse(courseId, SecurityUtil.getCurrentUserId());
        return R.ok();
    }

    @GetMapping("/{courseId}/members")
    public R<IPage<CourseMember>> members(@PathVariable Long courseId,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return R.ok(courseMemberService.pageList(page, size, courseId));
    }

    @DeleteMapping("/{courseId}/members/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> removeMember(@PathVariable Long courseId, @PathVariable Long studentId) {
        courseMemberService.removeMember(courseId, studentId);
        return R.ok();
    }
}
