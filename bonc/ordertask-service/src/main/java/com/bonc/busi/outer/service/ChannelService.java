package com.bonc.busi.outer.service;

import com.bonc.common.base.JsonResult;

import java.util.List;
import java.util.Map;

/**
 * Created by MQZ on 2017/11/4.
 * 工单渠道映射接口
 */

public interface ChannelService {
    /**
     * 创建渠道初始化映射信息
     * @param channelColumnList
     * @return
     */
    public JsonResult createChannelOrderMapping(List<Map<String,String>> channelColumnList);

    /**
     * 修改渠道渠道工单表映射
     * @param request
     */
    public JsonResult modifChannelOrderMapping(List<Map<String, String>> request);

}
