package com.bonc.utils;


public class IContants {
	public static final int LOG_MODE=2;
	
	public static final String PUBLIC = "public";					// 营销类常量
	
	public static final String ALL_LINK = "all_link";
	public static final String ALL_TYPE = "all_type";
	public static final String BASE_URL = "base_url";

	//前缀PREFIX 后缀SUFFIX
	public static final String FIELD_PREFIX = "field_";												// 字段名称前缀
	public static final String PUBLIC_PREFIX = "public_";										// 字段名称前缀
	public static final String EXCHANGE_FIELD_SUFFIX = "_ex_field";	 	// 字段名称后缀
	public static final String ID_SUFFIX = "_id";														//
	public static final String DATA_URL_SUFFIX = "_data_url";	
	public static final String COUNT_URL_SUFFIX = "_count_url";	
	public static final String TITLE_SUFFIX = "_title";	
	public static final String NAME_SUFFIX = "_name";	
	public static final String FOMART_SUFFIX = "_fomart";				//时间格式配置后缀
	public static final String TIMEX_SUFFIX = "_timex_field";				//时间格式化配置后缀
	public static final String KEY_SUFFIX = "_key_field";						//字段主键后缀
	public static final String DETAIL_URL_SUFFIX ="_detail_url";
	public static final String LINK_SUFFIX ="_link";
	public static final String SPECIAL_LINK_SUFFIX ="_special_link";
	public static final String BAK_SUFFIX ="_BAK";
	public static final String CODE_SUFFIX =".code";
	
	//分隔符
	public static final String CO_S = ",";		//逗号分隔符
	public static final String UN_S = "_";		//下划线分隔符
	public static final String SP_S = "/";		//斜杠分隔符
	public static final String PO_S = "\\.";	//点分隔符
	public static final String DO_S = ".";		//点分隔符
	public static final String EQ_S = "=";		//点分隔符
	public static final String VL_S = "\\|";	//竖线分隔符
	public static final String NU_S = "--";	//空值符号
	
	public static final String TI_S = "/";		//如果为复用字段则惊醒title分隔
	
	//查询总数
	public static final String QUERY_COUNT = "query_count";
	
	//时间格式转换 form_date_fomart to_date_fomart
	public static final String FORM_DATE_FOMART = "form_date_fomart";	
	public static final String TO_DATE_FOMART = "to_date_fomart";

	public static final String PAGE_SIZE = "page_size";
	public static final int DEFAULT_PAGE_SIZE = 20;
	
	//字段显示模式配置
	public static final String FIELD_MODE = "field_mode";		//字段模式配置
	public static final String ALL_MODE = "all";							//全集模式
	public static final String SUB_MODE = "sub";						//交集模式
	public static final String PAR_MODE = "par";						//偏序模式
	public static final String USE_MODE = "use";						//自助模式
	//自主列表
	public static final String USE_LIST_SUFFIX = "use_list";						//自助列表后缀
	
	public static final String DO_REF = ".ref";						//自助列表后缀
	public static final String DO_SREF = ".sref";						//自助列表后缀
	public static final String DO_TIME = ".time";						//自助列表后缀
	public static final String DO_KEY = ".key";						//自助列表后缀
	public static final String DO_TYPE = ".type";						//自助列表后缀
	public static final String DO_API_LIST = ".api.list";						//自助列表后缀

	public static final Integer CODE_TYPE = 0;
	public static final Integer TIME_TYPE = 1;
	
	public static final String SOURCE_TYPE = "SOURCE_TYPE";						//数据源字段
	public static final String ZC_SMS = "ZC-SMS";						//数据源字段
	public static final String ZC_VOICE = "ZC-VOICE";						//数据源字段
	public static final String SWRZ="SWRZ-DWA_D_IA_BASIC_KEYWORD";
	public static final String CBSS_TRADE_A="CBSS-DWD_D_EVT_CB_TRADE_A";
	public static final String CBSS_TRADE_HIS_A="CBSS-DWD_D_EVT_CB_TRADE_HIS_A";
	public static final String CBSS_PAYLOG_D="CBSS-DWD_D_ACC_CB_PAYLOG_D";
	public static final String BSS="BSS-DWD_D_ACC_AL_PAY_LOG";
	public static final String ECS="ECS-ZB_D_EVT_MB_ECS_ORD_FLUX";
	public static final String ESS="ESS-ZB_D_EVT_MB_ES_MAIN";
	public static final String CLYX="CLYX-CLYX_MARKETING_TRACK_INFO";
	public static final String JZYX="JZYX-DM_JZYX_SMS_RECEIVE_T_MIDS";
	
	//接触类型
	public static final String ALERT="alert";
	public static final String CONSULT="consult";
	public static final String SALE="sale";
	public static final String BUSINESS="business";
	public static final String BEHAVE="behave";

	public static final String CODE_ERROR = "999999";
	public static final String MSG_ERROR = "失败";
	public static final String CODE_SUCCESS="000000";
	public static final String MSG_SUCCESS="成功";

}
