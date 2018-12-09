package com.bonc.busi.service.dao;
/*
 * @desc：常规SQL操作
 * @author;zengdingyong
 * @time:2017-06-05
 */

import java.util.Map;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.task.base.SpringUtil;

public class CommonDao {
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);
	
	/*
	 * 通用更新 
	 */
	public	static		int		update(String sql){
		return jdbcTemplate.update(sql);
	}
	/*
	 * 通用查询 
	 */
	public	static	List<Map<String,Object>>   queryList(String sql){
		return jdbcTemplate.queryForList(sql);
	}
	/*
	 * 查询一条纪录 
	 */
	public	static	Map<String,Object>		queryOne(String sql){
		return jdbcTemplate.queryForMap(sql);
	}

}
