package com.bonc.busi.service.dao;
/*
 * @desc:表PLT_ACTIVITY_PROCESS_LOG的相关操作
 * @author:zengdingyong
 * @time:2017-06-05
 */

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.task.base.SpringUtil;

public class PltactivityprocesslogDao {
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);
	
	public	static	List<String>		getListChannelIdByActivitySeqId(int ActivitySeqId,String tenant_id){
		List<String>			listChannelId = jdbcTemplate.queryForList("SELECT CHANNEL_ID FROM PLT_ACTIVITY_PROCESS_LOG  WHERE ACTIVITY_SEQ_ID = ?"
				+ "  AND TENANT_ID = ?  ORDER BY ORI_AMOUNT", 
				new Object[]{ActivitySeqId,tenant_id},
				new int[]{java.sql.Types.INTEGER,java.sql.Types.VARCHAR},
				String.class
				);
		return listChannelId;
	}

}
