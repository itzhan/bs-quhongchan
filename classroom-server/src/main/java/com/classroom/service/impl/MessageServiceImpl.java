package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.entity.Course;
import com.classroom.entity.Message;
import com.classroom.entity.SysUser;
import com.classroom.mapper.CourseMapper;
import com.classroom.mapper.MessageMapper;
import com.classroom.mapper.SysUserMapper;
import com.classroom.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final SysUserMapper sysUserMapper;
    private final CourseMapper courseMapper;

    @Override
    public IPage<Message> pageList(int page, int size, Long courseId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) wrapper.eq(Message::getCourseId, courseId);
        wrapper.orderByDesc(Message::getCreateTime);
        IPage<Message> result = page(new Page<>(page, size), wrapper);
        // 填充发送者姓名和课程名称
        for (Message m : result.getRecords()) {
            SysUser sender = sysUserMapper.selectById(m.getSenderId());
            if (sender != null) {
                m.setSenderName(sender.getRealName());
            }
            Course course = courseMapper.selectById(m.getCourseId());
            if (course != null) {
                m.setCourseName(course.getCourseName());
            }
        }
        return result;
    }
}
