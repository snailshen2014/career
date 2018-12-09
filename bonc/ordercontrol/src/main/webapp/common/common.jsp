<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="tenantId" value="000000"></c:set>
<c:set var="ctx" value="${pageContext.request.contextPath}/"></c:set>
<script>
	var appUrl = (window.location+'').split('/');  
	var basePath = appUrl[0]+'//'+appUrl[2]+'/'+appUrl[3]+'/';  
</script>

<meta charset="utf-8" />
<link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctx }bootstrap-3.3.1-dist/dist/bootstrap-table.css" />
<link rel="stylesheet" href="${ctx }bootstrap-3.3.1-dist/dist/css/bootstrap-editable.css"/>
<link rel="stylesheet" href="${ctx }bootstrap-3.3.1-dist/dist/skins/minimal/minimal.css"/>
<link rel="stylesheet" href="${ctx }select2-4.0.3/dist/css/select2.css"/>
<%-- <link rel="stylesheet" href="${ctx }bootstrap-3.3.1-dist/dist/extensions/click-edit-row/bootstrap-table-click-edit-row.css"/> --%>


<script src="${ctx }bootstrap-3.3.1-dist/dist/js/jquery.min.js" ></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/js/bootstrap.min.js"></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/bootstrap-table.js"></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/js/bootstrap-editable.js"></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/extensions/editable/bootstrap-table-editable.js"></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/extensions/export/bootstrap-table-export.js"></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/js/icheck.min.js"></script>
<script src="${ctx }bootstrap-3.3.1-dist/dist/locale/bootstrap-table-zh-CN.js"></script>
<script src="${ctx }select2-4.0.3/dist/js/select2.js"></script>

<style>
.tab-top{
	margin-top: 50px ;
}

.operator-disable i{
	color:gray;
}

.modal-dialog{
	z-index:2000;
	margin-top:60px;
}
</style>