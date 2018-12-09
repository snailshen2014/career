package com.bonc.busi.orderschedule.utils;

import com.bonc.busi.orderschedule.bo.WhiteBlackFilterUser;
import com.bonc.busi.orderschedule.mapper.OrderMapper;
import com.bonc.busi.task.base.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
 * balck white user list,current only black user
 */
public class BlackWhiteUser {
    private final static Logger log = LoggerFactory.getLogger(BlackWhiteUser.class);
    //set 50w initial capacity
    private final Set<String> whiteUserSet = Collections.synchronizedSet(new HashSet<String>());
    private final Set<String> blackUserSet = Collections.synchronizedSet(new HashSet<String>());
    List<Callable<Integer>> callList = new ArrayList<Callable<Integer>>();
    //sync status
    private int syncStatus = 0;
    //sync time
    private String syncTime;

    //get spring bean instance
    private OrderMapper ordermapper = SpringUtil.getBean(OrderMapper.class);

    public BlackWhiteUser() {
        initUserSet();
    }

    public synchronized void initUserSet() {
        // 线程数
        int threadCounts = 10;

        this.syncStatus = 0;
        //reset white black user set
        if (this.whiteUserSet.size() > 0)
            this.whiteUserSet.clear();
        if (this.blackUserSet.size() > 0)
            this.blackUserSet.clear();

        ExecutorService exec = Executors.newFixedThreadPool(threadCounts);
        //黑白名单总数
        Integer usersSize = ordermapper.getWhitBlackUserSize();
        // 执行次数(按每个sql查询20000算)
        int step = usersSize/ threadCounts/ 20000;
        if (step == 0) step = 1;
        int len = usersSize / threadCounts /step;//平均分割
        //用户的数量没有线程数多（很少存在）
        if (len == 0) {
            len = usersSize;//重新平均分割
        }
        for (int i = 0; i < threadCounts*step; i++) {
            final Integer begin;
            final Integer end;
            begin = i * len;
            if (i == threadCounts*step - 1) {
                end = usersSize;
            } else {
                end = i * len + len;
            }
            //采用匿名内部类实现
            callList.add(new Callable<Integer>() {
                public Integer call() throws Exception {
                    List<WhiteBlackFilterUser> userList = new ArrayList<>();
                        userList = ordermapper.getWhitBlackUserList(begin, end);
                        for (WhiteBlackFilterUser user : userList) {
                            //black
                            if (user.getFILTE_TYPE().equals("0"))
                                blackUserSet.add(user.getUSER_PHONE());
                                //white
                            else if (user.getFILTE_TYPE().equals("1"))
                                whiteUserSet.add(user.getUSER_PHONE());
                            else
                                log.error("[OrderCenter] filte_type error:" + user.getFILTE_TYPE() + " phone_number:"
                                        + user.getUSER_PHONE());
                        }
                        log.info("[OrderTask] BlackWhite  data :begin " + begin + ",'" +
                                "end:" +  end  + " load ok. ") ;
                    return userList.size();
                }
            });
        }
        Integer blackSum = 0;
        try {
            List<Future<Integer>> futureList = exec.invokeAll(callList);
            for (Future<Integer> future : futureList) {
                blackSum += future.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        exec.shutdown();
        log.info("黑名单表中数据为：{},线程共处理了{},blackUserSet大小为{}" , usersSize,blackSum,blackUserSet.size());
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.syncTime = dateFormat.format(now);
        log.info("[OrderCenter] already initial finished, white user size=" + this.whiteUserSet.size()
                + ",black user size=" + this.blackUserSet.size());
        this.syncStatus = 1;
    }

    /*
     * set sync data time
     * @param date yyyy-MM-dd
     */
    public void setSyncTime(String date) {
        this.syncTime = date;
    }

    /*
     * get sync data time
     * @param
     * @return datetime yyyy-mm-dd
     */
    public String getSyncTime() {
        return this.syncTime;
    }

    /*
     * judge white black user
     * @param type 0:black;1:white
     * @return boolean
     */
    public boolean isExistsUser(String phone, int type) {
        //data on synchronization
        while (true) {
            if (this.syncStatus == 1)
                break;
            else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                log.error("[OrderCenter] Whithe black user data on synchronization  waiit...");
            }

        }

        //type:0;black user;1:white user
        return type == 0 ? this.blackUserSet.contains(phone) : this.whiteUserSet.contains(phone);
    }
}
