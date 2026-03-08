package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.common.exception.BusinessException;
import com.classroom.entity.Checkin;
import com.classroom.entity.CheckinRecord;
import com.classroom.mapper.CheckinMapper;
import com.classroom.mapper.CheckinRecordMapper;
import com.classroom.service.CheckinRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckinRecordServiceImpl extends ServiceImpl<CheckinRecordMapper, CheckinRecord> implements CheckinRecordService {

    private final CheckinMapper checkinMapper;

    @Override
    public void doCheckin(Long checkinId, Long studentId, Double latitude, Double longitude) {
        Checkin checkin = checkinMapper.selectById(checkinId);
        if (checkin == null) throw new BusinessException("签到活动不存在");
        if (checkin.getStatus() != 1) throw new BusinessException("签到已结束");
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(checkin.getEndTime())) throw new BusinessException("签到已截止");

        CheckinRecord record = getOne(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getCheckinId, checkinId)
                .eq(CheckinRecord::getStudentId, studentId));
        if (record == null) throw new BusinessException("签到记录不存在，您可能未加入该课程");
        if (record.getStatus() == 1) throw new BusinessException("已签到，请勿重复签到");

        record.setCheckinTime(now);
        record.setStatus(now.isAfter(checkin.getStartTime().plusMinutes(10)) ? 2 : 1); // 超过10分钟算迟到
        if (latitude != null) record.setLatitude(BigDecimal.valueOf(latitude));
        if (longitude != null) record.setLongitude(BigDecimal.valueOf(longitude));
        updateById(record);
    }

    @Override
    public IPage<CheckinRecord> pageList(int page, int size, Long checkinId, Integer status) {
        return baseMapper.selectRecordsWithName(new Page<>(page, size), checkinId, status);
    }

    @Override
    public Map<String, Object> getCheckinStats(Long courseId) {
        Map<String, Object> stats = new HashMap<>();
        long total = count(new LambdaQueryWrapper<CheckinRecord>().eq(CheckinRecord::getCourseId, courseId));
        long checked = count(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getCourseId, courseId).eq(CheckinRecord::getStatus, 1));
        long late = count(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getCourseId, courseId).eq(CheckinRecord::getStatus, 2));
        long absent = count(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getCourseId, courseId).eq(CheckinRecord::getStatus, 0));
        stats.put("total", total);
        stats.put("checked", checked);
        stats.put("late", late);
        stats.put("absent", absent);
        stats.put("rate", total > 0 ? String.format("%.1f", (checked + late) * 100.0 / total) : "0.0");
        return stats;
    }
}
