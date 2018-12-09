package com.bonc.busi.orderschedule.bo;

public class ActivityDailySummary {
	private String ACTIVITY_ID;
	private String ACTIVITY_TYPE;
	private String TENANT_ID;
	private String DEAL_MONTH;
	private String DEAL_DAY;
	private String CELL_ID;
	private String DAY_CNT;
	private String UNFINISH_TOTAL_CNT;
	private String FINISHED_TOTAL_CNT;
	private String TOTAL_CNT;
	
	public String  getACTIVITY_ID() {
		 return ACTIVITY_ID;
	 }
	 public void setACTIVITY_ID(String  id) {
		 this.ACTIVITY_ID = id;
	 }
	 public String  getACTIVITY_TYPE() {
		 return ACTIVITY_TYPE;
	 }
	 public void setACTIVITY_TYPE(String  type) {
		 this.ACTIVITY_TYPE = type;
	 }
	 public String  getTENANT_ID() {
		 return TENANT_ID;
	 }
	 public void setTENANT_ID(String  id) {
		 this.TENANT_ID = id;
	 }
	 public String  getDEAL_MONTH() {
		 return DEAL_MONTH;
	 }
	 public void setDEAL_MONTH(String  mon) {
		 this.DEAL_MONTH = mon;
	 }
	 public String  getDEAL_DAY() {
		 return DEAL_DAY;
	 }
	 public void setDEAL_DAY(String  day) {
		 this.DEAL_DAY = day;
	 }
	 public String  getCELL_ID() {
		 return CELL_ID;
	 }
	 public void setCELL_ID(String  id) {
		 this.CELL_ID = id;
	 }
	 public String  getDAY_CNT() {
		 return DAY_CNT;
	 }
	 public void setDAY_CNT(String  cnt) {
		 this.DAY_CNT = cnt;
	 }
	 public String  getUNFINISH_TOTAL_CNT() {
		 return UNFINISH_TOTAL_CNT;
	 }
	 public void setUNFINISH_TOTAL_CNT(String  cnt) {
		 this.UNFINISH_TOTAL_CNT = cnt;
	 }
	 public String  getFINISHED_TOTAL_CNT() {
		 return FINISHED_TOTAL_CNT;
	 }
	 public void setFINISHED_TOTAL_CNT(String  cnt) {
		 this.FINISHED_TOTAL_CNT = cnt;
	 }
	 public String  getTOTAL_CNT() {
		 return TOTAL_CNT;
	 }
	 public void setTOTAL_CNT(String  cnt) {
		 this.TOTAL_CNT = cnt;
	 }
}
