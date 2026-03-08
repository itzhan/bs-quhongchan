package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.entity.CourseMember;
import com.classroom.mapper.CourseMemberMapper;
import com.classroom.service.CourseMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseMemberServiceImpl extends ServiceImpl<CourseMemberMapper, CourseMember> implements CourseMemberService {

    @Override
    public IPage<CourseMember> pageList(int page, int size, Long courseId) {
        return baseMapper.selectMembersWithName(new Page<>(page, size), courseId);
    }

    @Override
    public List<CourseMember> listByCourseId(Long courseId) {
        return list(new LambdaQueryWrapper<CourseMember>()
                .eq(CourseMember::getCourseId, courseId)
                .eq(CourseMember::getStatus, 1));
    }

    @Override
    public void removeMember(Long courseId, Long studentId) {
        CourseMember member = getOne(new LambdaQueryWrapper<CourseMember>()
                .eq(CourseMember::getCourseId, courseId)
                .eq(CourseMember::getStudentId, studentId));
        if (member != null) {
            member.setStatus(0);
            updateById(member);
        }
    }
}
