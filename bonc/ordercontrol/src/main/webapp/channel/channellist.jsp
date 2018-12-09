<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<%@include file="../common/common.jsp"%>
</head>
<body>
	<div class="container body tab-top">
		<div class="modal" id="channel" role="dialog" aria-labelledby="渠道定义" >
		  <div class="modal-dialog modal-lg" role="document" data-show="true">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" >渠道定义</h4>
		      </div>
		      <div class="modal-body"  style="margin:0px 25px 0px;">
		      	<form class="form-horizontal">
							<div class="form-group">
								<label class="col-sm-2 control-label" for="channelId">渠道ID</label>
								<div class="col-sm-3">
									<input id="channelId" class="form-control" type="text" value="" />
									<input id="channelIndex" class="form-control" type="hidden" value="" />
								</div>
								<label class="col-sm-2 control-label" for="channelName">渠道名称</label>
								<div class="col-sm-3">
									<input id="channelName" class="form-control " type="text" value="" />
								</div>
								<input type="button" class="btn btn-success" value="保存" id="saveChannel" />
							</div>
							<div class="form-group">
								<div class="col-sm-10 col-sm-offset-1">
									<div id="subtoolbar">
										<input type="button" class="btn btn-default" value="增加" id="addSubChannel"/>
									</div>
									<table id="subchannel-table"></table>
								</div>
							</div>
						</form>
		      </div>
		    </div>
		  </div>
		</div>
		
		<input type='hidden' value='${tenantId }' id='tenantId'/>
		
		<div id="toolbar">
			<input class="btn btn-success" type='button' value='新增' data-toggle="modal" data-target="#channel"/>
	  </div>
	  <table id="channel-table"
	  		   data-toolbar="#toolbar"
	         data-toggle="table"
	         data-url="${ctx}cfg/channelListData"
	         data-height="550"
	         data-search="true"
	         data-striped="true"
	         data-sort-name="CHANNEL_ID"
	         data-sort-class="CHANNEL_ID"
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
	          <th data-field="CHANNEL_ID">渠道ID</th>
	          <th data-field="CHANNEL_NAME">渠道名称</th>
	          <th data-field="INSTANCE_ID" >渠道实例</th>
	          <th data-field="TENANT_ID">租户ID</th>
	          <th data-field="LOGIN_ID" >创建人</th>
	          <th data-field="CREATE_DATE" data-formatter="dateFormatter">创建时间</th>
	          <th data-field="OPERATE" data-formatter="operator" data-events="channelEvents">操作</th>
	      </tr>
	    </thead>
	  </table>
  </div>
</body>
<script src="${ctx }channelmanager/js/channellist.js"></script>
</html>