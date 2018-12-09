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
	<div class="selecttenant-wrap">
		<div class="selecttenant-content">
			<div style="width: 252px">
				<div class="selecttenant-title">
					<span style="font-size: 22px; font-weight: bold">工单监控</span>
				</div>
				<div class="selecttenant-box">

					<form role="form" class="form-inline form_search" method="post"
						action="">
						<div class="form-group">
						    <label> 
							  <label class="" style="font-size: 16px; font-weight: 100">租户类型：</label>
							    <input type="radio" name="busiMode" style="font-size: 16px; padding: 0px 12px; width: 20px;" value="0" checked>新增</input>
							    <input type="radio" name="busiMode" style="font-size: 16px; padding: 0px 12px; width: 20px;" value="1">已有</input>
							</label>
							<label id="tenantNameLabel"> 
							<label class="" style="font-size: 16px; font-weight: 100">租户名称：</label> 
							<select 
							    name="tenantId"
								style="font-size: 16px; font-weight: 100; padding: 0px 12px; width: 163px;"
								id="field_tenantId" class="form-control field_tenantId"
								pattern=".+" data-content="必须选择租户!">
									<option value="weixuanze">请选择</option>
									<!--  <option value="uni081">四川租户</option>-->
									<!--  <option value="uni097">黑龙江租户</option>-->
							</select>
							</label>
							<label> 
							<label class="" style="font-size: 16px; font-weight: 100">登陆口令：</label> 
							<input type="password" id="pass" class="form-control" style="font-size: 16px; font-weight: 100; padding: 0px 12px; width: 163px;">
							</label>
						</div>
						<div>
							<input id="button_confirm" type="button" disabled
								style="float: left; width: 250px; margin-top: 20px; color: white; font-weight: bold; background: #53a2f5"
								class="btn btn-primary" value="确认" />
						</div>

					</form>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	    $(function(){
	    	var v = $("input[type='radio']:checked").val();
	    	if(v == '0'){  //新租户 
	    		$("#tenantNameLabel").hide(); 
	    		$("#button_confirm").removeAttr("disabled");
	    	}else{
	    		$("#tenantNameLabel").show();
	    	}
	    	
	    	$(":radio").click(function(){
	    		$("#field_tenantId").empty();
	    		$("#field_tenantId").append("<option value='weixuanze'>"+'请选择'+"</option>");
	    		    var value = $(this).val()
	    		    if(value == '0'){
	    		    	//隐藏field_tenantId
	    		    	$("#tenantNameLabel").hide();
	    		    	$("#button_confirm").removeAttr("disabled");
	    		    }else{
	    		    	//显示field_tenantId
	    		       $.ajax({
	    			      url: '${ctx}back/getTenantId',
	    			      type: 'get',
	    			      dataType: 'json',
	    			      success:function(data){
	    			       console.log("----------------------   " + data);
	    				   var tenantArray = data;
	    				   for (var tenant in tenantArray){
	    					   console.log(tenantArray[tenant].TENANT_ID    +  " ---- "  +  tenantArray[tenant].TENANT_NAME);
	    					   $("#field_tenantId").append("<option value=" + tenantArray[tenant].TENANT_ID + ">" + tenantArray[tenant].TENANT_NAME + "</option>");	   
	    				     } 
	    			      }	        
	    		        });
	    		    	$("#tenantNameLabel").show();
	    		    }
	    	     });
	        });
	    
		$("#field_tenantId").change(function() { 
			if ($(this).val() == "weixuanze") {
					$("#button_confirm").attr("disabled", "disabled");	
			} else {
				$("#button_confirm").removeAttr("disabled");
			}
		});
		var toUrl = '${ctx}back/register';
		$("#button_confirm").click(
				function() {
					var pass = $('#pass').val();
					var isNewTenant = $("input[type='radio']:checked").val();  //0:新租户   1：已有租户
					var currentTenant = $("#field_tenantId :selected").val();
					window.location.href = toUrl
							+ (toUrl.indexOf("?") != -1 ? "&" : "?")
							+ "tenant_id=" + currentTenant + "&pass="+pass + "&isNewTenant=" + isNewTenant;
				});
	</script>
</body>
</html>