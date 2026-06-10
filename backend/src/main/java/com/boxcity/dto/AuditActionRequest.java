package com.boxcity.dto;

import lombok.Data;

@Data
public class AuditActionRequest {

    private Boolean approved;

    private String remark;

    private Integer status;
}
