package com.bonc.utils;


public class IContants {
	
	public static final String MSG_ERROR = "失败";
	public static final String MSG_SUCCESS="成功";
	public static final String CODE_SUCCESS="000000";
	public static final String CODE_FAIL="000001";
	public static final String SYSTEM_ERROR_CODE = "999999";
	public static final String BUSI_ERROR_CODE="888888";
	public static final String SYSTEM_ERROR_MSG = "系统异常";
	public static final String BUSI_ERROR_MSG="业务异常";
	
	//渠道 编码
	public static final String DX_CHANNEL = "7";//短信渠道
	public static final String WX_CHANNEL = "11";//微信渠道
	public static final String TC_CHANNEL = "8";//弹窗渠道
	public static final String TC_CHANNEL_1 = "81";//弹窗自有渠道
	public static final String TC_CHANNEL_2 = "82";//弹窗社会渠道
	public static final String WT_CHANNEL = "2";//网厅渠道
	public static final String ST_CHANNEL = "1";//手厅渠道
	public static final String WSC_CHANNEL = "9";//沃视窗渠道
	public static final String YX_CHANNEL = "5";//客户经理（一线）渠道
	
	public static final String DO_SPLIT = ".";//.分隔符
	public static final String CO_SPLIT = ",";//,分隔符
	public static final String SPACE = " ";//空格分隔
	public static final String SQ_SPLIT = "'";//单引号分隔
	
	public static String SMS_PATH="D://";	//短息发送批量文件路径
	public static final String SMS_SPLIT="`";	//短息文件发送分隔符
	public static final String FTP_URL = "ftp.url";
	public static final String FTP_PORT = "ftp.port";
	public static final String FTP_USERNAME = "ftp.username";
	public static final String FTP_PASSWORD = "ftp.password";
	public static final String FTP_FILE_PATH = "ftp.file.path";
	
	public static final Object SYS_CODE = "hnclyx";
	
	public static final String DATE_FORMAT = "yyyyMMdd";
	
	/**
	 * 码表TYPE值
	 */
	public static final String KD_CODE = "KD";
	public static final String MB_CODE = "MB";
	public static final String KM_CODE = "KM";
	public static final String KD_FLAG = "1";
	public static final String MB_FLAG = "0";
	
	public static final String ORDER_QUERY_PAMA="ORDER_QUERY_FIELD_MAP";
	public static final String XCLOUD_TABLE = "XLOUD_CODE_TABLE";
	public static final String FIELD_VALUE = "FIELD_VALUE";
	public static final String FIELD_KEY = "FIELD_KEY";
	public static final String TALK_VAR = "TALK_VARIABLE";
	public static final String TALK_ACTUAL_VAR = "TALK_VARIABLE_ACTUAL";
	public static final String USER_CODE = "USER_STATISTIC_CODE";
	public static final String USER_MODIFY_FIELDS = "USER_MODIFY_FIELDS";
	
	public static final String ORDER_QUERY_FIELDS_KD = "KD_ORDER_QUERY_RETURN_FIELDS";
	public static final String ORDER_QUERY_FIELDS_MB = "MB_ORDER_QUERY_RETURN_FIELDS";
	public static final String ORDER_QUERY_FIELDS = "ORDER_QUERY_RETURN_FIELDS";
	public static final String ORDER_QUERY_ITEMS_CODE = "ORDER_QUERY_ITEMS_CODE";
	
	public static final String USER_EXT_SELECT = "USER_EXT_SELECT";
	public static final String SELECT_FIELD = "SELECT_FIELD";
	//用户行云码表
	public static final String PLT_USER_LABEL = "PLT_USER_LABEL";
	//本地码表
//	public static final String LOCAL_USER_CODE = "LOCAL_USER_CODE";
	
	public static final String USER_MAP_FIELD = "USER_MAP_FIELD";
	public static final String MAP_FIELD = "MAP_FIELD";
	
	//不同渠道对应表名的码表
	public static final String CHANNEL_ORDER_TABLE = "CHANNEL_ORDER_TABLE";
	
	public static final String PLT_ORDER_INFO = "PLT_ORDER_INFO";
	
	public static final String CONTACT_CODE = "CONTACT_CODE";
	
	public static final String DIM_CHANNEL_DESC = "DIM_CHANNEL_DESC";
	
	/**
	 * 短信服务接口
	 */
	public static final String sendlist = "sms/sendlist";
	public static final String findErrorSms = "sms/findErrorSms";
	public static final String sendfile = "sms/sendfile";
	public static final String tenantid = "smsSet/tenantid";
	public static final String sendsingle = "sms/sendsingle";
	public static final String statistics = "statistics";
	
	public static String SMS_SEND_TASK="0";	//短信发送锁
	public static String SMS_STATC_TASK="0";	//短信同步统计锁
	
	
	public static String ORDER_DATE_FLAG="0"; //工单生失效时间标识
	
	
	public static final String CHANNEL_STATUS_WAIT = "0";//待办
	public static final String CHANNEL_STATUS_PRE = "1";//预发布
	public static final String CHANNEL_STATUS_IN = "2";//办理中
	public static final String CHANNEL_STATUS_END = "3";//已办
	public static final String CHANNEL_STATUS_FAIL = "5";//失败
	
	public static final String ORDER_STATUS_GET = "5";//工单已经准备好
	
	
}
