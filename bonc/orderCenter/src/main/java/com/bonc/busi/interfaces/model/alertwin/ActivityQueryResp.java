package com.bonc.busi.interfaces.model.alertwin;

import java.util.HashMap;
import java.util.List;

public class ActivityQueryResp {

	private String sourceCode;
	private List<HashMap<String, Object>> items;

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public List<HashMap<String, Object>> getItems() {
		return items;
	}

	public void setItems(List<HashMap<String, Object>> items) {
		this.items = items;
	}

}
