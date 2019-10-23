package com.huya.iplocation.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * WEB配置
 *
 * @author zhuhui
 */
@Configuration
@ServletComponentScan(basePackages = "com.huya.iplocation.servlet")
@ConditionalOnWebApplication
public class WebConfiguration extends WebMvcConfigurerAdapter {

}
