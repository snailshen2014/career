package com.bonc.busi.orderschedule.mapper;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;
/**
 * mapper operation class
 * @author yanjunshen
 *
 */
public class SqlWorker {
	/**
	 *  increase decrease table using numbers
	 *  @param params
	 *  @return 
	 */
	public String setTableCapacity(final Map<String, Object> param) {
		return new SQL() {{
		    UPDATE("PLT_ORDER_TABLES_USING_INFO");
		    //0 add,1 subtraction
		    if ((int)param.get("TYPE") == 0)
		    	SET("CURRENT_AMOUNT="+ "CURRENT_AMOUNT + " + param.get("NUMBER"));
		    if ((int)param.get("TYPE") == 1)
		    	SET("CURRENT_AMOUNT="+ "CURRENT_AMOUNT - " +  param.get("NUMBER"));
		    SET("AMOUNT_LAST_UPDATE_TIME = now()");
		    WHERE("TABLE_NAME='" + param.get("TABLE_NAME") + "' and USING_BUSI_TYPE=" +
		    		param.get("BUSI_TYPE") + " and TENANT_ID='" + param.get("TENANT_ID") + "'");
		    
		  }}.toString();
	}
}
