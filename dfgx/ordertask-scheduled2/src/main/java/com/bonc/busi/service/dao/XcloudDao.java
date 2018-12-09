package com.bonc.busi.service.dao;
/*
 * @desc:和行云相关表操作
 * @author:zengdingyong
 * @time:2017-06-05
 */

import 	java.util.Map;
import 	java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.task.base.SpringUtil;

public class XcloudDao {
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);
	
	public	static	List<Map<String,Object>>   getSucessUse(String  sql){
		return jdbcTemplate.queryForList(sql);
	}

}
