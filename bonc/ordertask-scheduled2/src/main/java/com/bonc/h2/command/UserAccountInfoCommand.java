package com.bonc.h2.command;

public class UserAccountInfoCommand extends BaseCommand{
	private String searchType;

	private final String OPERATOR_23G = "Z000XXDH";
	private final String DEPARTMENT_23G = "Z000GH";

	private final String OPERATOR_4G = "Z0000XXDH";
	private final String DEPARTMENT_4G = "Z000XXDH";
	
	
	
	public UserAccountInfoCommand(String deviceNumber, String searchType,String deviceNumberType) {
		super();
		this.searchType = searchType;
		
		if("T".equals(deviceNumberType)){
			   head.setPlace(DEPARTMENT_4G);  
			   head.setOperateNumber(OPERATOR_4G);
			}else{
			   head.setPlace(DEPARTMENT_23G); 
			   head.setOperateNumber(OPERATOR_23G);
			}
			
			head.setServiceType("GREALFEE");
			head.setDeviceNumberType(deviceNumberType);
			head.setDeviceNumber(deviceNumber);
	}


	@Override
	public void buildBody() {
		// TODO Auto-generated method stub
		body = searchType + PACKAGE_END_FLAG;
		
	}
	
}
