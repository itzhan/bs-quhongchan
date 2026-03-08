package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.Assignment;

public interface AssignmentService extends IService<Assignment> {
    IPage<Assignment> pageList(int page, int size, Long courseId, Integer status);
}
