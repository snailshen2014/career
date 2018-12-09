package com.bonc.busi.task.base;

import java.util.Date;
import java.util.Map;

import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.h2.util.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.common.thread.ThreadBaseFunction;
import com.bonc.xcloud.xserver.SessionService.Processor.login;

@Service("UpdateUserId")
public class UpdateUserId extends ThreadBaseFunction {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private final static Logger log = LoggerFactory.getLogger(UpdateUserId.class);

	@Autowired
	private BusiTools UpdateDataIns;
	@Autowired
	private BaseMapper TaskBaseMapperDao;

	@SuppressWarnings("unchecked")
	@Override
	public int handleData(Object data) {
		// --- 从全局类中得到当前租户变量 ---
		Map<String, Object> mapTenantInfo = (Map<String, Object>) data;
		log.info((String) mapTenantInfo.get("TENANT_ID"));

		String TenantId = (String) mapTenantInfo.get("TENANT_ID");

		// --- 首先判断是否有进程正在处理 ---
		String strRunFlag = UpdateDataIns.getValueFromGlobal("UPDATEUSERID.FLAG." + TenantId);// 获取当前进程状态

		if (strRunFlag != null) {
			if (strRunFlag.equals("true")) {
				log.warn("---租户：{}更新userid进程正在处理---",TenantId);
				return -1;
			}
		}
		long start = System.currentTimeMillis();

		// 获取上一次更新到的最大的RecId 配置
		String MaxRecId = UpdateDataIns.getValueFromGlobal("UPDATEUSERID.MAXRECID." + TenantId);
		long cfgMaxRecId = Long.parseLong(MaxRecId);

		strRunFlag = null; // --- 释放字符串 ---
		int SerialId = UpdateDataIns.getSequence("COMMONLOG.SERIAL_ID");
		PltCommonLog PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("02");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSPONSOR("UPDATEUSERID." +TenantId + "." + Thread.currentThread().getName());

		// 获取change表中最大的RecId
		String tableMaxRecId = TaskBaseMapperDao.changeTableMaxRecId(TenantId);
		long changeTableMaxRecId = Long.parseLong(tableMaxRecId);

		log.info("上一次更新到的最大REC_ID为：" + cfgMaxRecId + "  change表中读取的最大REC_ID为：" + changeTableMaxRecId);

		if (changeTableMaxRecId == cfgMaxRecId) {
			log.info("工单表的userid已经更新或数据还没有准备好");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("WARN");
			PltCommonLogIns.setBUSI_DESC("工单表的userid已经更新或数据还没有准备好,上一次更新到最大的RecId=" + cfgMaxRecId);// 存的是上一次更新到的REC_ID
			UpdateDataIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		// --- 设置更新工单表userid的进程正在处理 ---
		UpdateDataIns.setValueToGlobal("UPDATEUSERID.FLAG." + TenantId, "true");
		log.info("---租户：{}更新工单表的userid进程开始---",TenantId);

		// --- 更新工单表的userid ---
		String mysqlTableName = UpdateDataIns.getValueFromGlobal("UPDATEUSERID.TABLE");// 获取需要更新的工单表：客户经理和弹窗
		String mysqlUpdateSql = UpdateDataIns.getValueFromGlobal("UPDATEUSERID.UPDATE.SQL");// 获取更新sql语句
		String updateSql = mysqlUpdateSql.replaceAll("TTTTTENANT_ID",TenantId);

		String strTableItem[] = mysqlTableName.split(",");
		try {
			for (String tableName : strTableItem) {
				String realUpdateSqlData = updateSql.replaceAll("TTTTTABLENAME", tableName);
				StringBuilder sBuilder = new StringBuilder();
				sBuilder.append("SELECT COUNT(*) AS TOTAL,MIN(REC_ID) AS MINID,MAX(REC_ID) AS MAXID ");
				sBuilder.append(" FROM PLT_USER_CHANGE WHERE REC_ID >= ");
				sBuilder.append(cfgMaxRecId);
				sBuilder.append(" AND TENANT_ID = '");
				sBuilder.append(TenantId);
				sBuilder.append("'");
				Map<String, Object> mapResult = jdbcTemplate.queryForMap(sBuilder.toString());
				log.info("mapResult:" + mapResult);
				if (mapResult == null)
					return -1;
				long minRec = 0;
				long maxRec = 0;
				long total = 0;
				try {
					minRec = (long) mapResult.get("MINID");
					maxRec = (long) mapResult.get("MAXID");
					total = (long) mapResult.get("TOTAL");
					log.info("mapResult:" + mapResult);
				} catch (Exception e) { // --- 捕获空指针 ---
					System.out.println(e.getMessage());
					return -1;
				}
				log.info("sql = {},max recId = {},min recId = {},total = {}", sBuilder.toString(), maxRec, minRec,total);
				long lBeginRec = 0;
				long lEndRec = 0;
				long lRound = (maxRec - minRec) / 10000;
				for (int i = 0; i <= lRound; ++i) {
					lBeginRec = minRec + 10000 * i;
					if (i == lRound) {
						lEndRec = maxRec;
					} else {
						lEndRec = lBeginRec + 10000 - 1;
					}
					log.info("round = {}", i);
					// --- 开始执行 ---
					sBuilder.setLength(0);
					sBuilder.append(realUpdateSqlData);
					sBuilder.append(" AND u.REC_ID >= ");
					sBuilder.append(lBeginRec);
					sBuilder.append(" AND u.REC_ID <= ");
					sBuilder.append(lEndRec);
					log.info("更新工单sql = {}", sBuilder.toString());
					int result = jdbcTemplate.update(sBuilder.toString());
					log.info("更新数量:{}", result);
				}
				PltCommonLogIns.setSTART_TIME(new Date());
				PltCommonLogIns.setBUSI_CODE("WARN");
				PltCommonLogIns.setBUSI_DESC("用户的userid更新完毕,最大RecId更新到：" + tableMaxRecId);
				PltCommonLogIns.setBUSI_ITEM_1(realUpdateSqlData);
				UpdateDataIns.insertPltCommonLog(PltCommonLogIns);
				log.info("工单表的userid更新完毕，工单表名：" + tableName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("工单表的userid更新执行报错！！！");
		}
		// --- 更新最大RecId---
		UpdateDataIns.setValueToGlobal("UPDATEUSERID.MAXRECID." + TenantId,tableMaxRecId);
		// --- 更新进程标识 ---
		UpdateDataIns.setValueToGlobal("UPDATEUSERID.FLAG." + TenantId, "false");
		long end = System.currentTimeMillis();
		log.info("---租户：{}更新工单表的userid进程结束,time-consuming:",TenantId,(end - start) / 1000.0 + "s ------");
		return 0;
	}
}
