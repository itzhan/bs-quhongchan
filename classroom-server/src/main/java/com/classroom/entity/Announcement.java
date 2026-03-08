package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("announcement")
public class Announcement extends BaseEntity {
    private String title;
    private String content;
    private Integer type;
    private Long courseId;
    private Long publisherId;
    private Integer isTop;
    private Integer status;
}
