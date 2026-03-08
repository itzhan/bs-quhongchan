package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message")
public class Message extends BaseEntity {
    private Long courseId;
    private Long senderId;
    private String content;
    private Long parentId;
    private Integer isAnonymous;
}
