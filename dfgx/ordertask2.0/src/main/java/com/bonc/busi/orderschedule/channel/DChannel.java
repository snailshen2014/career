package com.bonc.busi.orderschedule.channel;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ChannelGroupPopupPo;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.activity.FrontlineChannelPo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;
import com.bonc.busi.task.base.SpringUtil;

import java.util.List;

public class DChannel extends ChannelFunc {

    private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);

    public DChannel(final Activity act) {
        super(act);
    }

    @Override
    public int recordActivityDetail() {
        List<FrontlineChannelPo> dChannel = ActivityJsonFactory.getActivityProvPo().getDChannel();
        for (int i = 0; i < dChannel.size(); i++) {
            if (actAttribute.get().getChannelId().equals(dChannel.get(i).getChannelId())) {
                FrontlineChannelPo po = dChannel.get(i);
                PltActivityChannelDetail detail = new PltActivityChannelDetail();
                detail.setTENANT_ID(ActivityJsonFactory.tenantId());
                detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
                detail.setFILTER_CON(po.getFilterCondition());
                detail.setCHANN_ID(po.getChannelId());
                detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
                detail.setFILTER_SQL(po.getFilterConditionSql());
                detail.setIS_SEND_SMS(po.getIsSendSMS());
                detail.setMARKET_WORDS(po.getMarketingWords());
                detail.setTOUCHLIMITDAY(po.getTouchLimitDay());
                detail.setSMS_WORDS(po.getSmsWords());
                List<ChannelSpecialFilterPo> filter_list = po.getChannelSpecialFilterList();
                if (null != filter_list) {
                    String jsonString = JSON.toJSONString(filter_list);
                    jsonString = jsonString.replace("'", "\\'");
                    detail.setCHANNEL_SPECIALFILTER_LIST(jsonString);
                }
                //因为是循环弹窗list中的所有记录，插入之前需要查询是否之前已经保存记录
                mapper.insertChannelDetailInfo(detail);
            }
        }
        return 0;
    }

    @Override
    public void setRecomendInfoAndSmsTemplate() {
        List<FrontlineChannelPo> dChannel = ActivityJsonFactory.getActivityProvPo().getDChannel();
        for (FrontlineChannelPo frontlineChannelPo : dChannel) {
            List<ChannelSpecialFilterPo> front = frontlineChannelPo.getChannelSpecialFilterList();
            String channelId = frontlineChannelPo.getChannelId();
            if (channelId.equals(actAttribute.get().getChannelId())) {
                if (front != null && front.size() > 0) {
                    setRecomendInfo(getSpecialFilterMap(front, 0));
                    setSmsTemplate(getSpecialFilterMap(front, 1));
                    setProductId(getSpecialFilterMap(front, 2));
                    setProductName(getSpecialFilterMap(front, 3));
                    setFilterName(getSpecialFilterMap(front, 4));
                }
            }
        }
    }
}


