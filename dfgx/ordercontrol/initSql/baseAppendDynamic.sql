-- mysql配置
INSERT INTO SYS_COMMON_CFG VALUES ('DS.MYSQL.URL.TTTTTENANTID','MYSQL_Url','租户工单mysql url');
INSERT INTO SYS_COMMON_CFG VALUES ('DS.MYSQL.USER.TTTTTENANTID','MYSQL_USER','租户工单mysql 用户名');
INSERT INTO SYS_COMMON_CFG VALUES ('DS.MYSQL.PASSWORD.TTTTTENANTID','MYSQL_PASS','租户工单mysql密码');
-- 行云配置 --
INSERT INTO SYS_COMMON_CFG VALUES ('DS.XCLOUD.URL.TTTTTENANTID','XCloud_Url','租户行云url');
INSERT INTO SYS_COMMON_CFG VALUES ('DS.XCLOUD.USER.TTTTTENANTID','XCloud_USER','租户行云用户名');
INSERT INTO SYS_COMMON_CFG VALUES ('DS.XCLOUD.PASSWORD.TTTTTENANTID','XCloud_PASS','租户行云密码');
-- 工单ftp配置 --
INSERT INTO SYS_COMMON_CFG VALUES ('HDFSSRV.IP.TTTTTENANTID','FTP_Url','工单ftp_ip地址--用于工单数据行云出库 用户群数据行云出库');
INSERT INTO SYS_COMMON_CFG VALUES ('HDFSSRV.PASSWORD.TTTTTENANTID','FTP_PASS','工单ftp_ip密码');
INSERT INTO SYS_COMMON_CFG VALUES ('HDFSSRV.PORT.TTTTTENANTID','FTP_Port','工单ftp_ip端口号');
INSERT INTO SYS_COMMON_CFG VALUES ('HDFSSRV.USER.TTTTTENANTID','FTP_USER','工单ftp_ip用户名');

