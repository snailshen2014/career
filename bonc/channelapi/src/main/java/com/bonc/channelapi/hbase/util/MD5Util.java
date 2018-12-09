package com.bonc.channelapi.hbase.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * md5算法工具类
 * 
 * @author caiqiang
 * @version 2016年8月3日
 * @see MD5Util
 * @since
 */
public class MD5Util {
    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(MD5Util.class);

    /**
     * Description:实现MD5算法生成32位大写密文
     * 
     * @param plainText
     * @return String
     * @see
     */
    public static String textToMD5L32(String plainText) {
        String result = null;
        // 首先判断是否为空
        if (StringUtils.isEmpty(plainText)) {
            LOG.error("please input ciphertext");
            return null;
        }
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] btInput = plainText.getBytes();

            md.update(btInput);

            byte[] btResult = md.digest();

            StringBuffer sb = new StringBuffer();
            for (byte b : btResult) {
                int bt = b & 0xff;
                if (bt < 16) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(bt));
            }
            result = sb.toString();
            result = result.toUpperCase();
        }
        catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage());
            return null;
        }
        return result;
    }

}
