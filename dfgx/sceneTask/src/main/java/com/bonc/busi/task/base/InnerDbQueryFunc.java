package com.bonc.busi.task.base;
/*
 * @desc:高耗时优化函数
 * @author:曾定勇
 * @time:2016-12-14
 */

import java.util.List;
import java.util.Map;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.bonc.busi.task.mapper.BaseMapper;
import com.bonc.common.thread.ThreadBaseFunction;

@Service("HighTimeFunc")
@Transactional
@SuppressWarnings("unchecked")
public class InnerDbQueryFunc extends ThreadBaseFunction{
	private final static Logger log= LoggerFactory.getLogger(InnerDbQueryFunc.class);
	@Autowired	 private JdbcTemplate jdbcTemplate;
//	@Autowired	private BaseMapper  BaseMapperDao;
	
	@Override
	public	int	begin(){
		return 0;
	}
	/*
	 * 结束
	 */
	@Override
	public	int	end(){
		return 0;
	}
	/*
	 * 获取数据（建议一次从数据库中取一批数据，如：500,1000
	 */
	public	synchronized Object	getData(){
		return null;
	}
	
	@Override
	//@Transactional
	public	int handleData(Object data){
		// --- 得到传入的参数 ---
		Date		dateBegin = new Date();
		Map<String,Object>	mapData = (Map<String,Object>	)data;
		List<Map<String, Object>> listData = jdbcTemplate.queryForList((String)mapData.get("SQL"));
		log.info("SQL={}",(String)mapData.get("SQL"));
		mapData.put("DATA", listData);
		mapData.put("ENDFLAG","1");
		Date		dateEnd = new Date();
		log.info(" {} 时间={}",Thread.currentThread().getName(),dateEnd.getTime() - dateBegin.getTime());
		return 1;
	}

}
