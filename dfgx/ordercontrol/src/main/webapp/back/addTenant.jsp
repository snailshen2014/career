<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>

<head>
    <title>渠道配置中心</title>
    <%@include file="../common/common.jsp" %>
    <script src="${ctx }messenger/build/js/messenger.js"></script>
    <script src="${ctx }messenger/build/js/messenger-theme-future.js"></script>
    <style>
        h3 {
            font-weight: normal;

        }

        .update {
            color: #333;
        }

        .remove {
            color: red;
        }

        .alert {
            padding: 0 14px;
            margin-bottom: 0;
            display: inline-block;
        }

        hr {
            height: 50px;
        }
    </style>
</head>
<body>
<%@include file="nav.jsp" %>
<div class="container">
    <h1 style="text-align: center;margin-top: 80px"> 添加租户信息</h1>
    <p class="text-left">
       <H3>工单数据初始化说明:</H3> <br>
        1.确保base和业务库mysql用户有权限操作数据<br>
        2.在点击确认前，要确保基础base库的表结构已导入<br>
        并且手工导入activity_id_info，sys_sequence，tenant_info的基础数据<br>
        3.注意将基础base库下的sys_common_cfg原始<strong>数据备份</strong>！！！<br>
        4.如果是基础base库中新增的租户需要选择全量，反之选增量<br>
    </p>
    <br>
    <div>
        <label class="control-label">要创建的租户id</label>
        <input id="tenantId" type="text" class="form-control" placeholder="输入租户编码--注意检查不要输入错误！！！">
        <br>
    </div>
    <div>
        <label class="control-label">要创建的租户名称</label>
        <input id="tenantName" type="text" class="form-control" placeholder="输入租户名称">
        <br>
    </div>
    <div>
        <label class="control-label">要创建的租户省份id</label>
        <input id="provId" type="text" class="form-control" placeholder="输入租户省份id">
        <br>
    </div>
    <div class="checkbox">
        <label>
            <input id="isStructure" type="checkbox" value="">
            是否需要初始化业务表结构
        </label>
    </div>
    <div class="checkbox">
        <label>
        <input id="isdianxinData" type="checkbox" value="">
        是否需要初始化电信业务表静态数据
    </label>
        <label>
            <input id="isliantongData" type="checkbox" value="">
            是否需要初始化联通业务表静态数据
        </label>
        <label>
            <input id="isdianxinywData" type="checkbox" value="">
            是否需要初始化电信异网业务表静态数据
        </label>
        <label>
            <input id="isliantongywData" type="checkbox" value="">
            是否需要初始化联通异网业务表静态数据
        </label>
    </div>
    <br>
    <form class="form-horizontal">
        <h2 style="text-align: center">SYS_CFG表配置</h2>
        <div class="checkbox">
            <label>
                <input id="isFullInit" type="checkbox" value="">
                是否全量添加SYS_CFG表数据 （默认增量添加不包含公用静态变量）
            </label>
            <div class="changediv">
                <label>
                    <input id="isLiantong" type="checkbox" value="">
                    是否联通 （默认电信）
                </label>
                <label>
                    <input id="isYW" type="checkbox" value="">
                    是否是异网 （默认本网）
                </label>
            </div>
        </div>
        <hr>
        <h3>MYSQL配置--上面所选的业务选项将根据此MYSQL配置初始化</h3>
        <div>
            <label class="col-sm-2 control-label">MYSQL_Url</label>
            <div  class="col-sm-10">
                <input class="form-control" type="text" id="MYSQL_Url" placeholder="jdbc:mysql://192.168.30.27:31606/mthenan?useUnicode=true&characterEncoding=utf-8&autoCommit=true&useSSL=false ">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">MYSQL_USER</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="MYSQL_USER" placeholder="MYSQL_USER">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">MYSQL_PASS</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="MYSQL_PASS" placeholder="MYSQL_PASS">
            </div>
        </div>
        <hr>
        <hr>
        <h3>行云配置</h3>
        <div>
            <label class="col-sm-2 control-label">XCloud_Url</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="XCloud_Url" placeholder="jdbc:xcloud:@192.168.0.162:1803/jingzhunhua@192.168.0.164:1803/jingzhunhua@192.168.0.172:1803/jingzhunhua@192.168.0.178:1803/jingzhunhua?connectRetry=3&socketTimeOut=36000000&connectDirect=false ">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">XCloud_USER</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="XCloud_USER" placeholder="XCloud_USER">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">XCloud_PASS</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="XCloud_PASS" placeholder="XCloud_PASS">
            </div>
        </div>
        <hr>
        <hr>
        <h3>FTP配置</h3>
        <div>
            <label class="col-sm-2 control-label">FTP_Url</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="FTP_Url" placeholder="FTP_Url ">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">FTP_Port</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="FTP_Port" placeholder="FTP_Port">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">FTP_USER</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="FTP_USER" placeholder="FTP_USER">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">FTP_PASS</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="FTP_PASS" placeholder="FTP_PASS">
            </div>
        </div>
        <div class="changediv">
            <label class="col-sm-2 control-label">FTP_LocalPath</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="FTP_LocalPath" placeholder="FTP_LocalPath">
            </div>
        </div>
        <div class="changediv">
            <label class="col-sm-2 control-label">FTP_remotePath</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="FTP_remotePath" placeholder="FTP_remotePath">
            </div>
        </div>
        <div class="changediv">
            <label class="col-sm-2 control-label">FTP_UserRemotePath</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="FTP_UserRemotePath" placeholder="FTP_UserRemotePath">
            </div>
        </div>
        <hr>
        <hr>
        <hr>
        <hr>
        <div id = "urlDiv">
        <h3>外部资源调用服务名称配置</h3>
        <div>
            <label class="col-sm-2 control-label">ordertask2</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="ordertask2" placeholder="ordertask2:8080/ordertask2 ">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">ordertasksche2</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="ordertasksche2" placeholder="ordertasksche2:8080/ordertasksche2">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">ordertask-service</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="ordertaskservice" placeholder="ordertaskservice:8080/ordertaskservice">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">channelmanager</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="channelmanager" placeholder="channelmanager:8080/channelmanager">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">资源划配</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="assignment" placeholder="epmwxwl:8080/epmwxwl">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">活动测接口、获取账期 </label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="xepmservices" placeholder="activityInter:8080/activityInter">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">获取用户群</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="xlabel" placeholder="activityInter:8080/activityInter">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">场景营销调去短信接口 </label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="smsinterface" placeholder="smsinterface:8080/smsinterface">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">渠道协同 </label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="channelcoord" placeholder="channelcoord:8080/channelcoord">
            </div>
        </div>
        <div>
            <label class="col-sm-2 control-label">场景营销</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="scenemarketing" placeholder="scenemarketing:8080/scenemarketing">
            </div>
        </div>
        </div>
    </form>

    <button id="createTenant" type="button" class="btn btn-primary submit" style="float: inherit">确认</button><span class="alert"></span>

