package com.bonc.busi.activity;

public class WebChatMidActivityPo {
	    //活动编码
        private String activityId;
        //公众号ID
        private String publicId;
        //公众号名称
        private String publicName;
        //公众号编码
        private String publicCode;
        //用户数
        private String userNum;
        //接收状态
        private String state;
        //接收时间
        private String acceptTime;
        //微信模板Id
        private String templateId;
        //接收人
        private String acceptUser;
        //停止接收时间
        private String stopTime;
        //租户Id
        private String tenantId;

		public String getPublicCode() {
			return publicCode;
		}

		public void setPublicCode(String publicCode) {
			this.publicCode = publicCode;
		}

		public String getPublicName() {
			return publicName;
		}

		public void setPublicName(String publicName) {
			this.publicName = publicName;
		}

		public String getActivityId() {
			return activityId;
		}

		public void setActivityId(String activityId) {
			this.activityId = activityId;
		}

		public String getPublicId() {
			return publicId;
		}

		public void setPublicId(String publicId) {
			this.publicId = publicId;
		}

		public String getUserNum() {
			return userNum;
		}

		public void setUserNum(String userNum) {
			this.userNum = userNum;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getAcceptTime() {
			return acceptTime;
		}

		public void setAcceptTime(String acceptTime) {
			this.acceptTime = acceptTime;
		}

		public String getTemplateId() {
			return templateId;
		}

		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}

		public String getAcceptUser() {
			return acceptUser;
		}

		public void setAcceptUser(String acceptUser) {
			this.acceptUser = acceptUser;
		}

		public String getStopTime() {
			return stopTime;
		}

		public void setStopTime(String stopTime) {
			this.stopTime = stopTime;
		}

		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}
        
}
