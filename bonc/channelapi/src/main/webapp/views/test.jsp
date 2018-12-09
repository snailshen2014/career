<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%String path=request.getContextPath(); %>
<script src="<%=path %>/js/jquery/jquery-1.11.1.min.js"></script>
<script src="<%=path %>/js/jquery/jquery.json-2.2.min.js"></script>
<title>Insert title here</title>
<script type="text/javascript">
var ctx = "<%=path%>";
function submitUserList_3() {alert("ok"+ctx);
var customerArray = new Array();
customerArray.push({phone: "211", channelType: "李四", eventType: "",pushTime:"1pusheTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "222", channelType: "李w", eventType: "",pushTime:"2pushTeime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "333", channelType: "李e四", eventType: "",pushTime:"3push1Time",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "444", channelType: "李四e", eventType: "",pushTime:"4pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "555", channelType: "李四", eventType: "",pushTime:"5pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "6", channelType: "李四", eventType: "",pushTime:"6pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "9", channelType: "李四", eventType: "",pushTime:"7pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "7", channelType: "李四", eventType: "",pushTime:"8pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "8", channelType: "李四", eventType: "",pushTime:"9pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "10", channelType: "李四", eventType: "",pushTime:"0pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "11", channelType: "李四", eventType: "",pushTime:"11pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "12", channelType: "李四", eventType: "",pushTime:"12pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "13", channelType: "李四", eventType: "",pushTime:"13pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "14", channelType: "李四", eventType: "",pushTime:"14pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "15", channelType: "李四", eventType: "",pushTime:"15pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "16", channelType: "李四", eventType: "",pushTime:"16pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "17", channelType: "李四", eventType: "",pushTime:"171pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "18", channelType: "李四", eventType: "",pushTime:"18pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "19", channelType: "李四", eventType: "",pushTime:"19pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});
customerArray.push({phone: "20", channelType: "李四", eventType: "",pushTime:"20pushTime",pushResult:"pushResult",activityId:"activityId",smsContent:"smsContent"});


alert("ok"+JSON.stringify(customerArray));
$.ajax({
    url: ctx+'/smsMarketingBatch',
    type: "POST",
    contentType : 'application/json;charset=utf-8', //设置请求头信息
    dataType:"json",
    //data: JSON.stringify(customerArray),    //将Json对象序列化成Json字符串，JSON.stringify()原生态方法
    data: $.toJSON(customerArray),            //将Json对象序列化成Json字符串，toJSON()需要引用jquery.json.min.js
    success: function(data){
        alert(data);
    },
    error: function(res){
        alert(res.responseText);
    }
});
}
</script>
</head>
<body>
       <h1>submitUserList_3</h1>
    <input id="submit" type="button" value="Submit" onclick="submitUserList_3();">
</body>
</html>