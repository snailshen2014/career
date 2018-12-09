INSERT INTO SYS_COMMON_CFG VALUES ('ORDERCHECK.FILTER.RUNFLG.TTTTTENANTID','FALSE','事后成功检查启动标识');
INSERT INTO SYS_COMMON_CFG VALUES ('ORDERCHECK.SUCCESS.XCLOUD.DATEID.TTTTTENANTID','20161228','事后成功检查--最大账期');
INSERT INTO SYS_COMMON_CFG VALUES ('ORDER_RUNNING_TTTTTENANTID','0','工单是否正在运行标识--在运行的时候是1 没有运行是0 如果程序出错或者强制停止服务导致状态无法回0');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNBLACKANDWHITE.EFFECTIVE_PARTITION.TTTTTENANTID','0','黑白名单数据同步-有效数据分区标识');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNBLACKANDWHITE.RUN.FLAG.TTTTTENANTID','FALSE','黑白名单数据同步标识');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNUSER.MYSQL.EFFECTIVE_PARTITION.TTTTTENANTID','0','用户数据同步-租户有效的数据分区标识');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNUSER.RUN.FLAG.TTTTTENANTID','FALSE','用户数据正在同步标识');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNPRODUCTSAVE.RUN.FLAG.TTTTTENANTID', 'FALSE', '受理成功数据记录处理标识');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNUSER.XCLOUD.DATEID.TTTTTENANTID','20161228','用户数据同步-租户最大账期');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNUSER.XCLOUD.SQLDATA.TTTTTENANTID', 'EXPORT SELECT ''TTTTTENANT_ID'' TENANT_ID , PROV_ID , AREA_ID , USER_ID , DEVICE_NUMBER , REPLACE( REPLACE( CUST_NAME , ''\\'' , ''/'' ) , ''"'' ,'''') CUST_NAME , CASE WHEN BALANCE_FEE IS NULL THEN ''0'' ELSE TO_CHAR( ROUND( BALANCE_FEE * 100 ) ) END BALANCE_FEE , CASE WHEN KD_BALANCE_FEE IS NULL THEN ''0'' ELSE TO_CHAR( ROUND( KD_BALANCE_FEE * 100 ) ) END KD_BALANCE_FEE , USER_STATUS ,USER_TYPE DATA_TYPE, MB_PACKAGE_ID , KD_PACKAGE_ID , MB_FIRST_OWE_MONTH , KD_FIRST_OWE_MONTH , OWE_FLAG , CASE WHEN MB_OWE_FEE IS NULL THEN ''0'' ELSE TO_CHAR(ROUND( MB_OWE_FEE * 100 )) END MB_OWE_FEE , KD_OWE_FEE, CASE WHEN MB_OWE_MONTHS IS NULL THEN 0 ELSE MB_OWE_MONTHS END MB_OWE_MONTHS, MB_ARPU,ELECCHANNEL_FLAG,CASE WHEN RENT_FEE > 96 THEN ''是'' ELSE ''否'' END HIGH_96_FLAG,CASE WHEN RENT_FEE IS NULL THEN ''0'' ELSE TO_CHAR(ROUND(RENT_FEE * 100 ) ) END RENT_FEE,MB_MIX_FLAG,MB_CUST_TYPE,CASE WHEN MB_ONLINE_DUR IS NULL THEN 0 ELSE MB_ONLINE_DUR END MB_ONLINE_DUR,MB_VALUE_LEVEL,MB_PRODUCT_TYPE , MB_NETIN_CHANNEL , MB_AGREEMENT_TYPE,REPLACE( REPLACE( MB_AGREEMENT_NAME,''\\'', ''/''),''"'','''')MB_AGREEMENT_NAME,MB_AGREEMENT_BEGIN_TIME,MB_AGREEMENT_END_TIME,CASE WHEN MB_AGREEMENT_REST_MONTHS IS NULL THEN ''0'' ELSE TO_CHAR( ROUND( MB_AGREEMENT_REST_MONTHS * 100 ) ) END MB_AGREEMENT_REST_MONTHS ,CONTACT_TELEPHONE_NO , MB_NET_TYPE , CASE WHEN MB_AVERAGE_FLOW IS NULL THEN ''0'' ELSE TO_CHAR( ROUND( MB_AVERAGE_FLOW * 100))END MB_AVERAGE_FLOW,MB_TERMINAL_BRAND , MB_NET_MODE , KD_AGREEMENT_END_TIME , KD_CONTINUE_AGREEMENT_FLAG , KD_SUB_PACKAGE_NAME , KD_MIX_TYPE , KD_MIX_BEGIN_TIME, KD_MIX_MOBILE_NO , KD_MIX_FIX_NO , KD_CUR_RATE , CASE WHEN KD_NETIN_MONTHS IS NULL THEN 0 ELSE KD_NETIN_MONTHS END KD_NETIN_MONTHS , KD_OWNER_AREA, REPLACE( REPLACE(KD_ADDR_SIX_NAME,''\\'',''/''),''"'','''') KD_ADDR_SIX_NAME ,KD_NETIN_HALL,KD_CONTINUE_AGREEMENT_TIME, REPLACE( REPLACE(KD_CUST_MANAGER,''\\'',''/'' ),''"'','''') KD_CUST_MANAGER , MB_SIM_TYPE , MB_CHG_MACHINE_FLAG, CASE WHEN REAL_NAME_FLAG IS NULL THEN ''0'' ELSE REAL_NAME_FLAG END REAL_NAME_FLAG, MB_ADDCARD_TYPE, MB_4G_NET_FLAG,CHR_30,PAY_MODE,PRODUCT_BASE_CLASS, ACTIVITY_TYPE,WENDING_FLAG,ACCT_FEE , CO_B_01 FROM PLT_USER_LABEL WHERE ACCT_DAY =''DDDDDATEID'' AND PROV_ID = ''PPPPPROV_ID'' ATTRIBUTE(LOCATION(''FFFFFILENAME'') SEPARATOR(''0x1A''))', '用户数据同步-导出数据sql');
INSERT INTO SYS_COMMON_CFG VALUES ('ORDERFAILURE.RUNNING.FLAG.TTTTTENANTID', 'FALSE', '工单失效运行标识（电信）');
INSERT INTO SYS_COMMON_CFG VALUES ('CTCC_JSON_TABLE_PREFIX_TTTTTENANTID', 'a.', '电信报文前缀（成功标准用）');
INSERT INTO SYS_COMMON_CFG VALUES ('CUCC_JSON_TABLE_PREFIX_TTTTTENANTID', 'UNICOM_D_MB_DS_ALL_LABEL_INFO.', '联通报文前缀（成功标准用）');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNUSER.MYSQL.CREATETABLE.TTTTTENANTID',' /*!mycat:sql=select * FROM PLT_USER_LABEL WHERE TENANT_ID = ''TTTTTENANT_ID''  */ CREATE TABLE PLT_USER_LABEL_PPPPPART (TENANT_ID VARCHAR (20),PROV_ID CHAR (3),AREA_ID VARCHAR (20),USER_ID VARCHAR (100) NOT NULL,DEVICE_NUMBER VARCHAR (100) NOT NULL,CUST_NAME VARCHAR (100),BALANCE_FEE VARCHAR(200),KD_BALANCE_FEE VARCHAR(200),USER_STATUS VARCHAR (100) , DATA_TYPE VARCHAR (100) ,MB_PACKAGE_ID VARCHAR (100),KD_PACKAGE_ID VARCHAR (100),MB_FIRST_OWE_MONTH  VARCHAR (100),KD_FIRST_OWE_MONTH  VARCHAR (100),OWE_FLAG VARCHAR (2),MB_OWE_FEE VARCHAR(100),KD_OWE_FEE VARCHAR(200),MB_OWE_MONTHS VARCHAR(100),MB_ARPU VARCHAR(100),ELECCHANNEL_FLAG VARCHAR(30),HIGH_96_FLAG VARCHAR(1),RENT_FEE VARCHAR(200),MB_MIX_FLAG VARCHAR(100),MB_CUST_TYPE VARCHAR (100),MB_ONLINE_DUR VARCHAR(100),MB_VALUE_LEVEL VARCHAR (100),MB_PRODUCT_TYPE VARCHAR (100),MB_NETIN_CHANNEL VARCHAR (100),MB_AGREEMENT_TYPE VARCHAR (100),MB_AGREEMENT_NAME VARCHAR (100),MB_AGREEMENT_BEGIN_TIME VARCHAR (100),MB_AGREEMENT_END_TIME VARCHAR (100),MB_AGREEMENT_REST_MONTHS VARCHAR (100),CONTACT_TELEPHONE_NO VARCHAR (100) ,MB_NET_TYPE VARCHAR(200),MB_AVERAGE_FLOW VARCHAR(100),MB_TERMINAL_BRAND VARCHAR (100) ,MB_NET_MODE VARCHAR (100) ,KD_AGREEMENT_END_TIME VARCHAR (100) ,KD_CONTINUE_AGREEMENT_FLAG VARCHAR(200),KD_SUB_PACKAGE_NAME VARCHAR(200),KD_MIX_TYPE VARCHAR (100) ,KD_MIX_BEGIN_TIME VARCHAR (100) ,KD_MIX_MOBILE_NO VARCHAR (100),KD_MIX_FIX_NO VARCHAR (100) ,KD_CUR_RATE VARCHAR (100),KD_NETIN_MONTHS VARCHAR(200),KD_OWNER_AREA VARCHAR (100),KD_ADDR_SIX_NAME VARCHAR (1000) ,KD_NETIN_HALL VARCHAR (100),KD_CONTINUE_AGREEMENT_TIME VARCHAR(200),KD_CUST_MANAGER VARCHAR(200),MB_SIM_TYPE VARCHAR (100) ,MB_CHG_MACHINE_FLAG VARCHAR(200),REAL_NAME_FLAG VARCHAR (2),MB_ADDCARD_TYPE VARCHAR (100),MB_4G_NET_FLAG VARCHAR(200),CHR_30 VARCHAR (2),PAY_MODE VARCHAR (100),PRODUCT_BASE_CLASS VARCHAR (100) ,ACTIVITY_TYPE VARCHAR (100) ,WENDING_FLAG VARCHAR (100),ACCT_FEE VARCHAR(200) ,CO_B_01 VARCHAR(200) ,PRIMARY KEY (USER_ID),INDEX IDX_DEVICE_NUMBER (DEVICE_NUMBER)) ENGINE = INNODB DEFAULT CHARSET = utf8 ','用户数据同步-建表sql');
INSERT INTO SYS_COMMON_CFG VALUES ('ASYNUSER.MYSQL.SQLDATA.TTTTTENANTID', 'LOAD DATA LOCAL INFILE ''FFFFFILENAME'' REPLACE INTO TABLE PLT_USER_LABEL_PPPPPART FIELDS TERMINATED BY ''\\Z'' LINES TERMINATED BY ''\\n'' (TENANT_ID,PROV_ID,AREA_ID,USER_ID,DEVICE_NUMBER,CUST_NAME,BALANCE_FEE,KD_BALANCE_FEE,USER_STATUS,DATA_TYPE,MB_PACKAGE_ID,KD_PACKAGE_ID,MB_FIRST_OWE_MONTH,KD_FIRST_OWE_MONTH,OWE_FLAG,MB_OWE_FEE,KD_OWE_FEE,MB_OWE_MONTHS, MB_ARPU,ELECCHANNEL_FLAG,HIGH_96_FLAG,RENT_FEE,MB_MIX_FLAG,  MB_CUST_TYPE,MB_ONLINE_DUR,MB_VALUE_LEVEL,  MB_PRODUCT_TYPE,MB_NETIN_CHANNEL,MB_AGREEMENT_TYPE,MB_AGREEMENT_NAME, MB_AGREEMENT_BEGIN_TIME,MB_AGREEMENT_END_TIME, MB_AGREEMENT_REST_MONTHS,CONTACT_TELEPHONE_NO, MB_NET_TYPE,MB_AVERAGE_FLOW,MB_TERMINAL_BRAND,MB_NET_MODE, KD_AGREEMENT_END_TIME,KD_CONTINUE_AGREEMENT_FLAG,KD_SUB_PACKAGE_NAME, KD_MIX_TYPE,KD_MIX_BEGIN_TIME,KD_MIX_MOBILE_NO,KD_MIX_FIX_NO, KD_CUR_RATE,KD_NETIN_MONTHS,KD_OWNER_AREA, KD_ADDR_SIX_NAME,KD_NETIN_HALL,KD_CONTINUE_AGREEMENT_TIME,KD_CUST_MANAGER,MB_SIM_TYPE,MB_CHG_MACHINE_FLAG,REAL_NAME_FLAG,  MB_ADDCARD_TYPE,MB_4G_NET_FLAG,CHR_30,PAY_MODE,PRODUCT_BASE_CLASS,ACTIVITY_TYPE,WENDING_FLAG,ACCT_FEE,CO_B_01)', '用户数据同步-导入数据sql');
INSERT INTO SYS_COMMON_CFG VALUES ('ORDER.USERLABEL.UPDATE.RUNFLAG.TTTTTENANTID', 'FALSE', '工单用户资料更新标识');
INSERT INTO SYS_COMMON_CFG VALUES ('ORDER.USERLABEL.UPDATE.SQL.UPDATE.TTTTTENANTID', '/*!mycat:sql=select * FROM PLT_ACTIVITY_INFO WHERE TENANT_ID = ''TTTTTENANT_ID''*/UPDATE TABLEAAAAA a,TABLEBBBBB b SET a.USERLABEL_RESERVE3 = b.CUST_NAME,a.USERLABEL_RESERVE4 = b.MB_ARPU,a.USERLABEL_RESERVE5 = b.MB_ONLINE_DUR,a.USERLABEL_RESERVE6 = b.MB_OWE_FEE,a.USERLABEL_RESERVE9 = b.KD_OWE_FEE,a.USERLABEL_RESERVE14 = b.MB_FIRST_OWE_MONTH, a.USERLABEL_RESERVE15 = b.RENT_FEE,a.USERLABEL_RESERVE18 = b.KD_NETIN_MONTHS,a.USERLABEL_RESERVE44 = b.ACCT_FEE,a.USER_ID = b.USER_ID,a.TENANT_ID = b.TENANT_ID,a.USERLABEL_RESERVE31 = b.KD_ADDR_SIX_NAME, a.USERLABEL_RESERVE43 = b.WENDING_FLAG WHERE a.USER_ID = b.USER_ID AND a.REC_ID >= MINID AND a.REC_ID < MAXID ', '工单用户资料更新-更新语句sql');
INSERT INTO SYS_COMMON_CFG VALUES ('ORDERCHECK.SUCESS.RUNFLG.TTTTTENANTID', 'FALSE', '事后成功标准检查标识');