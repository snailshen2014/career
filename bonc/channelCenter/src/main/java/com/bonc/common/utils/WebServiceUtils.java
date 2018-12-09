package com.bonc.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.bonc.busi.invoke.IDataSendService;

public class WebServiceUtils {

	public static String HttpWebServiceInvoke(String serviceUrl, String accept, int timeOut,String encode) throws Exception {

		String jsonResult = null;

		HttpURLConnection conn = (HttpURLConnection) new URL(serviceUrl).openConnection();
		conn.setRequestProperty("Accept", accept);
		conn.setRequestProperty("Accept-Charset", encode);
		conn.setRequestProperty("contentType", encode);
		conn.setConnectTimeout(timeOut);
		conn.setRequestMethod("GET");
		int code = conn.getResponseCode();
		// 调用web服务
		if (code == 200) {
			InputStream inStream1 = conn.getInputStream();
			jsonResult = getResponseString(inStream1,encode);
			 //System.out.println(jsonResult);

		} else {
			throw new Exception("获取活动数据失败！http请求出错，error=" + code);
		}

		return jsonResult;
	}

	private static String getResponseString(InputStream inStream,String encode) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(inStream, encode));  
        StringBuffer buffer = new StringBuffer();  
        String line = "";  
        while ((line = in.readLine()) != null){  
          buffer.append(line);  
        }  
		inStream.close();
		in.close();
		return buffer.toString();
	}

	/**
	 * webService 调用
	 * 
	 * @param jsonParam
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> String sendWebService(String jsonParam, String url, Class<T> service, String methodName)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {

		JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
		factoryBean.setServiceClass(IDataSendService.class);
		// ip和端口改成对应数据平台的ip和端口
		factoryBean.setAddress(url);
		T iProDealService = (T) factoryBean.create();

		Client proxy = ClientProxy.getClient(iProDealService);
		HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		// 设置接口调用响应时间
		policy.setConnectionTimeout(10 * 1000);
		// 设置接口调用反馈结果超时时间
		policy.setReceiveTimeout(2 * 60 * 60 * 1000);
		conduit.setClient(policy);
		// 获取调用结果
		Method method = iProDealService.getClass().getMethod(methodName, new Class[] { String.class });
		return (String) method.invoke(iProDealService, new Object[] { jsonParam });
	}

}
