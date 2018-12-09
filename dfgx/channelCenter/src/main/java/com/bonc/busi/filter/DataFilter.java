package com.bonc.busi.filter;

/**
 * 数据过滤 接口     每个渠道的数据进行再次过滤
 * 
 * @author sky
 *
 */
public interface DataFilter {
   /**
    * 过滤数据
    * @param object 
    * @throws Exception
    */
   Object filterData(Object object) throws Exception;
   /**
    * 判断是否为支持的渠道
    * @return
    */
   Boolean supports(String channelId);

}