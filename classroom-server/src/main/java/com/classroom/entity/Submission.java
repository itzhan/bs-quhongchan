package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("submission")
public class Submission extends BaseEntity {
    private Long assignmentId;
    private Long studentId;
    private Long courseId;
    private String content;
    private String attachmentUrl;
    private LocalDateTime submitTime;
    private Integer isLate;
    private BigDecimal score;
    private String feedback;
    private LocalDateTime gradeTime;
    private Integer status;
}
