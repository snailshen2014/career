<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@include file="../common/common.jsp"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<style type="text/css"> 
.selecttenant-wrap {
	width: 444px;
    margin: 0 auto;
    overflow:hidden;
	
}
.selecttenant-content {
	text-align: left;
    margin-top: 45%;
	
}
.selecttenant-box{
	margin-top:25px;
}
.selecttenant-content{
    background-color: #fff;
	border-radius:25px;
    padding: 48px 49px 80px 97px;
}
</style>
</head>
<body style="background-color: black;">
	<div class="container body tab-top">
     <form class="form-inline">
      <div class="form-group">
         <input type="text"  id="txtActivityId" class="form-control col-xs-4" placeholder="活动ID">
         <input type="text"  id="serviceURL" class="form-control col-xs-4" placeholder="服务地址">
      </div>
      <div class="form-group">
         <input type="button" id="stopActivity" class="btn btn-primary col-xs-" value="停止">
      </div>
    </form>
   </div>
   
   <script type="text/javascript">
   $('#stopActivity').click(function() {
		var actId = $('#txtActivityId').val();
		var serviceurl = $('#serviceURL').val();
		$.ajax({
			type : "GET",
			url : "${ctx}back/stopActivity",
			data : {
				activityId : actId,
				tenantId:'${sessionScope.tenantId}',
				serviceURL:serviceurl
			},
			dataType : "json",
			success : function(data, textStatus) {
				alert("停止成功");
			}
		});
	});
   </script>
</body>
</html>