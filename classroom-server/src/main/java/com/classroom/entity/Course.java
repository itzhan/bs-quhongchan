package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {
    private String courseName;
    private String courseCode;
    private String coverImage;
    private String description;
    private Long teacherId;
    private String semester;
    private Integer status;
    private Integer maxStudents;
    private BigDecimal checkinWeight;
    private BigDecimal assignmentWeight;
    private BigDecimal examWeight;
}
