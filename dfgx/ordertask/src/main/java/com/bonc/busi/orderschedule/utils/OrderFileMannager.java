package com.bonc.busi.orderschedule.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bonc.busi.orderschedule.service.impl.OrderServiceImpl;
import  com.bonc.busi.task.base.Global;
public class OrderFileMannager {
	//record file number and black user number
	private static Integer iOrignalNum = 0;
	private static Integer iBlackUserNum = 0;
	/*
	 * get order numbers from order file
	 */
	public static Integer getOrderNumber() { return iOrignalNum;}
	public static Integer getBlackUserOrderNumber() {return iBlackUserNum;}
	private static final Logger logger = Logger.getLogger(OrderFileMannager.class);
	/*
	 * split file by splitNum,a splitNum rows one file
	 * 
	 * @param strOrigFileName
	 * 
	 * @param splitNum
	 * 
	 * @filterBlackUser
	 * 
	 * @return after splitting file list
	 */
	public static List<String> splitFile(String strOrigFileName, int splitNum, boolean filterBlackUserList) {
		logger.info("[OrderCenter] filterBlackUserList is =  "+ filterBlackUserList);
		List<String> splitFileList = new ArrayList<String>();
		int iRowNum = 0;
		int iTotalNum = 0;
		int iBlackNum = 0;
		BufferedReader br = null;
		String blackFileName = strOrigFileName + "_black";
		OutputStreamWriter oswBlack = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(strOrigFileName), "UTF-8"));
			Integer resultFileSeq = 1;
			String splitFileName = strOrigFileName + "_" + resultFileSeq++;

			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(splitFileName, false), "UTF-8");
			oswBlack = new OutputStreamWriter(new FileOutputStream(blackFileName, false), "UTF-8");

			while (true) {
				String line = br.readLine();
				if (line != null) {
					++iTotalNum;
					// black user
					if (filterBlackUserList && isBlackUser(line)) {
						++iBlackNum;
						oswBlack.write(line);
						oswBlack.write('\n');
						continue;
					}
					osw.write(line);
					osw.write('\n');
					++iRowNum;
					if (iRowNum >= splitNum) {
						// add before file name
						splitFileList.add(splitFileName);
						try {
							iRowNum = 0;
							osw.flush();
							osw.close();
							// create new file
							splitFileName = strOrigFileName + "_" + resultFileSeq++;
							osw = new OutputStreamWriter(new FileOutputStream(splitFileName, false), "UTF-8");
						} catch (Exception e) {
							e.printStackTrace();
							br.close();
						}
					}

				} else { // readline end of
					//set number
					iOrignalNum = iTotalNum;
					iBlackUserNum = iBlackNum;
					
					System.out.println(
							"[OrderCenter] finished read file,fileNum=" + iTotalNum + " blackNum=" + iBlackNum);

					// last file
					if (iRowNum > 0) {
						splitFileList.add(splitFileName);
						iRowNum = 0;
						osw.flush();
						osw.close();
					} else {
						// delete empty file
						deleteFile(splitFileName);
					}
					br.close();
					if (iBlackNum > 0) {
						splitFileList.add(blackFileName);
					} else {
						// delete black user file
						deleteFile(blackFileName);
					}
					// close black user file
					oswBlack.flush();
					oswBlack.close();
					break;
				}
			} // --- while ---

		} catch (Exception e) {
			e.printStackTrace();
			// read file exception
			System.out.println("[OrderCenter] read file exception.");
		}

		return splitFileList;
	}

	private static boolean isBlackUser(String row) {
		int idex = 0;
		int times = 0;
		String tmp = row;
		String phone = null;
		while ((idex = tmp.indexOf("|")) != -1) {
			times++;
			tmp = tmp.substring(idex + 1);
			// find phone_number field
			if (times == 6) {
				phone = tmp.substring(0, tmp.indexOf("|"));
				break;
			}

		}

		// judge black user
		return Global.userList.isExistsUser(phone, 0);

	}
	
	/*
	 * delete file
	 */
	private static void deleteFile(String fileName) {
		// delete file

		try {
			File deleteFile = new File(fileName);
			if (deleteFile.delete()) {
				System.out.println(deleteFile.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
