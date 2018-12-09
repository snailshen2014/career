<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}/"></c:set>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">×</span>
				</button>
				<h4 class="modal-title" id="myModalLabel">工单生成监控</h4>
			</div>
			<div class="modal-body">
				<table id="monitorTable"></table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>


<div class="modal fade" id="detailModal" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">×</span>
				</button>
				<h4 class="modal-title" id="detailModalLabel">详情</h4>
			</div>
			<div class="modal-body" id="detailContent" style="overflow:auto;">
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			</div>
		</div>
	</div>
</div>


<script type="text/javascript">
var actId;
var url;
 $(function(){ 
	 $('#myModal').on('show.bs.modal',
				function(event) {
					var button = $(event.relatedTarget);
					var dataid = button.data('dataid');
					var tenantId = '${sessionScope.tenantId}';
					console.log("------------------" + tenantId);
					url="${ctx}back/orderGenerateStep?num="+Math.random()+"&activityId="+dataid+"&tenantId="+tenantId;
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
	 
	 $('#detailModal').on('show.bs.modal',
				function(event) {
					var button = $(event.relatedTarget);
					var detail = button.data('detail');
					var content = $('#monitorTable').bootstrapTable('getData')[detail];
					$('#detailContent').text(content.BUSI_ITEM_1);
				}
			); 
	 
	//关闭时销毁
	 $('#detailModal').on('hide.bs.modal',
				function(event) {
		        $('#detailContent').text("");
				}
			); 
	 
 });
 
 function initTable(url){
	 $('#monitorTable').bootstrapTable({
		    toolbar: '#toolbar', 
		    url:url,
		 	method: 'GET',
		 	striped: true,
		 	cache: false,
		 	sidePagination: "server",
		 	height : getHeight(),
		 	columns:[
		 		{
		 			title : '结束时间',
		 			field : 'END_DATE',
		 			align : 'center',
					valign : 'middle'
		 		},{
		 			title : '渠道',
		 			field : 'CHANNEL_ID',
		 			align : 'center',
					valign : 'middle'
		 		},{
					title : '步骤名称',
					field : 'log_info',
					align : 'center',
					sortable : true,
					valign : 'middle'
				},{
					title : '详情',
					field : 'operate',
					align : 'center',
					valign : 'middle',
					formatter : detailoperateFormatter
				}
		 	]
		});
 }
 
 function detailoperateFormatter(value, row, index) {
		return ['<a class="col-sm-offset-1" href="javascript:;" data-toggle="modal" data-target="#detailModal" data-detail="'+index+'"><i title="详情" class="glyphicon glyphicon-check"></i></a>']
				.join('');
	}
 
 function test(){
	 console.log("aaaaaaaaaaaa");
 }
 
 function getHeight() {
		return $(window).height();
  }
 
</script>
