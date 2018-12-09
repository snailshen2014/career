package com.bonc.busi.orderschedule.channel;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.*;
import com.bonc.busi.orderschedule.api.ApiManage;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.task.base.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;

public class MinAcceptChannel extends ChannelFunc {

    private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

    public MinAcceptChannel(final Activity act) {
        super(act);
    }

    @Override
    public int recordActivityDetail() {
        MinAcceptChannelList min = ActivityJsonFactory.getActivityProvPo().getMinAcceptChannelList();
        PltActivityChannelDetail detail = new PltActivityChannelDetail();
        detail.setCHANN_ID(min.getChannelId());  //渠道id
        detail.setMARKET_WORDS(min.getMarketingWords()); //话术内容
        detail.setTENANT_ID(ActivityJsonFactory.tenantId());  //租户id
        detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId()); //活动id
        detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());  //活动流水号
        detail.setFILTER_CON(min.getFilterCondition()); //筛选条件
        detail.setFILTER_SQL(min.getFilterConditionSql()); //筛选条件sql
        mapper.insertChannelDetailInfo(detail);
        return 0;
    }
    
    @Override
    public void setRecomendInfoAndSmsTemplate() {
    	if(ActivityJsonFactory.getActivityProvPo().getMinAcceptChannelList() != null) {
        List<ChannelSpecialFilterPo> special = ActivityJsonFactory.getActivityProvPo().getMinAcceptChannelList().getChannelSpecialFilterList();
        if (special!= null && special.size()>0){
            setRecomendInfo(getSpecialFilterMap(special,0));
            setSmsTemplate(getSpecialFilterMap(special,1));
            setProductId(getSpecialFilterMap(special,2));
            setProductName(getSpecialFilterMap(special,3));
            setFilterName(getSpecialFilterMap(special,4));
            setParameterType(getSpecialFilterMap(special,5));
        }
      }
    }

}
