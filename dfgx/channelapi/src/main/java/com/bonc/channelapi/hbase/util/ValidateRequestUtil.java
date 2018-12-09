package com.bonc.channelapi.hbase.util;


import javax.servlet.http.HttpServletRequest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bonc.channelapi.hbase.constant.ValidateConf;


/**
 * token验证工具类
 * 
 * @author caiqiang
 * @version 2016年8月3日
 * @see ValidateRequestUtil
 * @since
 */
public class ValidateRequestUtil {
    /**
     * 日志对象
     */
//    private static final Logger LOG = LoggerFactory.getLogger(ValidateRequestUtil.class);

    /**
     * Description:验证token
     * 
     * @param request
     * @return boolean
     */
    public static boolean validate(HttpServletRequest request) {
        ValidateConf validateConf = ApplicationUtil.getBean(ValidateConf.class);
        if (!validateConf.isFlag()) {
            return true;
        }
        String timestamp = request.getParameter("timestamp");
        String token = request.getParameter("token");
        if (!StringUtils.isEmpty(timestamp) && !StringUtils.isEmpty(token)) {
            // 此处加密返回的是32位的密文
            if (token.equals(MD5Util.textToMD5L32(timestamp + validateConf.getKey()))) {
                return true;
            }
        }
        return false;
    }

}
