<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../common/common.jsp"%>
<%@include file="nav.jsp"%>

<title>用户标签数据同步监控</title>
</head>
<style>
	.table tbody tr td{
      overflow: hidden; 
      text-overflow:ellipsis;  
      white-space: nowrap; 
 }
</style>
<body>

	<div class="container body tab-top">
		<h1 style="text-align: center;margin-top: 20px"> 用户标签数据同步监控
    </h1>
		<table id="table"  class="table table-bordered"></table>
		<!--<table id="table" 
			data-toggle="table" 
			data-height="460" 
			data-show-columns="true" 
			data-search="true" 
			data-striped="true" 
			data-show-refresh="true" 
			data-pagination ="true" 
	        data-side-pagination="server" 
	        data-pageSize="10" 
	        data-pageNumber="1" 
	        data-queryParamsType="" 
	        data-locale="zh-CN" 
			data-url="${ctx}/asyn/asynUserLabel?tenantId=${sessionScope.tenantId}"
			data-page-list="[5, 10, 20, 50, 100, 200]">
			<thead>
				<tr>
					<th data-field="ID">ID</th>
					<th data-field="TENANT_ID">租户ID</th>
					<th data-field="LOG_TIME">日志时间</th>
					<th data-field="APP_NAME">所属系统</th>
					<th data-field="BUSI_ITEM_1">日志要素1</th>
					<th data-field="BUSI_ITEM_2">活动批次id</th>
					<th data-field="BUSI_ITEM_3">日志要素2</th>
					<th data-field="BUSI_ITEM_4">日志要素3</th>
					<th data-field="BUSI_ITEM_5">标识</th>
					<th data-field="LOG_MESSAGE">日志内容</th>

				</tr>
			</thead>
		</table>-->
	</div>
</body>
<script type="text/javascript">
	//根据窗口调整表格高度
	$(window).resize(function() {
		$('#table').bootstrapTable('resetView', {
			height: tableHeight()
		})
	});
	 //tableHeight函数
    function tableHeight(){
        //可以根据自己页面情况进行调整
        return $(window).height() -100;
    }

	//生成用户数据
	$('#table').bootstrapTable({
		
		method: 'get',
		url: "${ctx}asyn/asynuserlabelmonitor?tenantId=${sessionScope.tenantId}",  //要请求数据的文件路径
		height: tableHeight(), //高度调整
		showColumns: false,
		showRefresh: true, //刷新按钮
		showToggle: false,
		striped: true, //是否显示行间隔色
		pageNumber: 1, //初始化加载第一页，默认第一页
		pageSize: 10, //单页记录数
		pagination: true, //是否分页
		locale:'zh-CN',//中文支持,
		queryParamsType: '', //查询参数组织方式
		queryParams: queryParams, //请求服务器时所传的参数
		sidePagination: 'server', //指定服务器端分页
		pageList:[10, 25, 50, 100],
		columns: [{
			title: '租户',
			field: 'TENANT_ID',
			align: 'center',
			valign: 'middle'
		}, {
			title: '日志时间',
			field: 'START_TIME',
			align: 'center',
			valign: 'middle'
		},{
			title: '数据同步过程',
			field: 'BUSI_DESC',
			align: 'center',
			valign: 'middle',
		},{
			title: '详情描述',
			field: 'BUSI_ITEM_1',
			align: 'left',
			valign: 'middle',
		}]
	})
	
    //请求服务数据时所传参数
    function queryParams(params){
        return{
        	pageSize: params.pageSize,  //页面大小
            pageNumber: params.pageNumber //页码
           
        }
    }
</script>
</html>