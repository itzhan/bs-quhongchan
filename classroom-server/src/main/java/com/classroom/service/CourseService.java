package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.dto.CourseDTO;
import com.classroom.entity.Course;

import java.util.List;

public interface CourseService extends IService<Course> {
    Course createCourse(CourseDTO dto, Long teacherId);
    void updateCourse(CourseDTO dto, Long teacherId);
    IPage<Course> pageList(int page, int size, String keyword, Long teacherId, Integer status);
    List<Course> getStudentCourses(Long studentId);
    Course joinCourse(String courseCode, Long studentId);
    void quitCourse(Long courseId, Long studentId);
    String generateCourseCode();
}
