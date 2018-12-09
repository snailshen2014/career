package com.bonc.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: Constants
 * @Description: 定义相关常量
 * @author: LiJinfeng
 * @date: 2016年11月20日 下午4:26:59
 */
public interface Constants {
	
	
    //=====================================时间格式相关变量============================================		
	public static final String REMARK_ORDER_DATE_FORMAT = "yyyy年MM月dd日";
	//关联ID码表中时间格式
	public static final String SYS_TIME_FORMAT = "yyyyMMddHH";
	//一级渠道回执表中的账期时间格式
	public static final String YJQD_HZ_DATE_FORMAT = "yyyyMMdd";
	//一级渠道回执时间格式补全
	/*public static final String YJQD_HZ_DATE_ADD = "000000";*/
	//微信渠道回执表中的回执时间格式
	public static final String WX_HZ_DATE_FORMAT_BEFORE = "yyyy-MM-dd HH:mm:ss";
	public static final String WX_HZ_DATE_FORMAT_AFTER = "yyyyMMddHHmmss";
	
	
	
	//=====================================微信渠道常量============================================		
	//有效租户状态值
	public static final String TENANT_VALID_STATE = "1";
	
	//租户ID前缀
	public static final String TENANT_ID_PREFIX = "uni";
	
	//实时接口无数据提示信息
	public static final String NULL_PROMPT = "(暂无信息)";
	
	//流量单位
	public static final String FLOW_UNIT = "MB";
	
	//分页大小
	public static final String HZ_PAGESIZE = "hz.pageSize";
	public static final String WX_SENT_PAGESIZE = "wx.sent.pageSize";
	public static final String WX_INSERT_SIZE = "wx.insert.size";
	
	//码表相关
	public static final String XCLOUD_TABLE = "XCLOUD_CODE_TABLE";
	public static final String FIXED_TABLE = "FIXED_CODE_TABLE";
	public static final String TALK_VAR = "TALK_VAR";
	public static final String FIELD_VALUE = "fieldValue";
	public static final String FIELD_KEY = "fieldKey";
	
	
	//base库中相关常量
	public static final String VALUE_VAR = "VALUE_VARIABLE";
	//dateTime时间类型转换
	public static final String DATE_VAR = "DATE_VARIABLE";
	//月类型
	public static final String STRING_MONTH_DATE_VARIABLE = "STRING_MONTH_DATE_VARIABLE";
	//日类型
	public static final String STRING_DATE_DATE_VARIABLE = "STRING_DATE_DATE_VARIABLE";
	//秒类型
	public static final String STRING_SECOND_DATE_VARIABLE = "STRING_SECOND_DATE_VARIABLE";
	//百分比类型
	public static final String PERCENT_VARIABLE = "PERCENT_VARIABLE";
	//话术列表
	/*public static final String REMARK_VAR = "REMARK_VARIABLE";*/
	//金额列表
	public static final String FEE_VAR = "FEE_VARIABLE";
	//实时列表
	public static final String REALFLOW_VARIABLE = "REALFLOW_VARIABLE";
	public static final String REALPRODUCT_VARIABLE = "REALPRODUCT_VARIABLE";
    //产品字段映射map
	public static final String PRODUCT_FIELD_JSON = "PRODUCT_FIELD_JSON";
	//环境标识
	public static final String ENVIRONMENT_FLAG = "ENVIRONMENT_FLAG";
	//实时接口URL
	public static final String XBUILDERORACLE_REALFLOW = "XBUILDERORACLE_REALFLOW";
	public static final String XBUILDERORACLE_REALPRODUCT = "XBUILDERORACLE_REALPRODUCT";
	//话术变量接口URL
	public static final String ACTIVITYINTER_ACTHUASHU = "ACTIVITYINTER_ACTHUASHU";
	//产品接口URL
	public static final String ACTIVITYINTER_PRODUCTINFO = "ACTIVITYINTER_PRODUCTINFO";
	//必输字段验证
	public static final String WX_ACTIVITY_INFO_FIELDS = "WX_ACTIVITY_INFO_FIELDS";
	public static final String WX_ORDER_INFO_FIELDS = "WX_ORDER_INFO_FIELDS";
	public static final String WX_PRODUCT_INFO_FIELDS_CBSS = "WX_PRODUCT_INFO_FIELDS_CBSS";
	public static final String WX_PRODUCT_INFO_FIELDS_BSS = "WX_PRODUCT_INFO_FIELDS_BSS";
	//list分隔符
	public static final String SEPARATOR = ",";
	
	
	//模板对应字段来源
	//常量
	public static final String FIELD_CONSTANT = "01";
	//工单信息表
	public static final String FIELD_OI = "02";
	//用户标签表
	public static final String FIELD_UL = "03";
	
