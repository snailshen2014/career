<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!-- Fixed navbar -->
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="${ctx}back/cfgConfig">工单配置信息</a></li>
                <li><a href="${ctx}back/monitor">工单生成执行监控</a></li>
                <li><a href="${ctx}back/genXSql">工单拼接行云sql</a></li>
                <li><a href="${ctx}back/tableUsed">工单表使用率监控</a></li>
                <li><a href="">工单生成依赖服务测试</a></li>
                <li><a href="${ctx}/success/successFilter">事前成功标准过滤监控</a></li>
                  <li><a href="${ctx}/success/successCheck">事后成功标准检查监控</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>