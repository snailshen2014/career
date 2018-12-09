package com.bonc.h2.command;
/**
 * 
 * @author quechao
 *协议头
 */
public class CommandHead {
	private String version; // 协议版本 A0
	private String packageLength; //数据包大小 A1
	private String number; // 流水号 A2
	private String flag; // 标志位 A3
	private String serviceType;//服务类型 A4
	private String deviceNumber;//业务号码 A5
	private String deviceNumberType;//号码类型 A6
	private String place;// 业务受理地点 A7
	private String operateNumber;//业务受理人A8；
	private String packageNumber;//包编号A9；
	private String endFlag; //最后一个包标志A10；
	private String errorCode;//错误标志 A11;
	
	
	/**
	 * @return 返回 deviceNumber 业务号码 A5 。
	 */
	public String getDeviceNumber() {
		return deviceNumber;
	}
	
	
	/**
	 * @param deviceNumber 要设置的 deviceNumber 业务号码 A5。
	 */
	public void setDeviceNumber(String deviceNumber) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("                    ");
		buffer.replace(0,deviceNumber.length(),deviceNumber);
		this.deviceNumber = buffer.toString();
	}
	
	
	/**
	 * @return 返回 deviceNumberType 号码类型 A6。
	 */
	public String getDeviceNumberType() {
		return deviceNumberType;
	}
	
	
	/**
	 * @param deviceNumberType 要设置的 deviceNumberType 号码类型 A6。
	 */
	public void setDeviceNumberType(String deviceNumberType) {
		this.deviceNumberType = deviceNumberType;
	}
	
	
	/**
	 * @return 返回 endFlag 最后一个包标志A10。
	 */
	public String getEndFlag() {
		return endFlag;
	}
	
	
	/**
	 * @param endFlag 要设置的 endFlag 最后一个包标志A10。
	 */
	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}
	
	
	/**
	 * @return 返回 errorCode。
	 */
	public String getErrorCode() {
		return errorCode;
	}
	
	
	/**
	 * @param errorCode 要设置的 errorCode。
	 */
	public void setErrorCode(String errorCode) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("     ");
		buffer.replace(0,errorCode.length(),errorCode);
		this.errorCode = buffer.toString();
	}
	
	
	/**
	 * @return 返回 flag。
	 */
	public String getFlag() {
		return flag;
	}
	
	
	/**
	 * @param flag 要设置的 flag。
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
	/**
	 * @return 返回 number 流水号 A2。
	 */
	public String getNumber() {
		return number;
	}
	
	
	/**
	 * @param number 要设置的 number 流水号 A2。
	 */
	public void setNumber(String number) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("                    ");
		buffer.replace(0,number.length(),number);
		this.number = buffer.toString();
	}
	
	
	/**
	 * @return 返回 operateNumber 业务受理人A8。
	 */
	public String getOperateNumber() {
		return operateNumber;
	}
	
	
	/**
	 * @param operateNumber 要设置的 operateNumber 业务受理人A8。
	 */
	public void setOperateNumber(String operateNumber) {
		if(operateNumber.length()>20){
			operateNumber = operateNumber.substring(0,20);
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("                    ");
		buffer.replace(0,operateNumber.length(),operateNumber);
		this.operateNumber = buffer.toString();
		
	}
	
	
	/**
	 * @return 返回 packageLength。
	 */
	public String getPackageLength() {
		return packageLength;
	}
	
	
	/**
	 * @param packageLength 要设置的 packageLength。
	 */
	public void setPackageLength(String packageLength) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("     ");
		buffer.replace(0,packageLength.length(),packageLength);
		this.packageLength = buffer.toString();
	}
	
	
	/**
	 * @return 返回 packageNumber 包编号A9。
	 */
	public String getPackageNumber() {
		return packageNumber;
	}
	
	
	/**
	 * @param packageNumber 要设置的 packageNumber 包编号A9。
	 */
	public void setPackageNumber(String packageNumber) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("     ");
		buffer.replace(0,packageNumber.length(),packageNumber);
		this.packageNumber = buffer.toString();
	}
	
	
	/**
	 * @return 返回 place 业务受理地点 A7。
	 */
	public String getPlace() {
		return place;
	}
	
	
	/**
	 * @param place 要设置的 place 业务受理地点 A7。
	 */
	public void setPlace(String place) {
		StringBuffer buffer = new StringBuffer();
//		buffer.append("      ");
		buffer.append("          "); //A7 营业点由6位变为10位，内容固定为XXDH
		buffer.replace(0,place.length(),place);
		this.place = buffer.toString();
	}
	
	
	/**
	 * @return 返回 serviceType 服务类型 A4。
	 */
	public String getServiceType() {
		return serviceType;
	}
	
	
	/**
	 * @param serviceType 要设置的 serviceType 服务类型 A4。
	 */
	public void setServiceType(String serviceType) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("            ");
		buffer.replace(0,serviceType.length(),serviceType);
		this.serviceType = buffer.toString();
	}
	
	
	/**
	 * @return 返回 version。
	 */
	public String getVersion() {
		return version;
	}
	
	
	/**
	 * @param version 要设置的 version。
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getVersion());
		buffer.append(getPackageLength());
		buffer.append(getNumber());
		buffer.append(getFlag());
		buffer.append(getServiceType());
		buffer.append(getDeviceNumber());
		buffer.append(getDeviceNumberType());
		buffer.append(getPlace());
		buffer.append(getOperateNumber());
		buffer.append(getPackageNumber());
		buffer.append(getEndFlag());
		buffer.append(getErrorCode());
		return buffer.toString();
	}
}