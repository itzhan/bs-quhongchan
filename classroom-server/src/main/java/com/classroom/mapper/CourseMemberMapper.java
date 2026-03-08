package com.classroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classroom.entity.CourseMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMemberMapper extends BaseMapper<CourseMember> {

    @Select("SELECT cm.*, u.real_name AS student_name, u.username AS student_no " +
            "FROM course_member cm LEFT JOIN sys_user u ON cm.student_id = u.id " +
            "WHERE cm.course_id = #{courseId} AND cm.status = 1 " +
            "ORDER BY cm.join_time ASC")
    IPage<CourseMember> selectMembersWithName(Page<CourseMember> page, @Param("courseId") Long courseId);
}
