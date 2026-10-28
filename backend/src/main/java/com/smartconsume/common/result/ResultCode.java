package com.smartconsume.common.result;

/**
 * 统一返回状态码枚举
 */
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "token失效或未登录"),
    FORBIDDEN(403, "权限不足或license冻结"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "数据冲突"),

    // 业务状态码
    LICENSE_EXPIRED(4031, "License已过期，租户功能受限"),
    LICENSE_FROZEN(4032, "License校验失败，租户已冻结"),
    USER_DISABLED(4033, "用户已被禁用"),
    SYNC_CONFLICT(4091, "同步数据冲突"),
    CALCULATION_ERROR(5001, "凑单计算异常");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
