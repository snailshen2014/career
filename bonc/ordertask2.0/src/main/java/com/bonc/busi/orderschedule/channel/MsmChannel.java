package com.bonc.busi.orderschedule.channel;

import com.bonc.busi.task.base.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.bonc.busi.activity.MsmChannelPo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;

public class MsmChannel extends ChannelFunc {
	private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

	public MsmChannel(final Activity act) {
		super(act);
	}

	@Override
	public int recordActivityDetail() {
		MsmChannelPo msm = ActivityJsonFactory.getActivityProvPo().getMsmChannelPo();
		PltActivityChannelDetail detail = new PltActivityChannelDetail();
		detail.setCHANN_ID( msm.getChannelId());
		if (msm.getCycleTimes() != null)
			detail.setTIMES(Integer.parseInt(msm.getCycleTimes()));
		detail.setFILTER_CON(msm.getFilterCondition());
		detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
		detail.setFILTER_SQL(msm.getFilterConditionSql());
		if (msm.getIntervalHours() != null)
			detail.setINTERVAL_HOUR(Integer.parseInt(msm.getIntervalHours()));
		detail.setNOSEND_TIME(msm.getNoSendTime());
		detail.setEND_TIME(msm.getSendEndTime());
		detail.setSTART_TIME(msm.getSendStartTime());
		detail.setSEND_LEVEL(msm.getSendLevel());
		detail.setTENANT_ID(ActivityJsonFactory.tenantId());
		detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
		detail.setTOUCHLIMITDAY(msm.getTouchLimitDay());
		detail.setCONTENT(msm.getSmsContent());
		detail.setRESERVE1(msm.getMessageSendTime());
		/**
		 * 短信中的添加3个字段，其中channelpo产品编码对应detail表中的PRODUCT_LIST 
		 * @author 王明新
		 * */
		detail.setPRODUCT_LIST(msm.getMsmProductCode());
		/**
		 * 短信中的保存字段reserve2为短信端口号
		 * */
		if(ActivityJsonFactory.getActivityProvPo().getSmsUsePort() != null){
			detail.setRESERVE2(ActivityJsonFactory.getActivityProvPo().getSmsUsePort());
		}
		/**
		 * 短信中
		 1、短信免打扰编码：messNodisturbCode  对应detail表中   MESS_NODISTURB_CODE
		 2、短信订购编码：messOrderCode  		对应detail表中   MESS_ORDER_CODE
		 3、短信有效时间：messEffectiveTime  	对应detail表中   MESS_EFFECTIVE_TIME
		 4、订购失败回复语：orderFailureReply	对应detail表中   ORDER_FAILURE_REPLY
		 5、订购超时回复语：orderOvertimeReply 对应detail表中   ORDER_OVERTIMEREPLY_REPLY
		 * @author 马擎泽
		 * */
		if(msm.getMessNodisturbCode()!=null){
			detail.setRESERVE4(msm.getMessNodisturbCode());
		}
		if(msm.getMessOrderCode()!=null){
			detail.setRESERVE5(msm.getMessOrderCode());
		}
		if(msm.getMessEffectiveTime()!=null){
			detail.setRESERVE6(msm.getMessEffectiveTime());
		}
		if(msm.getOrderFailureReply()!=null){
			detail.setRESERVE7(msm.getOrderFailureReply());
		}
		if(msm.getOrderOvertimeReply()!=null){
			detail.setRESERVE8(msm.getOrderOvertimeReply());
		}
		mapper.insertChannelDetailInfo(detail);
		return 0;
	}


}
