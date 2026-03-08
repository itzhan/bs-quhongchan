package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.entity.AuditLog;
import com.classroom.mapper.AuditLogMapper;
import com.classroom.service.AuditLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    @Override
    @Async
    public void log(AuditLog auditLog) {
        save(auditLog);
    }

    @Override
    public IPage<AuditLog> pageList(int page, int size, Long userId, String module) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) wrapper.eq(AuditLog::getUserId, userId);
        if (StringUtils.hasText(module)) wrapper.eq(AuditLog::getModule, module);
        wrapper.orderByDesc(AuditLog::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }
}
