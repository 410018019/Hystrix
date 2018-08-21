package com.example.demo.service;

import com.example.demo.entity.User;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description
 * <p>服务降级
 * </p>
 * DATE 2018/8/15.
 *
 * @author wuhao.
 */
@Component
public class UserClientServiceFallbackFactory implements  FallbackFactory<IUserClientService> {

    @Override
    public IUserClientService create(Throwable cause) {
        return new IUserClientService() {
            @Override
            public User get(long id) {
                User vo = new User("abc","123");
                return vo;
            }

            @Override
            public List<User> list() {
                return null;
            }

            @Override
            public boolean add(User user) {
                return false;
            }
        };
    }

}