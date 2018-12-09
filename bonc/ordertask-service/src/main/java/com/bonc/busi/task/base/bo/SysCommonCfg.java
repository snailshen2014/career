package com.bonc.busi.task.base.bo;

public class SysCommonCfg {
	private String CFG_VALUE;
	private String NOTE;
	private String CFG_TYPE;
	private String CFG_KEY;
	public String getCFG_VALUE() {
        return CFG_VALUE;
    }
	public void setCFG_VALUE(String CFG_VALUE) {
        this.CFG_VALUE = CFG_VALUE == null ? null : CFG_VALUE.trim();
    }
	public String getNOTE() {
        return NOTE;
    }
	public void setNOTE(String NOTE) {
        this.NOTE = NOTE == null ? null : NOTE.trim();
    }
	public String getCFG_TYPE() {
        return CFG_TYPE;
    }
	public void setCFG_TYPE(String CFG_TYPE) {
        this.CFG_TYPE = CFG_TYPE == null ? null : CFG_TYPE.trim();
    }
	public String getCFG_KEY() {
        return CFG_KEY;
    }
	public void setCFG_KEY(String CFG_KEY) {
        this.CFG_KEY = CFG_KEY == null ? null : CFG_KEY.trim();
    }
}
