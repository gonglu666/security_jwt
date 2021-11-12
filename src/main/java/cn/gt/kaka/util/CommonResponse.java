package cn.gt.kaka.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonResponse<R> implements Serializable{

	private static final long serialVersionUID = 7494007618264392183L;

	private Integer code;
	private Integer subCode;
	private String msg;
	
	@JsonIgnore
	private String errorCode;
	

	private R data;

	public static <R> CommonResponse<R> ofSuccess() {
		return new CommonResponse<R>().setCode(0);
	}

	public static <R> CommonResponse<R> ofSuccess(R data) {
		return new CommonResponse<R>().setCode(0).setData(data);
	}

	public static <R> CommonResponse<R> ofSuccessMsg(String msg) {
		return new CommonResponse<R>().setCode(0).setMsg(msg);
	}


	public static <R> CommonResponse<R> ofFail(int subCode, String errorCode,String msg) {
		CommonResponse<R> result = new CommonResponse<>();
		result.setCode(1);
		result.setSubCode(subCode);
		result.setMsg(msg);
		result.setErrorCode(errorCode);
		return result;
	}

	public Integer getCode() {
		return code;
	}

	public CommonResponse<R> setCode(Integer code) {
		this.code = code;
		return this;
	}

	public Integer getSubCode() {
		return subCode;
	}

	public CommonResponse<R> setSubCode(Integer subCode) {
		this.subCode = subCode;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public CommonResponse<R> setMsg(String msg) {
		this.msg = msg;
		return this;
	}
	

	public String getErrorCode() {
		return errorCode;
	}

	public CommonResponse<R> setErrorCode(String errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public R getData() {
		return data;
	}

	public CommonResponse<R> setData(R data) {
		this.data = data;
		return this;
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
