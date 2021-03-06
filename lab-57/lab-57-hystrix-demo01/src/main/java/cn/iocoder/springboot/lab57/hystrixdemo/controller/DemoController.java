package cn.iocoder.springboot.lab57.hystrixdemo.controller;

import cn.iocoder.springboot.lab57.hystrixdemo.service.IUserservice;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 参考
 * https://www.iocoder.cn/Spring-Boot/Hystrix/
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;
    @Resource
    private IUserservice userservice;

    @GetMapping("/get_user")
    public String getUserInfo(@RequestParam("id") Integer id) {

        logger.info("start thread..." + Thread.currentThread().getId());
        threadLocal.set("a");
        String user = userservice.getUser(id);
        return user;
    }

    @HystrixCommand(fallbackMethod = "getUserFallback")
    public String getUser(Integer id) {
        try {
            logger.info("[getUser][准备调用 user-service 获取用户({})详情]", id);
            logger.info("end thread..." + Thread.currentThread().getId());
            logger.info("threadlocal...get.." + threadLocal.get());
            return restTemplate.getForEntity("http://127.0.0.1:18080/user/get?id=" + id, String.class).getBody();
        } finally {
            threadLocal.remove();
        }
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        logger.info("[getUserFallback][id({}) exception({})]", id, ExceptionUtils.getRootCauseMessage(throwable));
        return "mock:User:" + id;
    }

}
