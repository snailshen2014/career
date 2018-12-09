<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<%@include file="../common/common.jsp"%>
</head>
<body>
	<input id="globalTenantId" type="hidden" value="${globalTenantId}">
	<div class="container body tab-top">
		<div id="toolbar">
			<a class="btn btn-success" href="${ctx}cfg/instancedefine?channelId=${channelId}">新增</a>
		</div>
	  <table id="instance-table"
	  		   data-toolbar="#toolbar"
	         data-toggle="table"
	         data-url="${ctx}cfg/channelInsListData?tenantId=${tenantId}"
	         data-height="550"
	         data-search="true"
	         data-striped="true"
	         data-sort-name="channelId"
	         data-show-refresh="true"
	         data-show-toggle="true"
	         data-show-columns="true"
	         data-show-export="true"
	         data-minimum-count-columns="2"
	         data-show-pagination-switch="true"
	         data-side-pagination="server"
	         data-pagination="true"
	         data-page-list="[5, 10, 20, 50, 100, 200]">
			<thead>
	      <tr>
	          <th data-field="STATUS" data-checkbox="true"></th>
	          <th data-field="insId" >渠道实例ID</th>
	          <th data-field="channelId" data-formatter="tenantIdFormatter">实例类型</th>
	          <th data-field="instanceName" >渠道实例名称</th>
	          <th data-field="instanceMemo" >渠道实例描述</th>
	          <th data-field="instanceStatus">渠道实例状态</th>
	          <th data-field="OPERATE" data-formatter="operator">操作</th>
	      </tr>
			</thead>
	  </table>
	</div>
</body>

<script src="${ctx }channelmanager/js/instancelist.js"></script>

</html>