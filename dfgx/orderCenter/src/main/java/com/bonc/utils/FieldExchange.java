package com.bonc.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.bonc.properties.FieldProperties;
import com.bonc.properties.ThreadPoolProperties;

@Component
public class FieldExchange {

	@Autowired
	private FieldProperties fieldProperties;

	@Autowired
	private ThreadPoolProperties threadPoolProperties;

	private ArrayList<String> pamaList = new ArrayList<String>();
	private HashMap<String, String> pamaToField = new HashMap<String, String>();
	private HashMap<String, String> fieldToPama = new HashMap<String, String>();

	public ArrayList<String> getPamaList() {
		return pamaList;
	}

	public HashMap<String, String> getPamaToField() {
		return pamaToField;
	}

	public HashMap<String, String> getFieldToPama() {
		return fieldToPama;
	}

	@Bean
	public FieldExchange fieldExchangeUtil(){
		FieldExchange fieldExchangeUtil=new FieldExchange();
		Map<String, Object> pamas = BeanUtil.transBean2Map(fieldProperties);
		for(String pama:pamas.keySet()){
			fieldExchangeUtil.getPamaList().add(pama);
			fieldExchangeUtil.getFieldToPama().put((String)pamas.get(pama), pama);
			fieldExchangeUtil.getPamaToField().put(pama, (String)pamas.get(pama));
		}
		return fieldExchangeUtil;
	}

	public ArrayList<String> pamaList() {
		return pamaList;
	}

	public CharSequence getFiled(String pama) {
		return pamaToField.get(pama);
	}

}
