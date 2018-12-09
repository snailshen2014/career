package com.bonc.busi.service.dao;
/*
 * SYS_LOG表的相关操作
 */

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.bonc.busi.service.entity.SysLog;
import com.bonc.busi.task.base.SpringUtil;

public class SyslogDao {
	private final static Logger log = LoggerFactory.getLogger(SyslogDao.class);
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);
	
	public		static	boolean	insert(final SysLog SysLogIns){
		// --- 判断时间是否设置 --
		if(SysLogIns.getLOG_TIME() == null)  return false;
		// --- 根据时间获取月份 ---
		SimpleDateFormat sdf = new SimpleDateFormat("MM"); 
		String	monthStr = sdf.format(SysLogIns.getLOG_TIME());
		StringBuilder			sb = new StringBuilder();
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
		  }});  
		return true;	
	}

}
