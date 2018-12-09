<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<style>
    #works{margin:0 auto;width:300px;margin-top: 30px}
</style>
<head>
    <title>工单运行监控</title>
    <%@include file="../common/common.jsp"%>
    <link rel="stylesheet" href="${ctx }messenger/build/css/messenger.css" />
    <link rel="stylesheet" href="${ctx }messenger/build/css/messenger-theme-future.css" />
</head>
<body>
<!-- portfolio section -->
<section id="works" class="works section no-padding">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title" href="back/cfgConfig">工单配置信息</h3>
        </div>
    </div>
    <div class="panel panel-success">
        <div class="panel-heading">
            <h3 class="panel-title" href="{ctx}back/monitor">工单生成执行监控</h3>
        </div>
    </div>
    <div class="panel panel-info">
        <div class="panel-heading">
            <h3 class="panel-title" href="back/tableUsed">工单表使用率监控</h3>
        </div>
    </div>
    <div class="panel panel-warning">
        <div class="panel-heading">
            <h3 class="panel-title">工单生成依赖服务测试</h3>
        </div>
    </div>

</section>
<!-- portfolio section -->

</body>



</html>