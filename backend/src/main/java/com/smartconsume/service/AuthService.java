package com.smartconsume.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartconsume.common.constant.Constants;
import com.smartconsume.common.exception.BusinessException;
import com.smartconsume.common.redis.RedisService;
import com.smartconsume.common.result.ResultCode;
import com.smartconsume.common.util.JwtUtil;
import com.smartconsume.entity.SysUser;
import com.smartconsume.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 认证服务类
 */
@Slf4j
@Service
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    public AuthService(SysUserMapper sysUserMapper, JwtUtil jwtUtil, RedisService redisService) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
    }

    /**
     * 账号密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return Token
     */
    public String login(String username, String password) {
        // 查询用户
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username)
                .eq(SysUser::getDelFlag, Constants.DEL_FLAG_NOT_DELETED);
        SysUser user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED.getCode(), "用户已被禁用");
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getTenantId());

        return token;
    }

    /**
     * 手机号验证码登录
     *
     * @param phone 手机号
     * @param code  验证码
     * @return Token
     */
    public String phoneLogin(String phone, String code) {
        // 这里简化处理，实际应该验证验证码
        // 查询用户
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, phone)
                .eq(SysUser::getDelFlag, Constants.DEL_FLAG_NOT_DELETED);
        SysUser user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "手机号未注册");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED.getCode(), "用户已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getTenantId());

        return token;
    }

    /**
     * 登出
     *
     * @param token Token
     */
    public void logout(String token) {
        // 将token加入Redis黑名单
        String blackKey = Constants.JWT_BLACKLIST_PREFIX + token;
        redisService.setEx(blackKey, true, 24, java.util.concurrent.TimeUnit.HOURS);
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public SysUser getUserById(Long userId) {
        return sysUserMapper.selectById(userId);
    }
}
