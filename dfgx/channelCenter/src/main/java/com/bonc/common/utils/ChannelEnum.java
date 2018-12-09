package com.bonc.common.utils;

public enum ChannelEnum {

	WX("微信", "11"), DX("集团短信", "3"), ST("集团手厅", "1"), WT("集团网厅", "2"), WSC("集团沃视窗", "9"), YJQD("一级渠道", "999");
	// 成员变量
	private String name;
	private String code;
	// 构造方法
	private ChannelEnum(String name, String code) {
		this.name = name;
		this.code = code;
	}
	// 普通方法
	public static String getCode(String name) {
		for (ChannelEnum c : ChannelEnum.values()) {
			if (name.equals(c.getName())) {
				return c.code;
			}
		}
		return null;
	}
	// 普通方法
	public static String getName(String code) {
		for (ChannelEnum c : ChannelEnum.values()) {
			if (code.equals(c.getCode())) {
				return c.name;
			}
		}
		return null;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
