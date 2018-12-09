package com.bonc.h2.thread;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.bonc.h2.command.UserFlowInfoCommand;
import com.bonc.h2.pojo.H2Config;
import com.bonc.h2.pojo.ResponseResult;
import com.bonc.h2.pojo.UserFlowInfoBean;
import com.bonc.h2.socket.IpPort;
import com.bonc.h2.socket.SocketTool;
import com.bonc.utils.SystemHelper;

public class UserFlowInfo {
	Logger log = Logger.getLogger(UserAccountInfo.class); 
	private IpPort ipPort;
	private String deviceNumber = null;   //设备号码 
	private String serviceType = null;   //查询类型
	private String searchDate=null;
    private String businessType=null;   //是否时4G用户
    private SystemHelper util=new SystemHelper();
    public UserFlowInfo(String deviceNumber,String searchDate,String serviceType,String businessType){
    	this.deviceNumber = deviceNumber;
    	this.serviceType=serviceType;
    	this.searchDate=searchDate;
    	this.businessType=businessType;
    }
    
    public UserFlowInfoBean getUserFlowInfo(){
    	 String errorCode = null;    //结果代码
		 try {
			 Properties properties =util.getProperties();
				H2Config.TIME_OUT_COUNT = Integer.valueOf(String.valueOf(properties.get("TimeOutCount")));
				H2Config.WAIT_TIME = Integer.valueOf(String.valueOf(properties.get("WaitTime")));
				H2Config.SEND_FREQ = Integer.valueOf(String.valueOf( properties.get("SendFreq")));
				H2Config.TOMCAT_H2TIME_BEGIN =Integer.valueOf(String.valueOf(properties.get("TOMCAT_H2_BEGIN_TIME")));
				H2Config.TOMCAT_H2TIME_END = Integer.valueOf(String.valueOf(properties.get("TOMCAT_H2_END_TIME"))); 
				IpPort ipPortObj=new IpPort(String.valueOf(properties.get("4G_SERVICE_IP")),Integer.valueOf(String.valueOf(properties.get("4G_SERVICE_PORT"))),"4GNET");
				IpPort.IpPortMap.put(ipPortObj.getServiceType(), ipPortObj);
				IpPort ipPortObj2=new IpPort(String.valueOf(properties.get("23G_SERVICE_IP")),Integer.valueOf(String.valueOf(properties.get("23G_SERVICE_PORT"))),"NOT4GNET");
				IpPort.IpPortMap.put(ipPortObj2.getServiceType(), ipPortObj2);
				
				if("T".equals(businessType)){
					this.ipPort = IpPort.IpPortMap.get("4GNET");
				}else{
					this.ipPort = IpPort.IpPortMap.get("NOT4GNET");
				}
				System.out.println(">>>>>>开始查询用户流量");
				if("T".equals(businessType)){
					System.out.println("H2接口》》》》》》4G用户－－－－－");
					System.out.println("H2接口》》》》》》链接地址－－－－－"+this.ipPort.getIp()+":"+this.ipPort.getPort());
				}else{
					System.out.println("H2接口》》》》》》2/3G用户－－－－－");
					System.out.println("H2接口》》》》》》链接地址－－－－－"+this.ipPort.getIp()+":"+this.ipPort.getPort());
				}

				UserFlowInfoCommand command= new UserFlowInfoCommand(deviceNumber,searchDate,serviceType,businessType);
				ResponseResult result = SocketTool.remoteOperate(command.toString(), ipPort); 
				UserFlowInfoBean userFlowInfo=(UserFlowInfoBean) result.getData();
				System.out.println("剩余总流量:>>>>>>"+userFlowInfo.getTotal_last_flow());
			    errorCode = result.getErrorCode();
			    userFlowInfo.setResponseCode(errorCode);
			    return  userFlowInfo;
		 }catch (Exception e){
				e.printStackTrace();
				log.error("GREALFEE config interface failed,errorCode:"+e); 
				return null;                // xxxxx接口异常
			} 
    }
    
	public String toSearch() {
		 String errorCode = null;    //结果代码
		 try {
			 Properties properties =util.getProperties();
				H2Config.TIME_OUT_COUNT = Integer.valueOf(String.valueOf(properties.get("TimeOutCount")));
				H2Config.WAIT_TIME = Integer.valueOf(String.valueOf(properties.get("WaitTime")));
				H2Config.SEND_FREQ = Integer.valueOf(String.valueOf( properties.get("SendFreq")));
				H2Config.TOMCAT_H2TIME_BEGIN =Integer.valueOf(String.valueOf(properties.get("TOMCAT_H2_BEGIN_TIME")));
				H2Config.TOMCAT_H2TIME_END = Integer.valueOf(String.valueOf(properties.get("TOMCAT_H2_END_TIME"))); 
				IpPort ipPortObj=new IpPort(String.valueOf(properties.get("4G_SERVICE_IP")),Integer.valueOf(String.valueOf(properties.get("4G_SERVICE_PORT"))),"4GNET");
				IpPort.IpPortMap.put(ipPortObj.getServiceType(), ipPortObj);
				IpPort ipPortObj2=new IpPort(String.valueOf(properties.get("23G_SERVICE_IP")),Integer.valueOf(String.valueOf(properties.get("23G_SERVICE_PORT"))),"NOT4GNET");
				IpPort.IpPortMap.put(ipPortObj2.getServiceType(), ipPortObj2);
				
				if("T".equals(businessType)){
					this.ipPort = IpPort.IpPortMap.get("4GNET");
				}else{
					this.ipPort = IpPort.IpPortMap.get("NOT4GNET");
				}
				UserFlowInfoCommand command= new UserFlowInfoCommand(deviceNumber,searchDate,serviceType,businessType);
				ResponseResult result = SocketTool.remoteOperate(command.toString(), ipPort); 
				UserFlowInfoBean userFlowInfo=(UserFlowInfoBean) result.getData();
				System.out.println("剩余总流量:>>>>>>"+userFlowInfo.getTotal_last_flow());
			    errorCode = result.getErrorCode();
			    return  errorCode;
		 }catch (Exception e){
				e.printStackTrace();
				log.error("GREALFEE config interface failed,errorCode:"+e); 
				return "xxxxx";                // xxxxx接口异常
			} 
	}
}
