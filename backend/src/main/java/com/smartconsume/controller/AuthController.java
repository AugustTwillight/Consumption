package com.smartconsume.controller;

import com.smartconsume.common.result.Result;
import com.smartconsume.entity.SysUser;
import com.smartconsume.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 账号密码登录
     *
     * @param request 请求参数 {username, password}
     * @return 登录结果 {token}
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        String token = authService.login(username, password);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);

        return Result.success(data);
    }

    /**
     * 手机号验证码登录
     *
     * @param request 请求参数 {phone, code}
     * @return 登录结果 {token}
     */
    @PostMapping("/phone-login")
    public Result<Map<String, String>> phoneLogin(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");

        String token = authService.phoneLogin(phone, code);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);

        return Result.success(data);
    }

    /**
     * 登出
     *
     * @param request HTTP请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            authService.logout(token);
        }
        return Result.success();
    }

    /**
     * 获取用户信息
     *
     * @param request HTTP请求（包含userId和tenantId）
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<SysUser> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = authService.getUserById(userId);
        return Result.success(user);
    }
}
