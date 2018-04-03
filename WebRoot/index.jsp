<%@page import="util.TextUtils"%>
<%@page import="dao.NewsDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	
	//获取发布者信息
	String publisher = (String) session.getAttribute("publisher");
	if (TextUtils.isEmpty(publisher)) {
		response.sendRedirect("login.html");
		return;
	}	
	//获取新闻类型
	NewsDAO dao = NewsDAO.newInstance();
	List<String> types = dao.queryNewsTypes();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>编辑</title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes" />
<script type="text/javascript" charset="utf-8"
	src="utf8-jsp/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8"
	src="utf8-jsp/ueditor.all.min.js">
	
</script>
<!--建议手动加在语言，避免在ie下有时因为加载语言失败导致编辑器加载失败-->
<!--这里加载的语言文件会覆盖你在配置项目里添加的语言类型，比如你在配置项目里配置的是英文，这里加载的中文，那最后就是中文-->
<script type="text/javascript" charset="utf-8"
	src="utf8-jsp/lang/zh-cn/zh-cn.js"></script>

<!-- 处理新闻图标 -->
<script type="text/javascript">
	//判断浏览器是否支持FileReader接口
	if (typeof FileReader == 'undefined') {
		document.getElementById("errorBox").InnerHTML = "<h1>当前浏览器不支持FileReader接口</h1>";
		//使选择控件不可操作
		document.getElementById("chooseImage").setAttribute("disabled",
				"disabled");
	}

	//选择图片，马上预览
	function uploadImg(obj) {
		var file = obj.files[0];

		console.log(obj);
		console.log(file);
		console.log("file.size = " + file.size); //file.size 单位为byte

		var reader = new FileReader();

		//读取文件过程方法
		reader.onloadstart = function(e) {
			console.log("开始读取....");
		};
		reader.onprogress = function(e) {
			console.log("正在读取中....");
		};
		reader.onabort = function(e) {
			console.log("中断读取....");
		};
		reader.onerror = function(e) {
			console.log("读取异常....");
		};
		reader.onload = function(e) {
			console.log("成功读取....");

			var img = document.getElementById("choosedImage");
			img.src = e.target.result;
			//或者 img.src = this.result;  //e.target == this
		};

		reader.readAsDataURL(file);
	}
	
	function checkText(form) {
		var title = form.news_title.value;
		if (title.trim().length == 0) {
			alert("新闻标题不能为空");
			form.news_title.focus();
			return false;
		}
		var ue = UE.getEditor('editor');
		var content = ue.getContent();
		if(content.trim().length == 0) {
			alert("新闻内容不能为空");
			ue.focus();
			return false;
		}
		return true;
	}
</script>

<style type="text/css">
div {
	width: 100%;
}
body{
  font-family: 'PT Sans', Helvetica, Arial, sans-serif;
  width: 100%;
  height: 100%;
  background: url(../NewsSystem/assets/img/2.png) center center no-repeat;
  background-size:100% 100%; 
}
button {
    cursor: pointer;
    width: 150px;
    height: 44px;
    margin-top: 20px;
    padding: 0;
    background: #FFC125;
    -moz-border-radius: 6px;
    -webkit-border-radius: 6px;
    border-radius: 6px;
    border: 1px solid #ff730e;
    font-family: 'PT Sans', Helvetica, Arial, sans-serif;
    font-size: 14px;
    font-weight: 700;
    color: #fff;
    text-shadow: 0 1px 2px rgba(0,0,0,.1);
    -o-transition: all .2s;
    -moz-transition: all .2s;
    -webkit-transition: all .2s;
    -ms-transition: all .2s;
}
</style>
</head>
<body>
	<div style="text-align:center;">
		<label style="font-size:20px; color:#7EC0EE;">欢迎您，<%=publisher %></label>
	</div>
	<div style="display: flex; justify-content: center; ">
		<form action="UeditorServlet" method="post" enctype="multipart/form-data">
			<div>
				<h2 style="color:#FFC125;">新闻编辑器</h2>
			</div>
			<div style="margin-bottom: 20px">
				<label for="news_title">新闻标题：</label> <input type="text"
					name="news_title" style="width: 300px; font-size:18px;" />
			</div>
			<div style="margin-bottom: 20px">
				<label for="news_type">新闻类型：</label>
				<select name="news_type" style="font-size:16px;">
					<%
						for(int i = 0; i < types.size(); i++) {
							out.println("<option value='"+types.get(i)+"'>"+types.get(i)+"</option>");
						}
						out.flush();
					 %>
				</select>
			</div>
			<div style="margin-bottom: 20px">
				<label for="news_icon">新闻图标：</label> <input type="file"
					id="chooseImage" name="news_icon" onchange="uploadImg(this)"
					accept="image/*" />
				<h4>选择文件如下：</h4>
				<!-- 保存用户自定义的背景图片 -->
				<img id="choosedImage" style="max-width:300px; max-height:180px;" />
				<div id="errorBox"></div>
			</div>
			<script id="editor" type="text/plain"
				style="max-width:800px; height:450px;">
			</script>
			<button type="submit" onclick="return checkText(this.form);">发布</button>
		</form>
	</div>

	<script type="text/javascript">
		//实例化编辑器
		//建议使用工厂方法getEditor创建和引用编辑器实例，如果在某个闭包下引用该编辑器，直接调用UE.getEditor('editor')就能拿到相关的实例
		var ue = UE.getEditor('editor');
	</script>
</body>
</html>
