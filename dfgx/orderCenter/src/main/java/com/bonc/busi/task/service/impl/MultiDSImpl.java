package com.bonc.busi.task.service.impl;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.bonc.busi.task.service.MutltiDSInterface;
import com.bonc.common.datasource.TargetDataSource;

@Service()
public class MultiDSImpl implements MutltiDSInterface{
	private final static Logger log = LoggerFactory.getLogger(MultiDSImpl.class);
	
	@Autowired
	 private JdbcTemplate jdbcTemplate;
	
	@TargetDataSource(name="mysqlslaveuni076")
	public		void			test1(){
		StringBuilder			sb = new StringBuilder();
		sb.append("select count(*) recnum from PLT_ORDER_INFO  PARTITION (P8)");
		Map<String,Object>  mapResult = jdbcTemplate.queryForMap(sb.toString());
		log.info("mapresult ={}",mapResult);
	}
}
