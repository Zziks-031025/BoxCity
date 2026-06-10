package com.boxcity.controller.app;

import com.boxcity.common.Constants;
import com.boxcity.common.Result;
import com.boxcity.dto.UserProfileDTO;
import com.boxcity.dto.UserProfileVO;
import com.boxcity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/app/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody UserProfileDTO dto) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID);
        userService.updateProfile(userId, dto);
        return Result.success();
    }
}