	//fieldJson相关常量
	//颜色键值对的键
	public static final String COLOR_KEY = "color";
	//颜色键值对的值
	public static final String COLOR_VALUE = "coloryanse";
	//value键值对的键
	public static final String VALUE_VALUE = "value";
	//微信话术对应的键名
	public static final String WX_REMARK = "remark";
	
	//网别相关常量
	//2\3\4G
	public static final String NET_TYPE_TW = "2";
	public static final String NET_TYPE_TH = "3";
	public static final String NET_TYPE_FO = "4";
	//预付费、后付费
	public static final String NET_TYPE_POST = "01";
	public static final String NET_TYPE_Z_PRE = "02";
	public static final String NET_TYPE_PRE = "03";
	//网别转码
	public static final Integer NET_TYPE_TW_POST = 10;
	public static final Integer NET_TYPE_TW_PRE = 16;
	public static final Integer NET_TYPE_TH_POST = 33;
	public static final Integer NET_TYPE_TH_PRE = 17;
	public static final Integer NET_TYPE_FO_ALL = 50;
	
	//活动有相关常量
	//活动没有产品标识
	public static final Integer ACTIVITY_NO_PRODUCT = 0;
	//活动有产品标识
	public static final Integer ACTIVITY_YES_PRODUCT = 1;
	//活动渠道状态列表
	public static final String ACTIVITY_CHANNEL_READY_STATUS = "0";
	public static final String ACTIVITY_CHANNEL_SUCCESS_STATUS = "1";
	//有效活动状态列表
	public static final List<Integer> ACTIVITY_STATUS_LIST = Arrays.asList(1,8,9);
	
    //微信公众号相关常量
    public static final String WX_PUBLIC_PUBLICID = "publicId";
    public static final String WX_PUBLIC_PUBLICCODE = "publicCode";
    //微信公众号接收状态（允许接收）
    public static final String WX_PUBLIC_STATUS = "02";
    //用户关注微信公众号状态值
    public static final String WX_PUBLIC_FOLLOW = "1";
    
    //产品相关常量
    //bss与cbss的编码
    public static final String NET_TYPE_BSS = "01";
    public static final String NET_TYPE_CBSS = "02";
    //bss产品编码前缀
    public static final String BSS_PREFIX = "bss_bonc_";
    //流量包类型
  	public static final int FLOW_TYPE_DAY = 2;  //日流量包
    
	//工单状态值
	public static final String WX_CHANNEL_STATUS_SEND_SUCCESS = "2";  //成功
	public static final String WX_CHANNEL_STATUS_SEND_FAIL = "5";  //失败
	/*public static final String DX_CHANNEL_STATUS_MUTEX = "403";  //互斥失效*/
	
	
	
	
    //=====================================一级渠道相关变量============================================	
	//一级渠道回执成功状态值
	public static final Integer YJQD_BACKINFO_SUCCESS= 1;
	public static final Integer YJQD_BACKINFO_FALSE = 0;
	
	//一级渠道下属渠道编码列表
	public static final String YJQD_CHANNEL_LIST = "yjqd.channel.list";
	
	//短信微信互斥发送状态值
	/*public static final String IS_MUTEX = "0";*/
	
