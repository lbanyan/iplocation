package com.huya.iplocation.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
@Import({ServerPropertiesAutoConfiguration.class, EmbeddedServletContainerAutoConfiguration.class, DispatcherServletAutoConfiguration.class,
        WebMvcAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, HttpEncodingAutoConfiguration.class})
@ComponentScan({"com.huya.iplocation.configuration", "com.huya.iplocation.controller", "com.huya.iplocation.component", "com.huya.iplocation.daemon"})
@EnableConfigurationProperties
@EnableAutoConfiguration
public class ApplicationConfiguration {

}
