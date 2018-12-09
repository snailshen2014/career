<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<c:set var="ctx" value="${pageContext.request.contextPath}/"></c:set>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>资源配置</title>
<link href="${ctx }bootstrap-3.3.1-dist/dist/skins/minimal/minimal.css" rel="stylesheet">
<link rel="stylesheet" href="http://www.bootcss.com/p/layoutit/css/bootstrap-combined.min.css">
<script src="${ctx }bootstrap-3.3.1-dist/dist/js/jquery.min.js"></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/js/bootstrap.min.js"></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/js/icheck.min.js"></script>
<style>
body{
    margin-top: 50px;
}
</style>
</head>
<body>
	<div class="container-fluid">
	<div class="row-fluid">
		<div class="span2"></div>
		<div class="span10">
			<h3>资源定义</h3>
			<form class="form-horizontal">
				<div class="control-group">
					<label class="control-label" for="resourceName">资源名称</label>
					<div class="controls">
						<input id="resourceName" type="text" />
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="resourceType">资源类型</label>
					<div class="controls">
						<select id="resourceType" onchange="_resourceType(this)">
						  <option value="table">表资源</option>
						  <option value="interface">接口资源</option>
						</select>
					</div>
				</div>
				<div class="control-group tableItem" >
					<label class="control-label" for="tableName">表名</label>
					<div class="controls">
						<input id="tableName" type="text" />
					</div>
				</div>
				<div class="control-group tableItem">
					<label class="control-label" >字段</label>
					<div class="controls">
						<input type="button" class="btn" value="增加" onclick="addItem('fieldMap')"/>
					</div>
				</div>
				<div class="hidden fieldMap">
					<div class="control-group tableItem">
						<div class="controls">
							 <input type="text" class="input-small"/> &nbsp;&nbsp;<input type="text" class="input-small"/>
							 <input type="button" class="btn" value="删除" onclick="delItem(this)"/>
						</div>
					</div>
				</div>
				
	 			<div class="control-group">
					<div class="controls">
						 <button type="button" class="btn" >测试</button>
						 <button type="button" class="btn">保存</button>
					</div>
				</div>
			</form>
			
		</div>
	</div>
</div>
<script>
var _resourceType = function(obj){
	if(obj.value!='table'){
		$(".tableItem").hide();
	}else{
		$(".tableItem").show();
	}
};

var addItem = function (_class){
	  $("."+_class).after($('.'+_class).html());
};

var delItem = function (_this){
	  $(_this).parent().parent().remove();
};
</script>
</body>
</html>