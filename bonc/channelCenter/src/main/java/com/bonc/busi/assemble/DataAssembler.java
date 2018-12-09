package com.bonc.busi.assemble;

/**
 * 组装数据接口  
 * 对解析后的数据安装渠道需要的格式组装
 * @author sky
 *
 */
public interface DataAssembler {
  /**
   * 组装数据
 * @param object 
 * @throws Exception 
   */
   Object assembleData(Object object) throws Exception;
   /**
    * 判断是否为支持的渠道
    * @return
    */
   Boolean supports(String channelId);
}