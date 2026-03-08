package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.AuditLog;

public interface AuditLogService extends IService<AuditLog> {
    void log(AuditLog auditLog);
    IPage<AuditLog> pageList(int page, int size, Long userId, String module);
}
