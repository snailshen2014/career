package com.bonc.busi.outer.dao;
/*
 * @desc:SYS_COMMON_CFG表的相关操作
 * @author:zengdingyong
 * @time:2017-06-05
 */

import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.task.base.SpringUtil;

public class SyscommoncfgDao {
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);
	
	/*
	 * 提取某个KEY对应的VALUE
	 */
	public		static	String		query(String conValue){
		int				count =  jdbcTemplate.queryForObject("SELECT count(*) FROM SYS_COMMON_CFG WHERE `CFG_KEY`= ?",
				new Object[]{conValue},
				new int[]{java.sql.Types.VARCHAR},
				Integer.class
		);
		if(count != 1) return null;
		String			resultValue = jdbcTemplate.queryForObject("SELECT CFG_VALUE FROM SYS_COMMON_CFG WHERE `CFG_KEY`= ?",
				new Object[]{conValue},
				new int[]{java.sql.Types.VARCHAR},
				String.class
		);
		return resultValue;
	}
	
	/*
	 * 更新某个键值
	 */
	public		static	int		update(String	colName,String colValue){
		StringBuilder			sb = new StringBuilder();
		sb.append("UPDATE SYS_COMMON_CFG SET CFG_VALUE='");
		sb.append(colValue);
		sb.append("' WHERE `CFG_KEY`= ?");
		int		count=jdbcTemplate.update(sb.toString(),
				new Object[]{colName},
				new int[]{java.sql.Types.VARCHAR}
		);
		return count;
	}

}
