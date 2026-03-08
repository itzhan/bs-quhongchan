package com.classroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classroom.entity.CheckinRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {

    @Select("<script>" +
            "SELECT cr.*, u.real_name AS student_name, u.username AS student_no " +
            "FROM checkin_record cr LEFT JOIN sys_user u ON cr.student_id = u.id " +
            "WHERE 1=1 " +
            "<if test='checkinId != null'> AND cr.checkin_id = #{checkinId}</if> " +
            "<if test='status != null'> AND cr.status = #{status}</if> " +
            "ORDER BY cr.create_time DESC" +
            "</script>")
    IPage<CheckinRecord> selectRecordsWithName(Page<CheckinRecord> page,
                                                @Param("checkinId") Long checkinId,
                                                @Param("status") Integer status);
}
