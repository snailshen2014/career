<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<style>
</style>
<head>
    <title>工单使用率监控</title>
    <%@include file="../common/common.jsp"%>
    <%@include file="nav.jsp"%>
    <link rel="stylesheet" href="${ctx }messenger/build/css/messenger.css" />
    <link rel="stylesheet" href="${ctx }messenger/build/css/messenger-theme-future.css" />
</head>
<body>
<!-- Fixed navbar -->
<!-- 
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${ctx }">工单使用率监控</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="back/cfgConfig">工单配置信息</a></li>
                <li><a href="monitor">工单生成执行监控</a></li>
                <li><a href="back/tableUsed">工单表使用率监控</a></li>
                <li><a href="">工单生成依赖服务测试</a></li>
                <li class="dropdown">
                    <a href="#" class="" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">选择租户 <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav> -->
<h1 style="text-align: center;margin-top: 80px"> 工单表使用情况 </h1>

<div class="container"  >
    <div id="toolbar">
        <input id="addCapacityBtn" class="btn btn-success" type='button' value='扩容'/>
    </div>
    <table id="used-table"
           data-toolbar="#toolbar"
           data-toggle="table"
           data-url="${ctx}back/tableUsedList?tanantId=${sessionScope.tenantId}"
           data-height="450"
           data-side-pagination="server"
           data-page-list="[5, 10, 20, 50, 100, 200]">
        <thead>
            <tr>
                <th data-field="TENANT_ID">租户ID</th>
                <th data-field="TABLE_NAME">表名</th>
                <th data-field="MAX_NUM">最大容量</th>
                <th data-field="USED_NUM" >使用量</th>
                <th data-field="USED_RATE" >使用率</th>
            </tr>
        </thead>
    </table>
</div>
<input type='hidden' value='${ItanantId}' id='tenantId'/>
</body>

<script type="text/javascript">
<%--
    $(function () {
        var ItanantId
            $.ajax({
                url:basePath+"back/getTenantId",
                type:"get",
                success:function(data){
                    var tenant = JSON.parse(data)
                    ItanantId=tenant[0].TENANT_ID
                    $('#used-table').attr("data-url","${ctx}back/tableUsedList"+ItanantId)
                    $('#tenantId').val(ItanantId)
                    for (var msg in tenant){
                        var txt1 = $("<li></li>").append($("<a></a>").text(tenant[msg].TENANT_NAME))
                            .attr("id",tenant[msg].TENANT_ID)
                            .attr("onclick","reloadTable('"+tenant[msg].TENANT_ID+"')")
                        $('.dropdown-menu').append(txt1)
                    }
                }
            });
    })

    function reloadTable( tenantId) {
        console.log(tenantId)
        $.ajax({
            url:basePath+"back/tableUsedList",
            type:"get",
            data:"ItanantId="+${sessionScope.tenantId},
            dataType : "json",
            success : function(data, textStatus) {
                console.log(textStatus);
                $('#used-table').bootstrapTable('load', data);
            }
        });

    }
 --%>
 $(function(){
	 $('#addCapacityBtn').click(function(){
		 if(confirm("确定扩容吗？")){
				$.ajax({
					url:'${ctx}back/addTableCapacity?tenantId=${sessionScope.tenantId}',
					 type:'GET',
					 dataType:'json',
					 success:function(data,textStatus,jqXHR){
					        if(data.code == '0000'){
					        	console.log("扩容成功");
					        	var opt={
                                   url:"${ctx}back/tableUsedList?tanantId=${sessionScope.tenantId}",
                                   silent:true		   
					        	};
					        	$('#used-table').bootstrapTable('refresh',opt);
					        }
					    }
				});
			}
	 });
 });
</script>


</html>