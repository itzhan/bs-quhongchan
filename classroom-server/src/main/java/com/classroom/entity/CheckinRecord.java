package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin_record")
public class CheckinRecord extends BaseEntity {
    private Long checkinId;
    private Long studentId;
    private Long courseId;
    private LocalDateTime checkinTime;
    private Integer status;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String remark;

    @TableField(exist = false)
    private String studentName;

    @TableField(exist = false)
    private String studentNo;
}
