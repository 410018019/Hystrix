package com.vipkid.uc.log;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Description
 * <p>hystrix服务降级熔断，可动态修改参数</p>
 * DATE 2018/9/26.
 * @author wuhao.
 */
@Component
@Aspect
public class HystrixCommonAdvice {


    @Pointcut("execution(* com.example.demo.service.*.*(..))")
    public void hystrixPointCut(){}

    @Around("hystrixPointCut()")
    public Object runCommond(final ProceedingJoinPoint proceedingJoinPoint){
        return wrapWithHystrixCommond(proceedingJoinPoint).execute();
    }


    private HystrixCommand<Object> wrapWithHystrixCommond(ProceedingJoinPoint proceedingJoinPoint) {
        String method = proceedingJoinPoint.getSignature().getName();
        return new HystrixCommand<Object>(setter(method)) {

            @Override
            protected Object run() throws Exception {
                try {
                    return proceedingJoinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return null;
            }

            @Override
            protected Object getFallback() {
                System.out.println("进入降级方法！！！！！！！！！！！！！！！！");
                return super.getFallback();
            }
        };
    }

    private HystrixCommand.Setter setter(String method){
        return HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(method))
                /* 配置依赖超时时间,500毫秒*/
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5000) //超时时间
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)//使用命令调用隔离方式,默认:采用线程隔离,
                        .withCircuitBreakerEnabled(true) //是否启用熔断器,默认true. 启动
                        .withCircuitBreakerRequestVolumeThreshold(10)   // 熔断器在整个统计时间内是否开启的阀值,5秒请求数量大于10
                        .withCircuitBreakerErrorThresholdPercentage(50) //默认:50%。当出错率超过50%后熔断器启动
                        .withMetricsRollingStatisticalWindowInMilliseconds(10*1000));  // 统计滚动的时间窗口,默认:5000毫秒（);
    }
}
