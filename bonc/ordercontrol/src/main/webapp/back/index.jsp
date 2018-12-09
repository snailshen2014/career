<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
  <title>工单管理中心</title>
  <%@include file="../common/common.jsp"%>
  <link rel="stylesheet" href="${ctx }messenger/build/css/messenger.css" />
	<link rel="stylesheet" href="${ctx }messenger/build/css/messenger-theme-future.css" />
	<script src="${ctx }messenger/build/js/messenger.js"></script>
	<script src="${ctx }messenger/build/js/messenger-theme-future.js"></script>
</head>
<body>
<!-- Fixed navbar -->
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
              <li><a href="${ctx}back/addTenant" id="newTenant">新开租户</a></li>
              <li class="dropdown" id="editTenant">
              	<a href="#" class="dropdown-toggle" data-toggle="dropdown">编辑租户配置<b class="caret"></b></a>
              	<ul class="dropdown-menu">
                  <li><a href="${ctx}back/cfgConfig">工单配置信息</a></li>
                  <li><a href="${ctx}back/genXSql">工单拼接行云sql</a></li>
                 </ul>
              </li>
              <li class="dropdown" id="monitorTenant">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">监控信息<b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="${ctx}back/statistics">活动数统计</a></li>
                  <li><a href="${ctx}back/monitor">工单生成执行监控</a></li>
                  <li><a href="${ctx}back/tableUsed">工单表使用率监控</a></li>
                  <li><a href="${ctx}/success/successFilter">事前成功标准过滤监控</a></li>
                  <li><a href="${ctx}/success/successCheck">事后成功标准检查监控</a></li>
                  <li><a href="${ctx}back/asynuserlabelmonitor.jsp">用户标签数据同步监控</a></li>
                </ul>
              </li>
            </ul>
            
            <ul class="nav navbar-nav navbar-right">
               <li id="showTenant"><a href="#">当前租户: ${sessionScope.tenantId}</a></li>
               <li><a href="#" onclick="logout()">退出</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="content" >
    <iframe width="100%" height="100%" ></iframe>
</div>
</body>


<script type="text/javascript">

function messager(msg,delay){
	Messenger().post({message:msg,hideAfter:delay==undefined||delay==null?1:delay});
}

function logout(){
	if(confirm("确定退出吗？")){
		top.location='${ctx}back/main';
		return false;
	}
}

$(function () {
	var href = location.hash.substring(1) || '${ctx}back/addTenant';
	var isNewTenant = '${sessionScope.isNewTenant}';
	if(isNewTenant == '0'){  //新租户
		$('#editTenant').hide();
		$('#monitorTenant').hide();
		$('#dependyTest').hide();
		$('#showTenant').hide();
		href = location.hash.substring(1) || '${ctx}back/addTenant';
	}else{
		$('#newTenant').hide();
		href = location.hash.substring(1) || '${ctx}back/cfgConfig';
	}
	
    $('.toggle').click(function () {
        $('.nav-list').toggleClass('active');
    });

    $(document).on('click', '#navbar li a, .nav-list li a, .navigation a', function (e) {
        var href = $(this).attr('href');
        if (href === '#' || /^http.*/.test(href)) {
            return;
        }
        e.preventDefault();
        $('.nav-list').removeClass('active');
        location.hash = href;
        $('iframe').attr('src', href);
        initNavigation(href);
    });

    <%--var href = location.hash.substring(1) || '${ctx}back/addTenant';--%>
    $('iframe').attr('src', href);
    initNavigation(href);

    $(window).on('blur',function() {
        $('.dropdown-toggle').parent().removeClass('open');
    });
    
    Messenger.options = {
   		extraClasses: 'messenger-fixed messenger-on-top',
   	  theme: 'future'
   	};
});

function initNavigation(href) {
    var $el = $('a[href="' + href + '"]'), $prev, $next;

    $('.ribbon a').attr('href','${ctx}' + href);

    if (!$el.length) {
        return;
    }
    $prev = $el.parent().prev('li');
    $next = $el.parent().next('li');
    $('.navigation a').hide();

    if ($prev.text()) {
        $('.navigation .previous').show()
            .attr('href', $prev.find('a').attr('href'))
            .find('span').text($prev.text());
    }
    if ($next.text()) {
        $('.navigation .next').show()
            .attr('href', $next.find('a').attr('href'))
            .find('span').text($next.text());
    }
}
</script>
</html>