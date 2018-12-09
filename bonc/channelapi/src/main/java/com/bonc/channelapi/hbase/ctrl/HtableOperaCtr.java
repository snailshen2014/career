package com.bonc.channelapi.hbase.ctrl;


import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.channelapi.hbase.client.HBaseClient;
import com.bonc.channelapi.hbase.client.HbaseOperateClient;
import com.bonc.channelapi.hbase.constant.BaseConstant;
import com.bonc.channelapi.hbase.constant.HbaseConstant;
import com.bonc.channelapi.hbase.entity.JsonResult;


@RestController
@RequestMapping("/v1")
public class HtableOperaCtr {
    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(HtableOperaCtr.class);

    /**
     * Description:创建Htable
     * 
     * @return Object
     * @see
     */
    @SuppressWarnings("resource")
    @RequestMapping(value = "/createTable", method = RequestMethod.GET)
    public Object createHTable() {

        try {
            HBaseAdmin hBaseAdmin = new HBaseAdmin(HBaseClient.getConfiguration());

            if (!hBaseAdmin.tableExists(HbaseConstant.TABLE_QDXT_CONTACT)) {

                HTableDescriptor hTableDescriptor = new HTableDescriptor(
                    TableName.valueOf(HbaseConstant.TABLE_QDXT_CONTACT));
                HColumnDescriptor hFamliyDescriptor = new HColumnDescriptor("f");
                hTableDescriptor.addFamily(hFamliyDescriptor);
                hBaseAdmin.createTable(hTableDescriptor);
            }
            else {
                return new JsonResult("5000", "table already exists");
            }

            hBaseAdmin.close();
            return new JsonResult("2000", "建表成功");
        }
        catch (IOException e) {
            LOG.error("错误信息" + e.getMessage());
            return new JsonResult(BaseConstant.CODE_ERROR, BaseConstant.MSG_ERROR);
        }
    }

    /**
     * Description:删除Htable
     * 
     * @return Object
     * @see
     */
    @RequestMapping(value = "/deleteTable", method = RequestMethod.GET)
    public Object deleteTable() {

        try {

            HbaseOperateClient.deleteTable(HbaseConstant.TABLE_QDXT_CONTACT);
        }
        catch (IOException e) {
            LOG.info("delete error  ", e);
            return new JsonResult("4004", "删除未成功");
        }
        catch (Exception e) {
            LOG.error("错误信息" + e.getMessage());
            return new JsonResult(BaseConstant.CODE_ERROR, BaseConstant.MSG_ERROR);
        }

        return new JsonResult("200", "删除成功");
    }

}
