package com.smartconsume.common.interceptor;

import com.smartconsume.common.exception.BusinessException;
import com.smartconsume.common.result.ResultCode;
import com.smartconsume.common.redis.RedisService;
import com.smartconsume.common.util.JwtUtil;
import com.smartconsume.service.TenantLicenseService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final TenantLicenseService tenantLicenseService;

    public JwtInterceptor(JwtUtil jwtUtil, RedisService redisService, TenantLicenseService tenantLicenseService) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.tenantLicenseService = tenantLicenseService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求头中的token
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        token = token.substring(7);

        // 验证token是否有效
        if (!jwtUtil.validateToken(token)) {
            // 检查是否在黑名单中
            String blackKey = "jwt:blacklist:" + token;
            if (redisService.hasKey(blackKey)) {
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 将用户信息存入请求属性中
        Claims claims = jwtUtil.getClaimsFromToken(token);
        Long userId = Long.valueOf(claims.getSubject());
        Long tenantId = Long.valueOf(claims.get("tenantId").toString());

        request.setAttribute("userId", userId);
        request.setAttribute("tenantId", tenantId);

        // 检查租户是否被冻结或License过期
        tenantLicenseService.checkTenantFrozen(tenantId);

        return true;
    }
}
