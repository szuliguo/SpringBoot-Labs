package cn.iocoder.springboot.lab57.hystrixdemo.strategy;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

/**
 * @author Legal
 * @date 2021/3/6
 * 参考：
 * https://bbs.huaweicloud.com/blogs/163581
 */
public class MyHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Callable wrapCallable(Callable callable) {
        /**
         * 1 获取当前线程的threadlocalmap
         */
        Object currentThreadlocalMap = getCurrentThreadlocalMap();

        Callable finalCallable = new Callable<Object>() {
            // 2
            private Object callerThreadlocalMap = currentThreadlocalMap;
            // 3
            private Callable targetCallable = callable;

            @Override
            public Object call() throws Exception {
                /**
                 * 4 将工作线程的原有线程变量保存起来
                 */
                Object oldThreadlocalMapOfWorkThread = getCurrentThreadlocalMap();
                /**
                 *5 将本线程的线程变量，设置为caller的线程变量
                 */
                setCurrentThreadlocalMap(callerThreadlocalMap);

                try {
                    // 6
                    return targetCallable.call();
                } finally {
                    // 7
                    setCurrentThreadlocalMap(oldThreadlocalMapOfWorkThread);
                    logger.info("restore work thread's threadlocal");
                }

            }
        };

        return finalCallable;
    }

    private Object getCurrentThreadlocalMap() {
        Thread thread = Thread.currentThread();
        try {
            Field field = Thread.class.getDeclaredField("threadLocals");
            field.setAccessible(true);
            Object o = field.get(thread);
            return o;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("{}", e);
        }
        return null;
    }

    private void setCurrentThreadlocalMap(Object newThreadLocalMap) {
        Thread thread = Thread.currentThread();
        try {
            Field field = Thread.class.getDeclaredField("threadLocals");
            field.setAccessible(true);
            field.set(thread, newThreadLocalMap);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("{}", e);
        }
    }

}