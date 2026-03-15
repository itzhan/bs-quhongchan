package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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

    /** 发送者姓名（非数据库字段） */
    @TableField(exist = false)
    private String senderName;

    /** 课程名称（非数据库字段） */
    @TableField(exist = false)
    private String courseName;
}
