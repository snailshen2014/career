package com.bonc.busi.orderschedule.bo;
//user_tool_weights_conditions
public class WeightsCondition {
	//客户群编号
	 private String CI_WA_ID;
	 //客户群条件(FrontQuery String) clob
	 private String CONDITIONS;
	 //保存的下发SQL CLOB
	 private String SQL;
	
	 //创建者
	 private String CI_WA_CREATER;
	 //创建时间
	 private String CI_WA_CREATERNAME;
	 //客户群名称
	 private String CI_WA_NAME;
	 //创建时间
	 private String CI_WA_CREATEDATE;
	 //客户群描述
	 private String CI_WA_DESC;
	 //⽤户群最⼤大账期ID
	 private String CI_WA_MAX_MONTH_ID;
	 //查询条件描述
	 private String CI_WA_DESCONLY;
	 //复合条件
	 private String CI_WA_MULTICONDITION;
	 //拍照条件
	 private String CI_WA_PHOTOCONDITION;
	 //⽗父节点ID
	 private String CI_WA_PARENTID;
	 //表过滤对应维度ID
	 private String FILTER_DIMEID;
	 //表过滤对应表名
	 private String FILTER_TABLE;
	 //提取⽤用户群⽤用户数
	 private String USER_COUNT;
	 //导⼊入的客户群对应的表名
	 private String IMPORT_TABLE;
	 private String CREATE_TYPE;
	 
	 private String UPLOAD_TABLE_NAME;
	 private String UPLOAD_DIME_ID;
	 private String CUBE_ID;
	 private String TENANT_ID;
	 //创建者id
	 private String CI_WA_CREATERID;
	 private String PROVINCE_CODE;
	 private String CITY_CODE;
	 private String USER_NAME;
	 private String PARENT_CI_WA_ID;
	 private String CI_WA_INSTRUCTION;
	 private String ORGS;
	 
