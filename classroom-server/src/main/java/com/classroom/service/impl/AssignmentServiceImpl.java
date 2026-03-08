package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.entity.Assignment;
import com.classroom.mapper.AssignmentMapper;
import com.classroom.service.AssignmentService;
import org.springframework.stereotype.Service;

@Service
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements AssignmentService {

    @Override
    public IPage<Assignment> pageList(int page, int size, Long courseId, Integer status) {
        LambdaQueryWrapper<Assignment> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) wrapper.eq(Assignment::getCourseId, courseId);
        if (status != null) wrapper.eq(Assignment::getStatus, status);
        wrapper.orderByDesc(Assignment::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }
}
