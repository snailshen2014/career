package com.bonc.busi.task.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.activity.SuccessStandardTypePo;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.busi.task.base.OrderCheck;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.ActivitySucessInfo;
import com.bonc.busi.task.bo.OrderCheckInfo;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;

/**
 * 事后成功检查任务类
 * 
 * @author Administrator
 *
 */
public class OrderCheckSuccessTask implements Runnable {
	
	private final static Logger log = LoggerFactory.getLogger(OrderCheck.class);
	private JdbcTemplate JdbcTemplateIns = SpringUtil.getBean(JdbcTemplate.class);
	private BusiTools BusiToolsIns = SpringUtil.getBean(BusiTools.class);
	private BaseMapper BaseMapperIns = SpringUtil.getBean(BaseMapper.class);

	private PltCommonLog PltCommonLogIns = new PltCommonLog(); // --- 日志变量 ---
	private Date dateBegin = null; // --- 开始时间 ---
	private List<Map<String, Object>> listTenantInfo = null; // --- 有效租户信息 ---
	// --- 定义当前租户变量 ---
	private String curTenantId = null;
	private int SerialId = 0; // --- 日志序列号 ---
	// --- 定义当前帐期变量 ---
	private String curDateId = null;
	// --- 定义保存租户成功条件的变量 ---
	private Map<String, List<ActivitySucessInfo>> mapActivitySucessInfo = new HashMap<String, List<ActivitySucessInfo>>();
	// --- 定义活动对应的产品列表 ---
	private Map<String, List<String>> mapActivityProduct = new HashMap<String, List<String>>();
	// --- 定义是否需要调用查询有效租户和活动的变更 ---
	private boolean bNeedGetData = true;
	private int m_iTotalNum = 0;
	// --- 定义当前活动变量 ---
	private int curActivitySeqId = -1;
	// --- 定义当前工单变量 ---
	private long curOrderRecId = -1;

	private int batchCount = 10000;

	// --- 定义当前操作的是哪个表 ---
	private String m_strTableName = null;

	public String getM_strTableName() {
		return m_strTableName;
	}

	public void setM_strTableName(String m_strTableName) {
		this.m_strTableName = m_strTableName;
	}
	
	CountDownLatch cdl;
	
	public CountDownLatch getCdl() {
		return cdl;
	}

	public void setCdl(CountDownLatch cdl) {
		this.cdl = cdl;
	}

	@Override
	public void run() {
		try {
			if (begin() == 0) {
				process();
			} else {
				log.info("=========================条件不满足，不能跑成功检查");
			}
			end();
		} catch (Exception ex) {
			ex.printStackTrace();
			
		}finally{
			cdl.countDown();
		}	
	}

