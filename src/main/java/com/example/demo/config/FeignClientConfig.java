package com.example.demo.config;

import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 * <p>此时如果要通过 Feign 进行远程 Rest 调用，那么必须要考虑服务的认证问题。
 · 此时可以删除原始的 RestConfig 进行的配置处理，然后添加feign的认证配置类
 * </p>
 * DATE 2018/8/15.
 *
 * @author wuhao.
 */
@Configuration
public class FeignClientConfig {
    @Bean
    public Logger.Level getFeignLoggerLevel() {
        //修改 FeignClientConfig，开启日志的输出
        return feign.Logger.Level.FULL ;
    }
    @Bean
    public BasicAuthRequestInterceptor getBasicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("hello", "hello");
    }
}