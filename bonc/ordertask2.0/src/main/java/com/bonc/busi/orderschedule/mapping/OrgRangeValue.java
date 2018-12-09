package com.bonc.busi.orderschedule.mapping;

import com.bonc.busi.orderschedule.config.SystemCommonConfigManager;

/**
 * for count org range
 * 
 * @author yanjunshen
 *
 */
public class OrgRangeValue extends Value {
	public void setValue(Object v) {
		value = v;
	}

	public Object getValue() {
		return value;
	}

	public OrgRangeValue(String orgRange , String conAdd) {
		/// root/770/,/root/HAB/,/root/763/,/root/766/,
		String orgpath = orgRange;
		String[] path_list = orgpath.split(",");
		String tmp = "( ";
		String tmp2 = conAdd;
		// 电信的版本无c表 用宽表中的YWD059 代替
//		String type = SystemCommonConfigManager.getSysCommonCfgValue("SERVICE_PROVIDER_TYPE");
//		if ("1".equals(type)){
//			tmp2 = "a.pb0043 like '%";
//		}else {
//			tmp2 = "c.ORGPATH like '%";
//		}
		String sql = "";
		for (String path : path_list) {
			if (path != null && path.length() != 0) {
				sql += tmp2;
				sql += path;
				sql += "%' ";

				sql += " or ";
			}
		}

		int pos = sql.lastIndexOf("or");
		sql = sql.substring(0, pos);
		sql += ")";
		setValue(tmp + sql);
	}

}
