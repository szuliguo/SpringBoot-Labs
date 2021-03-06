package cn.iocoder.springboot.lab57.hystrixdemo.config;

import cn.iocoder.springboot.lab57.hystrixdemo.strategy.MyHystrixConcurrencyStrategy;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

@Configuration
@EnableAspectJAutoProxy // 开启 AOP 代理的支持
public class HystrixConfig {

    @Bean
    public HystrixCommandAspect hystrixCommandAspect() {
        return new HystrixCommandAspect();
    }

    @PostConstruct
    public void init() {
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new MyHystrixConcurrencyStrategy());
    }

}
