package com.classroom.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class CourseDTO {
    private Long id;

    @NotBlank(message = "课程名称不能为空")
    private String courseName;

    private String coverImage;
    private String description;
    private String semester;
    private Integer maxStudents;
    private BigDecimal checkinWeight;
    private BigDecimal assignmentWeight;
    private BigDecimal examWeight;
}
