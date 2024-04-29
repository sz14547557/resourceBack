package com.sz.usercenter.common;

/**
 * 错误码
 *
 */
public enum ErrorCode {

    SUCCESS(0, "ok", "",true),
    PARAMS_ERROR(40000, "请求参数错误", "",false),
    NULL_ERROR(40001, "请求数据为空", "",false),
    NOT_LOGIN(40100, "未登录", "",false),
    NO_AUTH(40101, "无权限", "",false),
    LOGIN_ERROR(40200, "查询用户为空", "",false),
    SYSTEM_ERROR(50000, "系统内部异常", "",false);




    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 将失败的success为false 传递到antDesignPro界面，做全局的异常处理
     */
    private final boolean success;

    /**
     * 状态码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description,boolean success) {
        this.code = code;
        this.message = message;
        this.description = description;
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public boolean getSuccess() {
        return success;
    }
}
