package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin")
public class Checkin extends BaseEntity {
    private Long courseId;
    private String title;
    private Integer checkinType;
    private String gestureCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer locationRange;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
}
