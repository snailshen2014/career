package com.bonc.busi.orderschedule.utils;

import java.io.Serializable;
import redis.clients.jedis.JedisPoolConfig;

public class JodisProperties implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * zookeeper 地址
     */
    private String zkPath;

    /**
     * zookeeper超时时间
     */
    private int zkTimeout;

    /**
     * 产品名称
     */
    private String product;

    /**
     * 连接redis密码
     */
    private String password;

    /**
     * 连接池最大连接数
     */
    private int poolTotal = JedisPoolConfig.DEFAULT_MAX_TOTAL;

    /**
     * 最大空闲数
     */
    private int poolMaxIdle = JedisPoolConfig.DEFAULT_MAX_IDLE;

    /**
     * 最小空闲数
     */
    private int poolMinIdle = JedisPoolConfig.DEFAULT_MIN_IDLE;

    private String zkProxyDir;
    
//    private static volatile JodisProperties instance=null;
//    private JodisProperties() {}
//    
//    public static  JodisProperties newInstance() {
//    	 if(instance== null){  
//            synchronized (Singleton.class) {  
//    			if (instance == null) {  
//    				instance = new JodisProperties();  
//    			}  

//         }  
//         return instance;  
//    	
//	}
    //静态内部类实现单例模式
    private static class SingletonHolder{  
        public static JodisProperties instance = new JodisProperties();  
    }  
    private JodisProperties(){}  
    public static JodisProperties newInstance(){  
        return SingletonHolder.instance;  
    }  
     
    
	public String getZkPath() {
		return zkPath;
	}

	public void setZkPath(String zkPath) {
		this.zkPath = zkPath;
	}

	public int getZkTimeout() {
		return zkTimeout;
	}

	public void setZkTimeout(int zkTimeout) {
		this.zkTimeout = zkTimeout;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPoolTotal() {
		return poolTotal;
	}

	public void setPoolTotal(int poolTotal) {
		this.poolTotal = poolTotal;
	}

	public int getPoolMaxIdle() {
		return poolMaxIdle;
	}

	public void setPoolMaxIdle(int poolMaxIdle) {
		this.poolMaxIdle = poolMaxIdle;
	}

	public int getPoolMinIdle() {
		return poolMinIdle;
	}

	public void setPoolMinIdle(int poolMinIdle) {
		this.poolMinIdle = poolMinIdle;
	}

	public String getZkProxyDir() {
		return zkProxyDir;
	}

	public void setZkProxyDir(String zkProxyDir) {
		this.zkProxyDir = zkProxyDir;
	}

	@Override
	public String toString() {
		return "JodisProperties [zkPath=" + zkPath + ", zkTimeout=" + zkTimeout + ", product=" + product + ", password="
				+ password + ", poolTotal=" + poolTotal + ", poolMaxIdle=" + poolMaxIdle + ", poolMinIdle="
				+ poolMinIdle + ", zkProxyDir=" + zkProxyDir + "]";
	}

}
