package com.bonc.busi.interfaces.model.frontline;

import java.util.HashMap;
import java.util.List;

import com.bonc.busi.interfaces.model.ReqHeader;

public class ContactReq extends ReqHeader{

	private List<HashMap<String, Object>> pama;

	public List<HashMap<String, Object>> getPama() {
		return pama;
	}

	public void setPama(List<HashMap<String, Object>> pama) {
		this.pama = pama;
	}

	
	
}
