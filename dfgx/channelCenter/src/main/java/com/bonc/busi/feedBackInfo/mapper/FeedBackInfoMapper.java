
package com.bonc.busi.feedBackInfo.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * @ClassName: FeedBackInfoMapper
 * @Description: FeedBackInfoMapper
 * @author: LiJinfeng
 * @date: 2016年12月11日 下午5:44:29
 */
public interface FeedBackInfoMapper {
	
	
	/**
	 * @Title: findYJQDCountByProvIdAndAccountTime
	 * @Description: 查询一级渠道单个渠道回执数据总量
	 * @return: Integer
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<Integer> findYJQDCountByProvIdAndAccountTime(@Param("request")HashMap<String, Object> request);
	
	/**
	 * @Title: findSTFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询手厅回执数据
	 * @return: List<HashMap<String,Object>>
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String,Object>> findSTFeedBackByProvIdAndAccountTime
		(@Param("request")HashMap<String, Object> request,@Param("idList")List<Integer> idList);
	
	/**
	 * @Title: findWTFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询网厅回执数据
	 * @return: List<HashMap<String,Object>>
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String,Object>> findWTFeedBackByProvIdAndAccountTime
		(@Param("request")HashMap<String, Object> request,@Param("idList")List<Integer> idList);
	
	/**
	 * @Title: findWSCFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询沃视窗回执数据
	 * @return: List<HashMap<String,Object>>
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String,Object>> findWSCFeedBackByProvIdAndAccountTime
		(@Param("request")HashMap<String, Object> request,@Param("idList")List<Integer> idList);
	
	/**
	 * @Title: updateYJQDBack
	 * @Description: 更新一级渠道回执状态
	 * @return: Integer
	 * @param yjqdFeedBack
	 * @param request
	 * @return
	 * @throws: 
	 */
	public Integer updateYJQDBack(@Param("resultIdList")List<Integer> resultIdList, 
			@Param("request")HashMap<String, Object> request);
	
	
	/**
	 * @Title: findWXCountByTenantIdAndTouchTime
	 * @Description: 获得微信回执数据总量
	 * @return: Integer
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<Integer> findWXCountByTenantIdAndTouchTime
			(@Param("request")HashMap<String, Object> request);

	
	/**
	 * @Title: findWXFeedBackByTenantIdAndTouchTime
	 * @Description: 分页查询微信回执数据
	 * @return: List<HashMap<String,Object>>
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String,Object>> findWXFeedBackByTenantIdAndTouchTime
			(@Param("request")HashMap<String, Object> request,@Param("idList")List<Integer> idList);
	
	/**
	 * @Title: updateWXBack
	 * @Description: 更新微信渠道回执状态
	 * @return: Integer
	 * @param yjqdFeedBack
	 * @param request
	 * @return
	 * @throws: 
	 */
	public Integer updateWXBack(@Param("resultIdList")List<Integer> resultIdList, 
			@Param("request")HashMap<String, Object> request);
	
	
	/**
	 * @Title: findSTCountByProvIdAndAccountTime
	 * @Description: 查询手厅回执数据总量
	 * @return: Integer
	 * @param request
	 * @return
	 * @throws: 
	 */
	/*public Integer findSTCountByProvIdAndAccountTime
	    (@Param("request")HashMap<String, Object> request);*/
	
	/**
	 * @Title: findYJQDOtherInfo
	 * @Description: 关联一级渠道其他必要信息
	 * @return: HashMap<String,Object>
	 * @param yjqdFeedBackInfo
	 * @return
	 * @throws: 
	 */
	/*public HashMap<String,Object> findYJQDOtherInfo(@Param("yjqdFeedBackInfo")HashMap<String, Object> yjqdFeedBackInfo,
			@Param("request")HashMap<String, Object> request);*/
	
	/**
	 * @Title: findWTCountByProvIdAndAccountTime
	 * @Description: 查询网厅回执数据总量
	 * @return: Integer
	 * @param request
	 * @return
	 * @throws: 
	 */
	/*public Integer findWTCountByProvIdAndAccountTime
	    (@Param("request")HashMap<String, Object> request);*/
	
	
	/**
	 * @Title: findWSCCountByProvIdAndAccountTime
	 * @Description: 获得沃视窗回执数据总量
	 * @return: Integer
	 * @param request
	 * @return
	 * @throws: 
	 */
	/*public Integer findWSCCountByProvIdAndAccountTime
	    (@Param("request")HashMap<String, Object> request);*/
	
	
	
	
	
	/**
	 * @Title: findWXOtherInfo
	 * @Description: 关联微信渠道其他必要信息
	 * @return: HashMap<String,Object>
	 * @param wxFeedBackInfo
	 * @return
	 * @throws: 
	 */
	/*public HashMap<String,Object> findWXOtherInfo(@Param("wxFeedBackInfo")HashMap<String,Object> wxFeedBackInfo,
			@Param("request")HashMap<String, Object> request);*/
	
	
}
