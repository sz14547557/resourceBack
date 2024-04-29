package com.sz.usercenter.common;

/**
 * 返回工具类
 *

 */
public class ResultUtils {

    /**
     * 成功  data可以为数据、对象都可以，是一个通用返回工具类，不需要再单独定义。
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok",true);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String message, String description,boolean success) {
        return new BaseResponse(code, null, message, description,success);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description,boolean success) {
        return new BaseResponse(errorCode.getCode(), null, message, description, success);
    }

    // /

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String description,boolean success) {
        return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), description,success);
    }
}
