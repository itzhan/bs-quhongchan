package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assignment")
public class Assignment extends BaseEntity {
    private Long courseId;
    private String title;
    private String content;
    private String attachmentUrl;
    private LocalDateTime startTime;
    private LocalDateTime deadline;
    private BigDecimal maxScore;
    private Integer allowLate;
    private Integer status;
}
