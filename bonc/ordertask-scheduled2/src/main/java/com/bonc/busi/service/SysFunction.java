package com.bonc.busi.service;

import com.bonc.busi.service.entity.SysLog;

public interface SysFunction {
	
	/*
	 * 纪录系统日志
	 */
	public		boolean			saveSysLog(final SysLog log);
	/*
	 * 加锁成功检查标识
	 */
	public		int			lockOderSucessFlag(String tenant_id);
	/*
	 * 解锁成功检查标识
	 */
	public		int			unlockOderSucessFlag(String tenant_id);
	/*
	 * 查询当前帐期
	 */
	public		String			getCurMothDay(String tenant_id);
	/*
	 * 异常信息入库
	 */
	public				void			saveExceptioneMessage(Exception e ,SysLog logIns);

	

}
