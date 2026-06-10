package com.boxcity.interceptor;

import com.boxcity.common.Constants;
import com.boxcity.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader(Constants.TOKEN_HEADER);
        if (header == null || !header.startsWith(Constants.TOKEN_PREFIX)) {
            writeUnauthorized(response, "未提供有效的认证令牌");
            return false;
        }

        String token = header.substring(Constants.TOKEN_PREFIX.length());
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            writeUnauthorized(response, "认证令牌无效或已过期");
            return false;
        }

        Long userId = claims.get(Constants.USER_ID, Long.class);
        String userType = claims.get(Constants.USER_TYPE, String.class);

        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/api/app/") && !Constants.USER_TYPE_APP.equals(userType)) {
            writeUnauthorized(response, "认证身份不匹配");
            return false;
        }
        if (requestUri.startsWith("/api/merchant/") && !Constants.USER_TYPE_MERCHANT.equals(userType)) {
            writeUnauthorized(response, "认证身份不匹配");
            return false;
        }
        if (requestUri.startsWith("/api/admin/") && !Constants.USER_TYPE_ADMIN.equals(userType)) {
            writeUnauthorized(response, "认证身份不匹配");
            return false;
        }

        request.setAttribute(Constants.USER_ID, userId);
        request.setAttribute(Constants.USER_TYPE, userType);

        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("msg", message);
        result.put("data", null);

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
