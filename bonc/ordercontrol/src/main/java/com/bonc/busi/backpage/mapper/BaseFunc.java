package com.bonc.busi.backpage.mapper;

import com.bonc.busi.backpage.bo.CreateTenantBo;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Matcher;

/**
 * Created by MQZ on 2017/8/23.
 */
@Service
public class BaseFunc {
    private static final  Logger log = LoggerFactory.getLogger(BaseFunc.class);
    private String LocalPath = System.getProperty("user.dir") + "\\initSql\\";
    private BufferedWriter writer = null;
    private BufferedReader reader = null;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String pass;
    @Value("${spring.datasource.url}")
    private String url;
    /*
      * 在MYCat上执行 sql 文件
      */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean executeDdlOnMycat(String sqlPath) {
        Boolean bReturn = true;
        Connection conn = getConnection();
        try(FileInputStream stream = new FileInputStream(sqlPath)) {
//            String url = isBase?cfg.getBaseDatabaesURL() : cfg.getMYSQL_Url();
//            String username = isBase?cfg.getBaseDatabaesName() : cfg.getMYSQL_USER();
//            String password = isBase ? cfg.getBaseDatabaesPass() :cfg.getMYSQL_PASS();
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            conn = DriverManager.getConnection(url, username, password);
            ScriptRunner runner = new ScriptRunner(conn);
            runner.runScript(new BufferedReader(new InputStreamReader( stream)));
        } catch (Exception e) {
            log.error("在MYSQL上执行DDL语句出错 !!!");
            log.error("CONTEXT",e);
            bReturn = false;
        }
        return bReturn;
    }
        /*
      * 在MYSql上执行 sql 文件
      */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean executeDdlOnMysql(String sqlPath, CreateTenantBo tenant) {
        Boolean bReturn = true;
        Connection conn = getConnection();
        try(FileInputStream stream = new FileInputStream(sqlPath)) {
            String url = tenant.getMYSQL_Url();
            String username = tenant.getMYSQL_USER();
            String password = tenant.getMYSQL_PASS();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, username, password);
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setStopOnError(true);
            runner.runScript(new BufferedReader(new InputStreamReader( stream)));
        } catch (Exception e) {
            log.error("在MYSQL上执行DDL语句出错 !!!");
            log.error("CONTEXT",e);
            bReturn = false;
        }
        return bReturn;
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            log.error("CONTEXT",e);
        }
        return conn;
    }

    /**
     * 替换SQL文件中的租户id
     *
     * @param tenantId
     * @param sqlPath  原始sqlPath
     * @return 返回临时path
     */
    public String replaceSQLbyTenantId(String tenantId, String sqlPath) {
        String tempPath = LocalPath + tenantId + "replaceTenantId.sql";
        try (Writer out = new FileWriter( new File(tempPath));FileInputStream stream = new FileInputStream(sqlPath)){
            writer = new BufferedWriter(out);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                String temp = line.replaceAll("TTTTTENANTID", tenantId);
                writer.write(temp);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            log.error("CONTEXT",e);
        }
        return tempPath;
    }
    /**
     * 替换SQL文件中的租户id
     *
     * @param tenantId
     * @param sqlPath  原始sqlPath
     * @return 返回临时path
     */
    public String removeMycatSQL(String tenantId, String sqlPath) {
        String tempPath = LocalPath + tenantId + "replaceTenantId.sql";
        try (Writer out = new FileWriter( new File(tempPath));FileInputStream stream = new FileInputStream(sqlPath)){
            writer = new BufferedWriter(out);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("/*")&&line.contains("mycat")) continue;
                String temp = line.replaceAll("TTTTTENANTID", tenantId);
                writer.write(temp);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            log.error("CONTEXT",e);
        }
        return tempPath;
    }

    /**
     * 替换SQL文件中的数据库参数
     *
     * @param tenantId
     * @param sqlPath  原始sqlPath
     * @param cfg
     * @return 返回临时path
     */
    public String replaceSQLbyParams(String tenantId, String sqlPath, CreateTenantBo cfg) {
        String tempPath = LocalPath + tenantId + "replaceParams.sql";
        try(Writer out = new FileWriter( new File(tempPath));FileInputStream stream = new FileInputStream(sqlPath)) {
            writer = new BufferedWriter(out);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                String temp = line.replaceAll("TTTTTENANTID", tenantId);
                temp = temp.replaceAll("MYSQL_Url", cfg.getMYSQL_Url());
                temp = temp.replaceAll("MYSQL_USER", cfg.getMYSQL_USER());
                temp = temp.replaceAll("MYSQL_PASS", Matcher.quoteReplacement(cfg.getMYSQL_PASS()));
                temp = temp.replaceAll("XCloud_Url", cfg.getXCloud_Url());
                temp = temp.replaceAll("XCloud_USER", cfg.getXCloud_USER());
                temp = temp.replaceAll("XCloud_PASS", Matcher.quoteReplacement(cfg.getXCloud_PASS()));
                temp = temp.replaceAll("FTP_Url", cfg.getFTP_Url());
                temp = temp.replaceAll("FTP_PASS", Matcher.quoteReplacement(cfg.getFTP_PASS()));
                temp = temp.replaceAll("FTP_Port", cfg.getFTP_Port());
                temp = temp.replaceAll("FTP_USER", cfg.getFTP_USER());
                writer.write(temp);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            log.error("CONTEXT",e);
        }
        return tempPath;
    }


    /**
     * 替换SQL文件中的url类参数
     *
     * @param tenantId
     * @param sqlPath  原始sqlPath
     * @param cfg
     * @return 返回临时path
     */
    public String replaceSQLbyURL(String tenantId, String sqlPath, CreateTenantBo cfg) {
        String tempPath = LocalPath + tenantId + "replaceUrl.sql";
        try (Writer out = new FileWriter( new File(tempPath));FileInputStream stream = new FileInputStream(sqlPath)){
            writer = new BufferedWriter(out);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                String temp = line.replaceAll("TTTTTENANTID", tenantId);
                temp = temp.replaceAll("aaaaactivity", cfg.getXepmservices());
                temp = temp.replaceAll("xxxxxassignment", cfg.getAssignment());
                temp = temp.replaceAll("ooooordertasksche2", cfg.getOrdertasksche2());
                temp = temp.replaceAll("ccccchannelmanager", cfg.getChannelmanager());
                temp = temp.replaceAll("ccccchannelcoord", cfg.getChannelcoord());
                temp = temp.replaceAll("ooooordertask2", cfg.getOrdertask2());
                temp = temp.replaceAll("ooooordertaskservice", cfg.getOrdertaskservice());
                temp = temp.replaceAll("xxxxxlabel", cfg.getXlabel());
                temp = temp.replaceAll("ssssscenemarketing", cfg.getScenemarketing());
                temp = temp.replaceAll("sssssmsinterface", cfg.getSmsinterface());
                writer.write(temp);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            log.error("CONTEXT",e);
        }
        return tempPath;
    }
    /**
     * 往SQL文件中追加sql语句
     * @param sqls:追加的sql语句
     * @param sqlPath:被追加的文件的路径
     */
    public void appendSQLFile(String sqls, String sqlPath/*, String delimiter*/) {
        try(Writer out = new FileWriter(sqlPath, true)){
//          String[] sqlArray = sqls.split(delimiter);
//          for(String str:sqlArray){
//              out.append("\r");
                out.append(sqls);
//          }
            out.flush();
        }catch(IOException e){
            log.error("CONTEXT",e);
        }
    }
    /**
     * 创建.sql文件
     * @param fileName
     */
    public String createFile(String prefixName){
        String fileName = prefixName + "temp.sql"; 
        log.info("create file:" + LocalPath + fileName);
        //先判断路径是否存在
        File dir = new File(LocalPath);
        if(!dir.exists()){
        	dir.mkdirs();
        }
        dir.mkdirs();
        //再创建文件
        File file = new File(LocalPath, fileName);
        if(file.exists()){
        	file.delete();
        }
        try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return LocalPath + fileName;
    }

    /*
      * 删除文件
      */
    public void deleteFile(String fileName) {
        log.info("delete fileName:" + fileName);
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

}
