package com.classroom.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CheckinDTO {
    private Long id;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    private String title;
    private Integer checkinType;
    private String gestureCode;
    private Double latitude;
    private Double longitude;
    private Integer locationRange;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "截止时间不能为空")
    private LocalDateTime endTime;
}
