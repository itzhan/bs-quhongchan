package com.classroom.entity;

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
}
