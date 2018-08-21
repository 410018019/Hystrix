package com.example.demo.entity;


/**
 * Description
 * <p>统计属性说明</p>
 * DATE 2018/8/16.
 *
 * @author wuhao.
 */
public class Constant {
    //-----------------------------
    //HystrixCommandProperties
    //-----------------------------
   // 统计滚动的时间窗口,默认:5000毫秒（取自circuitBreakerSleepWindowInMilliseconds）
    private final int metricsRollingStatisticalWindowInMilliseconds = 5000;
    // 统计窗口的Buckets的数量,默认:10个,每秒一个Buckets统计
    private final int metricsRollingStatisticalWindowBuckets = 10; // number of buckets in the statisticalWindow
    // 是否开启监控统计功能,默认:true
    private final boolean metricsRollingPercentileEnabled = true;
    /* --------------熔断器相关------------------*/
    // 熔断器在整个统计时间内是否开启的阀值，默认20。也就是在metricsRollingStatisticalWindowInMilliseconds（默认10s）内至少请求20次，熔断器才发挥起作用
    private final int circuitBreakerRequestVolumeThreshold = 20;
    // 熔断时间窗口，默认:5秒.熔断器中断请求5秒后会进入半打开状态,放下一个请求进来重试，如果该请求成功就关闭熔断器，否则继续等待一个熔断时间窗口
    private final int circuitBreakerSleepWindowInMilliseconds = 5;
    //是否启用熔断器,默认true. 启动
    private final boolean circuitBreakerEnabled = true;
    //默认:50%。当出错率超过50%后熔断器启动
    private final int circuitBreakerErrorThresholdPercentage = 50;
    //是否强制开启熔断器阻断所有请求,默认:false,不开启。置为true时，所有请求都将被拒绝，直接到fallback
    private final boolean circuitBreakerForceOpen = false;
    //是否允许熔断器忽略错误,默认false, 不开启
    private final boolean circuitBreakerForceClosed = false;
    /* --------------信号量相关------------------*/
   //使用信号量隔离时，命令调用最大的并发数,默认:10
    private final int executionIsolationSemaphoreMaxConcurrentRequests = 10;
    //使用信号量隔离时，命令fallback(降级)调用最大的并发数,默认:10
    private final int fallbackIsolationSemaphoreMaxConcurrentRequests = 10;
    /* --------------其他------------------*/
    //使用命令调用隔离方式,默认:采用线程隔离,ExecutionIsolationStrategy.THREAD
    private final String executionIsolationStrategy = "THREAD";
    //使用线程隔离时，调用超时时间，默认:1秒
    private final int executionIsolationThreadTimeoutInMilliseconds = 1;
    //线程池的key,用于决定命令在哪个线程池执行
    private final String executionIsolationThreadPoolKeyOverride ="";
    //是否开启fallback降级策略 默认:true
    private final boolean fallbackEnabled = true;
    // 使用线程隔离时，是否对命令执行超时的线程调用中断（Thread.interrupt()）操作.默认:true
    private final boolean executionIsolationThreadInterruptOnTimeout = true;
    // 是否开启请求日志,默认:true
    private final boolean requestLogEnabled = true;
    //是否开启请求缓存,默认:true
    //private final boolean requestCacheEnabled = true; // Whether request caching is enabled.

    //-----------------------------
    //HystrixCollapserProperties
    //-----------------------------
    //请求合并是允许的最大请求数,默认: Integer.MAX_VALUE
    private final int maxRequestsInBatch = Integer.MAX_VALUE;
    //批处理过程中每个命令延迟的时间,默认:10毫秒
    private final int timerDelayInMilliseconds = 10;
    //批处理过程中是否开启请求缓存,默认:开启
    private final boolean requestCacheEnabled =true;


    //-----------------------------
    //HystrixThreadPoolProperties
    //-----------------------------
    /* 配置线程池大小,默认值10个. 建议值:请求高峰时99.5%的平均响应时间 + 向上预留一些即可 */
    private final int corePoolSize = 10;
    /* 配置线程值等待队列长度,默认值:-1 建议值:-1表示不等待直接拒绝,测试表明线程池使用直接决绝策略+ 合适大小的非回缩线程池效率最高.所以不建议修改此值。 当使用非回缩线程池时，queueSizeRejectionThreshold,keepAliveTimeMinutes 参数无效 */
    private final int maxQueueSize = -1;

}
