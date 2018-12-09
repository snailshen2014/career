package com.bonc.busi.service.dao;
/*
 * @desc:表PLT_ACTIVITY_EXECUTE_LOG的相关操作
 * @author:zengdingyong
 * @time:2017-06-09
 */

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.service.entity.PltActivityExecuteLog;

public class PltActivityExecuteLogDao {
	private static JdbcTemplate jdbcTemplate = (JdbcTemplate)SpringUtil.getBean(JdbcTemplate.class);

	public static boolean insert(final PltActivityExecuteLog data) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT  INTO PLT_ACTIVITY_EXECUTE_LOG");
		sb.append("(ACTIVITY_SEQ_ID,TENANT_ID,BUSI_CODE,CHANNEL_ID,PROCESS_STATUS,BEGIN_DATE,ACTIVITY_ID,END_DATE)");
		sb.append(" VALUES(?,?,?,?,?,?,?,?)");
		//log.info("sql="+sb.toString());
		int count = jdbcTemplate.update(sb.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException {
				pstmt.setObject(1, data.getACTIVITY_SEQ_ID());
				pstmt.setObject(2, data.getTENANT_ID());
				pstmt.setObject(3, data.getBUSI_CODE());
				pstmt.setObject(4, data.getCHANNEL_ID());
				pstmt.setObject(5, data.getPROCESS_STATUS());
				pstmt.setObject(6, data.getBEGIN_DATE());
				pstmt.setObject(7, data.getACTIVITY_ID());
				pstmt.setObject(8, data.getEND_DATE());
			}
		});
		return true;
	}

	/*
     * 更新
     */
	public static int updateStatus(final PltActivityExecuteLog data) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE PLT_ACTIVITY_EXECUTE_LOG SET END_DATE = ?,OPER_TIME = UNIX_TIMESTAMP(END_DATE)-UNIX_TIMESTAMP(BEGIN_DATE),PROCESS_STATUS =");
		sb.append(data.getPROCESS_STATUS());
		sb.append(" WHERE ACTIVITY_SEQ_ID = ?  AND TENANT_ID = ?  AND BUSI_CODE = 1011 ");
		int count = jdbcTemplate.update(sb.toString(),
				new Object[]{data.getEND_DATE(),data.getACTIVITY_SEQ_ID(), data.getTENANT_ID()},
				new int[]{Types.TIMESTAMP,java.sql.Types.INTEGER, java.sql.Types.VARCHAR}
		);
		return count;
	}
}
