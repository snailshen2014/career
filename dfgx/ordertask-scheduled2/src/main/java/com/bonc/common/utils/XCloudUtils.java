package com.bonc.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>Title: BONC - XCloudProcess </p>
 * 
 * <p>Description: xcloud导出处理类 </p>
 * 
 * <p>Copyright: Copyright BONC(c) 2013 - 2025 </p>
 * 
 * <p>Company: 北京东方国信科技股份有限公司 </p>
 * 
 * @author yangdx
 * @version 1.0.0
 */
public class XCloudUtils {

    /**
     * 属性文件辅助类型
     */
    private static PropertiesHelper helper;

    /**
     * 日志管理对象
     */
    private final static Logger LOGG = LoggerFactory.getLogger(XCloudUtils.class);

    // 加载配置
    static {
        helper = new PropertiesHelper("/xcloud-config.properties");
    }

    /**
     * 获取sql执行对象
     *
     * @return sql执行对象
     */
    private static Connection getConn() throws ClassNotFoundException, SQLException {
        // 加载驱动
        Class.forName("com.bonc.xcloud.jdbc.XCloudDriver");

        // 创建连接
        Connection conDB = DriverManager.getConnection(helper.getValue("xcloud.export.url"),
                helper.getValue("xcloud.export.username"),
                helper.getValue("xcloud.export.password"));

        return conDB;
    }

    /**
     * 获取sql执行对象
     *
     * @return sql执行对象
     */
    private static Statement getStatement(Connection conn) throws SQLException, ClassNotFoundException {
        return conn.createStatement();
    }

    private static void close(Connection conn, Statement statement) {
        if (null != statement) {
            try {
                statement.close();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }

        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 执行导出
     *
     * @param request    请求信息对象
     * @param sourcePath 源文件路径
     * @return 操作结果信息
     */
    public static void export() {

        Connection conn = null;
        Statement statement = null;
        try {
            // 获取数据连接
            conn = getConn();
            statement = getStatement(conn);

            // 构建导出sql
            StringBuilder sqlBuilder = new StringBuilder();
            /*sqlBuilder.append("EXPORT ")
                    .append(request.getSqlSource())
                    .append(" ATTRIBUTE(LOCATION('" + exportName + "') SEPARATOR('0x09')) ");*/

            // 处理执行sql
            statement.execute(sqlBuilder.toString());

            LOGG.error("执行SQL成功: {}", sqlBuilder.toString());
        } catch (Exception ex) {
            LOGG.error(ex.getMessage(), ex);
        } finally {
            close(conn, statement);
        }

    }
    
    /**
     * 查询传入sql的总记录数，查询不到为-1
     *
     *  @param sql    查询sql
     * @return 操作结果信息
     */
    public static int queryForCount(String sql) {
    	
    	Connection conn = null;
    	Statement statement = null;
    	ResultSet rs = null;
    	try {
    		// 获取数据连接
    		conn = getConn();
    		statement = getStatement(conn);
    		
    		rs = statement.executeQuery(sql);
    		if(rs.next()){
    			return rs.getInt(1);
    		}
    		LOGG.error("执行SQL成功: {}", sql);
    	} catch (Exception ex) {
    		LOGG.error(ex.getMessage(), ex);
    	} finally {
    		close(conn, statement);
    	}
    	return -1;
    }
    
    /**
     * 查询传入sql的结果集，查询不到为null
     *
     * @param sql    查询sql
     * @return 操作结果信息
     */
    public static ResultSet query(String sql) {
    	
    	Connection conn = null;
    	Statement statement = null;
    	ResultSet rs = null;
    	try {
    		// 获取数据连接
    		conn = getConn();
    		statement = getStatement(conn);
    		
    		rs = statement.executeQuery(sql);
    		if(rs.next()){
    			return rs;
    		}
    		LOGG.error("执行SQL成功: {}", sql);
    	} catch (Exception ex) {
    		LOGG.error(ex.getMessage(), ex);
    	} finally {
    		close(conn, statement);
    	}
    	return null;
    }
    
   public static void main(String[] args) {
    	String sql = "SELECT count(*) FROM UI_L_USER_LABEL_INFO_ALL_ORG";
    	System.out.println(queryForCount(sql));
	}
}
