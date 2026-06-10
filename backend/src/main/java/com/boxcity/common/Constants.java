package com.boxcity.common;

public class Constants {

    private Constants() {
    }

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String USER_ID = "userId";
    public static final String MERCHANT_ID = "merchantId";
    public static final String ADMIN_ID = "adminId";
    public static final String USER_TYPE = "userType";

    /** 用户类型：小程序用户 */
    public static final String USER_TYPE_APP = "app";
    /** 用户类型：商家 */
    public static final String USER_TYPE_MERCHANT = "merchant";
    /** 用户类型：管理员 */
    public static final String USER_TYPE_ADMIN = "admin";

    /** Redis key 前缀：短信验证码 */
    public static final String REDIS_SMS_CODE = "sms:code:";
    /** Redis key 前缀：token */
    public static final String REDIS_TOKEN = "token:";

    /** 短信验证码过期时间（秒），5分钟 */
    public static final long SMS_CODE_EXPIRE = 5 * 60;
}
