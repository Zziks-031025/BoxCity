package com.boxcity.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WechatUtil {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @Value("${wechat.mock-login:false}")
    private boolean mockLogin;

    @Value("${wechat.mock-openid-seed:local-dev}")
    private String mockOpenidSeed;

    private static final String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 通过 wx.login 获取的 code 换取 openid 和 session_key。
     * 开发环境未配置真实微信参数时，会自动回退到模拟登录。
     */
    public WxSession code2Session(String code) {
        if (shouldUseMockLogin()) {
            return buildMockSession();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("appid", appid);
        params.put("secret", secret);
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        try {
            String response = HttpUtil.get(JSCODE2SESSION_URL, params);
            log.info("微信 jscode2session 响应: {}", response);

            JSONObject json = JSONUtil.parseObj(response);
            if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
                log.error("微信登录失败: errcode={}, errmsg={}", json.getInt("errcode"), json.getStr("errmsg"));
                return null;
            }

            WxSession session = new WxSession();
            session.setOpenid(json.getStr("openid"));
            session.setSessionKey(json.getStr("session_key"));
            session.setUnionId(json.getStr("unionid"));
            return session;
        } catch (Exception ex) {
            log.error("调用微信 jscode2session 异常", ex);
            return null;
        }
    }

    private boolean shouldUseMockLogin() {
        return mockLogin || isPlaceholder(appid) || isPlaceholder(secret);
    }

    public boolean isUsingMockLogin() {
        return shouldUseMockLogin();
    }

    private boolean isPlaceholder(String value) {
        return value == null || value.isBlank() || value.contains("placeholder");
    }

    private WxSession buildMockSession() {
        String digest = DigestUtil.md5Hex(mockOpenidSeed);

        WxSession session = new WxSession();
        session.setOpenid("mock_openid_" + digest.substring(0, 16));
        session.setSessionKey("mock_session_" + digest);
        session.setUnionId("mock_union_" + digest.substring(0, 16));

        log.warn("当前使用模拟微信登录，请在 application.yml 中配置真实 wechat.appid / wechat.secret 后再接入正式环境");
        return session;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WxSession {
        private String openid;
        private String sessionKey;
        private String unionId;
    }
}
