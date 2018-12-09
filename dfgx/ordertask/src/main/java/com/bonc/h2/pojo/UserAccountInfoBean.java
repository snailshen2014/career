package com.bonc.h2.pojo;

public class UserAccountInfoBean {
	//当前帐期
	private String Date="0";
	//实时话费 金额单位元
	private String Fee="0";
	//信用额度 金额单位为元
	private String CreditNums;
	//是否欠费  N－否，Y－是
	private String isOwe;
	//话费余额 (参与信控的预存款余额，欠费时为0)
	private String balance;
	
	//返回结果代码 （00000 设置成功 ）
	private String responseCode;

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getFee() {
		//分转元
		String res="--";
		if(Fee!=null){
			int fee_int=Integer.valueOf(Fee);
			double tra = (Double.valueOf(fee_int))/100.00;
			java.text.DecimalFormat df =new java.text.DecimalFormat("#.##");  
			res = df.format(tra);
		}
		
		return res;
	}

	public void setFee(String fee) {
		Fee = fee;
	}

	public String getCreditNums() {
		return CreditNums;
	}

	public void setCreditNums(String creditNums) {
		CreditNums = creditNums;
	}

	public String getIsOwe() {
		return isOwe;
	}

	public void setIsOwe(String isOwe) {
		this.isOwe = isOwe;
	}

	public String getBalance() {
		//分转元
		String res="--";
		if(balance!=null){
			int balance_int=Integer.valueOf(balance);
			double tra = (Double.valueOf(balance_int))/100.00;
			java.text.DecimalFormat df =new java.text.DecimalFormat("#.##");  
			res = df.format(tra);
		}
		return res;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	
}
