package com.bonc.busi.sys.dao;
/*
 * @desc:和表ORDER_CONTROL_LOG的相关操作
 * @author:zengdingyong
 * @time:2017-06-02
 */

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.bonc.busi.sys.entity.OrderControlLog;
import com.bonc.busi.sys.mapper.SysMapper;
import com.bonc.busi.sys.base.SpringUtil;

public class OrdercontrollogDao {
	/*private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);*/
	private static SysMapper sysMapper = (SysMapper)SpringUtil.getBean(SysMapper.class);
	public		static		void		insert(final OrderControlLog rec){
		/*int count = jdbcTemplate.update("INSERT INTO ORDER_CONTROL_LOG(CONTROL_CODE,TENANT_ID,BUSI_TIME,"
				+ "BUSI_RESULT,BUSI_MESSAGE) VALUES(?,?,?,?,?)", new PreparedStatementSetter() {  
		      @Override  
		      public void setValues(PreparedStatement pstmt) throws SQLException {  
		          pstmt.setObject(1,rec.getCONTROL_CODE());
		          pstmt.setObject(2, rec.getTENANT_ID());
		          pstmt.setObject(3, rec.getBUSI_TIME());
		          pstmt.setObject(4, rec.getBUSI_RESULT());
		          pstmt.setObject(5, rec.getBUSI_MESSAGE());
		  }});  */
		sysMapper.insertOrderControlLog(rec);
	}

}
