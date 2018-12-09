package com.bonc.h2.socket;

import java.util.HashMap;

/**
 * 
 * @author quechao 2008-04-02
 * 把服务端IP、端口、服务代码放在一起
 * 
 */
public class IpPort {
	public static HashMap<String,IpPort> IpPortMap = new HashMap<String,IpPort>();
	private String ip;
	private int port;
	private String serviceType;// 服务代码

	public IpPort(String ip, int port, String serviceType) {
		this.ip = ip;
		this.port = port;
		this.serviceType = serviceType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

}