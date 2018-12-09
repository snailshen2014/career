package com.bonc.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bonc.busi.entity.ActivityBo;
import com.bonc.busi.entity.CostPo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerUtil {

	private static String ROOT_PATH = "";

	private static Logger log4j = Logger.getLogger(FreemarkerUtil.class);

	static {
		try {
			ROOT_PATH = URLDecoder.decode(FreemarkerUtil.class.getResource("/").getPath(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Template getTemplate(String relativePath, String name) {
		try {
			// 通过Freemaker的Configuration读取相应的ftl
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
			cfg.setDefaultEncoding("utf-8");
			// 设置模板读取的路径
			cfg.setDirectoryForTemplateLoading(new File(ROOT_PATH + relativePath));

			// 在模板文件目录中找到名称为name的文件
			Template temp = cfg.getTemplate(name);

			return temp;
		} catch (IOException e) {
			// e.printStackTrace();
			log4j.error("读取模板文件出错。。。");
		}
		return null;
	}

	/**
	 * 读取模板文件，整合map数据对象，生成新的文件
	 * 
	 * @param relativePath
	 *            模板的相对路径
	 * @param name
	 *            模板文件名称
	 * @param root
	 *            模板数据map对象
	 * @param output
	 *            整合模板文件和数据map对象，生成的文件输入到stringWriter对象中
	 * @return 成功返回true，异常返回false
	 */
	public static boolean print(String relativePath, String name, Map<String, Object> root, StringWriter output) {
		try {
			// 通过Template可以将模板文件输出到相应的流
			Template temp = FreemarkerUtil.getTemplate(relativePath, name);

			if (temp != null) {
				temp.process(root, new PrintWriter(output));
			}
			// temp.process(root, new PrintWriter(new File("E:/1.txt")));
		} catch (TemplateException e) {
			e.printStackTrace();
			log4j.error("模板文件异常。。。");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			log4j.error("IO异常。。。");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			log4j.error("模板文件数据生成文件错误。。。");
			return false;
		}
		return true;
	}


}