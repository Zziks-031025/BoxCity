package com.boxcity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boxcity.common.BusinessException;
import com.boxcity.common.Constants;
import com.boxcity.dto.BindPhoneRequest;
import com.boxcity.dto.LoginRequest;
import com.boxcity.dto.LoginResponse;
import com.boxcity.dto.SendSmsRequest;
import com.boxcity.dto.UserProfileDTO;
import com.boxcity.dto.UserProfileVO;
import com.boxcity.entity.City;
import com.boxcity.entity.Order;
import com.boxcity.entity.User;
import com.boxcity.mapper.CityMapper;
import com.boxcity.mapper.OrderMapper;
import com.boxcity.mapper.UserMapper;
import com.boxcity.service.UserService;
import com.boxcity.util.JwtUtil;
import com.boxcity.util.WechatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final CityMapper cityMapper;
    private final OrderMapper orderMapper;
    private final JwtUtil jwtUtil;
    private final WechatUtil wechatUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public LoginResponse wxLogin(LoginRequest request) {
        WechatUtil.WxSession session = wechatUtil.code2Session(request.getCode());
        if (session == null || session.getOpenid() == null) {
            throw new BusinessException("微信登录失败，请重试");
        }

        String openid = session.getOpenid();
        boolean isNewUser = false;

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
        );

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUnionId(session.getUnionId());
            user.setNickname("盒友" + openid.substring(openid.length() - 4));
            user.setGender(0);
            user.setStatus(1);
            user.setDeleted(0);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
            isNewUser = true;
            log.info("新用户注册: userId={}, openid={}", user.getId(), openid);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("该账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), Constants.USER_TYPE_APP);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setIsNewUser(isNewUser);
        response.setPhoneBound(user.getPhone() != null || wechatUtil.isUsingMockLogin());
        return response;
    }

    @Override
    public void sendSmsCode(SendSmsRequest request) {
        String phone = request.getPhone();
        String redisKey = Constants.REDIS_SMS_CODE + phone;

        String existingCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (existingCode != null) {
            Long ttl = stringRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            if (ttl != null && ttl > (Constants.SMS_CODE_EXPIRE - 60)) {
                throw new BusinessException("验证码已发送，请稍后再试");
            }
        }

        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        stringRedisTemplate.opsForValue().set(redisKey, code, Constants.SMS_CODE_EXPIRE, TimeUnit.SECONDS);

        // TODO: 接入真实短信服务商发送验证码
        log.info("【短信验证码】手机号: {}, 验证码: {}", phone, code);
    }

    @Override
    @Transactional
    public void bindPhone(Long userId, BindPhoneRequest request) {
        String phone = request.getPhone();
        String redisKey = Constants.REDIS_SMS_CODE + phone;

        String cachedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(request.getCode())) {
            throw new BusinessException("验证码错误");
        }

        User existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, phone)
                        .ne(User::getId, userId)
        );
        if (existingUser != null) {
            throw new BusinessException("该手机号已被其他账号绑定");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPhone(phone);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        stringRedisTemplate.delete(redisKey);

        log.info("用户绑定手机号成功: userId={}, phone={}", userId, phone);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setGender(user.getGender());
        vo.setBirthday(user.getBirthday() != null ? user.getBirthday().toString() : null);
        vo.setCityId(user.getCityId());

        if (user.getCityId() != null) {
            City city = cityMapper.selectById(user.getCityId());
            if (city != null) {
                vo.setCityName(city.getName());
            }
        }

        vo.setUnpaidCount(countOrders(userId, 0));
        vo.setUnshippedCount(countOrders(userId, 1));
        vo.setUnreceivedCount(countOrders(userId, 2));
        vo.setCompletedCount(countOrders(userId, 3));

        return vo;
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UserProfileDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            user.setBirthday(LocalDate.parse(dto.getBirthday(), DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (dto.getCityId() != null) {
            user.setCityId(dto.getCityId());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户更新资料: userId={}", userId);
    }
    private Integer countOrders(Long userId, Integer status) {
        return Math.toIntExact(orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .eq(Order::getStatus, status)));
    }
}
