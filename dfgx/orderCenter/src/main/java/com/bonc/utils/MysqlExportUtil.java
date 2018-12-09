package com.bonc.utils;

public class MysqlExportUtil {

	private String table;
	private String dataSql;
	
	public MysqlExportUtil() {
		super();
	}

	public MysqlExportUtil(String table, String dataSql) {
		super();
		this.table = table;
		this.dataSql = dataSql;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getDataSql() {
		return dataSql;
	}

	public void setDataSql(String dataSql) {
		this.dataSql = dataSql;
	}

	public String export(){
		return null;
	}
}
