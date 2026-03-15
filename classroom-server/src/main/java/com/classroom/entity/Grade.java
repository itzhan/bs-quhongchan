package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grade")
public class Grade extends BaseEntity {
    private Long courseId;
    private Long studentId;
    private BigDecimal checkinScore;
    private BigDecimal assignmentScore;
    private BigDecimal examScore;
    private BigDecimal totalScore;
    private String remark;

    /** 学生姓名（非数据库字段） */
    @TableField(exist = false)
    private String studentName;

    /** 学号/用户名（非数据库字段） */
    @TableField(exist = false)
    private String studentNo;

    /** 课程名称（非数据库字段） */
    @TableField(exist = false)
    private String courseName;
}
