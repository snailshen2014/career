package com.bonc.busi.resolve;
/**
 * 数据格式校验接口
 * @author sky
 *
 */
public interface DataValidator {
   /**
    * 数据格式校验
    * @return
    */
	boolean fomatValidate(String src);
	/**
	 * 业务性校验
	 * @return
	 */
	boolean busiValidate(Object src);
}
