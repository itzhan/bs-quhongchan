package com.classroom.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.common.exception.BusinessException;
import com.classroom.dto.CourseDTO;
import com.classroom.entity.Course;
import com.classroom.entity.CourseMember;
import com.classroom.mapper.CourseMapper;
import com.classroom.mapper.CourseMemberMapper;
import com.classroom.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseMemberMapper courseMemberMapper;

    @Override
    @Transactional
    public Course createCourse(CourseDTO dto, Long teacherId) {
        Course course = new Course();
        course.setCourseName(dto.getCourseName());
        course.setCourseCode(generateCourseCode());
        course.setCoverImage(dto.getCoverImage());
        course.setDescription(dto.getDescription());
        course.setTeacherId(teacherId);
        course.setSemester(dto.getSemester());
        course.setMaxStudents(dto.getMaxStudents() != null ? dto.getMaxStudents() : 200);
        course.setCheckinWeight(dto.getCheckinWeight());
        course.setAssignmentWeight(dto.getAssignmentWeight());
        course.setExamWeight(dto.getExamWeight());
        course.setStatus(1);
        save(course);
        return course;
    }

    @Override
    public void updateCourse(CourseDTO dto, Long teacherId) {
        Course course = getById(dto.getId());
        if (course == null) throw new BusinessException("课程不存在");
        if (!course.getTeacherId().equals(teacherId)) throw new BusinessException("无权修改此课程");
        course.setCourseName(dto.getCourseName());
        course.setCoverImage(dto.getCoverImage());
        course.setDescription(dto.getDescription());
        course.setSemester(dto.getSemester());
        course.setMaxStudents(dto.getMaxStudents());
        course.setCheckinWeight(dto.getCheckinWeight());
        course.setAssignmentWeight(dto.getAssignmentWeight());
        course.setExamWeight(dto.getExamWeight());
        updateById(course);
    }

    @Override
    public IPage<Course> pageList(int page, int size, String keyword, Long teacherId, Integer status) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Course::getCourseName, keyword);
        }
        if (teacherId != null) {
            wrapper.eq(Course::getTeacherId, teacherId);
        }
        if (status != null) {
            wrapper.eq(Course::getStatus, status);
        }
        wrapper.orderByDesc(Course::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public List<Course> getStudentCourses(Long studentId) {
        List<CourseMember> members = courseMemberMapper.selectList(
                new LambdaQueryWrapper<CourseMember>()
                        .eq(CourseMember::getStudentId, studentId)
                        .eq(CourseMember::getStatus, 1));
        List<Long> courseIds = members.stream().map(CourseMember::getCourseId).collect(Collectors.toList());
        if (courseIds.isEmpty()) return List.of();
        return listByIds(courseIds);
    }

    @Override
    @Transactional
    public Course joinCourse(String courseCode, Long studentId) {
        Course course = getOne(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, courseCode));
        if (course == null) throw new BusinessException("班课码无效");
        if (course.getStatus() != 1) throw new BusinessException("课程未开放");
        Long count = courseMemberMapper.selectCount(
                new LambdaQueryWrapper<CourseMember>()
                        .eq(CourseMember::getCourseId, course.getId())
                        .eq(CourseMember::getStudentId, studentId)
                        .eq(CourseMember::getStatus, 1));
        if (count > 0) throw new BusinessException("已加入该课程");
        CourseMember member = new CourseMember();
        member.setCourseId(course.getId());
        member.setStudentId(studentId);
        member.setJoinTime(LocalDateTime.now());
        member.setStatus(1);
        courseMemberMapper.insert(member);
        return course;
    }

    @Override
    public void quitCourse(Long courseId, Long studentId) {
        CourseMember member = courseMemberMapper.selectOne(
                new LambdaQueryWrapper<CourseMember>()
                        .eq(CourseMember::getCourseId, courseId)
                        .eq(CourseMember::getStudentId, studentId));
        if (member == null) throw new BusinessException("未加入该课程");
        member.setStatus(0);
        courseMemberMapper.updateById(member);
    }

    @Override
    public String generateCourseCode() {
        String code;
        do {
            code = RandomUtil.randomNumbers(6);
        } while (count(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, code)) > 0);
        return code;
    }
}
