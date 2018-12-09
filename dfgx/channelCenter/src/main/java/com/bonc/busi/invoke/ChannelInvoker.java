package com.bonc.busi.invoke;

/**
 * 渠道分发调用接口  
 * 使用组装后的数据 完成对应渠道的调用
 * @author sky
 *
 */
public interface ChannelInvoker {
   /**
    * 调用渠道
 * @param object 
    * @return  渠道返回结果
 * @throws Exception 
    */
   java.lang.String invoke(Object object) throws Exception;
   /**
    * 判断是否为支持的渠道
    * @return
    */
   Boolean supports(String channelId);

}