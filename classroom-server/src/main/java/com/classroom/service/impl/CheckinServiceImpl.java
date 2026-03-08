package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.common.exception.BusinessException;
import com.classroom.dto.CheckinDTO;
import com.classroom.entity.Checkin;
import com.classroom.entity.CheckinRecord;
import com.classroom.entity.CourseMember;
import com.classroom.mapper.CheckinMapper;
import com.classroom.mapper.CheckinRecordMapper;
import com.classroom.mapper.CourseMemberMapper;
import com.classroom.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckinServiceImpl extends ServiceImpl<CheckinMapper, Checkin> implements CheckinService {

    private final CourseMemberMapper courseMemberMapper;
    private final CheckinRecordMapper checkinRecordMapper;

    @Override
    @Transactional
    public Checkin createCheckin(CheckinDTO dto) {
        Checkin checkin = new Checkin();
        checkin.setCourseId(dto.getCourseId());
        checkin.setTitle(dto.getTitle());
        checkin.setCheckinType(dto.getCheckinType() != null ? dto.getCheckinType() : 1);
        checkin.setGestureCode(dto.getGestureCode());
        if (dto.getLatitude() != null) checkin.setLatitude(BigDecimal.valueOf(dto.getLatitude()));
        if (dto.getLongitude() != null) checkin.setLongitude(BigDecimal.valueOf(dto.getLongitude()));
        checkin.setLocationRange(dto.getLocationRange() != null ? dto.getLocationRange() : 200);
        checkin.setStartTime(dto.getStartTime());
        checkin.setEndTime(dto.getEndTime());
        checkin.setStatus(1);
        save(checkin);

        // 为课程所有学生创建未签到记录
        List<CourseMember> members = courseMemberMapper.selectList(
                new LambdaQueryWrapper<CourseMember>()
                        .eq(CourseMember::getCourseId, dto.getCourseId())
                        .eq(CourseMember::getStatus, 1));
        for (CourseMember member : members) {
            CheckinRecord record = new CheckinRecord();
            record.setCheckinId(checkin.getId());
            record.setStudentId(member.getStudentId());
            record.setCourseId(dto.getCourseId());
            record.setStatus(0); // 未签到
            checkinRecordMapper.insert(record);
        }
        return checkin;
    }

    @Override
    public void endCheckin(Long id) {
        Checkin checkin = getById(id);
        if (checkin == null) throw new BusinessException("签到活动不存在");
        checkin.setStatus(0);
        updateById(checkin);
    }

    @Override
    public IPage<Checkin> pageList(int page, int size, Long courseId, Integer status) {
        LambdaQueryWrapper<Checkin> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) wrapper.eq(Checkin::getCourseId, courseId);
        if (status != null) wrapper.eq(Checkin::getStatus, status);
        wrapper.orderByDesc(Checkin::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }
}
