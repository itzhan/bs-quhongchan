package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("material")
public class Material extends BaseEntity {
    private Long courseId;
    private String title;
    private String description;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private Integer downloadCount;
    private Long uploaderId;
}
