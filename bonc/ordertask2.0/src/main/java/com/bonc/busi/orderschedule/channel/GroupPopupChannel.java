package com.bonc.busi.orderschedule.channel;

import java.util.List;

import com.bonc.busi.task.base.SpringUtil;
import com.bonc.utils.IContants;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.activity.ChannelGroupPopupPo;
import com.bonc.busi.activity.ChannelSpecialFilterPo;
import com.bonc.busi.orderschedule.bo.PltActivityChannelDetail;
import com.bonc.busi.orderschedule.jsonfactory.ActivityJsonFactory;
import com.bonc.busi.orderschedule.mapper.ActivityInfoMapper;

public class GroupPopupChannel extends ChannelFunc {

    private ActivityInfoMapper mapper = SpringUtil.getApplicationContext().getBean(ActivityInfoMapper.class);


    public GroupPopupChannel(final Activity act) {
        super(act);
    }

    @Override
    public int recordActivityDetail() {
        for (int i = 0; i < ActivityJsonFactory.getPopWinList().size(); i++) {
            ChannelGroupPopupPo po = ActivityJsonFactory.getActivityProvPo().getChannelGroupPopupPoList().get(i);

            PltActivityChannelDetail detail = new PltActivityChannelDetail();
            detail.setBUSINESS_HALL_ID(po.getBusinessHall());
            detail.setBUSINESS_HALL_NAME(po.getBusinessHallName());
            detail.setCHANN_ID(super.actAttribute.get().getSupChannelId()+po.getBusinessHall());
            detail.setTENANT_ID(ActivityJsonFactory.tenantId());
            detail.setACTIVITY_ID(ActivityJsonFactory.getActivityId());
            detail.setCONTENT(po.getContent());
            detail.setFILTER_CON(po.getFilterCondition());
            detail.setACTIVITY_SEQ_ID(this.actAttribute.get().getActivitySeqId());
            detail.setFILTER_SQL(po.getFilterConditionSql());
            detail.setNUMBERLIMIT(po.getNumberLimit());
            detail.setTARGET(po.getTarget());

            //added by shenyj for special filter
            String specialFilterListString = null;
            if (po.getChannelSpecialList() != null) {
                specialFilterListString = JSON.toJSONString(po.getChannelSpecialList());
            }
            if (specialFilterListString != null) {
                detail.setCHANNEL_SPECIALFILTER_LIST(specialFilterListString);
            }
            /**
             * 弹窗添加SmsSendWords短信话术字段对应到detail表中的sms_words
             * @author 王明新
             * */
            detail.setSMS_WORDS(po.getSmsSendWords());
            //因为是循环弹窗list中的所有记录，插入之前需要查询是否之前已经保存记录
            List<PltActivityChannelDetail> record = mapper.queryChannelDetailInfo(detail);
            if (record == null || record.size()==0){
                mapper.insertChannelDetailInfo(detail);
            }
        }
        return 0;
    }

    @Override
    public void setRecomendInfoAndSmsTemplate() {
        List<ChannelSpecialFilterPo> poSpecialFilter = ActivityJsonFactory.getActivityProvPo().getChannelGroupPopupPoList().get(0).getChannelSpecialList();
        if (poSpecialFilter != null && poSpecialFilter.size()>0) {
            setRecomendInfo(getSpecialFilterMap(poSpecialFilter, 0));
            setSmsTemplate(getSpecialFilterMap(poSpecialFilter, 1));
            setProductId(getSpecialFilterMap(poSpecialFilter,2));
            setProductName(getSpecialFilterMap(poSpecialFilter,3));
            setFilterName(getSpecialFilterMap(poSpecialFilter,4));
        }
    }

}
