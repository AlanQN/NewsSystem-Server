<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style type="text/css">
img {
	clear: both;
	display: block;
	margin: auto;
	max-width: 98%; /*图片自适应宽度*/ 
	height: auto;
}
</style>
<title>Insert title here</title>
</head>
<body style="display: flex; justify-content: center;">
	<div style="max-width: 768px;">
		<h2>${title }</h2>
		<label style="font-size:14px; color:gray;">${publisher } </label>
		&nbsp;&nbsp;
		<label style="font-size:14px; color:gray;">${publishTime }</label>
		<div>${content }</div>
	</div>
</body>
</html>