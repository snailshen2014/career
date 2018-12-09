/**  
 * Copyright ©1997-2016 BONC Corporation, All Rights Reserved.
 * @Title: SendWXOrderByActivity.java
 * @Prject: channelCenter
 * @Package: com.bonc.task.sendWXOrder.impl
 * @Description: 按活动发送工单
 * @Company: BONC
 * @author: LiJinfeng  
 * @date: 2016年12月20日 上午11:14:24
 * @version: V1.0  
 */

package com.bonc.task.sendWXOrder.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.bonc.busi.entity.PageBean;
import com.bonc.busi.entity.PltCommonLog;
import com.bonc.busi.wxActivityInfo.po.WXActivityInfo;
import com.bonc.busi.wxOrderInfo.po.WXOrderInfo;
import com.bonc.busi.wxOrderInfo.service.WXOrderInfoService;
import com.bonc.utils.BusiTools;
import com.bonc.utils.ListUtil;

@Component("sendWXOrderByActivity")
public class SendWXOrderByActivity {
	
	@Autowired
	private WXOrderInfoService wxOrderInfoService;
	
	@Autowired
	private BusiTools busiTools;
	
	private String logMessage;
	
	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	private static Log log = LogFactory.getLog(SendWXOrderByActivity.class);
	
	public void orderProcess(WXActivityInfo wxActivityInfo,String channelId){
		
		//获取日志序列号
		int serialId = busiTools.getSequence("COMMONLOG.SERIAL_ID");
		//初始化通用日志对象
		PltCommonLog pltCommonLog = new PltCommonLog(serialId,wxActivityInfo.getTenantId(),"60",
				"SEND_WECHAT_ORDER."+Thread.currentThread().getName(),wxActivityInfo.getActivityId(),
				String.valueOf(wxActivityInfo.getRecId()));
		try {
			//插入通用日志表
			logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " start sending order of weChat!";
			busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString());
			//加载常量
			HashMap<String, Object> config = wxOrderInfoService.getConfig(channelId, wxActivityInfo.getTenantId());
			//判断wxActivityInfo的必输字段是否为空
			Boolean wxActivityInfoFieldIsEmpty = wxOrderInfoService.wxActivityInfoFieldIsEmpty(wxActivityInfo, config);
			if (!wxActivityInfoFieldIsEmpty) {
				//插入通用日志表
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " activity info dead field is null!";
				busiTools.insertCommonLog(logMessage, "warn", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), String.valueOf(config.get("wxActivityInfoFieldList")));
				//更改活动状态
				wxOrderInfoService.setActivityChannelStatus(wxActivityInfo, config);
				return;
			}
			//校验微信公众号信息
			Boolean webChatInfoFormat = wxOrderInfoService.webChatInfoFormat(wxActivityInfo, config);
			if (!webChatInfoFormat) {
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " get the info of the public error!";
				busiTools.insertCommonLog(logMessage, "warn", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), String.valueOf(config.get("weChatStatus")));
				return;
			}
			logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " get the info of the public success!";
			busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
					config.toString(), wxActivityInfo.getPublicId(), wxActivityInfo.getPublicCode(),
					wxActivityInfo.getTemplateId(), String.valueOf(config.get("weChatStatus")));
			//获取模板对应变量名、字段名
			Boolean findFieldList = wxOrderInfoService.findFieldList(wxActivityInfo, config);
			if (!findFieldList) {
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " get the info of template is error!";
				busiTools.insertCommonLog(logMessage, "warn", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), String.valueOf(config.get("keyList")),
						String.valueOf(config.get("fieldConstant")), String.valueOf(config.get("fieldOI")),
						String.valueOf(config.get("fieldUL")));
				return;
			}
			logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " get the info of template is success!";
			busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
					config.toString(), wxActivityInfo.getFieldList().toString(),
					wxActivityInfo.getFieldMap().toString(), String.valueOf(config.get("keyList")),
					String.valueOf(config.get("fieldConstant")), String.valueOf(config.get("fieldOI")),
					String.valueOf(config.get("fieldUL")));
			//判断活动对应的产品ID列表是否存在
			Boolean productIdListIsEmpty = wxOrderInfoService.productIdListIsEmpty(wxActivityInfo, config);
			if (productIdListIsEmpty) {
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " this activity have product list,"
						+ "begin to get the info of products!";
				busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), wxActivityInfo.getProductFlag().toString(),
						wxActivityInfo.getProductIdList().toString(), String.valueOf(config.get("activityNoProduct")),
						String.valueOf(config.get("activityYesProduct")));
				//从远程产品表中加载产品
				Boolean productIsExistInRomote = wxOrderInfoService.productIsExistInRomote(wxActivityInfo, config);
				if (!productIsExistInRomote) {
					logMessage = "ActivityId:" + wxActivityInfo.getActivityId()
							+ " get product info form remote error!";
					busiTools.insertCommonLog(logMessage, "warn", pltCommonLog, wxActivityInfo.toString(),
							config.toString(), wxActivityInfo.getProductIdList().toString(),
							String.valueOf(config.get("productUrl")), String.valueOf(config.get("productFieldJson")));
					return;
				}
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " get product info form remote success!";
				busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), wxActivityInfo.getProductIdList().toString(),
						wxActivityInfo.getProductInfoList().toString(), String.valueOf(config.get("productUrl")),
						String.valueOf(config.get("productFieldJson")));
				//校验产品字段
				Boolean productFieldIsEmpty = wxOrderInfoService.productFieldIsEmpty(wxActivityInfo, config);
				if (!productFieldIsEmpty) {
					logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " product info dead field is null!";
					busiTools.insertCommonLog(logMessage, "warn", pltCommonLog, wxActivityInfo.toString(),
							config.toString(), wxActivityInfo.getProductIdList().toString(),
							wxActivityInfo.getProductInfoList().toString(),
							String.valueOf(config.get("wxProductInfoFieldBssList")),
							String.valueOf(config.get("wxProductInfoFieldCbssList")));
					return;
				}

				//判断是否为同一类型的产品

				//更新本地产品码表
				Boolean updateWXProductInfo = wxOrderInfoService
						.updateWXProductInfo(wxActivityInfo.getProductInfoList(), config);
				if (!updateWXProductInfo) {
					logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " update productInfo error!";
					busiTools.insertCommonLog(logMessage, "warn", pltCommonLog, wxActivityInfo.toString(),
							config.toString(), wxActivityInfo.getProductIdList().toString(),
							wxActivityInfo.getProductInfoList().toString());
					return;
				}
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " update productInfo success!";
				busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), wxActivityInfo.getProductIdList().toString(),
						wxActivityInfo.getProductInfoList().toString());

				//设置产品ID
				Boolean setProductIds = wxOrderInfoService.setProductIds(wxActivityInfo, config);
				if (!setProductIds) {
					logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " set orderProductId error!";
					busiTools.insertCommonLog(logMessage, "error", pltCommonLog, wxActivityInfo.toString(),
							config.toString(), wxActivityInfo.getProductIdList().toString(),
							wxActivityInfo.getProductInfoList().toString());
					return;
				}

			} else {
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " this activity don't have products!";
				busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), wxActivityInfo.getProductFlag().toString(),
						String.valueOf(config.get("activityNoProduct")),
						String.valueOf(config.get("activityYesProduct")));
			}
			
			//应微信团队要求，产品更新之后线程停止几分钟
			log.info("thread start sleeping,wait the master-slave synchronization of mysql");
	        long sleepTime = Long.parseLong(busiTools.getGlobalValue("SLEEP_TIME"));
		    try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				logMessage = " ActivityId:" + wxActivityInfo.getActivityId()
						+ " thread occurred exception when sleeping!";
				busiTools.insertCommonLog(logMessage, "error", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), e.getMessage());
				e.printStackTrace();
				return;
			}
		    log.info("sleep end");
			//动态获取MySQL字段
			Boolean flag = false;
			try {
				flag = wxOrderInfoService.getTalkVarList(config);
			} catch (Exception e) {
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " get talk value occurred exception!";
				busiTools.insertCommonLog(logMessage, "error", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), String.valueOf(config.get("actHuaShu")), e.getMessage());
				return;
			}
			if (!flag) {
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " get talk value occurred error!";
				busiTools.insertCommonLog(logMessage, "warn", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), String.valueOf(config.get("actHuaShu")));
				return;
			}
			logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " get talk value success!";
			busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
					config.toString(), String.valueOf(config.get("actHuaShu")), config.get("mysqlFieldList").toString(),
					config.get("allTalkVarList").toString());
			//获得本次活动所有符合条件的微信工单的REC_ID
			List<Integer> ordereRecIdList = wxOrderInfoService.getOrdereRecIdList(wxActivityInfo, config);
			PageBean pageBean = (PageBean) config.get("pageBean");
			//存放发送总量
			int sendNum = 0;
			//存放本次符合条件的查询结果
			List<WXOrderInfo> resultList = new ArrayList<WXOrderInfo>();
			//判断微信工单数量是否为0
			if (pageBean.getTotal() < 1) {
				//记录日志
				logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " ordereRecIdList is empty!";
				busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
						config.toString(), String.valueOf(config.get("pageSize")));
				/*wxOrderInfoService.setActivityChannelStatus(wxActivityInfo,config);*/
				//更改活动状态
				wxOrderInfoService.setActivityChannelStatus(wxActivityInfo, config);
				return;
			}
			//开始分页下发微信工单
			logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " start to process weChat order,allNum = "
					+ ordereRecIdList.size() + "!";
			busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
					config.toString());
			//分页查询并下发微信工单	
			while (pageBean.getCurrentPage() <= pageBean.getTotalPage()) {

				//截取本次查询的orderId列表
				List<Integer> subOrdereRecIdList = ordereRecIdList.subList(pageBean.getStartPage(),
						pageBean.getEndPage());
				config.put("ordereRecIdList", subOrdereRecIdList);
				//获取当前用户有效分区标识
				Boolean currentPartitionFlag = wxOrderInfoService.getCurrentPartitionFlag(config);
				if (!currentPartitionFlag) {
					logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " page " + pageBean.getCurrentPage()
							+ " get partition flag error！";
					busiTools.insertCommonLog(logMessage, "warn", pltCommonLog, wxActivityInfo.toString(),
							config.toString());
				} else {

					//分页查询
					List<HashMap<String, Object>> wxOrderInfoMapList = wxOrderInfoService
							.findWXOrderInfoListByChannelId(config, wxActivityInfo);
					if (wxOrderInfoMapList == null) {
						String logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " page "
								+ pageBean.getCurrentPage() + " orderInfoList is null!";
						log.info(logMessage);
						/*wxOrderInfoService.insertLog(wxActivityInfo, logMessage);*/
						/*pageBean.setCurrentPage(pageBean.getCurrentPage()+1);*/
					} else {

						//将每页数据分为n个子集
						List<List<HashMap<String, Object>>> splitList = ListUtil.splitList(wxOrderInfoMapList,
								threadPoolTaskExecutor.getCorePoolSize());
						//用来存放n个子集的结果
						List<Future<List<WXOrderInfo>>> futureList = new ArrayList<Future<List<WXOrderInfo>>>();
						//起线程跑工单
						for (List<HashMap<String, Object>> subWXOrderInfoMapList : splitList) {
							if (subWXOrderInfoMapList.isEmpty()) {
								log.info("ActivityId:" + wxActivityInfo.getActivityId()
										+ " subOrderInfoList for thread is null!");
								continue;
							}
							/*log.info("wxOrderInfoMapList的子list大小："+splitList.size()+"线程池大小"
							 +threadPoolTaskExecutor.getActiveCount()+":"+threadPoolTaskExecutor.getPoolSize());*/
							Future<List<WXOrderInfo>> submit = threadPoolTaskExecutor.submit(
									new WXOrderRun(subWXOrderInfoMapList, wxOrderInfoService, config, wxActivityInfo));
							futureList.add(submit);

						}
						//加入结果集
						for (Future<List<WXOrderInfo>> future : futureList) {
							try {
								resultList.addAll((List<WXOrderInfo>) future.get());
							} catch (Exception e) {
								e.printStackTrace();
								String logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " page "
										+ pageBean.getCurrentPage() + " occurred exception in order thread";
								log.info(logMessage);
								/*wxOrderInfoService.insertLog(wxActivityInfo, logMessage);*/
							}
						}

					}

				}

				//判断是否有数据
				if (resultList.size() < 1) {
					String logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " page "
							+ pageBean.getCurrentPage() + "resultList is null!";
					log.info(logMessage);
					/*wxOrderInfoService.insertLog(wxActivityInfo, logMessage);*/
					pageBean.setCurrentPage(pageBean.getCurrentPage() + 1);
					continue;
				}
				if (resultList.size() > Integer.parseInt(String.valueOf(config.get("insertSize")))
						|| pageBean.getCurrentPage() == pageBean.getTotalPage()) {
					//批量插入微信工单信息	
					Boolean insertWXOrderInfo = wxOrderInfoService.insertWXOrderInfo(resultList, config);
					/*Boolean insertWXOrderInfo = true;*/
					if (!insertWXOrderInfo) {
						String logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " page "
								+ pageBean.getCurrentPage() + " occurred error when insert weChat order!";
						log.info(logMessage);
						/*wxOrderInfoService.insertLog(wxActivityInfo, logMessage);*/
						//微信工单统计
						wxOrderInfoService.countWXOrder(config, wxActivityInfo, ordereRecIdList.size(), pageBean,
								resultList.size(), false);
					}
					//微信工单统计
					else {
						wxOrderInfoService.countWXOrder(config, wxActivityInfo, ordereRecIdList.size(), pageBean,
								resultList.size(), true);
					}
					sendNum = sendNum + resultList.size();
					resultList.clear();
				}
				//进入下一页	
				pageBean.setCurrentPage(pageBean.getCurrentPage() + 1);
			}
			//本次活动工单发完
			wxOrderInfoService.setActivityChannelStatus(wxActivityInfo, config);
			logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " finish send weChat order！sendNum = "
					+ sendNum + "!";
			busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
					config.toString());
			sendNum = 0;
		} catch (Exception e) {
			logMessage = "ActivityId:" + wxActivityInfo.getActivityId() + " send weChat order occurred exception!";
			busiTools.insertCommonLog(logMessage, "info", pltCommonLog, wxActivityInfo.toString(),
					e.getMessage());
			e.printStackTrace();
		}
		return;
	}

}
