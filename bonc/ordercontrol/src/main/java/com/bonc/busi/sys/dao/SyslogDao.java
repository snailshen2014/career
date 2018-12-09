package com.bonc.busi.sys.dao;
/*
 * SYS_LOG表的相关操作
 */

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.bonc.busi.sys.entity.SysLog;
import com.bonc.busi.sys.mapper.SysMapper;
import com.bonc.busi.sys.base.SpringUtil;

public class SyslogDao {
	private final static Logger log = LoggerFactory.getLogger(SyslogDao.class);
	/*private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);*/
//	@Autowired
	private static SysMapper sysMapper = (SysMapper)SpringUtil.getBean(SysMapper.class);

	
	public		static	boolean	insert(final SysLog SysLogIns){
		// --- 判断时间是否设置 --
		if(SysLogIns.getLOG_TIME() == null)  return false;
		// --- 根据时间获取月份 ---
		SimpleDateFormat sdf = new SimpleDateFormat("MM"); 
		String	monthStr = sdf.format(SysLogIns.getLOG_TIME());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("MONTHSTR", monthStr);
		map.put("TENANT_ID", SysLogIns.getTENANT_ID());
		map.put("LOG_TIME", SysLogIns.getLOG_TIME());
		map.put("APP_NAME", SysLogIns.getAPP_NAME());
		map.put("BUSI_ITEM_1", SysLogIns.getBUSI_ITEM_1());
		map.put("BUSI_ITEM_2", SysLogIns.getBUSI_ITEM_2());
		map.put("BUSI_ITEM_3", SysLogIns.getBUSI_ITEM_3());
		map.put("BUSI_ITEM_4", SysLogIns.getBUSI_ITEM_4());
		map.put("BUSI_ITEM_5", SysLogIns.getBUSI_ITEM_5());
		map.put("LOG_MESSAGE", SysLogIns.getLOG_MESSAGE());
		/*StringBuilder			sb = new StringBuilder();
		sb.append("INSERT  INTO SYS_LOG_");
		sb.append(monthStr);
		sb.append("(TENANT_ID,LOG_TIME,APP_NAME,BUSI_ITEM_1,BUSI_ITEM_2,BUSI_ITEM_3,BUSI_ITEM_4,BUSI_ITEM_5,LOG_MESSAGE)");
		sb.append(" VALUES(?,?,?,?,?,?,?,?,?)");
		//log.info("sql="+sb.toString());
		int count = jdbcTemplate.update(sb.toString(), new PreparedStatementSetter() {  
		      @Override  
		      public void setValues(PreparedStatement pstmt) throws SQLException {  
		          pstmt.setObject(1, SysLogIns.getTENANT_ID());
		          pstmt.setObject(2, SysLogIns.getLOG_TIME());
		          pstmt.setObject(3, SysLogIns.getAPP_NAME());
		          pstmt.setObject(4, SysLogIns.getBUSI_ITEM_1());
		          pstmt.setObject(5, SysLogIns.getBUSI_ITEM_2());
		          pstmt.setObject(6, SysLogIns.getBUSI_ITEM_3());
		          pstmt.setObject(7, SysLogIns.getBUSI_ITEM_4());
		          pstmt.setObject(8, SysLogIns.getBUSI_ITEM_5());
		          pstmt.setObject(9, SysLogIns.getLOG_MESSAGE());
		  }}); */ 
		int count = sysMapper.insertSysLogMonth(map);
		if(count != 1){
			return false;
		}
		return true;	
	}

}
