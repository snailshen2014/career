package com.bonc.busi.task.base;
/*
 * @desc:常用工具
 * @author:曾定勇
 * @time:2016-12-27
 */

import	java.util.Map;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import	java.util.HashMap;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class CommonUtils {
	private final static Logger log= LoggerFactory.getLogger(CommonUtils.class);
	
	/*
	 * 从查询的数据中得到一行，设置将要插入的一行
	 */
	public		static	String	qryRowToMysqlRow(ResultSetMetaData rsmdInsert,ResultSetMetaData  rsmdSelect,
			ResultSet  rsSelect){
		//Map<Integer,Object>		mapColValue = new HashMap<Integer,Object>();
		StringBuilder			sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append("(");
		try{
			for(int i=1;i <= rsmdInsert.getColumnCount();++i){
				if(i > 1){
					sb.append(",");
				}
				switch(rsmdInsert.getColumnType(i)){
					case  java.sql.Types.CHAR:
					{
						//sb.append("'");
						switch(rsmdSelect.getColumnType(i)){
							case  java.sql.Types.VARCHAR:
								if(rsSelect.getString(i) != null){
									sb.append("'");
									sb.append(rsSelect.getString(i));
									sb.append("'");
								}
								else{
									sb.append("null");
								}
								break;
							case  java.sql.Types.CHAR:
								if(rsSelect.getString(i) != null){
									sb.append("'");
									sb.append(rsSelect.getString(i));
									sb.append("'");
								}
								else{
									sb.append("null");
								}
								break;
							case  java.sql.Types.INTEGER:
								sb.append("'");
								sb.append(rsSelect.getInt(i));
								sb.append("'");
								break;
							case  java.sql.Types.BIGINT:
								sb.append("'");
								sb.append(rsSelect.getLong(i));
								sb.append("'");
								break;
							case  java.sql.Types.FLOAT:
								sb.append("'");
								sb.append(rsSelect.getFloat(i));
								sb.append("'");
								break;
							case  java.sql.Types.DATE:
								if(rsSelect.getDate(i) != null){
									sb.append("'");
									sb.append(sdf.format(rsSelect.getDate(i)));
									sb.append("'");
								}
								else{
									sb.append("null");
								}
								break;
							case  java.sql.Types.DOUBLE:
								sb.append("'");
								sb.append(rsSelect.getDouble(i));
								sb.append("'");
								break;
							default:
								log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.VARCHAR");
								return null;
							//	break;
							}	
						//sb.append("'");
						}
						break;
					case  java.sql.Types.VARCHAR:
						{
							//sb.append("'");
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.VARCHAR:
									if(rsSelect.getString(i) != null){
										sb.append("'");
										sb.append(rsSelect.getString(i));
										sb.append("'");
									}
									else{
										sb.append("null");
									}
									break;
								case  java.sql.Types.CHAR:
									if(rsSelect.getString(i) != null){
										sb.append("'");
										sb.append(rsSelect.getString(i));
										sb.append("'");
									}
									else{
										sb.append("null");
									}
									break;
								case  java.sql.Types.INTEGER:
									sb.append("'");
									sb.append(rsSelect.getInt(i));
									sb.append("'");
									break;
								case  java.sql.Types.BIGINT:
									sb.append("'");
									sb.append(rsSelect.getLong(i));
									sb.append("'");
									break;
								case  java.sql.Types.FLOAT:
									sb.append("'");
									sb.append(rsSelect.getFloat(i));
									sb.append("'");
									break;
								case  java.sql.Types.DATE:
									if(rsSelect.getDate(i) != null){
										sb.append("'");
										sb.append(sdf.format(rsSelect.getDate(i)));
										sb.append("'");
									}
									else{
										sb.append("null");
									}
									break;
								case  java.sql.Types.DOUBLE:
									sb.append("'");
									sb.append(rsSelect.getDouble(i));
									sb.append("'");
									break;
								default:
									log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.VARCHAR");
									return null;
								//	break;
							}	
							//sb.append("'");
						}
						break;
					case  java.sql.Types.INTEGER:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.INTEGER:
									sb.append(rsSelect.getInt(i));
									break;
								case  java.sql.Types.VARCHAR:
									if(rsSelect.getString(i) != null){
										sb.append(rsSelect.getString(i));
									}
									else{
										sb.append("null");
									}
									break;
								case  java.sql.Types.CHAR:
									if(rsSelect.getString(i) != null){
										sb.append(rsSelect.getString(i));
									}
									else{
										sb.append("null");
									}
									break;
								case  java.sql.Types.BIGINT:
									sb.append(rsSelect.getLong(i));
									break;
								default:
									log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.INTEGER");
									return null;
							}	
						}
						break;
					case  java.sql.Types.DATE:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.DATE:
									if(rsSelect.getDate(i) != null){
										sb.append("'");
										sb.append(sdf.format(rsSelect.getDate(i)));
										sb.append("'");
									}
									else{
										sb.append("null");
									}
									break;
								case  java.sql.Types.VARCHAR:
									if(rsSelect.getString(i) != null){
										sb.append("'");
										sb.append(sdf.format(rsSelect.getString(i)));
										sb.append("'");
									}
									else{
										sb.append("null");
									}
									break;
								default:
									log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.DATE");
									return null;
							}
						}
						break;
					case  java.sql.Types.BIGINT:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.BIGINT:
									sb.append(rsSelect.getLong(i));
									break;
								case  java.sql.Types.INTEGER:
									sb.append(rsSelect.getInt(i));
									break;
								case  java.sql.Types.VARCHAR:
									if(rsSelect.getString(i) != null){
										sb.append(rsSelect.getString(i));
									}
									else{
										sb.append("null");
									}
									break;
								case  java.sql.Types.CHAR:
									if(rsSelect.getString(i) != null){
										sb.append(rsSelect.getString(i));
									}
									else{
										sb.append("null");
									}
									break;
								default:
									log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.BIGINT");
									return null;
							}
						}
						break;
					default:
						log.info(" unsupported: "+ rsmdInsert.getColumnType(i));
						return null;
				}				
			}
			sb.append(")");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return sb.toString();	
	}
	
	/*
	 * 从查询的数据中得到一行，设置将要插入的一行
	 */
	public		static	Map<Integer,Object>	dbRowExchange(ResultSetMetaData rsmdInsert,ResultSetMetaData  rsmdSelect,
			ResultSet  rsSelect){
		Map<Integer,Object>		mapColValue = new HashMap<Integer,Object>();
		try{
			for(int i=1;i <= rsmdInsert.getColumnCount();++i){
				switch(rsmdInsert.getColumnType(i)){
					case  java.sql.Types.VARCHAR:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.VARCHAR:
									mapColValue.put(i, rsSelect.getString(i));
									break;
								case  java.sql.Types.CHAR:
									mapColValue.put(i, rsSelect.getString(i));
									break;
								case  java.sql.Types.INTEGER:
									mapColValue.put(i, String.valueOf(rsSelect.getInt(i)));
									break;
								case  java.sql.Types.BIGINT:
									mapColValue.put(i, String.valueOf(rsSelect.getLong(i)));
									break;
								case  java.sql.Types.FLOAT:
									mapColValue.put(i, String.valueOf(rsSelect.getFloat(i)));
									break;
								case  java.sql.Types.DATE:
									mapColValue.put(i, rsSelect.getDate(i).toString());
									break;
								case  java.sql.Types.DOUBLE:
									mapColValue.put(i, String.valueOf(rsSelect.getDouble(i)));
									break;
								default:
									log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.VARCHAR");
									return null;
								//	break;
							}						
						}
						break;
					case  java.sql.Types.INTEGER:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.INTEGER:
									mapColValue.put(i, rsSelect.getInt(i));
									break;
								case  java.sql.Types.VARCHAR:
									mapColValue.put(i, Integer.parseInt(rsSelect.getString(i)));
									break;
								case  java.sql.Types.CHAR:
									mapColValue.put(i,Integer.parseInt(rsSelect.getString(i)));
									break;
								case  java.sql.Types.BIGINT:
									mapColValue.put(i,String.valueOf(rsSelect.getLong(i)));
									break;
								default:
									log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.INTEGER");
									return null;
							}	
						}
						break;
					case  java.sql.Types.DATE:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.DATE:
									mapColValue.put(i,rsSelect.getDate(i));
									break;
								default:
									log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.DATE");
									return null;
							}
						}
						break;
					case  java.sql.Types.BIGINT:
						{
							switch(rsmdSelect.getColumnType(i)){
								case  java.sql.Types.BIGINT:
									mapColValue.put(i,rsSelect.getLong(i));
									break;
								case  java.sql.Types.INTEGER:
									mapColValue.put(i,rsSelect.getInt(i));
									break;
								case  java.sql.Types.VARCHAR:
									mapColValue.put(i,Long.parseLong(rsSelect.getString(i)));
									break;
								case  java.sql.Types.CHAR:
									mapColValue.put(i,Long.parseLong(rsSelect.getString(i)));
									break;
								default:
									log.info("can't  change  "+ rsmdSelect.getColumnType(i) + "  to  java.sql.Types.BIGINT");
									return null;
							}
						}
						break;
					default:
						log.info(" unsupported: "+ rsmdInsert.getColumnType(i));
						return null;
				}				
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return mapColValue;	
	}

}
