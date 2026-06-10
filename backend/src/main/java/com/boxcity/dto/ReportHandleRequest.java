package com.boxcity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReportHandleRequest {

    @NotBlank(message = "处理结果不能为空")
    private String handleResult;
}
