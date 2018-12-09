package com.bonc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
public class HttpUtil {
	private static final Logger logger = Logger.getLogger(HttpUtil.class);
	
	private static int CON_TIME_OUT = 60000;
	private static int READ_TIME_OUT = 120000;

	public static String doPost(String url, Map<String, Object> params) {
		URL u = null;
		HttpURLConnection con = null;
		//构建请求参数
		StringBuffer sb = new StringBuffer();
		if(params!=null){
			for (Entry<String, Object> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			sb.substring(0, sb.length() - 1);
		}
		//尝试发送请求
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(CON_TIME_OUT); 
			con.setReadTimeout(READ_TIME_OUT);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			String cont =sb.substring(0, sb.length() - 1).toString();
			osw.write(cont.toString());
			osw.flush();
			osw.close();
		} catch (Exception e){
			logger.error("API对接异常！"+url+"参数——》"+sb.substring(0, sb.length() - 1).toString());
			e.printStackTrace();
		} finally {
		  if (con != null) {
		       con.disconnect();
		     }
		}
		//读取返回内容
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null){
				buffer.append(temp);
				buffer.append("\n");
			}
		}catch (Exception e){
			logger.error("API数据读取异常！"+url+"参数——》"+sb.substring(0, sb.length() - 1).toString());
			e.printStackTrace();
		}
		return buffer.toString();
	}
	
	public static String doGet(String url, Map<String, Object> params) {
		URL u = null;
		HttpURLConnection con = null;
		//构建请求参数
		StringBuffer sb = new StringBuffer();
		if(params!=null){
			for (Entry<String, Object> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			sb.substring(0, sb.length() - 1);
		}
		StringBuffer result = new StringBuffer();
		//尝试发送请求
		try {
//			url = url+"?"+URLEncoder.encode(sb.substring(0, sb.length() - 1).toString(),"UTF-8");
			if(!"".equals(sb.toString().trim())){
				url = url+"?"+sb.substring(0, sb.length() - 1).toString();
			}
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setConnectTimeout(CON_TIME_OUT); 
			con.setReadTimeout(READ_TIME_OUT);
			con.connect();
//			System.out.println("[doGet]ResponseCode=====" + con.getResponseCode());
	        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8")); 
	        String lines;  
	        while ((lines = reader.readLine()) != null){  
	        	result.append(lines);  
	        }
		} catch (Exception e){
			logger.error("API对接异常！"+url+"参数——》"+sb.substring(0, sb.length() - 1).toString());
			System.out.println("API对接异常！"+url+"参数——》"+sb.substring(0, sb.length() - 1).toString());
			e.printStackTrace();
			return "ERROR";
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return result.toString();
	}
	
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
    
}
