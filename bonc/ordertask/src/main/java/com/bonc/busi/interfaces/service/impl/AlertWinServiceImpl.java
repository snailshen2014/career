package com.bonc.busi.interfaces.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonc.busi.interfaces.mapper.AlertWinMapper;
import com.bonc.busi.interfaces.service.AlertWinService;
import com.bonc.busi.task.base.BusiTools;
import com.bonc.utils.IContants;

@Transactional
@Service("alertWinService")
public class AlertWinServiceImpl implements AlertWinService{
	
	@Autowired
	private AlertWinMapper mapper;
	
	@Autowired
	private BusiTools  BusiTools;
	
	@Override
	public void updateLimitNum() {
		List<Map<String, Object>> tenantIds = BusiTools.getValidTenantInfo();
		for(Map<String, Object> map:tenantIds){
			///////////////////////////////// 渠道81
			//map.put("CHANNEL_ID", IContants.TC_CHANNEL_1);
			
			for(int i = 0;i<10;i++) {
				map.put("tableName", "PLT_ORDER_INFO_POPWIN_"+i);
				System.out.println("渠道81运行开始");
				map.put("CHANNEL_ID", IContants.TC_CHANNEL_1);
				HashMap<String, Long> recRange=mapper.getPopwinRange(map);
				
				if(null==recRange){
					System.out.println("渠道81没有RESERVE1>0的");
					return;
				}
				long min=1;
				long max=0;
				if(null!=recRange.get("min")&&null!=recRange.get("max")){
					String temMin = String.valueOf(recRange.get("min"));
					min= Long.parseLong(temMin) ;
					System.out.println("渠道81min是"+min);
					String temMax = String.valueOf(recRange.get("max"));
					max=Long.parseLong(temMax);
					System.out.println("渠道81max是"+max);
				}
				
				while (min<=max) {
					Long temporary = max - min ;
					if(temporary > 100000 ){
						temporary = min + 100000;
					}else {
						temporary = max;
					}
					System.out.println("渠道81运行中min是"+min);
					System.out.println("渠道81运行中max是"+max);
					map.put("min", min);
					map.put("max", temporary);
					mapper.updateLimitNum(map);
					//判断是否为早上6点
					int hour = getHour();
					if(hour > 5){
						return ;
					}
					min = min+100000;
				}
				System.out.println("渠道81运行结束");
			///////////////
			///////////////////////////////// 渠道82
			map.put("CHANNEL_ID", IContants.TC_CHANNEL_2);
			HashMap<String, Long> recRange2=mapper.getPopwinRange(map);
			System.out.println("渠道82运行开始");
			if(null==recRange2){
				System.out.println("渠道82没有RESERVE1>0的");
				return;
			}
			 min=1;
			 max=0;
			if(null!=recRange2.get("min")&&null!=recRange.get("max")){
				String temMin = String.valueOf(recRange.get("min"));
				min= Long.parseLong(temMin) ;
				System.out.println("渠道82min是"+min);
				String temMax = String.valueOf(recRange.get("max"));
				max=Long.parseLong(temMax);
				System.out.println("渠道82max是"+max);
			}
			
			while (min<=max) {
				Long temporary = max - min ;
				if(temporary > 100000 ){
					temporary = min + 100000;
				}else {
					temporary = max;
				}
				System.out.println("渠道82运行中min是"+min);
				System.out.println("渠道82运行中max是"+max);
				map.put("min", min);
				map.put("max", temporary);
				mapper.updateLimitNum(map);
				//判断是否为早上6点
				int hour = getHour();
				if(hour > 5){
					return ;
				}
				min = min+100000;	
			}
			System.out.println("渠道82运行结束");
			///////////////
//				map.put("CHANNEL_ID", IContants.TC_CHANNEL_1);
//				int updateNum = mapper.updateLimitNum(map);
//				
//			  
//				map.put("CHANNEL_ID", IContants.TC_CHANNEL_2);
//				int num = mapper.updateLimitNum(map);
			}			
			
		}
	}
	
	public int getHour(){
		System.out.println("弹窗开始判断时间");
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		String hehe = dateFormat.format( now );
//		System.out.println("日期修改："+hehe);
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
//		int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH);
//		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
//	    int minute = c.get(Calendar.MINUTE);
//		int second = c.get(Calendar.SECOND);
		//System.out.println(year + "/" + month + "/" + date + " " +hour + ":" +minute + ":" + second); 
		System.out.println("日期修改小时："+hour);
		return hour; 
	}

}
