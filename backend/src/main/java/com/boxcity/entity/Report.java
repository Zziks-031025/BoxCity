package com.boxcity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report")
public class Report {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reporterId;

    private Integer targetType;

    private Long targetId;

    private String reason;

    private String evidenceImages;

    private Integer status;

    private String handleResult;

    private Long handlerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
