package com.huya.iplocation.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * HTTP返回结果
 *
 * @author xingping
 * @date 2019-10-12
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResult<T> {

    /**
     * 1: ok，0：异常
     */
    public int status;

    /**
     * status=1时，message中是IP信息，status=0时，message中是异常信息
     */
    public T message;

    @SuppressWarnings("rawtypes")
    public static final HttpResult OK = new HttpResult();

    public HttpResult() {
    }

    public HttpResult(T message) {
        this.message = message;
    }

    public HttpResult(int status, T message) {
        this.status = status;
        this.message = message;
    }
}
