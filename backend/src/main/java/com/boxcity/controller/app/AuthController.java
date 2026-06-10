package com.boxcity.controller.app;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.BindPhoneRequest;
import com.boxcity.dto.LoginRequest;
import com.boxcity.dto.LoginResponse;
import com.boxcity.dto.SendSmsRequest;
import com.boxcity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/app/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 微信小程序登录
     */
    @PostMapping("/wxLogin")
    public Result<LoginResponse> wxLogin(@RequestBody @Validated LoginRequest request) {
        LoginResponse response = userService.wxLogin(request);
        return Result.success(response);
    }

    /**
     * 发送短信验证码
     */
    @PostMapping("/sendSms")
    public Result<Void> sendSms(@RequestBody @Validated SendSmsRequest request) {
        userService.sendSmsCode(request);
        return Result.success();
    }

    /**
     * 绑定手机号（需登录）
     */
    @PostMapping("/bindPhone")
    public Result<Void> bindPhone(@RequestBody @Validated BindPhoneRequest request,
                                  HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute(Constants.USER_ID);
        userService.bindPhone(userId, request);
        return Result.success();
    }
}
