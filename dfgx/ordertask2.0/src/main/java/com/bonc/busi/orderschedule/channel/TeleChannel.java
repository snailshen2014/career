package com.bonc.busi.orderschedule.channel;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.MsmChannelPo;
import com.bonc.busi.activity.TelePhoneChannelPo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;
import com.bonc.busi.task.base.SpringUtil;

import java.util.List;

public class TeleChannel extends ChannelFunc {
	private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

	public TeleChannel(final Activity act) {
		super(act);
	}

	@Override
	public int recordActivityDetail() {
		TelePhoneChannelPo tel = ActivityJsonFactory.getActivityProvPo().getChannelTelePhone();
		PltActivityChannelDetail detail = new PltActivityChannelDetail();
		detail.setCHANN_ID(tel.getChannelId());  //渠道id
		detail.setRESERVE1(tel.getCompanyId());  //渠道选择的外呼公司id
		detail.setMARKET_WORDS(tel.getTelephoneHuashuContent()); //话术内容
		detail.setTENANT_ID(ActivityJsonFactory.tenantId());  //租户id
		detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId()); //活动id
		detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());  //活动流水号
		detail.setFILTER_CON(tel.getFilterCondition()); //筛选条件
		detail.setFILTER_SQL(tel.getFilterConditionSql()); //筛选条件sql
		detail.setCONTENT(JSON.toJSONString(tel.getCompanyInfoList()));// 公司列表
		String specialFilterListString = null;
		if (tel.getTelchannelSpecialFilterList() != null) {
			specialFilterListString = JSON.toJSONString(tel.getTelchannelSpecialFilterList());
		}
		if (specialFilterListString != null) {
			detail.setCHANNEL_SPECIALFILTER_LIST(specialFilterListString);
		}
		// ordermapper.InsertActivityChannelStatus(status);
		mapper.insertChannelDetailInfo(detail);
		return 0;
	}



	@Override
	public void setRecomendInfoAndSmsTemplate() {
		List<ChannelSpecialFilterPo> tele = ActivityJsonFactory.getActivityProvPo().getChannelTelePhone().getTelchannelSpecialFilterList();
		if (tele!= null && tele.size()>0){
			setRecomendInfo(getSpecialFilterMap(tele,0));
			setSmsTemplate(getSpecialFilterMap(tele,1));
			setProductId(getSpecialFilterMap(tele,2));
            setProductName(getSpecialFilterMap(tele,3));
			setFilterName(getSpecialFilterMap(tele,4));
		}
	}
}
