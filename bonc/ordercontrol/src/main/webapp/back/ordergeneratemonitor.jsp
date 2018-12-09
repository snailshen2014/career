<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/common.jsp"%>
<%@include file="nav.jsp"%>
<html>
<head>
    <title>工单生成监控</title>
</head>
<body>
   <div>占位</div>
   <div class="container body tab-top">
     <form class="form-inline">
      <div class="form-group">
         <input type="text"  id="txtActivityId" class="form-control col-xs-4" placeholder="活动ID">
      </div>
      <div class="form-group">
         <input type="button" id="btnSearch" class="btn btn-primary col-xs-" value="搜索">
      </div>
    </form>  
	  <table class="table"
	         data-striped ="true"
	         data-url="${ctx}back/activityList?tenantId=${sessionScope.tenantId}"
	         data-height="550"
	         data-pagination ="true"
	         data-side-pagination="server",
	         data-locale="zh-CN",
	         data-page-list="[5, 10, 20, 50, 100, 200]">
	  </table>
	</div>

	<div class="modal fade" id="recycleModel">
		<div class="modal-dialog">
			<div class="modal-content message_align">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">提示信息</h4>
				</div>
				<div class="modal-body">
					<p>您确认要重跑活动吗？</p>
				</div>
				<div class="modal-footer">
					<input type="hidden" id="url" />
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<a onclick="urlSubmit()" class="btn btn-success"
						data-dismiss="modal">确定</a>
				</div>
			</div>
		</div>
	</div>

	<%@include file="showMonitor.jsp"%>
</body>
<script type="text/javascript">
	function operateFormatter(value, row, index) {
		return ['<a class="col-sm-offset-1" href="#"  data-toggle="modal" data-target="#myModal" data-dataid="'+row.activityId+'"><i title="查看" class="glyphicon glyphicon-camera"></i></a>',
			    '<a class="col-sm-offset-1  recycle"><i title="重跑" class="glyphicon glyphicon-remove-circle"></i></a>']
				.join('');
	}
	function urlSubmit(){  
		   var url=$.trim($("#url").val());//获取会话中的隐藏属性URL 
		   window.location.href=url;    
	}
	window.operateEvents = {
		    'click .recycle': function (e, value, row, index) {
		    		var param = {};
		    		param.activityId=row.activityId;
		    		var recyleUrl = "${ctx}back/recycleActivityOrder?activityId="+row.activityId+"&tenantId=${sessionScope.tenantId}";
		    		console.log("--------------------" + recyleUrl);
		    		$('#url').val(recyleUrl);
		    		$('#recycleModel').modal();
		    	}
		};
	$('.table').bootstrapTable({
		 rowStyle:function rowStyle(row, index) {
			return {
			   css: {"padding":"0"}
			 };
	    },
		columns : [ {
			field : 'state',
			checkbox : true,
			align : 'center',
			valign : 'middle'
		}, {
			title : '活动ID',
			field : 'activityId',
			align : 'center',
			valign : 'middle'
		},{
			title : '活动名称',
			field : 'activityName',
			align : 'center',
			valign : 'middle'
		}, {
			title : '工单生成状态',
			field : 'orderStatus',
			align : 'center',
			valign : 'middle',
			width : '250px',
			formatter:function(value,row,index){
			  if(value=='success'){
				  return "<div class=\"list-group-item list-group-item-success\">生成成功</div>";
			  } 
			  if(value=='failed') {
				  return "<div class=\"list-group-item list-group-item-danger\">生成失败</div>";
			  }
			  if(value=='running') {
				  return "<div class=\"list-group-item list-group-item-info\">生成中</div>";
			  }			  
			}
		}, {
			field : 'operate',
			title : '工单生成监控',
			align : 'center',
			valign : 'middle',
			events:  operateEvents,
			formatter : operateFormatter
		} ]
	});
	
	$('#btnSearch').click(function() {
		var actId = $('#txtActivityId').val();
		$.ajax({
			type : "GET",
			url : "${ctx}back/activityList",
			data : {
				activityId : actId,
				tenantId:'${sessionScope.tenantId}'
			},
			dataType : "json",
			success : function(data, textStatus) {
				console.log(textStatus);
				$('.table').bootstrapTable('load', data);
			}
		});
	});
</script>
</html>