	 public String  getCI_WA_ID() {
		 return CI_WA_ID;
	 }
	 public void setCI_WA_ID(String  id) {
		 this.CI_WA_ID = id;
	 }
	 public String  getCONDITIONS() {
		 return CONDITIONS;
	 }
	 public void setCONDITIONS(String  cs) {
		 this.CONDITIONS = cs;
	 }
	 public String  getSQL() {
		 return SQL;
	 }
	 public void setSQL(String   sql) {
		 this.SQL = sql;
	 }
	 public String  getCI_WA_CREATER() {
		 return CI_WA_CREATER;
	 }
	 public void setCI_WA_CREATER(String  creater) {
		 this.CI_WA_CREATER = creater;
	 }
	 public String  getCI_WA_CREATERNAME() {
		 return CI_WA_CREATERNAME;
	 }
	 public void setCI_WA_CREATERNAME(String  date) {
		 this.CI_WA_CREATERNAME = date;
	 }
	 public String  getCI_WA_NAME() {
		 return CI_WA_NAME;
	 }
	 public void setCI_WA_NAME(String  name) {
		 this.CI_WA_NAME = name;
	 }
	 public String  getCI_WA_CREATEDATE() {
		 return CI_WA_CREATEDATE;
	 }
	 public void setCI_WA_CREATEDATE(String  date) {
		 this.CI_WA_CREATEDATE = date;
	 }
	 public String  getCI_WA_DESC() {
		 return CI_WA_DESC;
	 }
	 public void setCI_WA_DESC(String  desc) {
		 this.CI_WA_DESC = desc;
	 }
	 public String  getCI_WA_MAX_MONTH_ID() {
		 return CI_WA_MAX_MONTH_ID;
	 }
	 public void setCI_WA_MAX_MONTH_ID(String  monthid) {
		 this.CI_WA_MAX_MONTH_ID = monthid;
	 }
	 public String  getCI_WA_DESCONLY() {
		 return CI_WA_DESCONLY;
	 }
	 public void setCI_WA_DESCONLY(String  con) {
		 this.CI_WA_DESCONLY = con;
	 }
	 public String  getCI_WA_MULTICONDITION() {
		 return CI_WA_MULTICONDITION;
	 }
	 public void setCI_WA_MULTICONDITION(String  con) {
		 this.CI_WA_MULTICONDITION = con;
	 }
	 public String  getCI_WA_PHOTOCONDITION() {
		 return CI_WA_PHOTOCONDITION;
	 }
	 public void setCI_WA_PHOTOCONDITION(String  con) {
		 this.CI_WA_PHOTOCONDITION = con;
	 }
	 public String  getCI_WA_PARENTID() {
		 return CI_WA_PARENTID;
	 }
	 public void setCI_WA_PARENTID(String  pid) {
		 this.CI_WA_PARENTID = pid;
	 }
	 public String  getFILTER_DIMEID() {
		 return FILTER_DIMEID;
	 }
	 public void setFILTER_DIMEID(String  filter) {
		 this.FILTER_DIMEID = filter;
	 }
	 public String  getFILTER_TABLE() {
		 return FILTER_TABLE;
	 }
	 public void setFILTER_TABLE(String  table) {
		 this.FILTER_TABLE = table;
	 }
	 public String  getUSER_COUNT() {
		 return USER_COUNT;
	 }
	 public void setUSER_COUNT(String  cnt) {
		 this.USER_COUNT = cnt;
	 }
	 public String  getIMPORT_TABLE() {
		 return IMPORT_TABLE;
	 }
	 public void setIMPORT_TABLE(String  imptab) {
		 this.IMPORT_TABLE = imptab;
	 }
	 public String  getCREATE_TYPE() {
		 return CREATE_TYPE;
	 }
	 public void setCREATE_TYPE(String  tp) {
		 this.CREATE_TYPE = tp;
	 }
	 public String  getUPLOAD_TABLE_NAME() {
		 return UPLOAD_TABLE_NAME;
	 }
	 public void setUPLOAD_TABLE_NAME(String  tp) {
		 this.UPLOAD_TABLE_NAME = tp;
	 }
	 public String  getUPLOAD_DIME_ID() {
		 return UPLOAD_DIME_ID;
	 }
	 public void setUPLOAD_DIME_ID(String  ud) {
		 this.UPLOAD_DIME_ID = ud;
	 }
	 public String  getCUBE_ID() {
		 return CUBE_ID;
	 }
	 public void setCUBE_ID(String  cid) {
		 this.CUBE_ID = cid;
	 }
	 public String  getTENANT_ID() {
		 return TENANT_ID;
	 }
	 public void setTENANT_ID(String  tid) {
		 this.TENANT_ID = tid;
	 }
	 public String  getCI_WA_CREATERID() {
		 return CI_WA_CREATERID;
	 }
	 public void setCI_WA_CREATERID(String  tid) {
		 this.CI_WA_CREATERID = tid;
	 }
	 public String  getPROVINCE_CODE() {
		 return PROVINCE_CODE;
	 }
	 public void setPROVINCE_CODE(String  code) {
		 this.PROVINCE_CODE = code;
	 }
	 public String  getCITY_CODE() {
		 return CITY_CODE;
	 }
	 public void setCITY_CODE(String  code) {
		 this.CITY_CODE = code;
	 }
	 public String  getUSER_NAME() {
		 return USER_NAME;
	 }
	 public void setUSER_NAME(String  name) {
		 this.USER_NAME = name;
	 }
	 public String  getPARENT_CI_WA_ID() {
		 return PARENT_CI_WA_ID;
	 }
	 public void setPARENT_CI_WA_ID(String  id) {
		 this.PARENT_CI_WA_ID = id;
	 }
	 public String  getCI_WA_INSTRUCTION() {
		 return CI_WA_INSTRUCTION;
	 }
	 public void setCI_WA_INSTRUCTION(String  ins) {
		 this.CI_WA_INSTRUCTION = ins;
	 }
	 public String  getORGS() {
		 return ORGS;
	 }
	 public void setORGS(String  ogrs) {
		 this.ORGS = ogrs;
	 }
}
