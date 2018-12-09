package com.bonc.h2.thread;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.bonc.h2.command.UserAccountInfoCommand;
import com.bonc.h2.pojo.H2Config;
import com.bonc.h2.pojo.ResponseResult;
import com.bonc.h2.pojo.UserAccountInfoBean;
import com.bonc.h2.socket.IpPort;
import com.bonc.h2.socket.SocketTool;
import com.bonc.utils.SystemHelper;

public class UserAccountInfo {
	Logger log = Logger.getLogger(UserAccountInfo.class); 
	private IpPort ipPort;
	private String deviceNumber = null;   //设备号码 
	private String searchType = null;   //查询类型
    private String businessType=null;   //是否时4G用户
    private SystemHelper util=new SystemHelper();
    public UserAccountInfo(String deviceNumber,String searchType,String businessType) {
    	this.deviceNumber = deviceNumber;
    	this.searchType=searchType;
    	this.businessType=businessType;
    }
    /**
     * 返回用户账期对象
     * @return
     */
    public UserAccountInfoBean getUserAccountInfo(){
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
				
				System.out.println(">>>>>>开始查询账户状态");
				if("T".equals(businessType)){
					System.out.println("H2接口》》》》》》4G用户－－－－－");
					System.out.println("H2接口》》》》》》链接地址－－－－－"+this.ipPort.getIp()+":"+this.ipPort.getPort());
				}else{
					System.out.println("H2接口》》》》》》2/3G用户－－－－－");
					System.out.println("H2接口》》》》》》链接地址－－－－－"+this.ipPort.getIp()+":"+this.ipPort.getPort());
				}
				
				UserAccountInfoCommand command= new UserAccountInfoCommand(deviceNumber,searchType,businessType);
				ResponseResult result = SocketTool.remoteOperate(command.toString(), ipPort); 
			    errorCode = result.getErrorCode();    //返回结果代码 （00000 设置成功 ）
			    System.out.println(">>>>>>开始查询账户状态》》。返回码"+errorCode);
			    UserAccountInfoBean userInfo=(UserAccountInfoBean) result.getData();
			    userInfo.setResponseCode(errorCode);
			    return userInfo;
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
				
				System.out.println(">>>>>>开始查询账户状态");
				UserAccountInfoCommand command= new UserAccountInfoCommand(deviceNumber,searchType,businessType);
				ResponseResult result = SocketTool.remoteOperate(command.toString(), ipPort); 
			    errorCode = result.getErrorCode();    //返回结果代码 （00000 设置成功 ）
			    log.debug("this is GREALFEE config,resultCode: "+errorCode);
			    UserAccountInfoBean userInfo=(UserAccountInfoBean) result.getData();
			    userInfo.setResponseCode(errorCode);
			    System.out.println(">>>>>"+userInfo.getDate());
			    System.out.println(">>>>>"+userInfo.getFee());
			    System.out.println(">>>>>"+userInfo.getBalance());
				return  errorCode;
		 }catch (Exception e){
				e.printStackTrace();
				log.error("GREALFEE config interface failed,errorCode:"+e); 
				return "xxxxx";                // xxxxx接口异常
			} 
	}


	public String getDeviceNumber() {
		return deviceNumber;
	}

	public String getSearchType() {
		return searchType;
	}
	
	
}
