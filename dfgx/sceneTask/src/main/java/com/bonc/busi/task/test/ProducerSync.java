package com.bonc.busi.task.test;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.alibaba.fastjson.JSONObject;

public class ProducerSync {
	private static final String TOPIC = "sixthtopic";
	private static final String BROKERCLIST = "172.16.11.167:9093,172.16.11.167:9094,172.16.11.167:9095";

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", BROKERCLIST);
		properties.put("acks", "all");
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		//properties.put("producer.type", "async");
		properties.put("batch.size", "16384");
		
		JSONObject json = new JSONObject();
		json.put("tenantId", "uni076");
		json.put("ipAddr", "133.160.96.66");
		json.put("channelType", "7");
		json.put("smsPort", "8801");
		json.put("eventType", "E02|E020004");
		json.put("externalId", "117384");
		json.put("sendLev", "4");
		json.put("areaId", "810");
		json.put("uniqueId", "a38dd114c7b640b4a8a2c0d03114d79c");
		json.put("occurTime", "2017-06-23 17:26:31");
		json.put("smsResource", "3");
		json.put("extendedField", "{\"external_id\":\"117384\",\"area_id\":\"810\",\"effective_time_slot\":\"12\",\"phone_number\":\"13096361938\",\"external_type\":\"1\",\"success_criteria\":\"y\",\"product_code\":\"\",\"service_type\":\"200101AA\",\"pay_mode\":\"05\",\"tenant_id\":\"uni081\",\"nodisturb\":\"n\"}");
		json.put("userName", "KHWX45623");
		json.put("passWd", "KHWX45623");
		json.put("eventTypeId", "E020004");
		json.put("areaCode", "0371");
		json.put("smsSetId", "3037145623");
		json.put("telPhone", "13096361937");
		json.put("product_code", "");
		json.put("upKafkaTime", "2017-06-23 17:26:31");
		json.put("topicId", "sixthtopic");
		json.put("downKafkaTime", "2017-06-23 17:29:48");
		json.put("sendContent", "周末");
		json.put("spPhone", "10016");
		json.put("compCode", "45623");
		String message = json.toString();
		System.out.println("要传的数据："+message);
		
		Producer<String, String> producer = new KafkaProducer<String, String>(properties);
		for (int i = 0; i < 10005; i++) {
			
			//String message = "{'tenantId': 'uni076','ipAddr': '133.160.96.66','channelType': '7','smsPort': '8801','eventType': 'E02|E020004','externalId': '117384','sendLev': '4','areaId': '810','uniqueId': 'a38dd114c7b640b4a8a2c0d03114d79c','occurTime': '2017-06-23 17:26:31","smsResource": "3","extendedField": "{\"external_id\":\"117384\",\"area_id\":\"810\",\"effective_time_slot\":\"12\",\"phone_number\":\"13096361938\",\"external_type\":\"1\",\"success_criteria\":\"y\",\"product_code\":\"\",\"service_type\":\"200101AA\",\"pay_mode\":\"05\",\"tenant_id\":\"uni076\",\"nodisturb\":\"n\"}","userName": "KHWX45623","passWd": "KHWX45623","eventTypeId": "E020004","areaCode": "0371","smsSetId": "3037145623","telPhone": "13096361938","product_code": "","upKafkaTime": "2017-06-23 17:26:31","topicId": "BoncUnicomSms_3037145623_4","downKafkaTime": "2017-06-23 17:29:48","sendContent": "周末","spPhone": "10016","compCode": "45623"}";
			ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(TOPIC, message);
			producer.send(producerRecord, new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null)
						e.printStackTrace();
					System.out.println("The offset of the record we just sent is: " + metadata.offset());
				}
			});
		}
		producer.close();
	}
}
