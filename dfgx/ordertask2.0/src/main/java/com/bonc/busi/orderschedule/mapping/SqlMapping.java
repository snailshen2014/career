package com.bonc.busi.orderschedule.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.busi.orderschedule.mapper.SqlMappingMapper;
import com.bonc.busi.task.base.SpringUtil;

/**
 * column mapping for order table column 
 * @author yanjunshen
 * @Date 2017-05-17 14:40
 */
public class SqlMapping {
	/*
	 * LOG handel
	 */
	private final static Logger LOG = LoggerFactory.getLogger(SqlMapping.class);
	//mapper bean
	private  final static SqlMappingMapper MAPPER = (SqlMappingMapper) SpringUtil.getApplicationContext().getBean("sqlMappingMapper");  
	//table ,column ,condition mapping info
	private Map<MappingType,Map<Integer,SqlElement>>  sqlElementMap = new HashMap<MappingType,Map<Integer,SqlElement>>();
	/**
	 * consturctor 
	 */
	public SqlMapping(String tenantId) {
		initColumnMap(tenantId,MappingType.COLUMN);
		initTableMap(tenantId,MappingType.TABLE);
		initConditionMap(tenantId,MappingType.CONDITION);
	}
	/**
	 * 
	 * @return order table's columns (col1,col2...)
	 */
	public String toColumns() {
		Map<Integer,SqlElement> columnMap = sqlElementMap.get(MappingType.COLUMN);
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (Map.Entry<Integer, SqlElement> entry : columnMap.entrySet()) {
			sb.append(((Column)entry.getValue()).getColumnName());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}
	/**
	 * 
	 * @return order table's select sql
	 */
	public String toSelect() {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct ");
		sb.append(formatSqlElement(MappingType.COLUMN));
		sb.append(" from ");
		//tables and condition
		sb.append(formatSqlElement(MappingType.TABLE));
		sb.append(" where ");
		sb.append(formatSqlElement(MappingType.CONDITION));
		return sb.toString();
	}
	
	/**
	 * 
	 * @param seq, the sql element sequence
	 * @param v, value interface Instance
	 * @param type:0 column,1:table,2:condition
	 */
	public void setElementValue(int seq,Value v,int type) {
		MappingType mapType = null;
		
		if (0 == type) {
			mapType = MappingType.COLUMN;
		} else if (1 == type) {
			mapType = MappingType.TABLE;
		} else if (2 == type) {
			mapType = MappingType.CONDITION;
		} else {
			LOG.error("Error sql element type " + type);
			return ;
		}
		Map<Integer, SqlElement> elementMap = sqlElementMap.get(mapType);
		if (null == elementMap.get(seq)) {
			LOG.error("Element key:" + seq + " no exists in element map.");
		} else {
			elementMap.get(seq).setElementValue(v);
		}
	}

	/**
	 *
	 * @param seq, the sql element sequence
	 * @param type:0 column,1:table,2:condition
	 */
	public String getElementValue(int seq,int type) {
		MappingType mapType = null;
		if (2 == type) {
			mapType = MappingType.CONDITION;
			Condition con = (com.bonc.busi.orderschedule.mapping.Condition) this.sqlElementMap.get(mapType).get(seq);
			return con.getConAdd();
		} else {
			LOG.error("Only type 2 support Condition Add  " + type);
			return "";
		}

	}
	/**
	 * 
	 * @param seq, the sql element sequence
	 * @param type:0 column,1:table,2:condition
	 */
	public void closeElement(int seq,int type) {
		MappingType mapType = null;
		
		if (0 == type) {
			mapType = MappingType.COLUMN;
		} else if (1 == type) {
			mapType = MappingType.TABLE;
		} else if (2 == type) {
			mapType = MappingType.CONDITION;
		} else {
			LOG.error("Error sql element type " + type);
			return ;
		}
		Map<Integer, SqlElement> elementMap = sqlElementMap.get(mapType);
		if (null == elementMap.get(seq)) {
			LOG.error("Element key:" + seq + " no exists in element map.");
		} else {
			elementMap.get(seq).setElementEnable(false);
		}
	}
	
	/**
	 * init column mapping info
	 * 
	 * @param tenantId
	 */
	private void initColumnMap(String tenantId,MappingType type) {
		// get db info
		List<Map<String, Object>> columns = MAPPER.getTableColumnMappingInfo(tenantId);
		Map<Integer,SqlElement> columnMap = new HashMap<Integer,SqlElement>();
		// construct columns info
		for (Map<String, Object> column : columns) {
			int seq = (Integer) column.get("ORDER_COLUMN_SEQ");
			String name = (String) column.get("ORDER_COLUMN");
			String nameDes = (String) column.get("ORDER_COLUMN_DES");
			String alias = (String) column.get("SOURCE_TABLE_ALIAS");
			String souTabColumn = (String) column.get("SOURCE_TABLE_COLUMN");
			int colType = (Integer) column.get("COLUMN_TYPE");
			String block = (String) column.get("SQL_BLOCK");
			Column col = new Column(seq, name, nameDes, alias, souTabColumn, colType,block);
			col.setElementValue(null);
			columnMap.put(seq, col);
		}
		sqlElementMap.put(type, columnMap);
		
	}
	/**
	 * init table mapping info
	 * 
	 * @param tenantId
	 */
	private void initTableMap(String tenantId,MappingType type) {
		// get db info
		List<Map<String, Object>> columns = MAPPER.getSqlTableMappingInfo(tenantId);
		//tables map 
		Map<Integer,SqlElement> tableMap = new HashMap<Integer,SqlElement>();
		// construct columns info
		for (Map<String, Object> column : columns) {
			int seq = (int) column.get("TABLE_SEQ");
			String name = (String) column.get("SOURCE_TABLE");
			String alias = (String) column.get("TABLE_ALIAS");
			int tabType = (Integer) column.get("TABLE_TYPE");
			Table tab = new Table(name,seq,alias,tabType);
			tab.setElementValue(null);
			tableMap.put(seq, tab);
		}
		sqlElementMap.put(type, tableMap);
	}
	
	/**
	 * init condition mapping info
	 * 
	 * @param tenantId
	 */
	private void initConditionMap(String tenantId,MappingType type) {
		// get db info
		List<Map<String, Object>> columns = MAPPER.getSqlConditionMappingInfo(tenantId);
		Map<Integer,SqlElement> conditionMap = new HashMap<Integer,SqlElement>();
		
		// construct columns info
		for (Map<String, Object> column : columns) {
			int seq = (int) column.get("CON_SEQ");
			String conSql = (String) column.get("CON_SQL");
			int conType = (int) column.get("CON_TYPE");
			String conAdd = (String) column.get("CON_ADD");
			Condition con = new Condition(conSql,seq,conType,conAdd);
			con.setElementValue(null);
			conditionMap.put(seq, con);
		}
		sqlElementMap.put(type, conditionMap);
	}
	/**
	 * format sql element mapping info to string
	 * @param type
	 * @return
	 */
	private String formatSqlElement(MappingType type) {
		StringBuilder sb = new StringBuilder();
		Map<Integer,SqlElement> elementMap =  sqlElementMap.get(type);
		for (Map.Entry<Integer, SqlElement> entry : elementMap.entrySet()) {
			//when element close no join it.
			if (!entry.getValue().getElementEnable())
				continue;
			try {
				sb.append(entry.getValue().toElement());
			} catch (Exception e) {
			    System.out.println("Key:" + entry.getKey() + ",sql element sequence:"
			    		+ entry.getValue().getElementSeq() +" not  set value handle.");
				e.printStackTrace();
				break;
			}
			
			switch (type) {
			case COLUMN :
			case TABLE : {
				sb.append(", ");
				break;
			}
			case CONDITION: {
				sb.append(" and ");
				break;
			}
			default: {
				LOG.error("formatSqlElement element type error!");
			}
			}
		}
		String result = sb.toString();
		int index1 = result.lastIndexOf(",");
		int index2 = result.lastIndexOf("and");
		if (index1 != -1 && (type == MappingType.COLUMN || type == MappingType.TABLE) ) {
			result = result.substring(0, index1);
		}
		if (index2 != -1 && type == MappingType.CONDITION) {
			result = result.substring(0, index2);
		}
		return result;
	}
} 
/**
 * Mapping Type
 * @author yanjunshen
 *
 */
 enum MappingType {
	COLUMN,TABLE,CONDITION
 }

