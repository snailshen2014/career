package com.bonc.h2.command;

public class UserFlowInfoCommand extends BaseCommand{
	private String serviceType;
	private String searchDate;
	//01—	手机上网流量
	//02-3G上网卡流量
	private final String OPERATOR_23G = "Z000XXDH";
	private final String DEPARTMENT_23G = "Z000GH";

	private final String OPERATOR_4G = "Z0000XXDH";
	private final String DEPARTMENT_4G = "Z000XXDH";
	
	public UserFlowInfoCommand(String deviceNumber,String searchDate,String serviceType,String deviceNumberType){
		super();
		this.searchDate = searchDate;
		this.serviceType=serviceType;
		
		if(	"T".equals(deviceNumberType)){
			   head.setPlace(DEPARTMENT_4G);  
			   head.setOperateNumber(OPERATOR_4G);
			}else{
			   head.setPlace(DEPARTMENT_23G); 
			   head.setOperateNumber(OPERATOR_23G);
			}
			head.setServiceType("GFLUX");
			head.setDeviceNumberType(deviceNumberType);
			head.setDeviceNumber(deviceNumber);
	}
	
	@Override
	public void buildBody() {
		// TODO Auto-generated method stub
		body = searchDate+super.TABLE+serviceType + PACKAGE_END_FLAG;
	}

}
