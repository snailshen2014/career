package com.bonc.h2.command;
/**
 * 应答包
 * @author quechao
 *
 */
public class ResponseHead { 
	private byte[] headData;//数据包包头数据流
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
	
	
	public ResponseHead( byte[] headData){
		this.headData = headData;
	}
	
	/**
	 * 解析应答包头
	 *
	 */
	public void AnalyseHead(){
		byte[] byte1 = new byte[1];
		byte[] byte2 = new byte[2];
		byte[] byte5 = new byte[5];
		byte[] byte6 = new byte[6];
		byte[] byte10 = new byte[10];//
		byte[] byte8 = new byte[8];
		byte[] byte12 = new byte[12];
		byte[] byte20 = new byte[20];
		//读版本号
		BytesCopy(headData, byte2, 0, 2,0);
		this.setVersion(new String(byte2));
		//读数据包大小
		BytesCopy(headData, byte5, 2, 5,0);
		this.setPackageLength(new String(byte5).trim());
		//读流水号
		BytesCopy(headData, byte20, 7, 20,0);
		this.setNumber(new String(byte20).trim());
		//读成功标志
		BytesCopy(headData, byte1, 27, 1,0);
		this.setFlag(new String(byte1));
		//读服务类型
		BytesCopy(headData, byte12, 28, 12,0);
		this.setServiceType(new String(byte12).trim());
		//读电话号码
		BytesCopy(headData, byte20, 40, 20,0);
		this.setDeviceNumber(new String(byte20).trim());
		//读业务号码类型
		BytesCopy(headData, byte1, 60, 1,0);
		this.setDeviceNumberType(new String(byte1));
		//读营业点
		BytesCopy(headData, byte10, 61, 10,0);
		this.setPlace(new String(byte10).trim());
		//读营业员
		BytesCopy(headData, byte20, 71, 20,0);
		this.setOperateNumber(new String(byte20).trim());
		//读包编号
		BytesCopy(headData, byte5, 91, 5,0);
		this.setPackageNumber(new String(byte5).trim());
		//读最后一包标志
		BytesCopy(headData, byte1, 96, 1,0);
		this.setEndFlag(new String(byte1).trim());
		//读错误码
		BytesCopy(headData, byte5, 97, 5,0);
		this.setErrorCode(new String(byte5).trim());
	}
	
	
	/**
	 * @return 返回 deviceNumber。
	 */
	public String getDeviceNumber() {
		return deviceNumber;
	}
	
	
	/**
	 * @param deviceNumber 
	 */
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	
	
	/**
	 * @return 返回 deviceNumberType。
	 */
	public String getDeviceNumberType() {
		return deviceNumberType;
	}
	
	
	/**
	 * @param deviceNumberType 
	 */
	public void setDeviceNumberType(String deviceNumberType) {
		this.deviceNumberType = deviceNumberType;
	}
	
	
	/**
	 * @return 返回 endFlag。
	 */
	public String getEndFlag() {
		return endFlag;
	}
	
	
	/**
	 * @param endFlag 
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
	 * @param errorCode 
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	
	/**
	 * @return 返回 flag。
	 */
	public String getFlag() {
		return flag;
	}
	
	
	/**
	 * @param flag 
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
	/**
	 * @return 返回 number。
	 */
	public String getNumber() {
		return number;
	}
	
	
	/**
	 * @param number 
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	
	
	/**
	 * @return 返回 operateNumber。
	 */
	public String getOperateNumber() {
		return operateNumber;
	}
	
	
	/**
	 * @param operateNumber 
	 */
	public void setOperateNumber(String operateNumber) {
		this.operateNumber = operateNumber;
	}
	
	
	/**
	 * @return 返回 packageLength。
	 */
	public String getPackageLength() {
		return packageLength;
	}
	
	
	/**
	 * @param packageLength 
	 */
	public void setPackageLength(String packageLength) {
		this.packageLength = packageLength;
	}
	
	
	/**
	 * @return 返回 packageNumber。
	 */
	public String getPackageNumber() {
		return packageNumber;
	}
	
	
	/**
	 * @param packageNumber 
	 */
	public void setPackageNumber(String packageNumber) {
		this.packageNumber = packageNumber;
	}
	
	
	/**
	 * @return 返回 place。
	 */
	public String getPlace() {
		return place;
	}
	
	
	/**
	 * @param place 
	 */
	public void setPlace(String place) {
		this.place = place;
	}
	
	
	/**
	 * @return 返回 serviceType。
	 */
	public String getServiceType() {
		return serviceType;
	}
	
	
	/**
	 * @param serviceType 
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	
	/**
	 * @return 返回 version。
	 */
	public String getVersion() {
		return version;
	}
	
	
	/**
	 * @param version 
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	
	/**
	 * 实现字节拷贝
	 * @param abyte0
	 * @param abyte1
	 * @param i 源起始
	 * @param j 拷贝字节数
	 * @param k 目标起始
	 */
	public static void BytesCopy(byte from[], byte to[], int i, int j,
			int k) {
		int m = 0;
		for (int l = i; l < j+i; l++) {
			to[k + m] = from[l];
			m++;
		}

	}
}