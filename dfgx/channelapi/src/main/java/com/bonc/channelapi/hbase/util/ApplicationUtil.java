package com.bonc.channelapi.hbase.util;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * spring动态生成bean
 *
 * @author caiqiang
 * @version 2016年8月4日
 * @see ApplicationUtil
 * @since
 */
@Component
public class ApplicationUtil implements ApplicationContextAware {
    /**
     * 应用上下文，可获得Spring中定义的Bean实例
     */
    private static ApplicationContext APPLICATIONCONTEXT;

    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        ApplicationUtil.APPLICATIONCONTEXT = applicationContext;
    }

    /**
     * Description: 根据bean name生成bean
     * 
     * @param name
     * @return Object
     * @see
     */
    public static Object getBean(String name) {
        return APPLICATIONCONTEXT.getBean(name);
    }

    /**
     * Description: 根据class生成bean
     * 
     * @param name
     * @return Object
     * @see
     */
    public static <T> T getBean(Class<T> clazz) {
        return APPLICATIONCONTEXT.getBean(clazz);
    }
}
