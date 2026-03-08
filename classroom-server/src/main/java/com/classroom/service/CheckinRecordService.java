package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.CheckinRecord;

import java.util.Map;

public interface CheckinRecordService extends IService<CheckinRecord> {
    void doCheckin(Long checkinId, Long studentId, Double latitude, Double longitude);
    IPage<CheckinRecord> pageList(int page, int size, Long checkinId, Integer status);
    Map<String, Object> getCheckinStats(Long courseId);
}
