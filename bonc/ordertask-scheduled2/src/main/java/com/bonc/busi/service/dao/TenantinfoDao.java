package com.bonc.busi.service.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.task.base.SpringUtil;

public class TenantinfoDao {
	private final static Logger log = LoggerFactory.getLogger(TenantinfoDao.class);
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);
	
	public	static String queryPROV_ID(String tenant_id){
		
		String			resultValue = jdbcTemplate.queryForObject("SELECT PROV_ID FROM TENANT_INFO WHERE TENANT_ID = ? AND STATE='1' ",
				new Object[]{tenant_id},
				new int[]{java.sql.Types.VARCHAR},
				String.class
		);
		return resultValue;
	}

}
