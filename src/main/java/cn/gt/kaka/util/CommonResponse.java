package cn.gt.kaka.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 通用响应结果封装类
 * 
 * 用于统一封装所有API接口的响应结果，提供统一的响应格式
 * 支持泛型，可以封装任意类型的数据
 * 
 * 响应格式：
 * - code: 响应状态码（0表示成功，1表示失败）
 * - subCode: 子状态码，用于更细粒度的错误分类
 * - msg: 响应消息，用于向用户展示的提示信息
 * - data: 响应数据，实际的业务数据
 * - errorCode: 错误代码，用于开发人员定位问题（不返回给前端）
 * 
 * @param <R> 响应数据的类型
 * @author 系统管理员
 * @version 2.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonResponse<R> implements Serializable {

    private static final long serialVersionUID = 7494007618264392183L;

    /**
     * 响应状态码
     * 0: 成功
     * 1: 失败
     */
    private Integer code;
    
    /**
     * 子状态码
     * 用于更细粒度的错误分类，便于前端做不同的处理
     */
    private Integer subCode;
    
    /**
     * 响应消息
     * 向用户展示的提示信息，支持国际化
     */
    private String msg;
    
    /**
     * 错误代码
     * 用于开发人员调试和定位问题，不会序列化到JSON中
     */
    @JsonIgnore
    private String errorCode;

    /**
     * 响应数据
     * 实际的业务数据，支持任意类型
     */
    private R data;

    // ======================== 静态工厂方法 ========================

    /**
     * 创建成功响应（无数据）
     * 
     * @param <R> 数据类型
     * @return 成功响应对象
     */
    public static <R> CommonResponse<R> success() {
        return new CommonResponse<R>().setCode(0);
    }

    /**
     * 创建成功响应（带数据）
     * 
     * @param <R> 数据类型
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <R> CommonResponse<R> success(R data) {
        return new CommonResponse<R>()
                .setCode(0)
                .setData(data);
    }

    /**
     * 创建成功响应（带消息）
     * 
     * @param <R> 数据类型
     * @param message 成功消息
     * @return 成功响应对象
     */
    public static <R> CommonResponse<R> success(String message) {
        return new CommonResponse<R>()
                .setCode(0)
                .setMsg(message);
    }

    /**
     * 创建成功响应（带数据和消息）
     * 
     * @param <R> 数据类型
     * @param data 响应数据
     * @param message 成功消息
     * @return 成功响应对象
     */
    public static <R> CommonResponse<R> success(R data, String message) {
        return new CommonResponse<R>()
                .setCode(0)
                .setData(data)
                .setMsg(message);
    }

    /**
     * 创建失败响应
     * 
     * @param <R> 数据类型
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <R> CommonResponse<R> failure(String message) {
        return new CommonResponse<R>()
                .setCode(1)
                .setMsg(message);
    }

    /**
     * 创建失败响应（详细错误信息）
     * 
     * @param <R> 数据类型
     * @param subCode 子错误码
     * @param errorCode 错误代码
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <R> CommonResponse<R> failure(int subCode, String errorCode, String message) {
        return new CommonResponse<R>()
                .setCode(1)
                .setSubCode(subCode)
                .setErrorCode(errorCode)
                .setMsg(message);
    }

    // ======================== 便捷判断方法 ========================

    /**
     * 判断响应是否成功
     * 
     * @return true表示成功，false表示失败
     */
    @JsonIgnore
    public boolean isSuccess() {
        return Integer.valueOf(0).equals(this.code);
    }

    /**
     * 判断响应是否失败
     * 
     * @return true表示失败，false表示成功
     */
    @JsonIgnore
    public boolean isFailure() {
        return !isSuccess();
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "code=" + code +
                ", subCode=" + subCode +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
