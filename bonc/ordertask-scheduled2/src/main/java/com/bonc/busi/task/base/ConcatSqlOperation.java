package com.bonc.busi.task.base;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bonc.busi.service.dao.SyscommoncfgDao;
import com.bonc.busi.service.dao.SysrunningcfgDao;

public class ConcatSqlOperation {
	
	private static final Logger log = Logger.getLogger(ConcatSqlOperation.class);
    // --- 从行云导出数据 ---
	public String getExportSql(String TenantId,List<Map<String, Object>> columnsList,String xcloudTableName,String asynMonthDate) {
		StringBuilder sBuilder = new StringBuilder();
		StringBuilder doubleFields = new StringBuilder();
		sBuilder.append("/*!mycat:sql=select * FROM XCLOUD_TTTTTENANT_ID*/");
		sBuilder.append("EXPORT SELECT ");
		try {
			for (int i = 0; i < columnsList.size(); i++) {
				String columnName = columnsList.get(i).get("COLUMN_NAME").toString();
				String dataType = columnsList.get(i).get("DATA_TYPE").toString();
				//（可以用三目运算符）  判断是不是加逗号
				if (i == columnsList.size() - 1) {
					if (columnName.equalsIgnoreCase("TENANT_ID")) {
						sBuilder.append("'TTTTTENANT_ID'" + columnName);
					} else if (dataType.equalsIgnoreCase("varchar")||dataType.equalsIgnoreCase("char")) {
						sBuilder.append("REPLACE(REPLACE(" + columnName + ",'" + "\\" + "','/'),'" + "\"" + "','')" + columnName);
					} else{
						sBuilder.append(columnName);
					}
					
				} else {
					if (columnName.equalsIgnoreCase("TENANT_ID")) {
						sBuilder.append("'TTTTTENANT_ID'" + columnName + ",");
					} else if (dataType.equalsIgnoreCase("varchar")||dataType.equalsIgnoreCase("char")) {
						sBuilder.append("REPLACE(REPLACE(" + columnName + ",'" + "\\" + "','/'),'" + "\"" + "','')"+ columnName + ",");
					} else  {
						sBuilder.append(columnName+",");
					}
				}
			}
			sBuilder.append(" FROM " + xcloudTableName 
					+ " WHERE "+ asynMonthDate + "='DDDDDATEID'"
					+ " AND PROV_ID = 'PPPPPROV_ID' ATTRIBUTE(LOCATION('FFFFFILENAME') SEPARATOR('0x1A'))");
			System.out.println(sBuilder);
			// --- 某租户下用户标签表double类型的字段(double类型的字段导出时要扩大一百倍) ---
//			String doubleField = doubleFields.toString();
//			log.info("--------字段类型为double的字段："+doubleField);
//			boolean endCharacter = doubleField.endsWith(",");
//			if(endCharacter){
//				doubleField = doubleField.substring(0, doubleField.length()-1);	
//			}
//			log.info("字段类型为double的字段："+doubleField);
//			SyscommoncfgDao.update("ASYNUSER.DOUBLEFIELDS."+TenantId,doubleField);
//			SysrunningcfgDao.update("ASYNUSER.DOUBLEFIELDS."+TenantId,doubleField);
		} catch (Exception exception) {
			exception.printStackTrace();
			log.info("Export语句拼接出现问题！");
		}
		return sBuilder.toString();

	}
	
	// --- 向mysql导入数据 ---
	public String getLoadSql(List<Map<String, Object>> columnsList) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("LOAD DATA LOCAL INFILE 'FFFFFILENAME' IGNORE INTO TABLE PLT_USER_LABEL_PPPPPART "
				+ "FIELDS TERMINATED BY '\\Z' LINES TERMINATED BY '\\n' (");
		try{
			for(int i = 0; i < columnsList.size(); i++){
				String columnName = columnsList.get(i).get("COLUMN_NAME").toString();
				if(i == columnsList.size()-1){
					sBuilder.append(columnName + ")");
				}else{
					sBuilder.append(columnName + ",");
				}			
			}
		}catch(Exception exception){
			exception.printStackTrace();
			log.info("Load语句拼接出现问题！");
		}
		return sBuilder.toString();
	}
	
	// --- 在mysql建表 ---
	public String getCreateSql(List<Map<String, Object>> columnsList){
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("/*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = 'TTTTTENANT_ID' */");
		sBuilder.append("CREATE TABLE PLT_USER_LABEL_PPPPPART (");
		try{
			for(int i = 0; i < columnsList.size(); i++){
				String columnName = columnsList.get(i).get("COLUMN_NAME").toString();
				String columnlength = columnsList.get(i).get("LENGTH").toString();
				String dataType = columnsList.get(i).get("DATA_TYPE").toString();
			
				String comment="";
				if(columnsList.get(i).get("LABEL_COMMENT")!=null){
					comment = columnsList.get(i).get("LABEL_COMMENT").toString();
				}
				if(dataType.equalsIgnoreCase("double")){
					int length = 20;
					columnlength = Integer.toString(length);
					sBuilder.append(columnName + " VARCHAR (" + columnlength + ") COMMENT '" + comment +"'" + ",");
				}
				if(dataType.equalsIgnoreCase("int")||dataType.equalsIgnoreCase("long")){
					sBuilder.append(columnName + " BIGINT COMMENT '" + comment +"'" + ",");
				}else if(dataType.equalsIgnoreCase("varchar")||dataType.equalsIgnoreCase("char")){
					sBuilder.append(columnName + " VARCHAR (" + columnlength + ") COMMENT '" + comment +"'" + ",");
				}
			}
			sBuilder.append("PRIMARY KEY (USER_ID),INDEX IDX_DEVICE_NUMBER (DEVICE_NUMBER)) ENGINE = INNODB DEFAULT CHARSET = utf8");
		}catch(Exception exception){
			exception.printStackTrace();
			log.info("Create语句拼接出现问题！");
		}
		return sBuilder.toString();
	}	
}
