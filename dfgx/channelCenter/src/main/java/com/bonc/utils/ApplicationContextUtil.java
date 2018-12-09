/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: ApplicationContextProvider.java
 * @Prject: channelCenter
 * @Package: com.bonc.utils
 * @Description: TODO
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月3日 下午4:39:06
 * @version: V1.0  
 */

package com.bonc.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ApplicationContextProvider
 * @Description: TODO
 * @author: LiJinfeng
 * @date: 2016年12月3日 下午4:39:06
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware{
	
	 private static ApplicationContext context;

     private ApplicationContextUtil(){}

     @Override
     public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
         context = applicationContext;
     }

     public static <T> T getBean(String name,Class<T> clazz){
         return context.getBean(name, clazz);
     }


}
