package com.bonc.busi.sys.service;

import com.bonc.busi.sys.entity.SysLog;
import com.bonc.common.base.BDIJsonResult;
import com.bonc.common.base.JsonResult;

import java.util.List;

public interface SysFunction {
	/*
	 * 单实例控制
	 */
	public		boolean			singleInstaceControl(String  dbcheckFlag,String dbstartupFlag,String  colName);
	/*
	 * 纪录系统日志
	 */
	public		boolean			saveSysLog(final SysLog log);
	/*
	 * 解锁系统
	 */
	public		JsonResult			unlockSystem(String cltIP);
	/*
	 * 启动同步资料
	 */
	public		JsonResult			StartUserLabelAsyn(String TENANT_ID,char flag);
	/*
	 * 启动工单用户资料刷新
	 */
	public		JsonResult			StartOrderUserLabelUpdate(String TENANT_ID,String updateType);
	/*
	 * 查询当前帐期
	 */
	public		String			getCurMothDay(String tenant_id);
	/*
	 * 启动工单成功标准检查
	 */
	public		JsonResult			StartOrderSucessCheck(String TENANT_ID,char modeFalg);
	/*
	 * 异常信息入库
	 */
	public				void			saveExceptioneMessage(Exception e ,SysLog logIns);

	/*
	* 启动工单生成
	*/
	public 		BDIJsonResult 		startGenOrder(String TENANT_ID, char flag);
	/*
	* 启动电信工单生成
	*/
	public 		BDIJsonResult 		startTelecomGenOrder(String TENANT_ID, List<String> ActivityList , char flag);

	/*
	 * 启动黑白名单数据同步
	 */
	public      JsonResult          startBlackandWhiteAsyn(String TENANT_ID,char flag);


	/**
	 * 场景营销工单生成
	 * @param item
	 * @param c
	 */
	public void startSceneGenOrder(String item, char c);
	
	/*
	 * 启动受理成功
	 */
	public JsonResult startProductSaveSuccess(String TENANT_ID, char flag);
}
