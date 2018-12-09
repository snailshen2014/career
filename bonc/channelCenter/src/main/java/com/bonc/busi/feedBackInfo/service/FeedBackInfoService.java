package com.bonc.busi.feedBackInfo.service;

import java.util.HashMap;
import java.util.List;

import com.bonc.busi.entity.PageBean;


/**
 * @ClassName: FeedBackInfoService
 * @Description: 回执处理服务接口
 * @author: LiJinfeng
 * @date: 2016年12月11日 下午5:32:33
 */
public interface FeedBackInfoService {
	
	/**
	 * @Title: getConfig
	 * @Description: 设置所需常量
	 * @return: HashMap<String,Object>
	 * @param tenantId
	 * @return
	 * @throws: 
	 */
	public HashMap<String, Object> getConfig(String tenantId,String channelId);
	
	/**
     * @Title: getYJQDPageBeanByProvIdAndAccountTime
     * @Description: 获得一级渠道各渠道回执数据总量
     * @return: PageBean
     * @param request
     * @return
     * @throws: 
     */
    public PageBean getYJQDPageBeanByProvIdAndAccountTime(HashMap<String, Object> request);
    
    /**
	 * @Title: findSTFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询手厅回执数据
	 * @return: List<HashMap<String,Object>>
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String, Object>> findSTFeedBackByProvIdAndAccountTime(HashMap<String, Object> request,
			List<Integer> subIdList);
	
	/**
	 * @Title: findWTFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询网厅回执数据
	 * @return: List<HashMap<String,Object>>
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String, Object>> findWTFeedBackByProvIdAndAccountTime(HashMap<String, Object> request,
			List<Integer> subIdList);
	
	/**
	 * @Title: findWSCFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询沃视窗回执数据
	 * @return: List<HashMap<String,Object>>
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String, Object>> findWSCFeedBackByProvIdAndAccountTime(HashMap<String, Object> request,
			List<Integer> subIdList);
	
    /**
     * @Title: yjqdFeedBackFilter
     * @Description: 一级渠道回执数据过滤
     * @return: HashMap<String,Object>
     * @param yjqdtFeedBackInfo
     * @param request
     * @return
     * @throws: 
     */
    public HashMap<String, Object> yjqdFeedBackFilter(HashMap<String, Object> yjqdtFeedBackInfo, 
			HashMap<String, Object> request);
    
    /**
     * @Title: yjqdTranslator
     * @Description: 一级渠道字段名称转换
     * @return: void
     * @param yjqdFeedBack
     * @param request
     * @throws: 
     */
    public void yjqdTranslator(HashMap<String, Object> yjqdtFeedBackInfo, HashMap<String, Object> request);
    
    /**
     * @Title: updateYJQDBack
     * @Description: 更新一级渠道回执状态 
     * @return: Boolean
     * @param yjqdFeedBack
     * @param request
     * @return
     * @throws: 
     */
    public Boolean updateYJQDBack(List<Integer> resultIdList, HashMap<String, Object> request);
    
    /**
     * @Title: sendFeedBackList
     * @Description: 发送回执数据
     * @return: void
     * @param resultList
     * @param request
     * @throws: 
     */
    public void sendFeedBackList(List<HashMap<String, Object>> resultList,HashMap<String, Object> request); 
    
    /**
     * @Title: getWXPageBeanByProvIdAndAccountTime
     * @Description: 获得微信回执数据总量
     * @return: PageBean
     * @param request
     * @return
     * @throws: 
     */
    public PageBean getWXPageBeanByProvIdAndAccountTime(HashMap<String, Object> request);
	
	/**
	 * @Title: findWXFeedBackByProvIdAndAccountTime
	 * @Description: 分页查询微信回执数据
	 * @return: List<HashMap<String,Object>>
	 * @param request
	 * @return
	 * @throws: 
	 */
	public List<HashMap<String, Object>> findWXFeedBackByProvIdAndAccountTime(HashMap<String, Object> request);
    
    /**
     * @Title: wxFeedBackFilter
     * @Description: 微信回执数据过滤
     * @return: HashMap<String,Object>
     * @param wxFeedBackInfo
     * @param request
     * @return
     * @throws: 
     */
    public HashMap<String, Object> wxFeedBackFilter(HashMap<String, Object> wxFeedBackInfo, 
			HashMap<String, Object> request);
    
    /**
     * @Title: wxTranslator
     * @Description: 微信渠道字段名称转换
     * @return: void
     * @param wxFeedBack
     * @param request
     * @throws: 
     */
    public void wxTranslator(HashMap<String, Object> wxFeedBackInfo, HashMap<String, Object> request);
    
    
    /**
     * @Title: updateWXBack
     * @Description: 更新微信回执状态
     * @return: Boolean
     * @param wxFeedBack
     * @param request
     * @return
     * @throws: 
     */
    public Boolean updateWXBack(List<Integer> resultIdList, HashMap<String, Object> request);
    

    
    
    
    /**
     * @Title: getSTPageBeanByProvIdAndAccountTime
     * @Description: 获得手厅回执数据总量
     * @return: PageBean
     * @param request
     * @return
     * @throws: 
     */
    /*public PageBean getSTPageBeanByProvIdAndAccountTime(HashMap<String, Object> request);*/
	
	
	
    /**
     * @Title: getWTPageBeanByProvIdAndAccountTime
     * @Description: 获得网厅回执数据总量
     * @return: PageBean
     * @param request
     * @return
     * @throws: 
     */
    /*public PageBean getWTPageBeanByProvIdAndAccountTime(HashMap<String, Object> request);*/
	
	
	
    /**
     * @Title: getWSCPageBeanByProvIdAndAccountTime
     * @Description: 获得沃视窗回执数据总量
     * @return: PageBean
     * @param request
     * @return
     * @throws: 
     */
    /*public PageBean getWSCPageBeanByProvIdAndAccountTime(HashMap<String, Object> request);*/
	
}
