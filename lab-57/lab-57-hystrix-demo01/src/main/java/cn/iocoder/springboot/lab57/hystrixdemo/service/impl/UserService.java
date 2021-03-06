package cn.iocoder.springboot.lab57.hystrixdemo.service.impl;

import cn.iocoder.springboot.lab57.hystrixdemo.controller.DemoController;
import cn.iocoder.springboot.lab57.hystrixdemo.service.IUserservice;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Legal
 * @date 2021/3/6
 */

@Service
public class UserService implements IUserservice {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;


    @Override
    @HystrixCommand(fallbackMethod = "getUserFallback")
    public String getUser(Integer id) {
        try {
            logger.info("[getUser][准备调用 user-service 获取用户({})详情]", id);
            logger.info("threadlocal...get.." + DemoController.threadLocal.get());
            logger.info("end thread..." + Thread.currentThread().getId());
            return restTemplate.getForEntity("http://127.0.0.1:18080/user/get?id=" + id, String.class).getBody();
        } finally {
            logger.info("end....");
        }
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        logger.info("[getUserFallback][id({}) exception({})]", id, ExceptionUtils.getRootCauseMessage(throwable));
        return "mock:User:" + id;
    }
}
