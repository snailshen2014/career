package com.bonc.busi.orderschedule.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import  com.bonc.busi.task.base.Global;
/**
 * mannager order file,split it by special lines
 * @author yanjunshen
 * 2017-04-12 15:00
 */

public class OrderFileMannager {
	//record file number and black user number
//	private static Integer iOrignalNum = 0;
	private static  final  ThreadLocal<Integer> iOrignalNum = new ThreadLocal<Integer>(){
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};
	/*
	 * get order numbers from order file
	 */
	public static Integer getOrderNumber() { return iOrignalNum.get();}

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
		List<String> splitFileList = new ArrayList<String>();
		int iRowNum = 0;
		int iTotalNum = 0;
		int iBlackNum = 0;
		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(strOrigFileName), "UTF-8"));
			Integer resultFileSeq = 1;
			String splitFileName = strOrigFileName + "_" + resultFileSeq++;

			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(splitFileName, false), "UTF-8");

			while (true) {
				String line = br.readLine();
				if (line != null) {
					++iTotalNum;
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
					iOrignalNum.set(iTotalNum);

//					System.out.println("[OrderCenter] finished read file,fileNum=" + iTotalNum );

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
					break;
				}
			} // --- while ---

		} catch (Exception e) {
			e.printStackTrace();
			// read file exception
			System.out.println("[OrderCenter] read file exception.");
			iOrignalNum.set(iTotalNum);
		}

		return splitFileList;
	}

	/*
	 * delete file
	 */
	public static void deleteFile(String fileName) {
		// delete file

		try {
			File deleteFile = new File(fileName);
			if (deleteFile.delete()) {
//				System.out.println(deleteFile.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
