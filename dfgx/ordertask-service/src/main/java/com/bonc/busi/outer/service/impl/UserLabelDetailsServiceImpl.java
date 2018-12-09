package com.bonc.busi.outer.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.language.bm.Rule;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.util.EstimatableObjectWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jta.narayana.NarayanaXAConnectionFactoryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.bonc.busi.outer.bo.RequestParamMap;
import com.bonc.busi.outer.bo.StoreDemandLabelRequest;
import com.bonc.busi.outer.bo.StoreRefreshOrderUsedLabel;
import com.bonc.busi.outer.bo.UserLabel;
import com.bonc.busi.outer.dao.SyscommoncfgDao;
import com.bonc.busi.outer.mapper.PltActivityInfoDao;
import com.bonc.busi.outer.service.UserLabelDetailsService;
import com.bonc.utils.HttpUtil;

@Service("UserLabelDetailsService")
public class UserLabelDetailsServiceImpl implements UserLabelDetailsService{
	
	@Autowired
	PltActivityInfoDao		PltActivityInfoDaoIns;
	
	/**
	 * 根据租户ID查询对应的租户库名：clyx_app_gd_hlj,clyx_app_gd_hainan,sichuan
	 * @param 租户
	 */
	@Override
	public String getSchemaName(String param) {
		return PltActivityInfoDaoIns.getSchemaName(param);
	}	
	
	/**
	 * 查询用户标签表的全部字段
	 * @param 有效的标签表名和租户库名
	 */
	@Override
	public List<Map<String,Object>> getUserLabel(RequestParamMap param) {
		
		String tableSchema = param.getTableName();
		
		System.out.println("schemaName="+tableSchema);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = PltActivityInfoDaoIns.getUserLabel(param);
		System.out.println("list="+list);
		return list;
	}
	
	/**
	 * 模糊查询用户标签表的部分字段
	 * @param 租户和模糊查询的信息
	 */
	@Override
	public List<Map<String, Object>> getUserLabelIndistinct(RequestParamMap param) {
		List<Map<String, Object>> list = PltActivityInfoDaoIns.getUserLabelIndistinct(param);
		return list;
	}
	
	/**
	 * 获取当前有效数据分区标识
	 * @param ASYNUSER.MYSQL.EFFECTIVE_PARTITION.uni050
	 */
	@Override
	public String getValidFlag(String validFlagKey) {
		return PltActivityInfoDaoIns.getValidFlag(validFlagKey);
	}

	/**
	 * 保存渠道注册时需要的用户标签到表中
	 * @param 请求保存的用户标签信息
	 * @throws Exception 
	 */
	@Override
	@Transactional
	public void storeDemandLabel(StoreDemandLabelRequest request) throws Exception {
		String tenantId = request.getTenantId();
		List<UserLabel> userLabels = request.getColumns();
		//租户Id不能为空
		if(StringUtils.isNotBlank(tenantId) && userLabels !=null && userLabels.size() > 0){
			for(UserLabel userLabel : userLabels){
				if(StringUtils.isNotBlank(userLabel.getxCloudColumn())){
				//先查询表中是否存在这个租户下的这个用户标签信息，如果不存在，执行保存逻辑，否则执行更新逻辑
				int count = PltActivityInfoDaoIns.queryIsExistTenantUserLable(tenantId,userLabel.getxCloudColumn());
				if(count == 0){   //如果count==0,表示表中该租户还没有这个标签
					PltActivityInfoDaoIns.saveDemandUserLabel(tenantId,userLabel);
				}else{  //不为0，需要执行更新逻辑
					PltActivityInfoDaoIns.updateDemandUserLabel(tenantId,userLabel);
				}
			  }
			}
			//TODO 调用黄世惠的接口,先判断用户资料同步是否正在跑，如果是的话只更改标志状态，否则的话调用接口执行资料同步
			String runFlag = SyscommoncfgDao.query("ASYNUSER.RUN.FLAG."+tenantId);
			if(runFlag !=null && runFlag.equals("TRUE")){     //ordertask-schedule正在同步用户资料
				//更改sys_common_cfg表里的ASYNUSER.DYNAMICLABELUPDATE.FLAG.TenantId的状态
				String cfgKey = "ASYNUSER.DYNAMICLABELUPDATE.FLAG."+tenantId;
				String state = SyscommoncfgDao.query(cfgKey);
				if(state == null){ //sys_common_cfg里还没有改配置项
					PltActivityInfoDaoIns.insertSysCommonCfg(cfgKey,"TRUE","3.0版本新增的用户标签同步标识");
				}else{
					PltActivityInfoDaoIns.updateSysCommonCfg(cfgKey,"TRUE");
				}
			} else {
			String url = SyscommoncfgDao.query("ASYNUSER.SERVICE.URL");
			if(StringUtils.isBlank(url)){
				throw new Exception("SYS_COMMON_CFG表里需要配置同步用户标签的服务,请检查是否存在！");
			}else{ 
				Map<String, Object> param = new HashMap<String,Object>();
				param.put("TENANT_ID", tenantId);      //调用该服务时需要加上租户Id
				try{	
				  HttpUtil.sendPost(url, JSON.toJSONString(param));
				}catch(Exception ex){
					throw new Exception("保存需要同步的用户标签数据后调用同步用户标签的服务异常,请检查或手工触发");	
				}
			}
		  }
		} else {
			throw new Exception("请求的参数有问题,请检查");
		}
	}

