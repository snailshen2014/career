package com.bonc.busi.service.func;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.service.SysFunction;
import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SyslogDao;
import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.service.impl.ServiceControlImpl;
import com.bonc.busi.service.mapper.ProductSaveMapper;
import com.bonc.busi.task.base.SpringUtil;


public class ProductSaveForSuccessThread extends Thread{
	private final static Logger log = LoggerFactory.getLogger(OrderAfterCheckThread.class);
	
	private SysFunction SysFunctionIns = SpringUtil.getBean(SysFunction.class);
	
	private Map<String, Object> Para = null;
	
	public ProductSaveForSuccessThread(){
		
	}
	
	public ProductSaveForSuccessThread(Map<String, Object> para){
		this.Para = para;
	}
	
	@Override
	public void run(){
		SysLog			SysLogIns = new SysLog();
		SysLogIns.setAPP_NAME("ORDERTASK-SCHEDULED2-"+ProductSaveForSuccessThread.class+"-run");		
		String TenantId = (String) Para.get("TENANT_ID");
		log.info("tenantid={}", TenantId);
		SysLogIns.setTENANT_ID(TenantId);
		// ---  加锁检查标识 ---
		String		curRunFlag = SyscommoncfgDao.query("ASYNPRODUCTSAVE.RUN.FLAG."+TenantId);
		if (curRunFlag.equals("TRUE")) {
			log.info("当前有受理成功在运行");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("当前有受理成功在运行");
			SyslogDao.insert(SysLogIns);
//			return;
		}
		try {
			// --- 更新运行标识 ---
			SyscommoncfgDao.update("ASYNPRODUCTSAVE.RUN.FLAG." + TenantId, "TRUE");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("受理成功开始运行");
			SyslogDao.insert(SysLogIns);
			
			// --- 定义4个线程 ---
			int iThreadNum = 4;
			String strTmp = SyscommoncfgDao.query("ORDERSUCESSCHECK.THREADSNUM");
			if (strTmp != null) {
				iThreadNum = Integer.parseInt(strTmp);
				if (iThreadNum < 4)
					iThreadNum = 4;
			}

			// --- 调用受理成功 ---
			ProductSaveForSuccess ProductSaveForSuccessIns = new ProductSaveForSuccess();
			ProductSaveForSuccessIns.setTenantId(TenantId);	
			ParallelManageThread ParallelManageIns = new ParallelManageThread(ProductSaveForSuccessIns, iThreadNum);
			ParallelManageIns.execute();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setBUSI_ITEM_1("受理成功出错");
			SysFunctionIns.saveExceptioneMessage(e, SysLogIns);
		}finally {
			SyscommoncfgDao.update("ASYNPRODUCTSAVE.RUN.FLAG." + TenantId, "FALSE");
			SysLogIns.setLOG_TIME(new Date());
			SysLogIns.setLOG_MESSAGE("受理成功结束."+TenantId);
			SyslogDao.insert(SysLogIns);
		}
		
	}
	
}
