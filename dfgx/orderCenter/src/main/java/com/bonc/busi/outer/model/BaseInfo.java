package com.bonc.busi.outer.model;

import com.bonc.busi.interfaces.model.ReqHeader;

public class BaseInfo extends ReqHeader{
	private String provId; // 租户标识
	private String channelId; // 渠道标识
	private String cellId;// 基础单元标识
	
	private String cellPath;//组织机构路径
	
	private String terminal;//中断类型 有可能是 app 网页 还有微信

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	public String getCellPath() {
		return cellPath;
	}

	public void setCellPath(String cellPath) {
		this.cellPath = cellPath;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

}
