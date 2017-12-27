<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <meta content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport" /> 
	<link rel="stylesheet" href="${CONTEXT_PATH}/assets/bootstrap/css/bootstrap.css" type="text/css" media="screen" />
	<script src="${CONTEXT_PATH}/assets/lib/jquery-2.1.1.min.js"></script>
   	<style>
   	.container {
	    margin-right: 0px;
	    margin-left: 0px;
	    }
   	</style>
    <title>App下载</title>
</head>
<body style="margin: 0px 0px;">
	<div style="color:red;text-align: center;word-wrap:break-word;">
			<!-- 如果微信中无法下载，请点击右上角按钮用浏览器打开该页面再下载 -->
			<img src="../assets/img/guide.png" class="img-responsive"  alt="Responsive image" width="100%">
	</div>
	<div>
	<img src="http://app.tongjichaye.com:88/common/download.jpg" class="img-responsive"  alt="Responsive image" width="100%">
	</div>
	<div style="margin: 0px 0px;text-align: center;margin-top: 10px;">
			<a href="http://app.tongjichaye.com:88/app/tj_android.apk"><button type="button" class="btn btn-default" style="border-radius:30px;">Android客户端</button></a>
			&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://itunes.apple.com/us/app/%E6%8E%8C%E4%B8%8A%E8%8C%B6%E5%AE%9D/id1311337970?l=zh&ls=1&mt=8"><button type="button" class="btn btn-default" style="border-radius:30px;">IOS客户端</button></a>
	</div>
	<div style="margin: 0px 0px;text-align: center;margin-top: 10px;height: 100px;">
	</div>
	<input type="hidden" id="businessId"/>
</body>
</html>