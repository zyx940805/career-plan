package com.career.plan.common;

import lombok.Data;

/**
 * 全局统一响应结果类
 * 用于前后端数据交互的标准格式
 * * @param <T> 响应数据的泛型类型
 */
@Data
public class Result<T> {
    private int code;      // 状态码，200表示成功
    private String message; // 状态描述信息
    private T data;         // 具体的业务数据内容

    /**
     * 成功响应的静态构造方法
     * @param data 返回的数据
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 错误响应的静态构造方法
     * @param code 错误码
     * @param message 错误描述
     * @return Result对象
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}