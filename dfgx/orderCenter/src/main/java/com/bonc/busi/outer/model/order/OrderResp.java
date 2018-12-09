package com.bonc.busi.outer.model.order;

import java.util.HashMap;
import java.util.List;

public class OrderResp {

	private Integer total;
	
	private List<HashMap<String, Object>> items;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<HashMap<String, Object>> getItems() {
		return items;
	}

	public void setItems(List<HashMap<String, Object>> items) {
		this.items = items;
	}
	
}
