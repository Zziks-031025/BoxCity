package com.boxcity.dto;

import lombok.Data;

@Data
public class AdminLoginResponse {

    private String token;

    private Long adminId;

    private String username;

    private String role;
}
