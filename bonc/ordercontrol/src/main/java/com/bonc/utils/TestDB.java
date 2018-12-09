package com.bonc.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDB {

	public static void main(String[] args) {

		// Test(); // 生成测试数据
		// Exp();
		Exp(0);
		// System.out.println(readText("/opt/id.txt"));
	}
	
	/**
	 * 导出数据
	 */
	public static void Exp() {

		Connection Conn = null;

		try {

			Class.forName("com.MySQL.jdbc.Driver").newInstance();
			String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/henan0?characterEncoding=UTF-8";
			// String jdbcUsername = "root";
			// String jdbcPassword = "mysql";
			Conn = DriverManager.getConnection(jdbcUrl, "root", "root");

			System.out.println("conn" + Conn);

			Exp(Conn);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				Conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void Exp(int startid) {
		Connection Conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/henan0?characterEncoding=UTF-8";
			String jdbcUsername = "root";
			String jdbcPassword = "root";
			Conn = DriverManager.getConnection(jdbcUrl, jdbcUsername,jdbcPassword);
			System.out.println("conn" + Conn);
			Exp(Conn, startid);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				Conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 导出从startid开始的数据
	 * 
	 * @param conn
	 * @param start_id
	 */
	public static void Exp(Connection conn, int start_id) {
		String Sql = "SELECT PHONE_NUMBER,ORDER_CONTENT FROM plt_channel_order_list WHERE CHANNEL_ID='a' LIMIT 20";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(Sql);
			while (rs.next()) {
				String PHONE_NUMBER = rs.getString("PHONE_NUMBER");
				String ORDER_CONTENT = rs.getString("ORDER_CONTENT");
				writeContent(PHONE_NUMBER+ "`" + ORDER_CONTENT + "\n",IContants.SMS_PATH, "log.txt", true);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出一小时内的数据
	 * 
	 * @param conn
	 */

	public static void Exp(Connection conn) {
		int counter = 0;
		// 一小时内的数据
		boolean flag = true;
		while (flag) {
			flag = false;
			String Sql = "SELECT PHONE_NUMBER,ORDER_CONTENT FROM plt_channel_order_list WHERE CHANNEL_ID='a' ";

			System.out.println("sql===" + Sql);
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(Sql);
				while (rs.next()) {
					flag = true;
					String PHONE_NUMBER = rs.getString("PHONE_NUMBER");
					String ORDER_CONTENT = rs.getString("ORDER_CONTENT");
					counter++;
					System.out
							.println("i=" + counter + "--PHONE_NUMBER--"
									+ PHONE_NUMBER + "--ORDER_CONTENT-"
									+ ORDER_CONTENT);
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void Test() {

		Connection Conn = null;

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/henan0?characterEncoding=UTF-8";
			String jdbcUsername = "root";
			String jdbcPassword = "root";
			Conn = DriverManager.getConnection(jdbcUrl, jdbcUsername,
					jdbcPassword);

			System.out.println("conn" + Conn);

			for (int i = 1; i <= 10000; i++) {
				add(Conn, "testTitle" + i + "-" + System.currentTimeMillis());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				Conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void add(Connection conn, String title) {
		PreparedStatement pstmt = null;
		String insert_sql = "insert into t_test(title,createTime) values (?,?)";

		System.out.println("sql=" + insert_sql);
		try {
			pstmt = conn.prepareStatement(insert_sql);
			pstmt.setString(1, title);
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 写入内容到文件
	 * 
	 * @param number
	 * @param filename
	 * @return
	 */
	public static boolean writeContent(String c, String dirname,
			String filename, boolean isAppend) {
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

	/**
	 * 从文件读取内容
	 * 
	 * @param filename
	 * @return
	 */
	public static String readText(String filename) {
		String content = "";
		try {
			File file = new File(filename);
			if (file.exists()) {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String str = "";
				String newline = "";
				while ((str = br.readLine()) != null) {
					content += newline + str;
					newline = "\n";
				}
				br.close();
				fr.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
