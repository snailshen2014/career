package com.bonc.h2.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bonc.h2.command.ResonsePackgeParseTool;
import com.bonc.h2.command.ResponseHead;
import com.bonc.h2.pojo.ResponseResult;

/**
 * 
 * @author quechao 2009-11-28
 */
public class SocketTool {
	static Logger log = Logger.getLogger(SocketTool.class);
	
	public static ResponseResult remoteOperate(String command, IpPort ipPort) throws IOException {
		Socket socket = null;
		OutputStream out = null;
		InputStream input = null;
		// 
		log.debug("==ip:端口"+ipPort.getIp()+"  "+ipPort.getPort());
		socket = new Socket(ipPort.getIp(), ipPort.getPort());
		socket.setSoTimeout(10 * 1000);
		input = socket.getInputStream();
		out = socket.getOutputStream();
		
		out.write(command.getBytes());// 
		ResponseResult result = null;
		try {
			List<byte[]> response = read(input,log);
			if(response!=null){
				result = ResonsePackgeParseTool.Analyse(response);
			}
		} catch (Exception e) {
			if (log != null) {
				log.error(e);
			}
		}
		try {
			if (input != null) {
				input.close();
				input = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (Exception es) {

		}
		return result;//
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<byte[]> read(InputStream input,Logger log) throws Exception {
		List<byte[]> responseData = new ArrayList<byte[]>();
		byte[] buffer = new byte[500];
		byte[] oneByte= {0};
		int count=0;
		while(oneByte[0]!=26){
			input.read(oneByte);
			buffer[count] = oneByte[0];
			count++;
			if(count==300) break;
		}
		byte[] byteBody = new byte[count];// 
		ResponseHead.BytesCopy(buffer,byteBody,0,count,0);
		log.debug("应答报文内容:"+new String(byteBody,"GBK"));
		responseData.add(byteBody);
		return responseData;
	}
}
