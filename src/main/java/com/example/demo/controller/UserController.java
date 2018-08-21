package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.IUserClientService;
import com.example.demo.service.UserClientServiceFallbackFactory;
import com.example.demo.service.UserCommand;
import com.example.demo.service.UserService;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Description
 * <p>
 * </p>
 * DATE 2018/8/14.
 *
 * @author wuhao.
 */
@RestController
@RequestMapping("user")
public class UserController {

    private final Logger logger =  LoggerFactory.getLogger(UserController.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    IUserClientService iUserClientService;
    /**
     * 模拟异常使用Hystrix断路器
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "fallback")
    @GetMapping("/getUserEx/{id}")
    public String getUserEx(@PathVariable("id") Integer id){
        String returnJson = StringUtils.EMPTY;
        try {
            int i = 1/0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnJson;
    }

    private String fallback(Integer id){
        logger.info("ExceptionError:"+id);
        return "ExceptionError:"+id;
    }

    /**
     * 模拟超时使用Hystrix断路器
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "timeOutFallback")
    @GetMapping("/getUserTimeOut/{id}")
    public String getUserTimeOut(@PathVariable("id") Long id) throws InterruptedException {
        Thread.sleep(5000);
        return "success";
    }

    private String timeOutFallback(Long id){
        logger.info("timeOutFallbackError:"+id.toString());
        return "timeOutFallbackError:"+id.toString();
    }

    /**
     * 自定义Hystrix命令，同步调用方式
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/getUserHystrix/{id}")
    public User getUserHystrix(@PathVariable("id") Integer id) throws ExecutionException, InterruptedException {
        HystrixRequestContext.initializeContext();//初始化请求上下文
        //自定义组名
        com.netflix.hystrix.HystrixCommand.Setter setter = com.netflix.hystrix.HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserGroup"));
        UserCommand userCommand = new UserCommand(setter,restTemplate,id);
        //同步调用
        User user = userCommand.execute();
        return user;
    }

    /**
     * 自定义Hystrix命令，异步调用方式
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/getUserSyncHystrix/{id}")
    public User getUserSyncHystrix(@PathVariable("id") Integer id) throws ExecutionException, InterruptedException {
        HystrixRequestContext.initializeContext();//初始化请求上下文
        //自定义组名
        com.netflix.hystrix.HystrixCommand.Setter setter = com.netflix.hystrix.HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserGroup"));
        UserCommand userCommand = new UserCommand(setter,restTemplate,id);
        //异步请求
        Future<User> queue = userCommand.queue();
        User user = queue.get();
        return user;
    }

    @GetMapping("/oneCommand/{id}")
    public User oneCommand(@PathVariable("id") Integer id) throws ExecutionException, InterruptedException {
        //初始化，不初始化会报错
        HystrixRequestContext.initializeContext();
        UserCommand u1 = new UserCommand(restTemplate,id);
        User user1 = u1.execute();
        System.out.println("第一次请求"+user1);
        return user1;
    }

    @GetMapping("/moreCommand/{id}")
    public User moreCommand(@PathVariable("id") Integer id) throws ExecutionException, InterruptedException {
        //初始化，不初始化会报错
        HystrixRequestContext.initializeContext();
        UserCommand u1 = new UserCommand(restTemplate,id);
        UserCommand u2 = new UserCommand(restTemplate,id);
        UserCommand u3 = new UserCommand(restTemplate,id);
        UserCommand u4 = new UserCommand(restTemplate,id);
        User user1 = u1.execute();
        System.out.println("第一次请求"+user1);
        User user2 = u2.execute();
        System.out.println("第二次请求"+user2);
        User user3 = u3.execute();
        System.out.println("第三次请求"+user3);
        User user4 = u4.execute();
        System.out.println("第四次请求"+user4);
        return user1;
    }

    @GetMapping("/cache/{id}")
    public User findUserCache(@PathVariable("id") Integer id){
        HystrixRequestContext.initializeContext();
        User user1  = userService.getUserById(id);
        System.out.println("第一次请求"+user1);
        User user2 = userService.getUserById(id);
        System.out.println("第二次请求"+user2);
        User user3 = userService.getUserById(id);
        System.out.println("第三次请求"+user3);
        User user4 =userService.getUserById(id);
        System.out.println("第四次请求"+user4);
        return userService.getUserById(id);
    }

    @RequestMapping(value = "/getuserinfo", method = RequestMethod.GET)
    public User getuserinfo() {
        return iUserClientService.get(1l);
    }

    @RequestMapping(value = "/getuserinfostr", method = RequestMethod.GET)
    public boolean getuserinfostr() {
        return iUserClientService.add(new User("wh","wh"));
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public List<User> info() {
        return iUserClientService.list();
    }

    @RequestMapping(value = "/paramConfig/{id}", method = RequestMethod.GET)
    public String paramConfig(@PathVariable("id") String id) {
        return userService.paramConfig(id);
    }
}
