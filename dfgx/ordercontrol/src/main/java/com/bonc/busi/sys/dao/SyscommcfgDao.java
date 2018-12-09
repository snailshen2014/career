package com.bonc.busi.sys.dao;
/*
 * 表SYS_COMMON_CFG的相关操作
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bonc.busi.sys.base.SpringUtil;
import com.bonc.busi.sys.mapper.SysMapper;

import org.springframework.stereotype.Component;

@Component
public class SyscommcfgDao {
	private final static Logger log = LoggerFactory.getLogger(SyscommcfgDao.class);
//	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);
	/*@Autowired  JdbcTemplate jdbcTemplate;*/
	//private static SysMapper sysMapper = (SysMapper)SpringUtil.getBean(SysMapper.class);
	@Autowired
	private SysMapper sysMapper;
	/*
	 * 提取某个KEY对应的VALUE
	 */
	public			String		query(String conValue){
		/*int				count =  jdbcTemplate.queryForObject("SELECT count(*) FROM SYS_COMMON_CFG WHERE `CFG_KEY`= ?",
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
		return resultValue;*/
		return sysMapper.getSystemValueByKey(conValue);
	}
	/*
	 * 更新某个键值
	 */
	public			int		update(String	colName,String colValue){
		/*StringBuilder			sb = new StringBuilder();
		sb.append("UPDATE SYS_COMMON_CFG SET CFG_VALUE='");
		sb.append(colValue);
		sb.append("' WHERE `CFG_KEY`= ?");
		int		count=jdbcTemplate.update(sb.toString(),
				new Object[]{colName},
				new int[]{java.sql.Types.VARCHAR}
		);
		return count;*/
		return sysMapper.updateSystemValueByKey(colName, colValue);
	}

}
