package com.bonc.busi.activity;
/**
 * 
 * @author yanyafei 2016/8/27
 * 数字短信附件实体类
 */
public class ActivityDigmessFile {
  
  private String id;
  private String upload_filename;//文件名称 用
  private String type;//文件类型 用
  private String file_size;//文件大小
  private String video_name;//视频名称
  private String video_size  ; //视频文件大小
  private String activity_id;//活动id 
  private String uuidandname;//uuid+name
  

public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}

public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}

public String getUuidandname() {
	return uuidandname;
}
public void setUuidandname(String uuidandname) {
	this.uuidandname = uuidandname;
}
public String getUpload_filename() {
	return upload_filename;
}
public void setUpload_filename(String upload_filename) {
	this.upload_filename = upload_filename;
}
public String getFile_size() {
	return file_size;
}
public void setFile_size(String file_size) {
	this.file_size = file_size;
}
public String getVideo_name() {
	return video_name;
}
public void setVideo_name(String video_name) {
	this.video_name = video_name;
}
public String getVideo_size() {
	return video_size;
}
public void setVideo_size(String video_size) {
	this.video_size = video_size;
}
public String getActivity_id() {
	return activity_id;
}
public void setActivity_id(String activity_id) {
	this.activity_id = activity_id;
}
 
  
}
