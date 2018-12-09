package com.bonc.busi.task.base;
/*
 * @desc:更新工单表上的用户信息
 * @author:曾定勇
 * @time:2016-12-19
 */
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.PltCommonLog;
import com.bonc.busi.task.mapper.BaseMapper;

public class UpdateOrderUserInfo extends ParallelFunc {
	private final static Logger log= LoggerFactory.getLogger(UpdateOrderUserInfo.class);
	private	JdbcTemplate JdbcTemplateIns = SpringUtil.getBean(JdbcTemplate.class);
	private	BusiTools		BusiToolsIns = SpringUtil.getBean(BusiTools.class);
	private	BaseMapper		BaseMapperIns = SpringUtil.getBean(BaseMapper.class);
	private	List<Map<String, Object>>	listTenantInfo = null;
	
	private	long				lCurRecId = 0;
	private	String			curTenantId = null;
	private	Date				dateBegin = null;
	private	String			m_strGetOrderUserSql = null;
	private	String			m_strMbUserQuerySql = null;
	private	String			m_strMbUserUpdateSql = null;
	private	String			m_strKdUserQuerySql = null;
	private	String			m_strKdUserUpdateSql = null;
	private	PltCommonLog		PltCommonLogIns = new PltCommonLog();
	int  			SerialId =0;
	
	/*
	 * 开始方法
	 */
	@Override
	public	int	begin(){
		dateBegin = new Date();
		lCurRecId = 0;
		log.info("更新工单表用户信息开始");
		SerialId = BusiToolsIns.getSequence("COMMONLOG.SERIAL_ID");
		//PltCommonLog		PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("10");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(dateBegin);
		PltCommonLogIns.setSPONSOR("UPDATEORDERUSER");
		PltCommonLogIns.setBUSI_CODE("BEGIN");
		PltCommonLogIns.setBUSI_DESC("更新工单表用户信息开始");
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		listTenantInfo = BusiToolsIns.getValidTenantInfo();
		curTenantId = (String)listTenantInfo.get(0).get("TENANT_ID");	
		// --- 提取SQL语句 ---
		m_strGetOrderUserSql = BusiToolsIns.getValueFromGlobal("UPDATEORDERUSER.GETORDERUSERSQL");
		m_strMbUserQuerySql = BusiToolsIns.getValueFromGlobal("UPDATEORDERUSER.MBUSERQUERYSQL");
		m_strMbUserUpdateSql = BusiToolsIns.getValueFromGlobal("UPDATEORDERUSER.MBUSERUPDATESQL");
		m_strKdUserQuerySql = BusiToolsIns.getValueFromGlobal("UPDATEORDERUSER.KDUSERQUERYSQL");
		m_strKdUserUpdateSql = BusiToolsIns.getValueFromGlobal("UPDATEORDERUSER.KDUSERUPDATESQL");
		return 0;
	}
	/*
	 * 结束方法
	 */
	@Override
	public	int	end(){
		log.info("更新工单表用户信息结束,耗时={}",new Date().getTime()-dateBegin.getTime());
		PltCommonLog		PltCommonLogIns = new PltCommonLog();
		PltCommonLogIns.setLOG_TYPE("10");
		PltCommonLogIns.setSERIAL_ID(SerialId);
		PltCommonLogIns.setSTART_TIME(new Date());
		PltCommonLogIns.setSPONSOR("UPDATEORDERUSER");
		PltCommonLogIns.setBUSI_CODE("END");
		PltCommonLogIns.setBUSI_DESC("更新工单表用户信息结束");
		BusiToolsIns.insertPltCommonLog(PltCommonLogIns);
		return 0;
	}
	/*
	 * 寻找下一个租户
	 */
	private	String		getNextTenantId(){
		for(int i=0;i < listTenantInfo.size();++i){
			String		strTenantId = (String)listTenantInfo.get(i).get("TENANT_ID");
			if(strTenantId.equalsIgnoreCase(curTenantId)){
				if(i == listTenantInfo.size()-1)  return null;
				else return (String)listTenantInfo.get(i+1).get("TENANT_ID");
			}
			
		}
		return null;
	}
	/*
	 * 提取数据，子类需要实现此方法
	 */
	@Override
	public	Object		get(){
		log.info("--- into get ---");
		List<Map<String,Object>>		listRecId;
		while(1 > 0){
			listRecId = JdbcTemplateIns.queryForList(m_strGetOrderUserSql, 
					new Object[]{curTenantId,lCurRecId}, 
					new int[]{java.sql.Types.VARCHAR,java.sql.Types.BIGINT});
			if(listRecId == null || listRecId.size() == 0){
				log.info("无数据");
				// --- 当前租户数据结束 ---
				curTenantId = getNextTenantId();
				if(curTenantId == null){
					return null;
				}
				lCurRecId = 0;
			}
			else{
				// --- 设置当前的工单序列号 --------------
				lCurRecId = (long)listRecId.get(listRecId.size()-1).get("REC_ID");
				log.info("size={},当前工单序列号:{}",listRecId.size(),lCurRecId);
				break;
			}
		}
		return  listRecId;
	}
	/*
	 * 处理
	 */
	@Override
	public		int		handle(Object data){
		log.info("--- into handle ---");
		// --- 参数转换 ---
		List<Map<String, Object>>		listRecId =(List<Map<String, Object>>) data;	
		StringBuilder		sb = new StringBuilder();
		int 		i=0;
		int		iMbWhere = m_strMbUserUpdateSql.indexOf("WHERE");
		for(Map<String, Object> item:listRecId){
			if("0".equalsIgnoreCase((String)item.get("SERVICE_TYPE"))){  // --- 移动 --
				Map<String, Object> mapMbUserInfo = JdbcTemplateIns.queryForMap(m_strMbUserQuerySql, 
						new Object[]{item.get("USER_ID"),item.get("TENANT_ID")}, 
						new int[]{java.sql.Types.VARCHAR,java.sql.Types.VARCHAR});
				if(mapMbUserInfo == null) continue;
				// ---  根据查询中的字段替换更新中的字段  ---
				sb.setLength(0);
				sb.append(FtpTools.getChangeSql(m_strMbUserUpdateSql, mapMbUserInfo));
				//log.info("update sql={}",sb.toString());
				JdbcTemplateIns.update(sb.toString(),
						new Object[]{item.get("REC_ID"),item.get("TENANT_ID")}, 
						new int[]{java.sql.Types.INTEGER,java.sql.Types.VARCHAR});	
			}
			else{  // --- 宽带 ---
				Map<String, Object> mapKdUserInfo = JdbcTemplateIns.queryForMap(m_strKdUserQuerySql, 
						new Object[]{item.get("USER_ID"),item.get("TENANT_ID")}, 
						new int[]{java.sql.Types.VARCHAR,java.sql.Types.VARCHAR});
				if(mapKdUserInfo == null) continue;
				// ---  根据查询中的字段替换更新中的字段  ---
				sb.setLength(0);
				sb.append(FtpTools.getChangeSql(m_strKdUserUpdateSql, mapKdUserInfo));
				JdbcTemplateIns.update(sb.toString(),
						new Object[]{item.get("REC_ID"),item.get("TENANT_ID")}, 
						new int[]{java.sql.Types.INTEGER,java.sql.Types.VARCHAR});	
			}
		}
		
		return 0;
	}
	
	

}
