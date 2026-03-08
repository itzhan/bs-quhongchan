package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.CourseMember;

import java.util.List;

public interface CourseMemberService extends IService<CourseMember> {
    IPage<CourseMember> pageList(int page, int size, Long courseId);
    List<CourseMember> listByCourseId(Long courseId);
    void removeMember(Long courseId, Long studentId);
}
