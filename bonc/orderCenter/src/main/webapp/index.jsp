<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form method="POST" enctype="multipart/form-data"  
      action="/ordercenter/visiteBack">  
    File to upload: <input type="file" name="file"><br /> Name: 
    <input type="text" name="provId"/><br /> <br /> 
    <input type="text" name="cellPath"/>
    <input type="text" name="backType"/>
    <input type="text" name="pama"/>
    <input type="submit" value="Upload">
</form>  
</body>
</html>