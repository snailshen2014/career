<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<style>
#works {
	margin: 0 auto;
	width: 300px;
	margin-top: 30px
}
</style>
<head>
<title>工单运行监控</title>
<%@include file="../common/common.jsp"%>
<%@include file="../common/echartrs.jsp"%>
</head>
<body>
	<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
	<div id="main" style="height: 400px"></div>
	<script type="text/javascript">
		// 路径配置
		require.config({
			paths : {
				echarts : '${ctx }echarts-2.2.7/build/dist'
			}
		});
		require(
			[ 
			'echarts', 
			'echarts/chart/bar',
			'echarts/chart/line', // 使用柱状图就加载bar模块，按需加载
		   ], 
		   drewEcharts
		);
		function drewEcharts(ec) {
			// 基于准备好的dom，初始化echarts图表
			var myChart = ec.init(document.getElementById('main'));

			var option = {
				tooltip : {
					trigger : 'axis'
				},
				title : {
			        text: '活动数统计'
			    },
				legend : {
					data : [ '总活动数', '成功活动数', '失败活动数' ]
				},
				toolbox: {
			        show : true,
			        feature : {
			            mark : {show: false},
			            dataView : {show: true, readOnly: false},
			            magicType : {show: true, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
				calculable : false,
				xAxis : [ {
					type : 'category'
				} ],
				yAxis : [ {
					type : 'value'
				} ],
				series : [ {
					name : '总活动数',
					type : 'bar'
				}, {
					name : '成功活动数',
					type : 'bar'
				}, {
					name : '失败活动数',
					type : 'bar'
				} ]
			};
			//加载数据
            loadDATA(option);
			// 为echarts对象加载数据 
			myChart.setOption(option);	
			myChart.on('click', function (params) {
				   alert(params.name + " " + params.seriesIndex)
			});
		}
		
		function loadDATA(option){
			$.ajax({
				type : "GET",
	               async : false, //同步执行
	               url : "${ctx}back/activityStatistics/${sessionScope.tenantId}",
	               data : {},
	               dataType : "json", //返回数据形式为json
	               success : function(result) {
                       if (result) {
                              //初始化option.xAxis[0]中的data
                               option.xAxis[0].data=[];
                               for(var i=0;i<result.length;i++){
                                 option.xAxis[0].data.push(result[i].date);
                               }
                               //初始化option.series[0]中的data
                               option.series[0].data=[];
                               option.series[1].data=[];
                               option.series[2].data=[];
                               for(var i=0;i<result.length;i++){
                                 option.series[0].data.push(result[i].total);
                                 option.series[1].data.push(result[i].success);
                                 option.series[2].data.push(result[i].fail);
                               }
                        }
                     }
			});
		}
	</script>
</body>
</html>