<?xml version="1.0" encoding="UTF-8"?>
<config>
	<#if bo.po.provId=="-1"&&bo.po.cityId=="-1">
	<#if bo.po.splitType != "" && bo.po.splitType == "2">
	<#-- 集团活动线下拆分活动 -->
	<ACTIVITY_init>0</ACTIVITY_init>
	<ACTIVITY_INFO>
		<ACTIVITY_ID>${bo.po.activityId}</ACTIVITY_ID>
		<ACTIVITY_NAME>${bo.po.activityName}</ACTIVITY_NAME>
		<ACTIVITY_EFFECT_DATE_st>${bo.po.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.po.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE>
			<#list bo.provIds?split(",") as province>
			<elmt>${province}</elmt>
			</#list>
		</USE_RANGE>
		<split_type>${bo.po.splitType!""}</split_type>
		<ACTIVITY_status>${status}</ACTIVITY_status>
	</ACTIVITY_INFO>
	<#list offLineList as offLine>
	<PROV_ACTIVITY_INFO>
		<PROV_ACTIVITY_ID>${offLine.subActivityId!""}</PROV_ACTIVITY_ID>
		<PROV_ACTIVITY_NAME>${offLine.activityName!""}</PROV_ACTIVITY_NAME>
		<prov_id>${offLine.provId!""}</prov_id>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type>0</split_type>
		<ACTIVITY_status>0</ACTIVITY_status>
	</PROV_ACTIVITY_INFO>
	</#list>
	<CITY_ACTIVITY_INFO>
		<CITY_ACTIVITY_ID></CITY_ACTIVITY_ID>
		<CITY_ACTIVITY_NAME></CITY_ACTIVITY_NAME>
		<city_id></city_id>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</CITY_ACTIVITY_INFO>
	<#else>
	<#-- 集团活动主活动 -->
	<ACTIVITY_init>0</ACTIVITY_init>
	<ACTIVITY_INFO>
		<ACTIVITY_ID>${bo.po.activityId}</ACTIVITY_ID>
		<ACTIVITY_NAME>${bo.po.activityName}</ACTIVITY_NAME>
		<ACTIVITY_EFFECT_DATE_st>${bo.po.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.po.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE>
			<#list bo.provIds?split(",") as province>
			<elmt>${province}</elmt>
			</#list>
		</USE_RANGE>
		<split_type>${bo.po.splitType!""}</split_type>
		<ACTIVITY_status>${status}</ACTIVITY_status>
	</ACTIVITY_INFO>
	<PROV_ACTIVITY_INFO>
		<PROV_ACTIVITY_ID></PROV_ACTIVITY_ID>
		<PROV_ACTIVITY_NAME></PROV_ACTIVITY_NAME>
		<prov_id></prov_id>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</PROV_ACTIVITY_INFO>
	<CITY_ACTIVITY_INFO>
		<CITY_ACTIVITY_ID></CITY_ACTIVITY_ID>
		<CITY_ACTIVITY_NAME></CITY_ACTIVITY_NAME>
		<city_id></city_id>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</CITY_ACTIVITY_INFO>
	</#if>
	<#elseif bo.po.provId!="-1"&&bo.po.cityId=="-1">
	<#-- -----------------------------省分活动 开始------------------------------ -->
	<#if bo.parentPo.activityId??>
	<#-- 集团子活动线上拆分 -->
	<ACTIVITY_init>0</ACTIVITY_init>
	<ACTIVITY_INFO>
		<ACTIVITY_ID>${bo.parentPo.activityId}</ACTIVITY_ID>
		<ACTIVITY_NAME>${bo.parentPo.activityName}</ACTIVITY_NAME>
		<ACTIVITY_EFFECT_DATE_st>${bo.parentPo.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.parentPo.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE>
		<#list bo.parentProvIds?split(",") as parentProvince>
			<elmt>${parentProvince}</elmt>
		</#list>
		</USE_RANGE>
		<split_type>${bo.parentPo.splitType!""}</split_type>
		<ACTIVITY_status>${parentStatus}</ACTIVITY_status>
	</ACTIVITY_INFO>
	<PROV_ACTIVITY_INFO>
		<PROV_ACTIVITY_ID>${bo.po.activityId}</PROV_ACTIVITY_ID>
		<PROV_ACTIVITY_NAME>${bo.po.activityName}</PROV_ACTIVITY_NAME>
		<prov_id>${bo.po.provId}</prov_id>
		<ACTIVITY_EFFECT_DATE_st>${bo.po.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.po.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type>${bo.po.splitType!""}</split_type>
		<ACTIVITY_status>${status}</ACTIVITY_status>
	</PROV_ACTIVITY_INFO>
	<CITY_ACTIVITY_INFO>
		<CITY_ACTIVITY_ID></CITY_ACTIVITY_ID>
		<CITY_ACTIVITY_NAME></CITY_ACTIVITY_NAME>
		<city_id></city_id>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</CITY_ACTIVITY_INFO>
	<#elseif bo.po.splitType != "" && bo.po.splitType == "2">
	<ACTIVITY_init>1</ACTIVITY_init>
	<ACTIVITY_INFO>
		<ACTIVITY_ID></ACTIVITY_ID>
		<ACTIVITY_NAME></ACTIVITY_NAME>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</ACTIVITY_INFO>
	<PROV_ACTIVITY_INFO>
		<PROV_ACTIVITY_ID>${bo.po.activityId}</PROV_ACTIVITY_ID>
		<PROV_ACTIVITY_NAME>${bo.po.activityName}</PROV_ACTIVITY_NAME>
		<prov_id>${bo.po.provId}</prov_id>
		<ACTIVITY_EFFECT_DATE_st>${bo.po.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.po.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE>
		<#list bo.provIds?split(",") as province>
			<elmt>${province}</elmt>
		</#list>
		</USE_RANGE>
		<split_type>${bo.po.splitType!""}</split_type>
		<ACTIVITY_status>${status}</ACTIVITY_status>
	</PROV_ACTIVITY_INFO>
	<#list offLineList as offLine>
	<CITY_ACTIVITY_INFO>
		<CITY_ACTIVITY_ID>${offLine.subActivityId!""}</CITY_ACTIVITY_ID>
		<CITY_ACTIVITY_NAME>${offLine.activityName!""}</CITY_ACTIVITY_NAME>
		<city_id>${offLine.cityId!""}</city_id>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type>0</split_type>
		<ACTIVITY_status>0</ACTIVITY_status>
	</CITY_ACTIVITY_INFO>
	</#list>
	<#else>
	<#-- 省分活动 -->
	<ACTIVITY_init>1</ACTIVITY_init>
	<ACTIVITY_INFO>
		<ACTIVITY_ID></ACTIVITY_ID>
		<ACTIVITY_NAME></ACTIVITY_NAME>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</ACTIVITY_INFO>
	<PROV_ACTIVITY_INFO>
		<PROV_ACTIVITY_ID>${bo.po.activityId}</PROV_ACTIVITY_ID>
		<PROV_ACTIVITY_NAME>${bo.po.activityName}</PROV_ACTIVITY_NAME>
		<prov_id>${bo.po.provId}</prov_id>
		<ACTIVITY_EFFECT_DATE_st>${bo.po.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.po.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE>
			<#list bo.provIds?split(",") as province>
			<elmt>${province}</elmt>
			</#list>
		</USE_RANGE>
		<split_type>${bo.po.splitType!""}</split_type>
		<ACTIVITY_status>${status}</ACTIVITY_status>
	</PROV_ACTIVITY_INFO>
	<CITY_ACTIVITY_INFO>
		<CITY_ACTIVITY_ID></CITY_ACTIVITY_ID>
		<CITY_ACTIVITY_NAME></CITY_ACTIVITY_NAME>
		<city_id></city_id>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</CITY_ACTIVITY_INFO>
	</#if>
	<#-- -----------------------------省分活动结束------------------------------ -->
	<#else>
	<#-- -----------------------------地市活动开始------------------------------ -->
	<#if bo.parentPo.activityId??>
	<#-- 省分子活动 -->
	<ACTIVITY_init>1</ACTIVITY_init>
	<ACTIVITY_INFO>
		<ACTIVITY_ID></ACTIVITY_ID>
		<ACTIVITY_NAME></ACTIVITY_NAME>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</ACTIVITY_INFO>
	<PROV_ACTIVITY_INFO>
		<PROV_ACTIVITY_ID>${bo.parentPo.activityId}</PROV_ACTIVITY_ID>
		<PROV_ACTIVITY_NAME>${bo.parentPo.activityName}</PROV_ACTIVITY_NAME>
		<prov_id>${bo.parentPo.provId}</prov_id>
		<ACTIVITY_EFFECT_DATE_st>${bo.parentPo.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.parentPo.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE>
		<#list bo.parentProvIds?split(",") as parentProvince>
			<elmt>${parentProvince}</elmt>
		</#list>
		</USE_RANGE>
		<split_type>${bo.parentPo.splitType!""}</split_type>
		<ACTIVITY_status>${parentStatus}</ACTIVITY_status>
	</PROV_ACTIVITY_INFO>
	<CITY_ACTIVITY_INFO>
		<CITY_ACTIVITY_ID>${bo.po.activityId}</CITY_ACTIVITY_ID>
		<CITY_ACTIVITY_NAME>${bo.po.activityName}</CITY_ACTIVITY_NAME>
		<city_id>${bo.po.cityId}</city_id>
		<ACTIVITY_EFFECT_DATE_st>${bo.po.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.po.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE>
		<#list bo.provIds?split(",") as province>
			<elmt>${province}</elmt>
		</#list>
		</USE_RANGE>
		<split_type>${bo.po.splitType!""}</split_type>
		<ACTIVITY_status>${status}</ACTIVITY_status>
	</CITY_ACTIVITY_INFO>
	<#else>
	<#-- 地市活动 -->
	<ACTIVITY_init>2</ACTIVITY_init>
	<ACTIVITY_INFO>
		<ACTIVITY_ID></ACTIVITY_ID>
		<ACTIVITY_NAME></ACTIVITY_NAME>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</ACTIVITY_INFO>
	<PROV_ACTIVITY_INFO>
		<PROV_ACTIVITY_ID></PROV_ACTIVITY_ID>
		<PROV_ACTIVITY_NAME></PROV_ACTIVITY_NAME>
		<prov_id></prov_id>
		<ACTIVITY_EFFECT_DATE_st></ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end></ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE></USE_RANGE>
		<split_type></split_type>
		<ACTIVITY_status></ACTIVITY_status>
	</PROV_ACTIVITY_INFO>
	<CITY_ACTIVITY_INFO>
		<CITY_ACTIVITY_ID>${bo.po.activityId}</CITY_ACTIVITY_ID>
		<CITY_ACTIVITY_NAME>${bo.po.activityName}</CITY_ACTIVITY_NAME>
		<city_id>${bo.po.cityId}</city_id>
		<ACTIVITY_EFFECT_DATE_st>${bo.po.startDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_st>
		<ACTIVITY_EFFECT_DATE_end>${bo.po.endDate?string("yyyy-MM-dd")}</ACTIVITY_EFFECT_DATE_end>
		<USE_RANGE>
		<#list bo.provIds?split(",") as province>
			<elmt>${province}</elmt>
		</#list>
		</USE_RANGE>
		<split_type>${bo.po.splitType!""}</split_type>
		<ACTIVITY_status>${status}</ACTIVITY_status>
	</CITY_ACTIVITY_INFO>
	</#if>
	<#-- -----------------------------地市活动结束------------------------------ -->
	</#if>
	<USER_GROUP_ID>${bo.po.userGroupId}</USER_GROUP_ID>
	<USER_GROUP_NAME>${bo.po.userGroupName}</USER_GROUP_NAME>
	<PRODUCT_ID>
	<#-- 循环输出活动产品信息 -->
	<#if bo.productList?exists>
		<#list bo.productList as product>
		<elmt>
			<id>${product.productCode!""}</id>
			<name>${product.productName!""}</name>
		</elmt>
		</#list>
	</#if>
	</PRODUCT_ID>
	<channel_LIST>
		<SUGGESTION_CONTENT>${bo.po.suggestionContent!""}</SUGGESTION_CONTENT>
		<#-- 循环输出渠道信息 -->
		<#if bo.channelInfoList?exists>
			<#-- 循环输出渠道信息 -->
			<#list bo.channelInfoList as channel>
			<channel_INFO>
				<channel_ID>${channel.channelId}</channel_ID>
				<channel_name>${channel.channelShortName}</channel_name>
				<channel_ORDER>${channel.channelLevel!""}</channel_ORDER>
				<title>${channel.title!""}</title>
				<URL>${channel.url!""}</URL>
				<WORD_TYPE>0</WORD_TYPE>
				<THE_WORD>${(channel.huashuContent!"")?trim}</THE_WORD>
				<IMG_URL>${channel.imgUrl!""}</IMG_URL>
				<IMG_SIZE>${channel.imgSize!""}</IMG_SIZE>
				<#-- 循环输出批次信息 -->
				<#if channel.channelCode=="101"||channel.channelCode=="201">
				<BATCH_LIST>
				<#if bo.channelBatchList?exists>
				<#-- 循环输出批次信息 -->
				<#list bo.channelBatchList as batchInfo>
					<#if channel.channelId == batchInfo.mappedChannelId>
					<#-- 设置整数格式 -->
					<#setting number_format="#">
					<BATCH_INFO>
						<BATCH_TASK_NUMBER>${batchInfo.userNumber}</BATCH_TASK_NUMBER>
						<BATCH_EFFECT_DATE_st>${batchInfo.startTime}</BATCH_EFFECT_DATE_st>
						<BATCH_EFFECT_DATE_end>${batchInfo.endTime}</BATCH_EFFECT_DATE_end>
					</BATCH_INFO>	
					</#if>
				</#list>
				</#if>
				</BATCH_LIST>
				</#if>
				<#-- 循环输出弹窗配置信息 -->
				<#if channel.channelCode == "104">
				<dialogue_INFO>
				<#if bo.popInfo!="">
				<#list bo.popInfo?split(";") as pop>
				<#assign pop_info=pop?split(",")/>
					<dialogue_CHANNEL_INFO>
						<dialogue_CHANNEL_ID>${pop_info[0]!""}</dialogue_CHANNEL_ID>
						<execute_TIME>${pop_info[2]!""}</execute_TIME>
						<objective>${pop_info[4]!""}</objective>
						<WORD_DESC>${pop_info[5]!""}</WORD_DESC>
					</dialogue_CHANNEL_INFO>
				</#list>
				</#if>
				</dialogue_INFO>
				</#if>
				<#-- 循环输出数字短信信息 -->
				<#if channel.channelCode == "105">
				<digital_info>
					<digital_type_info>
						<digital_type_id>1</digital_type_id>
						<title>${DIGMESSTITLE1!""}</title>
						<content>${TXT!""}</content>
					</digital_type_info>
					<digital_type_info>
						<digital_type_id>2</digital_type_id>
						<title>${DIGMESSTITLE2!""}</title>
						<content>${TXT2!""}</content>
					</digital_type_info>
				</digital_info>
				</#if>
			</channel_INFO>
			</#list>
		</#if>
	</channel_LIST>
	<channel_MAPPING_LIST>
		<#if bo.channelRelateLabelList?exists>
		<#list bo.channelRelateLabelList as mappedChannel>
		<#-- 2016年4月13日 添加 9：其它/空值-->
		<#if mappedChannel.labelId == "5">
		<channel_MAPPING>
			<SOURCE_channel_INFO>9</SOURCE_channel_INFO>
			<TARGET_channel_INFO>${mappedChannel.channelId}</TARGET_channel_INFO>
		</channel_MAPPING>
		</#if>
		<channel_MAPPING>
			<SOURCE_channel_INFO>${mappedChannel.labelId}</SOURCE_channel_INFO>
			<TARGET_channel_INFO>${mappedChannel.channelId}</TARGET_channel_INFO>
		</channel_MAPPING>
		</#list>
		</#if>
	</channel_MAPPING_LIST>
	<CYCLE_INFO>
		<CYCLE_TYPE>${bo.po.cycleInfo}</CYCLE_TYPE>
		<#if bo.po.cycleInfo == "2">
		<CYCLE_MODE>
			<CYCLE_MODE_ID>${bo.activityCycleInfo.cycleMode!""}</CYCLE_MODE_ID>
			<CYCLE_month>${bo.activityCycleInfo.everyUnit!""}</CYCLE_month>
			<CYCLE_day>${bo.activityCycleInfo.dayNumber!""}</CYCLE_day>
			<CYCLE_EFFECT_DATE_ST>${bo.activityCycleInfo.scopeStartDay?string("yyyy-MM-dd")}</CYCLE_EFFECT_DATE_ST>
			<CYCLE_EFFECT_DATE_END><#if bo.activityCycleInfo.scopeEndDay??>${bo.activityCycleInfo.scopeEndDay?string("yyyy-MM-dd")}<#else></#if></CYCLE_EFFECT_DATE_END>
		</CYCLE_MODE>
		<#else>
		<CYCLE_MODE>
			<CYCLE_MODE_ID></CYCLE_MODE_ID>
			<CYCLE_month></CYCLE_month>
			<CYCLE_day></CYCLE_day>
			<CYCLE_EFFECT_DATE_ST></CYCLE_EFFECT_DATE_ST>
			<CYCLE_EFFECT_DATE_END></CYCLE_EFFECT_DATE_END>
		</CYCLE_MODE>
		</#if>
	</CYCLE_INFO>
	<COST_INFO>
		<#-- 设置整数格式 -->
		<#setting number_format="#">
		<COST_RULE>${bo.costInfo.costRule!""}</COST_RULE>
		<COST_BUDGET>${bo.costInfo.budget!""}</COST_BUDGET>
	</COST_INFO>
	<ISSUE_INFO>
		<MONTH_ID>${bo.po.dataSendingDate!""}</MONTH_ID>
		<ISSUE_FIELDS>
			<#if bo.dataSendingConfigList?exists>
			<#list bo.dataSendingConfigList as dataSendingConfig>
			<ISSUE_FIELD_ID>
				<ISSUE_FIELD_NAME>${dataSendingConfig.fieldName!""}</ISSUE_FIELD_NAME>
				<ISSUE_FIELD_TYPE>${dataSendingConfig.fieldType!""}</ISSUE_FIELD_TYPE>
				<ISSUE_FIELD_LENGTH>${dataSendingConfig.fieldLength!""}</ISSUE_FIELD_LENGTH>
				<ISSUE_FIELD_DESC>${dataSendingConfig.fieldDesc!""}</ISSUE_FIELD_DESC>
				<ISSUE_FIELD_ALIAS>${dataSendingConfig.fieldAlias!""}</ISSUE_FIELD_ALIAS>
			</ISSUE_FIELD_ID>
			</#list>
			</#if> 
		</ISSUE_FIELDS>
	</ISSUE_INFO>
</config>