</div>


<script type="text/javascript">

    $(function () {
        $alert = $('.alert');

        $('#urlDiv').hide();
        $('.changediv').hide();
        $('#isFullInit').change(function () {
            if ($('#isFullInit').prop('checked')){
                $('#urlDiv').show();
                $('.changediv').show();
            }else {
                $('#urlDiv').hide();
                $('.changediv').hide();
            }
        })

        $('#createTenant').on('click', function () {
            var rows = {}
            rows.tenantId = $('#tenantId').val().trim();
            rows.tenantName = $('#tenantName').val().trim();
            rows.provId = $('#provId').val().trim();
            if ($('#isStructure').prop('checked')) {
                rows.isStructure = 'true';
            } else {
                rows.isStructure = 'false';
            }
            if ($('#isliantongData').prop('checked')) {
                rows.isliantongData = 'true';
            } else {
                rows.isliantongData = 'false';
            }
            if ($('#isdianxinData').prop('checked')) {
                rows.isdianxinData = 'true';
            } else {
                rows.isdianxinData = 'false';
            }
            if ($('#isliantongywData').prop('checked')) {
                rows.isliantongywData = 'true';
            } else {
                rows.isliantongywData = 'false';
            }
            if ($('#isdianxinywData').prop('checked')) {
                rows.isdianxinywData = 'true';
            } else {
                rows.isdianxinywData = 'false';
            }
            var $form = $('.form-horizontal')
            $form.find('input[id]').each(function () {
                rows[$(this).attr('id')] = $(this).val();
            })
            if ($('#isFullInit').prop('checked')) {
                rows.isFullInit = 'true';
            } else {
                rows.isFullInit = 'false';
            }
            if ($('#isLiantong').prop('checked')) {
                rows.isLiantong = 'true';
            } else {
                rows.isLiantong = 'false';
            }
            if ($('#isYW').prop('checked')) {
                rows.isYW = 'true';
            } else {
                rows.isYW = 'false';
            }

            if (confirm('确定租户id是' + rows.tenantId + '吗？')) {
                $.ajax({
                    url: '${ctx}back/initTenantData',
                    type: 'post',
                    contentType: 'application/json',
                    data: JSON.stringify(rows),
                    success: function (msg) {
                        showAlert('创建成功', 'success');
                    },
                    error: function (msg) {
                        showAlert('创建失败', 'danger');
                    }
                })

            }
        })
    })
    function showAlert(title, type) {
        $alert.attr('class', 'alert alert-' + type || 'success')
            .html('<i class="glyphicon glyphicon-check"></i> ' + title).show();
        setTimeout(function () {
            $alert.hide();
        }, 3000);
    }

</script>


</body>


</html>