package com.example.demo.service;

import com.example.demo.entity.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Description
 * <p>
 * </p>
 * DATE 2018/8/14.
 *
 * @author wuhao.
 */
@Service
public class UserService {

    private final Logger logger =  LoggerFactory.getLogger(UserCommand.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * @CacheResult：用来标记其你去命令的结果应该被缓存，必须与@HystrixCommand注解结合使用；
       @CacheRemove：该注解用来让请求命令的缓存失败，失效的缓存根据定义的Key决定；
       @CacheKey：该注解用来在请求命令的参数上标记，是其作文缓存的Key值，如果没有标注则会使用所有参数。如果同时使用了@CacheResult和 @CacheRemove注解的cacheKeyMethod方法指定缓存Key生成，那么该注解将不会起作用。
       使用@CacheResult(cacheKeyMethod）添加缓存方式一
        * @param id
     * @return
     */
    @HystrixCommand(commandKey = "commandKey1",fallbackMethod = "fallback")
    @CacheResult(cacheKeyMethod = "getUserId")
    public User getUserById(Integer id){
        return restTemplate.getForObject("http://USER-SERVICE/user/{1}",User.class,id);
    }

    /**
     * 使用cacheKey 添加缓存方式二，不需要CacheResult中的缓存参数cacheKeyMethod
     * @param id
     * @return
     */
    @CacheResult
    @HystrixCommand(commandKey = "commandKey2")
    public Integer openCacheByAnnotation2(@CacheKey Long id){
        //此次结果会被缓存
        return restTemplate.getForObject("http://eureka-service/hystrix/cache", Integer.class);
    }


    /**
     * 使用注解清除缓存 方式1
     * @CacheRemove 必须指定commandKey才能进行清除指定缓存
     */
    @CacheRemove(commandKey = "commandKey1", cacheKeyMethod = "getUserId")
    @HystrixCommand
    public void flushCacheByAnnotation1(Long id){
        logger.info("请求缓存已清空！");
        //这个@CacheRemove注解直接用在更新方法上效果更好
    }

    /**
     * 使用注解清除缓存 方式2
     * @CacheRemove 必须指定commandKey才能进行清除指定缓存
     */
    @CacheRemove(commandKey = "commandKey2")
    @HystrixCommand
    public void flushCacheByAnnotation2(@CacheKey Long id){
        logger.info("请求缓存已清空！");
        //这个@CacheRemove注解直接用在更新方法上效果更好
    }

    //异常或超时时处发方法
    private User fallback(Integer id, Throwable e){
        logger.info("getUserByIdExceptionError:"+e.getMessage()+id);
        return new User("wh","123");
    }

    /**
     * 缓存方法
     * @param id
     * @return
     */
    protected String getUserId(Integer id) {
        return String.valueOf(id);
    }

    @HystrixCommand(commandKey = "testCommand", groupKey = "testGroup", threadPoolKey = "testThreadKey",
            fallbackMethod = "hiConsumerFallBack", ignoreExceptions = {NullPointerException.class},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),//配置线程池大小,默认值10个.
                    @HystrixProperty(name = "maxQueueSize", value = "101"),//配置线程值等待队列长度,默认值:-1
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),//设置存活时间，单位分钟。如果coreSize小于maximumSize，那么该属性控制一个线程从实用完成到被释放的时间。
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),//设置队列拒绝的阈值——一个人为设置的拒绝访问的最大队列值，即使maxQueueSize还没有达到
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),//设置滚动的统计窗口被分成的桶（bucket）的数目。
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")//设置统计的滚动窗口的时间段大小。该属性是线程池保持指标时间长短。默认值：10000（毫秒）
            },commandProperties = {@HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value = "50"),//默认:50%。当出错率超过50%后熔断器启动.
                                   @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value = "10"),// 熔断器在整个统计时间内是否开启的阀值，默认20。也就是10秒钟内至少请求20次，熔断器才发挥起作用
                                   @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value = "5000"),////熔断器默认工作时间,默认:5秒.熔断器中断请求5秒后会进入半打开状态,放部分流量过去重试
                                   @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "10000"),//超时时间毫秒
                                   @HystrixProperty(name="execution.timeout.enabled",value = "true"),//是否开始超时限制
            }
    )
    public String paramConfig(String id) {
        //SERVICE_HI是服务端的spring.application.name，并且大写，hi为服务端提供的接口
        if(!id.equals("succ")){
            int i = 5/0;
        }
        return  id;
    }

    public String hiConsumerFallBack(String id, Throwable e) {
        logger.info("this is a error"+e.getMessage());
        return "This is a error";
    }
}