package com.boxcity.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private Boolean isNewUser;

    private Boolean phoneBound;
}
