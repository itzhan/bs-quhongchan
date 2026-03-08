package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_member")
public class CourseMember extends BaseEntity {
    private Long courseId;
    private Long studentId;
    private LocalDateTime joinTime;
    private Integer status;

    /** 学生姓名（非数据库字段，关联查询填充） */
    @TableField(exist = false)
    private String studentName;

    /** 学号（非数据库字段，关联查询填充） */
    @TableField(exist = false)
    private String studentNo;
}
