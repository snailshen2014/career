package com.bonc.busi.orderschedule.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonc.busi.task.base.SpringUtil;
import com.bonc.busi.task.bo.SysCommonCfg;
import com.bonc.busi.task.mapper.BaseMapper;
/**
 * 提供获取系统变量值的功能：使用BaseMapper提供的功能
 * 获取SYS_COMMON_CFG表的配置信息
 * @see com.bonc.busi.task.mapper.BaseMapper
 * @author Administrator
 */
public class SystemCommonConfigManager {
	
   private  final static BaseMapper mapper = (BaseMapper) SpringUtil.getApplicationContext().getBean("baseMapper");
   //保存系统所有的系统变量
   private static List<SysCommonCfg> sysCommonCfgList;
   public static Map<String,String> sysCommonCfgMap = new HashMap<String,String>();

   static {
	   sysCommonCfgList = mapper.getAllSysCommonCfg();
	   for(SysCommonCfg cfg : sysCommonCfgList) {
		   sysCommonCfgMap.put(cfg.getCFG_KEY(), cfg.getCFG_VALUE());
	   }
   }
   /**
    * 
    * @param cfgKey 系统公共配置的Key
    * @return  系统公共配置的Value
    */
   public static String getSysCommonCfgValue(String cfgKey) {
	   String cfgValue = sysCommonCfgMap.get(cfgKey);
	   if(cfgValue==null){
		   cfgValue = mapper.getValueFromSysCommCfg(cfgKey);
		   if(cfgValue!=null){
			   sysCommonCfgMap.put(cfgKey, cfgValue);
		   }
	   }

	     return cfgValue;
   } 
}