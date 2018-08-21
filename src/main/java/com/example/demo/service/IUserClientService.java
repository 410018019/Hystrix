package com.example.demo.service;

import com.example.demo.config.FeignClientConfig;
import com.example.demo.entity.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Description
 * <p>调用服务名为uc-demo-1服务的接口</p>
 * DATE 2018/8/15.
 * @author wuhao.
 */
@FeignClient(value = "uc-demo-1", configuration = FeignClientConfig.class, fallbackFactory = UserClientServiceFallbackFactory.class)
public interface IUserClientService {
    @RequestMapping(method = RequestMethod.GET, value = "/user/getUserEx/{id}")
    public User get(@PathVariable("id") long id);
    @RequestMapping(method = RequestMethod.GET, value = "/user/list")
    public List<User> list();
    @RequestMapping(method = RequestMethod.POST, value = "/user/add")
    public boolean add(User User);
}