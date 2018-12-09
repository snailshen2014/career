package com.bonc.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtil {

	/**
	 * 在指定路径下创建文件
	 */
	public static File createFile(String path,String fileName){
		File f = new File(path);
		if(!f.exists()){		
			f.mkdirs();
		} 
		String date = TimeUtil.formatTime(IContants.DATE_FORMAT);
		fileName = fileName + date;
		File file = new File(f,fileName); 
		
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}		
		return file;
    }
	
	/**
	 * 在指定文件中添加记录
	 */
	public static File writeFile(File file,String content){
			
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file,true);
			fileWriter.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fileWriter != null){
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
		
	}
	
	public static String getAppPath(){
		
		File file = new File("");
		String appPath = file.getAbsolutePath();
		appPath.replace("\\","/");
		return appPath;
		
	}
	
	/**
	 * 写入内容到文件
	 * 
	 * @param number
	 * @param filename
	 * @return
	 */
	public static boolean writeFile(String c, String dirname,String filename, boolean isAppend) {
		File f = new File(dirname);
		if (!f.exists()) {
			f.mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(dirname+ File.separator + filename, isAppend);
			OutputStreamWriter writer = new OutputStreamWriter(fos);
			writer.write(c);
			writer.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
