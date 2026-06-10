package com.boxcity.service;

import com.boxcity.dto.*;
import com.boxcity.entity.User;

public interface UserService {

    LoginResponse wxLogin(LoginRequest request);

    void sendSmsCode(SendSmsRequest request);

    void bindPhone(Long userId, BindPhoneRequest request);

    User getUserById(Long id);

    UserProfileVO getProfile(Long userId);

    void updateProfile(Long userId, UserProfileDTO dto);
}
