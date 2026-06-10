package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DisputeHandleRequest {

    @NotNull(message = "处理结果不能为空")
    private Boolean approve;

    private String remark;
}