	public static final String CHANNEL_STATUS_SUCCESS = "1";
	public static final String CHANNEL_STATUS_ERROR = "0";
	public static final String ORDER_STATUS_READY = "5";
	public static final String CHANNEL_STATUS_READY = "0";
	
	public static final String CHANNEL_STATUS_PROCCESS = "3";
	
	public static final String CHANNEL_STATUS_RESEND = "8";
	
	public static final String CHANNEL_STATUS_HUNG = "9";
	
	
	
	//=====================================回执常量============================================	
	//一级渠道表名前缀
	public static final String TABLE_NAME_PREFIX = "DWA_CLJY_D_CONTACT_INFO";
	public static final String TABLE_FIELD_ST = "IS_GWAP_CNT";
	public static final String TABLE_FIELD_WT = "IS_GNET_CNT";
	public static final String TABLE_FIELD_WSC = "IS_GWOW_CNT";
	
	//一级渠道接触状态
	public static final String YJQD_CONTACT_FAIL = "yjqd.contact.fail";
	public static final String YJQD_CONTACT_SUCCESS_NO_CLICK = "yjqd.contact.success.no.click";
	public static final String YJQD_CONTACT_SUCCESS_CLICK_SUCCESS = "yjqd.contact.success.click.success";
	public static final String YJQD_CONTACT_SUCCESS_CLICK_FAIL = "yjqd.contact.success.click.fail";
	public static final String YJQD_CONTACT_SUCCESS_ORDER_SUCCESS = "yjqd.contact.success.order.success";
	
	//回执接口返回参数名称
	public static final String RESULT_CODE = "code";	
	public static final String RESULT_MESSAGE = "msg";
	public static final String RESULT_CODE_SUCCESS = "000000";
	//回执处理接口参数名称列表
	public static final String FEEDBACK_SEND_PRIVATE_FIELDLIST = "feedBack.send.private.field.list";
	public static final String FEEDBACK_SEND_PUBLIC_FIELDLIST = "feedBack.send.public.field.list";
	//一级渠道本地字段前缀
	public static final String LOCAL_WX_PREFIX= "local.wx.";
	//微信渠道本地字段前缀
    public static final String LOCAL_YJQD_PREFIX= "local.yjqd.";
    //公共字段前缀
    public static final String LOCAL_PUBLIC_PREFIX= "local.public.";
    
    
    
   
	//=====================================已废弃常量============================================	
	public static final String DO_SPLIT = ".";
	
	//必输字段验证
	/*public static final List<String> WX_ACTIVITY_INFO_FIELDS = Arrays.asList("recId","activityId","activityName",
			"tenantId","webChatInfo");
	public static final List<String> WX_ORDER_INFO_FIELDS = Arrays.asList("orderId","telInt","startTime","endTime",
			"netType","openId");
	public static final List<String> WX_PRODUCT_INFO_FIELDS = Arrays.asList("productType","price","SAPName","SAPId",
			"flowType","extraProductType","order_flag","productDesc","elementId");
	public static final List<String> WX_PRODUCT_INFO_FIELDS_CBSS = Arrays.asList("SAPName","SAPId","flowType","netType",
			"extraProductType","order_flag");
	public static final List<String> WX_PRODUCT_INFO_FIELDS_BSS = Arrays.asList("SAPName","SAPId","flowType","netType",
			"order_flag");*/
	
	//微信渠道接触状态
	/*public static final String WX_ORDER_SUCCESS = "wx.order.success";
	public static final String WX_ORDER_FAIL = "wx.order.fail";
	public static final String WX_CONTACT_SUCCESS = "wx.contact.success";*/
	
	/*//是否接触标识
	public static final String CONTACT_STATUS_SUCCESS = "1";
	public static final String CONTACT_STATUS_FAIL = "0";*/
	
	/*//OrderProductIds的分割符
	public static final String ORDER_PRODUCT_IDS_SPLIT = "OrderProductIds_Split";
	
	//流量包类型分隔符
	public static final String FLOW_TYPE_SPLIT = "FlowType_Split";
	
	//流量包类型字符串
	public static final String FLOW_TYPE_LIST = "FlowType_List";*/
	

}
