<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}/"></c:set>
<!DOCTYPE html>
<html>

<head>
	<%@include file="../common/common.jsp"%>
	<%@include file="nav.jsp"%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>事后成功标准检查监控</title>
</head>
<style>
	.table tbody tr td {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		table-layout: fixed;
	}
</style>

<body>
<!--style="table-layout: fixed;"-->
	<div class="container body tab-top">
		<h1 style="text-align: center;margin-top: 20px">事后成功标准检查监控</h1>

		<div id="search" class="input-group col-md-3" style="margin-top:0px ;positon:relative ;float:right;">
			<input id="searchtext" type="text" class="form-control" placeholder="账期" />
			<span class="input-group-btn">  
               <button id="search_btn" class="btn btn-primary">搜索</button>  
            </span>
		</div>
		</div>
		<div class=" container body table-responsive">
		<table id="table" class="table table-bordered text-nowrap" ></table>
		</div>


	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document" style="width: 1000px;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">×</span>
				</button>
					<h4 class="modal-title" id="myModalLabel">事后成功标准检查详情</h4>
				</div>
				<div class="modal-body">
					<div class="table-responsive">
					<table id="monitorTable" class="table table-responsive text-nowrap" ></table>
				</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>

</body>
<script type="text/javascript">
	//根据窗口调整表格高度
	$(window).resize(function() {
		$('#table').bootstrapTable('resetView', {
			height: tableHeight()
		})
	});

	function operateFormatter(value, row, index) {
		return ['<a class="col-sm-offset-1" href="#"  data-toggle="modal" data-target="#myModal" data-dataid="' + row.DATEID + '" data-begintime="' + row.BEGIN_TIME + '"  data-endtime="' + row.END_TIME + '" ><i title="查看" class="glyphicon glyphicon-camera"></i></a>']
			.join('');
	}
	//tableHeight函数
	function tableHeight() {
		//可以根据自己页面情况进行调整
		return $(window).height() - 200;
	}

	//生成用户数据
	$('#table').bootstrapTable({

		method: 'get',
		url: "${ctx}success/successcheck?tenantId=${sessionScope.tenantId}", //要请求数据的文件路径
		height: tableHeight(), //高度调整
		striped: true, //是否显示行间隔色
		pageNumber: 1, //初始化加载第一页，默认第一页
		pageSize: 5, //单页记录数
		pagination: true, //是否分页
		locale: 'zh-CN', //中文支持,
		queryParamsType: '', //查询参数组织方式
		queryParams: queryParams, //请求服务器时所传的参数
		sidePagination: 'server', //指定服务器端分页
		pageList: [5, 10, 25, 50],

		columns: [{
			title: '账期',
			field: 'DATEID',
			align: 'center',
			width: 'auto',
			valign: 'middle'
		}, {
			title: '持续时间',
			field: 'FOR_TIME',
			align: 'center',
			width: 'auto',
			valign: 'middle'
		}, {
			title: '开始时间',
			field: 'BEGIN_TIME',
			align: 'center',
			valign: 'middle'
		}, {
			title: '开始日志',
			field: 'BEGIN_MESSAGE',
			align: 'center',
			valign: 'middle'
		}, {
			title: '结束时间',
			field: 'END_TIME',
			align: 'center',
			valign: 'middle',
		}, {
			title: '结束日志',
			field: 'END_MESSAGE',
			align: 'center',
			valign: 'middle',
		},{
			title: '成功检查状态',
			field: 'CHECK_STATUS',
			align: 'center',
			valign: 'middle',
			
			formatter:function(value,row,index){
			  if(value=='success'){
				  return "<div class=\"list-group-item list-group-item-success\">检查完成</div>";
			  } 
			  if(value=='failed') {
				  return "<div class=\"list-group-item list-group-item-danger\">检查失败</div>";
			  }
			  if(value=='running') {
				  return "<div class=\"list-group-item list-group-item-info\">检查中</div>";
			  }			  
			}
		}, {
			field: 'operate',
			title: '查看详情',
			align: 'center',
			valign: 'middle',
			formatter: operateFormatter
		}]
	})
	$('#myModal').on('show.bs.modal',
		function(event) {
			var button = $(event.relatedTarget);
			var dataid = button.data('dataid');
			var begintime = button.data('begintime');
			var endtime = button.data('endtime');
			var tenantId = '${sessionScope.tenantId}';
console.log("dataid="+dataid);
			url = "${ctx}success/successcheckdetail?dateId=" + dataid + "&beginTime=" + begintime + "&tenantId=" + tenantId + "&endTime=" + endtime;
			initTable(url);
			$('#monitorTable').bootstrapTable('refresh');
		}
	);

	//关闭时销毁
	$('#myModal').on('hide.bs.modal',
		function(event) {
			url = null;
			$('#monitorTable').bootstrapTable('destroy');
		}
	);

	//请求服务数据时所传参数
	function queryParams(params) {
		return {
			pageSize: params.pageSize, //页面大小
			pageNumber: params.pageNumber, //页码
			dateId: $("#searchtext").val()

		}
	}
	//请求服务数据时所传参数
	function queryDetailParams(params) {
		return {
			pageSize: params.pageSize, //页面大小
			pageNumber: params.pageNumber//页码
			
		}
	}
	$('#search').on('click', '#search_btn',
		function() {
			$('#table').bootstrapTable('refresh', {
				url: '${ctx}success/successcheck?tenantId=${sessionScope.tenantId}'
			});

		}
	);

	function initTable(url) {
		$('#monitorTable').bootstrapTable({
			toolbar: '#toolbar',
			url: url,
			method: 'GET',
			striped: true, //是否显示行间隔色
			pageNumber: 1, //初始化加载第一页，默认第一页
			pageSize: 10, //单页记录数
			pagination: true, //是否分页
			locale: 'zh-CN', //中文支持,
			queryParamsType: '', //查询参数组织方式
			queryParams: queryDetailParams, //请求服务器时所传的参数
			sidePagination: 'server', //指定服务器端分页
			pageList: [10, 25, 50, 100],
			height: tableHeight(),
			columns: [{
				title: 'ID',
				field: 'ID',
				align: 'center',

				valign: 'middle'
			}, {
				title: '账期',
				field: 'BUSI_ITEM_4',
				align: 'center',

				valign: 'middle'
			}, {
				title: '活动批次ID',
				field: 'BUSI_ITEM_2',
				align: 'center',

				valign: 'middle'
			}, {
				title: 'BUSI_ITEM_3',
				field: 'BUSI_ITEM_3',
				align: 'center',

				valign: 'middle'
			}, {
				title: 'BUSI_ITEM_1',
				field: 'BUSI_ITEM_1',
				align: 'center',

				valign: 'middle'
			}, {
				title: '日志时间',
				field: 'LOG_TIME',
				align: 'center',
				sortable: true,
				valign: 'middle'
			}, {
				title: '日志内容',
				field: 'LOG_MESSAGE',
				align: 'left',
				valign: 'middle'
			}]
		});
	}
</script>

</html>