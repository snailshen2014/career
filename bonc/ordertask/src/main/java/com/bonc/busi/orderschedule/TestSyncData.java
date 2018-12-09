package com.bonc.busi.orderschedule;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonc.busi.orderschedule.utils.BlackWhiteUser;
import com.bonc.busi.orderschedule.utils.SyncWhiteBlackUserData;

@Component
public class TestSyncData {
	
	
	
//	@Scheduled(cron = "0 0/2 * * * ?")
    public void syncData() {
		System.out.println("ddddd");
		BlackWhiteUser userList = new BlackWhiteUser();
		SyncWhiteBlackUserData syncData = new SyncWhiteBlackUserData(userList);
		syncData.start();
		while (true) {
			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(userList.isExistsUser("15517516685", 0));
			System.out.println(userList.isExistsUser("15517516685", 1));
		}
		
	}
		
}
