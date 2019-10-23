package com.huya.iplocation.component;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Request 请求参数绑定方式处理 <br/>
 * Request GET 请求参数 POJO 类无需 getter/setter 方法
 *
 * @author xingping
 * @date 2019-06-12
 */
@ControllerAdvice
public class BindingControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }
}
