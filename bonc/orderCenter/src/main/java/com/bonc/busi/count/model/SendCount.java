package com.bonc.busi.count.model;

public class SendCount {
//	租户标识
	private String tenant_id;
//	发送批次序列号
	private String external_id;
//	活动序列号
	private String activity_seq_id;
//	活动名称
	private String activity_name;
//	渠道标识
	private String channel_id;
	private String send_all_count;
//	工单总数
	private int valid_num;
//	有效工单数
	private int  send_all_num;
//	无效工单数
	private int  send_no_num;
//	已发送数量
	private int send_num;
//	发送成功数量
	private int send_suc_num;
//	发送失败数量
	private int send_err_num;
//	 '0'  '默认未完成，发送完成时打上标识'
	private int is_finish;
//	文件全路径
	private String file_name;
//	发送时间
	private String send_date;
//	起始页
	private int startnum;
//	终止页
	private int endnum;
	
	public String getTenant_id() {
		return tenant_id;
	}
	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}
	public String getExternal_id() {
		return external_id;
	}
	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}
	public String getActivity_seq_id() {
		return activity_seq_id;
	}
	public void setActivity_seq_id(String activity_seq_id) {
		this.activity_seq_id = activity_seq_id;
	}
	public String getChannel_id() {
		return channel_id;
	}
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	public int getSend_all_num() {
		return send_all_num;
	}
	public void setSend_all_num(int send_all_num) {
		this.send_all_num = send_all_num;
	}
	public int getSend_num() {
		return send_num;
	}
	public void setSend_num(int send_num) {
		this.send_num = send_num;
	}
	public int getSend_suc_num() {
		return send_suc_num;
	}
	public void setSend_suc_num(int send_suc_num) {
		this.send_suc_num = send_suc_num;
	}
	public int getSend_err_num() {
		return send_err_num;
	}
	public void setSend_err_num(int send_err_num) {
		this.send_err_num = send_err_num;
	}
	public int getIs_finish() {
		return is_finish;
	}
	public void setIs_finish(int is_finish) {
		this.is_finish = is_finish;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getSend_date() {
		return send_date;
	}
	public void setSend_date(String send_date) {
		this.send_date = send_date;
	}
	public String getActivity_name() {
		return activity_name;
	}
	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}
	public int getStartnum() {
		return startnum;
	}
	public void setStartnum(int startnum) {
		this.startnum = startnum;
	}
	public int getEndnum() {
		return endnum;
	}
	public void setEndnum(int endnum) {
		this.endnum = endnum;
	}
	public int getSend_no_num() {
		return send_no_num;
	}
	public void setSend_no_num(int send_no_num) {
		this.send_no_num = send_no_num;
	}
	public int getValid_num() {
		return valid_num;
	}
	public void setValid_num(int valid_num) {
		this.valid_num = valid_num;
	}
	public String getSend_all_count() {
		return send_all_count;
	}
	public void setSend_all_count(String send_all_count) {
		this.send_all_count = send_all_count;
	}
	
	
	
}
