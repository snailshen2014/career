package com.bonc.h2.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.log4j.Logger;


/**
 * 
 * @author quechao
 * 数据包构造、解析的基础类
 */
public abstract class BaseCommand {
	
	Logger log = Logger.getLogger(BaseCommand.class); 
	
	protected CommandHead head;
	protected final char PACKAGE_END_FLAG = 0x1a;//(char)0x1a为结束标志
	protected final String TABLE = ""+(char)0x09;// 数据字段间分隔符
	protected final String ENTER = ""+(char)0x0d+(char)0x0a;// 数据字段间分隔符
	public String body;
	
	public BaseCommand() {
		head = new CommandHead();
		this.initHead();
	}
	
	/**
	 * 返回流水号
	 * @return
	 */
	public String getNumber(){
		return head.getNumber().trim();
	}
	
	/**
	 *  构建包体
	 */
	public abstract void buildBody();
	
	/**
	 * 构建整个包的字符串
	 * @return BaseCommand的String
	 */
	public String toString() {
		buildBody();
		head.setPackageLength(Integer.toString(body.length() + 102));
		log.debug("请求报文包头:"+head.toString());
		log.debug("请求报文包体:"+body.toString());
		
		return head.toString() + body;
	}
	
	/**
	 * 初始化命令包头
	 *
	 */
	private void initHead(){
		head.setFlag("1");
		head.setVersion("11");
       	head.setDeviceNumberType(this.getDeviceNumberType());  // T--4G，G--2、3G，F固话 ，T与G、F 在营帐是不同接口
		head.setPlace("");
		head.setPackageNumber("00001");
		head.setEndFlag("1");
		head.setErrorCode("00000");
		head.setNumber(this.getSequenceNum());
	}
	
	public String getDeviceNumber(){
		return head.getDeviceNumber();
	}
	
	
	
	public String getDeviceNumberType(){
		return head.getDeviceNumberType();
	}
	
	/**
	 * 生成查询命令的序列(20位长)
	 * @return
	 */
	private String getSequenceNum(){
		String number = "";
		Calendar calendar = new GregorianCalendar();
		Date now = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		try{
			number = format.format(now);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "XXDH"+number+this.getRand();
	}
	
	/**
	 * 取两位随机数
	 * @return
	 */
	private  String getRand() {
		   Random random = new Random();
		   Integer randNumber = random.nextInt();
		   randNumber=100+Math.abs(randNumber);
		   String result=Integer.toString(randNumber).substring(0, 2);
		   return result;
	}

	/**
	 * 实现字节拷贝
	 * @param abyte0
	 * @param abyte1
	 * @param i 源起始
	 * @param j 拷贝字节数
	 * @param k 目标起始
	 */
	protected static void BytesCopy(byte from[], byte to[], int i, int j,
			int k) {
		int m = 0;
		for (int l = i; l < j+i; l++) {
			to[k + m] = from[l];
			m++;
		}
	}
	
	/**
	 * 空格占位符号填充
	 */
	protected static String spaceFill(String source, int length){
		StringBuffer buffer = new StringBuffer();
		buffer.append(source);
		for(int i=source.length() ; i<length ;i++){
			buffer.append(" ");	
		}
		return buffer.toString();
	}
}