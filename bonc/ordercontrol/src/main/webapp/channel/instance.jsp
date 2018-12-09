<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@include file="../common/common.jsp"%>
</head>
<body>
	<%@include file="datacfg.jsp"%>
	
  <div class="tab-top">
	<div class="col-md-2"></div>
	
	<div class="col-md-8">
		<h3>渠道实例定义</h3>
		<form class="form-horizontal">
			<div class="form-group">
				<label class="col-sm-2 control-label" for="channelInsName">渠道实例名称</label>
				<div class="col-sm-4">
					<input id="channelInsName" class="form-control" type="text" value="${channelInsInfo.instanceName}"/>
					<input id="insId" class="form-control" type="hidden" value="${channelInsInfo.insId}"/>
				</div>
			</div>
			<div class="form-group">
				<label class="col-sm-2 control-label" for="channelInsMemo">渠道实例描述</label>
				<div class="col-sm-4">
					<input id="channelInsMemo" class="form-control" type="text" value="${channelInsInfo.instanceMemo}"/>
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-2">
					<input type="button" class="btn btn-default" value="保存" id="saveInstance"/>
				</div>
			</div>
		</form>
		
		<%@include file="busi.jsp"%>
		
		<%@include file="entity.jsp"%>
		
		<%@include file="element.jsp"%>
		

		<%@include file="../common/footer.jsp"%>
	</div>
	<div class="col-md-2"></div>
</div>

</body>

<script src="${ctx }channelmanager/js/instance.js"></script>
<script src="${ctx }channelmanager/js/busi.js"></script>
<script src="${ctx }channelmanager/js/entity.js"></script>
<script src="${ctx }channelmanager/js/element.js"></script>
<script src="${ctx }channelmanager/js/datacfg.js"></script>

</html>