package com.example.demo.service;

import com.example.demo.entity.User;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Description
 * <p>
 * </p>
 * DATE 2018/8/14.
 *
 * @author wuhao.
 */
public class UserCommand extends HystrixCommand<User> {

    private final Logger logger =  LoggerFactory.getLogger(UserCommand.class);
    private static final HystrixCommandKey GETTER_KEY = HystrixCommandKey.Factory.asKey("CommandKey");
    private RestTemplate restTemplate;
    private Integer id;

    public UserCommand(Setter setter, RestTemplate restTemplate, Integer id){
        super(setter);
        this.restTemplate = restTemplate;
        this.id = id;
    }

    public UserCommand(RestTemplate restTemplate,Integer id){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"))
                /* 配置依赖超时时间,500毫秒*/
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500) //超时时间
                                               .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)//使用命令调用隔离方式,默认:采用线程隔离,
                                                .withCircuitBreakerEnabled(true) //是否启用熔断器,默认true. 启动
                                                .withCircuitBreakerRequestVolumeThreshold(10)   // 熔断器在整个统计时间内是否开启的阀值,5秒请求数量大于10
                                                .withCircuitBreakerErrorThresholdPercentage(50) //默认:50%。当出错率超过50%后熔断器启动
                                                 .withMetricsRollingStatisticalWindowInMilliseconds(10*1000)));  // 统计滚动的时间窗口,默认:5000毫秒（
                 // 使用不同的线程池做隔离，防止上层线程池跑满，影响降级逻辑.
               // .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("RemoteServiceXFallback")));
        this.restTemplate = restTemplate;
        this.id = id;
    }

    @Override
    protected User run() throws Exception {
        logger.info(">>>>>>>>>>>>>自定义HystrixCommand请求>>>>>>>>>>>>>>>>>>>>>>>>>>");
        int i = 1/0;
        User forObject = restTemplate.getForObject("http://USER-SERVICE/user/{1}", User.class, id);
        //刷新缓存，清理失效的缓存
        flushCache(forObject.getId());
        return forObject;
    }

    @Override
    protected User getFallback() {
        Throwable e = getExecutionException();
        logger.info(">>>>>>>>>>>>>>>>>>>>>{异常信息}<<<<<<<<<<<<<<<<",e.getMessage());
        return new User("wh","wh");
    }

//    @Override
//    protected User run() throws Exception {
//        logger.info(">>>>>>>>>>>>>自定义HystrixCommand请求>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        Thread.sleep(5000);
//        User forObject = restTemplate.getForObject("http://USER-SERVICE/user/{1}", User.class, id);
//        //刷新缓存，清理失效的缓存
//        flushCache(forObject.getId());
//        return forObject;
//    }



    /**
     * 添加缓存，提升效率
     * @return
     */
    @Override
    protected String getCacheKey() {
        return String.valueOf(id);
    }

    /**
     * 根据id清理缓存
     * @param id
     */
    public static void flushCache(Integer id){
        HystrixRequestCache.getInstance(GETTER_KEY,
                HystrixConcurrencyStrategyDefault.getInstance()).clear(String.valueOf(id));
    }
}