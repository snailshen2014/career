package com.bonc.busi.orderschedule.mapper;
/*
 * @desc:表PLT_ACTIVITY_EXECUTE_LOG的相关操作
 * @author:zengdingyong
 * @time:2017-06-09
 */

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.bonc.busi.orderschedule.bo.PltActivityExecuteLog;
import com.bonc.busi.task.base.SpringUtil;

public class ActivityExecuteLogDao {
    private static JdbcTemplate jdbcTemplate = (JdbcTemplate) SpringUtil.getBean(JdbcTemplate.class);

    public static boolean insert(final PltActivityExecuteLog data) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT  INTO PLT_ACTIVITY_EXECUTE_LOG");
        sb.append("(ACTIVITY_SEQ_ID,TENANT_ID,BUSI_CODE,CHANNEL_ID,PROCESS_STATUS,BEGIN_DATE,ACTIVITY_ID)");
        sb.append(" VALUES(?,?,?,?,?,?,?)");
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
            }
        });
        return true;
    }

    /*
     * 更新
     */
    public static int updateStatus(final PltActivityExecuteLog data) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE PLT_ACTIVITY_EXECUTE_LOG SET BUSI_ITEM = ? ,END_DATE = ?,OPER_TIME = UNIX_TIMESTAMP(END_DATE)-UNIX_TIMESTAMP(BEGIN_DATE),PROCESS_STATUS =");
        sb.append(data.getPROCESS_STATUS());
        sb.append(" WHERE ACTIVITY_SEQ_ID = ?  AND TENANT_ID = ?  AND BUSI_CODE = ? ");
        //如果busiItem的列长于6000，截取
        String busiItem = data.getBUSI_ITEM();
        if (null != busiItem && busiItem.length() > 5999) {
            busiItem = busiItem.substring(0, 5999);
        }
        int count = jdbcTemplate.update(sb.toString(),
                new Object[]{busiItem, data.getEND_DATE(), data.getACTIVITY_SEQ_ID(), data.getTENANT_ID(), data.getBUSI_CODE()},
                new int[]{Types.VARCHAR, Types.TIMESTAMP, java.sql.Types.INTEGER, java.sql.Types.VARCHAR, java.sql.Types.VARCHAR}
        );
        return count;
    }

    public static Integer getStatus(final PltActivityExecuteLog data) {
        StringBuilder sb = new StringBuilder();
        sb.append("select PROCESS_STATUS from  PLT_ACTIVITY_EXECUTE_LOG ");
        sb.append(" where  ACTIVITY_SEQ_ID = ?  AND TENANT_ID = ?  AND BUSI_CODE = ? ORDER BY BEGIN_DATE DESC  LIMIT 1");
//		int		count=jdbcTemplate.update(sb.toString(),
//				new Object[]{data.getACTIVITY_SEQ_ID(),data.getTENANT_ID(),data.getBUSI_CODE()},
//				new int[]{java.sql.Types.INTEGER,java.sql.Types.VARCHAR}
//		);
        Integer status;
        try {
            status = (Integer) jdbcTemplate.queryForObject(sb.toString(),
                    Integer.class, new Object[]{data.getACTIVITY_SEQ_ID(), data.getTENANT_ID(), data.getBUSI_CODE()});
//            System.out.println(sb.toString());
//            System.out.println(data.getACTIVITY_SEQ_ID());
//            System.out.println(data.getTENANT_ID());
//            System.out.println(data.getBUSI_CODE());
        } catch (EmptyResultDataAccessException e) {
            status = 0;
        }
        return status;
    }
}
