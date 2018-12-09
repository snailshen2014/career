package com.bonc.busi.orderschedule.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class JodisPoolConfiguration {
	
	private final static Logger LOG= LoggerFactory.getLogger(JodisPoolConfiguration.class);
	 public JedisResourcePool createJedisPool() {
		 
		 JodisProperties jp=JodisProperties.newInstance();
			String zkPath = jp.getZkPath();
			String product = jp.getProduct();
			String password = jp.getPassword();
			int  zkTimeOut = jp.getZkTimeout();
			String zkProxyDir = jp.getZkProxyDir();
			int poolTotal = jp.getPoolTotal();
			int poolMaxIdle = jp.getPoolMaxIdle();
			int  poolMinIdle = jp.getPoolMinIdle();
			LOG.info("zkPath：{},product：{}，password：{}，zkProxyDir：{}",zkPath, product,password ,zkProxyDir);
			LOG.info("zkTimeOut ：{}，pooltotal：{},maxidle：{},minidle：{}", zkTimeOut ,poolTotal, poolMaxIdle, poolMinIdle);
			
			LOG.debug("zkPath:{}, product:{},zkProxyDir", zkPath, product, zkProxyDir);
			LOG.debug("pooltotal:{},maxidle:{},minidle:{}", poolTotal, poolMaxIdle, poolMinIdle);
			// Jedis 配置信息
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxTotal(poolTotal);
			jedisPoolConfig.setMaxIdle(poolMaxIdle);
			jedisPoolConfig.setMinIdle(poolMinIdle);
		 return RoundRobinJedisPool.create()
					.curatorClient(zkPath, zkTimeOut)
					.zkProxyDir(zkProxyDir)
					.password(password)
					.poolConfig(jedisPoolConfig)
					.build();
	 }
}
