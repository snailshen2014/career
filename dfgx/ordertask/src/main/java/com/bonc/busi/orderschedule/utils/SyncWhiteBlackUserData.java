package com.bonc.busi.orderschedule.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * sync data
 */
public class SyncWhiteBlackUserData extends Thread{
	private final static Logger log = LoggerFactory.getLogger(SyncWhiteBlackUserData.class);
	//white black user data
	private final BlackWhiteUser userData;
	
	public SyncWhiteBlackUserData(BlackWhiteUser data) {
		this.userData = data;
	}
	
	@Override
    public void run() {
        while (true) {
        	//get sysdate from system yyyy-MM-dd
        	Date now = new Date();
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    		String sysDate = dateFormat.format(now);
    		//every data sync data
    		if(!sysDate.equals(userData.getSyncTime())) {
    			log.info("[OrderCenter] sync data begin...");
    			this.userData.initUserSet();
    			log.info("[OrderCenter] sync data end...");
    		}
    		else {
    			log.info("[OrderCenter] sync data waitting,current date :" + sysDate + 
    					",data Sync time: " + userData.getSyncTime());
    			//waitting
    			try {
					Thread.sleep(3600 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
        }
    }
	
}
