package com.bonc.common.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    public static List<String> dataSourceIds = new ArrayList<>();
    private static final ThreadLocal<Stack<String>> contextHolderOld = new ThreadLocal<Stack<String>>();

    public static void setOldDataSourceType(String dataSourceType) {
    	if(contextHolderOld.get()==null){
    		contextHolderOld.set(new Stack<String>());
    	}
    	contextHolderOld.get().push(dataSourceType);
    }

    public static String getOldDataSourceType() {
    	String oldDataSource=null;
    	if(contextHolderOld.get()!=null){
	    	if(!contextHolderOld.get().isEmpty()){
	    		  oldDataSource= contextHolderOld.get().pop();
	    		if(contextHolderOld.get().isEmpty()){
	    			contextHolderOld.remove();
	    		}
	    	}else{
	    		contextHolderOld.remove();
	    	}
    	}
    	return oldDataSource;
    }
    public static void setDataSourceType(String dataSourceType) {
        contextHolder.set(dataSourceType);
    }

    public static String getDataSourceType() {
        return contextHolder.get();
    }

    public static void clearDataSourceType() {
        contextHolder.remove();
    }

    /**
     * 判断指定DataSrouce当前是否存在
     *
     * @param dataSourceId
     * @return
     * @author SHANHY
     * @create  2016年1月24日
     */
    public static boolean containsDataSource(String dataSourceId){
        return dataSourceIds.contains(dataSourceId);
    }
}