	/**
	 * 保存刷新工单数据要刷新的的用户标签字段
	 */
	@Override
	@Transactional
	public void storeRefreshOrderUsedLabel(StoreRefreshOrderUsedLabel request) throws Exception {
		String tenantId = request.getTenantId();
		String channelId = request.getChannelId();
		List<String> userLabels = request.getColumns();
		if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(channelId) && userLabels != null
				&& userLabels.size() > 0) {
			// 首先查询表里是否已经存在该渠道下的用户标签信息，如果已经存在，则先删除，再插入
			int count = PltActivityInfoDaoIns.checkLabelIsAreadyExsit(tenantId, channelId);
			if (count == 0) { // 不存在的话执行Insert操作
				storeOrderUsedLabel(tenantId,channelId,userLabels);
			}else{  //如果存在,需要先把存在的全部删除，然后再插入
				PltActivityInfoDaoIns.deleteAreadyExistLabel(tenantId, channelId);
				storeOrderUsedLabel(tenantId,channelId,userLabels);
				
			}
			//TODO  调用明新接口,立即执行
			String runFlag = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.RUNFLAG."+tenantId);
			if(runFlag!=null && runFlag.equals("TRUE")){ //正在运行
				//更改sys_common_cfg表里的ASYNUSER.DYNAMICLABELUPDATE.FLAG.TenantId的状态
				String cfgKey = "ASYNUSER.DYNAMICLABELUPDATE.FLAG."+tenantId;
				String state = SyscommoncfgDao.query(cfgKey);
				if(state == null){ //sys_common_cfg里还没有改配置项
					PltActivityInfoDaoIns.insertSysCommonCfg(cfgKey,"TRUE","3.0版本新增的用户标签同步标识");
				}else{
					PltActivityInfoDaoIns.updateSysCommonCfg(cfgKey,"TRUE");
				}
			} else {
			String url = SyscommoncfgDao.query("ORDER.USERLABEL.UPDATE.SERVICE.URL");
			if(StringUtils.isBlank(url)){
				throw new Exception("SYS_COMMON_CFG表里需要配置更新工单用户标签数据的服务,请检查是否存在！");
			}else{ 
				Map<String, Object> param = new HashMap<String,Object>();
				param.put("TENANT_ID", tenantId);      //调用该服务时需要加上租户Id
				String callRemoteServiceResult = HttpUtil.sendPost(url, JSON.toJSONString(param));
				if(callRemoteServiceResult != null && callRemoteServiceResult.equals("ERROR")){
					throw new Exception("API对接异常,请检查！" + url);
				}else if(callRemoteServiceResult != null && callRemoteServiceResult.equals("false")){
					throw new Exception("保存需要更新工单中的用户标签数据后调用更新工单用户标签数据的服务出错了,请检查！");
				}
			}
		  }
		} else {
			throw new Exception("请求的参数有问题,请检查");
		}
	}

	/**
	 * 保存刷新工单时需要刷新的用户标签集合
	 * @param tenantId
	 * @param channelId
	 * @param userLabels
	 * @throws Exception 
	 */
	@Transactional
	private void storeOrderUsedLabel(String tenantId, String channelId, List<String> userLabels) throws Exception {
		for(String userLable : userLabels){
			//查询工单中与userLable对应的字段名称 、标签的描述信息以及标签是否在使用
			Map<String, String> orderColumnMap =  PltActivityInfoDaoIns.queryOrderColumnNameFromMapping(channelId,userLable,tenantId);
			if(orderColumnMap != null){
			  	String orderColumn = orderColumnMap.get("ORDER_COLUMN");
			  	String colunDesc = orderColumnMap.get("COLUMN_DESC");
			  	PltActivityInfoDaoIns.insertOrderUsedLabel(tenantId,channelId,orderColumn,userLable,colunDesc);
			}else{
				throw new Exception("请求的标签:" +userLable + "在工单表上不存在");
			}
		}
	}
}
