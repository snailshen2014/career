package com.bonc.h2;

import com.bonc.h2.thread.UserAccountInfo;
import com.bonc.h2.thread.UserFlowInfo;
import com.bonc.h2.thread.UserStatusInfo;

public class H2PortMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		UserAccountInfo uai=new UserAccountInfo("13213153720","0","T");
//		String result =uai.toSearch();
//		System.out.println(">>>>>>"+result);
//		
//		UserStatusInfo usi = new UserStatusInfo("13213153720","T");
//		String result2 = usi.toSearch();
//		System.out.println(">>>>>>"+result2);

		UserFlowInfo ufi=new UserFlowInfo("13213153720","201612","02","G");
		String result3 = ufi.toSearch();
		System.out.println(">>>>>>"+result3);
	}

}
