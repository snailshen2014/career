package com.bonc.channelapi.hbase.constant;


import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 配置文件类
 * 
 * @author caiqiang
 * @version 2016年8月3日
 * @see ValidateConf
 */
@ConfigurationProperties(prefix = "validate")
public class ValidateConf {
    /**
     * MD5密钥
     */
    private String key;

    /**
     * 是否验证token
     */
    private boolean flag;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

}