	public int begin() {
		// --- 纪录开始时间 ---
		dateBegin = new Date();
		// --- 提取有效租户信息 ---
		listTenantInfo = BusiToolsIns.getValidTenantInfo();
		if (listTenantInfo == null || listTenantInfo.size() == 0) { // ---
			// 无有效租户纪录
			// ---
			log.error("--- 无有效租户纪录---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，无有效租户纪录");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		// --- 设置当前租户编号 ---
		curTenantId = (String) listTenantInfo.get(0).get("TENANT_ID");
		// --- 得到序列号 ---
		SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
		// --- 纪录日志 ---
		PltCommonLogIns.setLOG_TYPE("01");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setSPONSOR("ORDERCHECK");
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("工单检查开始");
		PltCommonLogIns.setBUSI_ITEM_1(curTenantId);
		PltCommonLogIns.setBUSI_ITEM_3(m_strTableName);
		// PltCommonLogIns.setBUSI_ITEM_10("" + m_strTableName);
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		PltCommonLogIns.setBUSI_ITEM_10(null);
		// --- 得到最大帐期 ---
		curDateId = BaseMapperIns.getMaxDateId();
		if (curDateId == null) {
			log.warn("--- 取当前帐期失败,得到的是空值 ---");
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setBUSI_CODE("EXCCUTE-ERROR");
			PltCommonLogIns.setBUSI_DESC("调用方法begin()出错，curDateid为空");
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			return -1;
		}
		return 0;
	}

	public int end() {
		Date dateEnd = new Date();
		log.info("--- 工单成功检查结束 耗时:{},表:{}", dateEnd.getTime() - dateBegin.getTime(),m_strTableName);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setSPONSOR("ORDERCHECK");
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setBUSI_ITEM_1(curTenantId);
		PltCommonLogIns.setBUSI_DESC("工单检查结束");
		PltCommonLogIns.setDURATION((int) (dateEnd.getTime() - dateBegin.getTime()));
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		return 0;
	}

	public void process() {
		List<OrderCheckInfo> listOrderCheckInfo = null;
		log.info("事后成功检查开始 ，时间:" + new Date() + " 表:" +m_strTableName);
		if (bNeedGetData) {
			m_iTotalNum = 0;
			bNeedGetData = false;
			PltCommonLogIns.setSTART_TIME(new Date());
			PltCommonLogIns.setSPONSOR("ORDERCHECK");
			PltCommonLogIns.setBUSI_CODE("GET");
			PltCommonLogIns.setBUSI_DESC("新活动编号");
			PltCommonLogIns.setBUSI_ITEM_1(curTenantId);
			// PltCommonLogIns.setBUSI_ITEM_2(String.valueOf(curActivitySeqId));
			PltCommonLogIns.setBUSI_ITEM_5(curDateId);
			BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
			PltCommonLogIns.setBUSI_ITEM_1(null);
			PltCommonLogIns.setBUSI_ITEM_2(null);
			PltCommonLogIns.setBUSI_ITEM_5(null);
		}

		List<OrderCheckInfo> handleOrderCheckInfoList = new ArrayList<OrderCheckInfo>(); //批量处理的工单集合
		for (Map<String, Object> mapTenantInfo : listTenantInfo) {
			String TENANT_ID = (String) mapTenantInfo.get("TENANT_ID");
			// 每个租户下需要做成功检查的活动
			List<ActivitySucessInfo> listTmp = new ArrayList<ActivitySucessInfo>();
			if (m_strTableName.contains("PLT_ORDER_INFO_HIS")) {
				listTmp = BaseMapperIns.getOrderHisInfo(TENANT_ID);
			} else {
				listTmp = BaseMapperIns.getActivityForTenantId(TENANT_ID);
			}
			listTmp = BaseMapperIns.getActivityForTenantId(TENANT_ID);
			if (listTmp != null && listTmp.size() > 0) {
				mapActivitySucessInfo.put(TENANT_ID, listTmp);
				for (ActivitySucessInfo item : listTmp) {
					List<String> listTemp = BaseMapperIns.getProductListForActivity(item.getACTIVITY_SEQ_ID(),
							TENANT_ID);
					if (listTemp != null && listTemp.size() > 0) {
						mapActivityProduct.put(TENANT_ID + "-" + item.getACTIVITY_SEQ_ID(), listTemp);
					}
				}
				StringBuilder sb = new StringBuilder();
				StringBuilder sbb = new StringBuilder();
				for (ActivitySucessInfo activitySucessInfo : listTmp) {
					int activitySeqId = activitySucessInfo.getACTIVITY_SEQ_ID();
					listOrderCheckInfo = BaseMapperIns.getOrderListForActivityV3(TENANT_ID, activitySeqId,
							m_strTableName);
					if (listOrderCheckInfo != null && listOrderCheckInfo.size() > 0) {
						int count = listOrderCheckInfo.size();
						int minRecId = (int) listOrderCheckInfo.get(0).getOrderRedId();
						int maxRecId = (int) listOrderCheckInfo.get(count - 1).getOrderRedId();
						int round = (maxRecId - minRecId) / batchCount;
						int lBeginRec = 0;
						int lEndRec = 0;
						for (int i = 0; i <= round; ++i) { // 批量移动，每次最多移动batchCount条
							lBeginRec = minRecId + batchCount * i;
							if (i == round) {
								lEndRec = maxRecId;
							} else {
								lEndRec = lBeginRec + batchCount - 1;
							}
							handleOrderCheckInfoList = BaseMapperIns.getRequiredOrderListForSucess(TENANT_ID,
									activitySeqId, m_strTableName, lBeginRec, lEndRec);
							if(handleOrderCheckInfoList != null && handleOrderCheckInfoList.size()>0){
							String SucessTypeSql = getSqlForSucessType(TENANT_ID, activitySeqId,
									handleOrderCheckInfoList);
							if (SucessTypeSql != null) { // 拿到了成功检查的sql
								log.info("--- begin to handle data 时间:" + new Date());
								sb.setLength(0);
								sb.append(SucessTypeSql);
								log.info("--SuccessSql:{}--,表{},批次:{},lBeginRec:{}---lEndRec:{}",sb.toString(),m_strTableName,activitySeqId,lBeginRec,lEndRec);
								// --- 执行SQL ---
								List<Map<String, Object>> listUserId = (List<Map<String, Object>>) JdbcTemplateIns
										.queryForList(sb.toString());
								// 一条成功标准sql插入一条日志
								PltCommonLog logdb = new PltCommonLog();
								// int SerialId =
								// AsynDataIns.getSequence("COMMONLOG.SERIAL_ID");
								logdb.setLOG_TYPE("22");
								logdb.setSERIAL_ID(SerialId);
								logdb.setSTART_TIME(new Date());
								logdb.setSPONSOR("orderCheck");
								logdb.setBUSI_CODE("getSqlForSucessType");
								logdb.setBUSI_DESC("Execute successfully+SuccessSql");
								logdb.setBUSI_ITEM_2("" + activitySeqId);
								logdb.setBUSI_ITEM_3(m_strTableName);
								// logdb.setBUSI_ITEM_4(sb.toString());
								String tmp = sb.toString();
								if (tmp.length() > 1024) {
									String tmp1 = tmp.substring(0, 1024);
									logdb.setBUSI_ITEM_1(tmp1);
								} else {
									logdb.setBUSI_ITEM_1(tmp);
								}
								BusiToolsIns.insertPltCommonLog(logdb);

								if (listUserId == null || listUserId.size() == 0) {
									// log.info("无数据,sql:{}",sb.toString());
									log.info("表{},批次{},lBeginRec:{}---lEndRec{}之间没有满足成功检查条件的工单,返回",m_strTableName,activitySeqId,lBeginRec,lEndRec);
									continue; // --- 无数据 ---
								}

								sb.setLength(0);
								for (int j = 0; j < listUserId.size(); ++j) {
									if (j != 0)
										sb.append(",");
									sb.append("'");
									sb.append(listUserId.get(j).get("USER_ID"));
									sb.append("'");
								}

								sbb.setLength(0);
								sbb.append("UPDATE " + m_strTableName
										+ " SET  CHANNEL_STATUS = '3' ,LAST_UPDATE_TIME = now() ,AGREEMENT_EXPIRE_TIME = "
										+ curDateId + " ");
								sbb.append(" WHERE TENANT_ID='");
								sbb.append(TENANT_ID);
								sbb.append("'");
								sbb.append("  AND ACTIVITY_SEQ_ID =");
								sbb.append(activitySeqId);
								sbb.append("  AND USER_ID IN (");
								sbb.append(sb.toString());
								sbb.append(")");
								int result = JdbcTemplateIns.update(sbb.toString());
								log.info("表{},批次： {}, lBeginRec： {}, lEndRec： {} 之间的工单有成功检查的更新,更新了{}条",m_strTableName,activitySeqId,lBeginRec,lEndRec,result);
								log.info("--- end to handle data ,更新数量：{}  ,时间：{}", result, new Date());
							}
						}
                      }	
					}
				}
			}
		}
		log.info("============== 事后成功检查结束 ，时间:" + new Date() + " 表:" +m_strTableName);
	}

	private String getSqlForSucessType(String tenant_id, int activity_seq_id, List<OrderCheckInfo> listOrderCheckInfo) {
		// --log--
		PltCommonLog logdb = new PltCommonLog();
		logdb.setLOG_TYPE("01");
		logdb.setSERIAL_ID(SerialId);
		logdb.setSTART_TIME(new Date());
		logdb.setSPONSOR("orderCheck");
		logdb.setBUSI_ITEM_2("" + activity_seq_id);
		logdb.setBUSI_ITEM_3(m_strTableName);
		logdb.setBUSI_CODE("getSqlForSucessType");
		// --- 得到活动成功信息 ---
		ActivitySucessInfo ActivitySucessInfoIns = getActivitySucessInfo(tenant_id, activity_seq_id);
		if (ActivitySucessInfoIns == null) {
			logdb.setBUSI_DESC("该活动成功标准检查无成功标准ActivitySucessInfo");
			BusiToolsIns.insertPltCommonLog(logdb);
			return null;
		}

		if (com.bonc.busi.task.base.StringUtils.isNotNull(ActivitySucessInfoIns.getSucessType()) == false) {
			logdb.setBUSI_DESC("该活动成功标准检查无成功类型SucessType");
			BusiToolsIns.insertPltCommonLog(logdb);
			return null;
		}
		// if(ActivitySucessInfoIns.getSucessType() == null) return null;
		boolean bProduct = false; // --- 是否成功标准中有产品 ---
		StringBuilder sbProduct = new StringBuilder();
		SimpleDateFormat sdfYm = new SimpleDateFormat("yyyy-MM"); // 设置时间格式
		SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		String OrderCreateTime;
		if (ActivitySucessInfoIns.getLastOrderCreateTime() != null) {
			OrderCreateTime = sdfYmd.format(ActivitySucessInfoIns.getLastOrderCreateTime());
		} else {
			OrderCreateTime = sdfYm.format(new Date()) + "-01";
		}

		if (com.bonc.busi.task.base.StringUtils.isNotNull(ActivitySucessInfoIns.getMatchingType())) {
			if (ActivitySucessInfoIns.getMatchingType().equals("2")
					&& ActivitySucessInfoIns.getIsHaveRenewProduct().equals("0")) { // ---
																					// 精确匹配产品
																					// ---
				// --- 提取活动成功产品列表 ---
				List<String> listProductCode = mapActivityProduct
						.get(tenant_id + "-" + ActivitySucessInfoIns.getACTIVITY_SEQ_ID());
				bProduct = true;
				int i = 0;
				if (listProductCode != null && listProductCode.size() > 0) { // ---
																				// 得到SQL
																				// ---

					sbProduct.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE BONC_PRODUCT_ID IN (");
					for (i = 0; i < listProductCode.size(); ++i) {
						if (i > 0)
							sbProduct.append(",");
						sbProduct.append("'");
						sbProduct.append(listProductCode.get(i));
						sbProduct.append("'");
					}
					sbProduct.append(")");
					sbProduct.append(" AND ACCEPTED_DATE > '");
					sbProduct.append(OrderCreateTime);
					sbProduct.append("'  ");
				} else {
					log.info("精准匹配产品时，无产品编码");
					logdb.setBUSI_CODE("getSqlForSucessType,精准匹配产品时，无产品编码");
					BusiToolsIns.insertPltCommonLog(logdb);
					return null;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		String strAddSql = null;
		String strTmp = null;
		if (com.bonc.busi.task.base.StringUtils.isNotNull(ActivitySucessInfoIns.getSucessConSql())) {
			strAddSql = ActivitySucessInfoIns.getSucessConSql().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		if (com.bonc.busi.task.base.StringUtils.isNotNull(ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL())) {
			strTmp = ActivitySucessInfoIns.getSUCCESS_TYPE_CON_SQL().replaceAll("UNICOM_D_MB_DS_ALL_LABEL_INFO.", "");
		}
		String successType = ActivitySucessInfoIns.getSucessType();
		// --add--
		StringBuilder userIdForCheck = new StringBuilder();// --MySQL表中的USERID--
		for (int i = 0; i < listOrderCheckInfo.size(); ++i) {
			if (i > 0)
				userIdForCheck.append(",");
			userIdForCheck.append("'");
			userIdForCheck.append(listOrderCheckInfo.get(i).getUserId());
			userIdForCheck.append("'");
		}
		// --add end--
		// 精准营销时
		if (bProduct) {
			if (strTmp != null) {
				sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
				sbProduct.append("(" + strTmp + ")");
				sbProduct.append(" AND ");
				sbProduct.append(" DATE_ID = '");
				sbProduct.append(curDateId);
				sbProduct.append("'");
				sbProduct.append(" AND USER_ID IN (");
				sbProduct.append(userIdForCheck);
				sbProduct.append(")");
				if (strAddSql != null) {
					sbProduct.append(" AND " + "(" + strAddSql + ")");
				}
				sbProduct.append(" ) ");
			} else { // --- 成功标准类型中，无成功标准 如3,4,5
				if (strAddSql != null) {
					sbProduct.append(" AND USER_ID IN ( SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE ");
					sbProduct.append(" DATE_ID = '");
					sbProduct.append(curDateId);
					sbProduct.append("'");
					sbProduct.append(" AND USER_ID IN (");
					sbProduct.append(userIdForCheck);
					sbProduct.append(")");
					sbProduct.append(" AND " + "(" + strAddSql + ")");
					sbProduct.append(" ) ");
					sbProduct.append(" ) ");
				}
			}
			log.info("精准营销：");

		} else {
			// 无精准营销但有成功标准
			if (strTmp != null) {
				sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
				sb.append("(" + strTmp + ")");
				sb.append(" AND DATE_ID =  '");
				sb.append(curDateId);
				sb.append("'");
				sb.append(" AND USER_ID IN (");
				sb.append(userIdForCheck);
				sb.append(")");
				if (strAddSql != null) {
					sb.append(" AND " + "(" + strAddSql + ")");
				}

			} else {
				if (strAddSql != null) {// --- 无成功标准 ---
					if (successType.equals("3") || successType.equals("4") || successType.equals("5")) { // ---
																											// 成功标准类型在3,4,5之间时
						sb.append(" SELECT USER_ID FROM CLJY_ALL_ACCEPTED_LIST WHERE  PROD_TYPE ='");
						if (successType.equals("3")) {
							sb.append("03");
						}
						if (successType.equals("4")) {
							sb.append("02");
						}
						if (successType.equals("5")) {
							sb.append("01");
						}
						sb.append("' AND ACCEPTED_DATE > '");
						sb.append(OrderCreateTime);
						sb.append("' AND USER_ID IN ( ");
						sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
						sb.append("(" + strAddSql + ")");
						sb.append(" AND DATE_ID = '");
						sb.append(curDateId);
						sb.append("' ");
						sb.append(" AND USER_ID IN (");
						sb.append(userIdForCheck);
						sb.append(")");
						sb.append(")");
					} else {
						sb.append(" SELECT USER_ID FROM UNICOM_D_MB_DS_ALL_LABEL_INFO WHERE  ");
						sb.append("(" + strAddSql + ")");
						sb.append(" AND DATE_ID = '");
						sb.append(curDateId);
						sb.append("' ");
						sb.append(" AND USER_ID IN (");
						sb.append(userIdForCheck);
						sb.append(")");

					}
				} else { // --- 全没有时 ---
					log.info("此产品不存在或者没有成功标准可以判断");
					logdb.setBUSI_CODE("getSqlForSucessType,成功标准条件附加条件为空，此产品不存在或者没有成功标准可以判断");
					BusiToolsIns.insertPltCommonLog(logdb);
					return null;
				}
			}
		}

		log.info("工单检查sql=" + sb.toString() + sbProduct.toString() + ", 成功类型=" + successType);

		// 一条成功标准sql插入一条日志
		logdb.setBUSI_DESC("成功标准检查SusccessSql");

		String tmp = sb.toString() + sbProduct.toString();
		if (tmp.length() > 1024) {
			String tmp1 = tmp.substring(0, 1024);
			logdb.setBUSI_ITEM_1(tmp1);
		} else {
			logdb.setBUSI_ITEM_1(tmp);
		}
		logdb.setBUSI_CODE("getSqlForSucessType,成功类型=" + successType + "精确匹配：" + bProduct);
		BusiToolsIns.insertPltCommonLog(logdb);

		if (bProduct) {
			return sbProduct.toString();
		} else {
			return sb.toString();
		}
	}

	/*
	 * 根据活动序列号得到成功标准
	 */
	private ActivitySucessInfo getActivitySucessInfo(String tenant_id, int activity_seq_id) {
		List<ActivitySucessInfo> listAcitvitySucessInfo = mapActivitySucessInfo.get(tenant_id);
		int i = 0;
		for (i = 0; i < listAcitvitySucessInfo.size(); ++i) {
			if (listAcitvitySucessInfo.get(i).getRecId() == activity_seq_id)
				break;
		}
		if (i == listAcitvitySucessInfo.size()) { // --- 活动数据发生了变化 ---
			return null;
		}
		return listAcitvitySucessInfo.get(i);
	}
}
