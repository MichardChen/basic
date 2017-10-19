<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>同记后台管理系统- 登录</title>
<%@include file="../common/header.jsp"%>
<link href="${CONTEXT_PATH}/assets/css/animate.css" rel="stylesheet">
<style type="text/css">
.bg-custom1 {
	width: 100%;
	height: 100%;
	background-image: url(/image/background-photo.jpg);
	background-position: center center;
	background-repeat: no-repeat;
	background-attachment: fixed;
	background-size: cover;
	background-color: #464646;
}

.bg-custom{
	background:url(${CONTEXT_PATH}/assets/img/body.jpg);
}

.captcha-input {
	width: 59.5%;
	display: inline-block;
	vertical-align: middle;
	height: 45px;
}

.captcha-img {
	width: 39.5%;
	height: 45px;
	cursor: pointer;
}

.container {
	margin:100px auto;
	padding: 0;
	width:700px;
}

.login-pannel {
	background: rgba(255, 255, 255, .6);
	padding: 20px 30px;
}

.login-logo {
	width: 160px;
	height:160px;
	background: #00a4a3;
	border-radius:50%;
	overflow:hidden;
	text-align:center;
	margin:5px auto;
}
</style>
</head>
<body class="bg-custom">
	<div class="text-center  animated fadeInDown container">
		<p>
			<img src="${CONTEXT_PATH}/assets/img/logo3.png" style=""/>
		<p>
		<div class="login-pannel" style="width:600px;margin:auto;">
			<div class=""><img src="${CONTEXT_PATH}/assets/img/logo2.png" style="margin-top:20px;"/></div>
			<div style="width:450px;margin:auto;">
			<form class="m-t" role="form" id="login_form" method="post"
				action="${CONTEXT_PATH}/login/checkin" onsubmit="return false;">
				<div class="form-group">
					<input type="text" name="userName" class="form-control"
						placeholder="用户名" value="admin" required>
				</div>
				<div class="form-group">
					<input type="password" name="password" class="form-control"
						placeholder="密码" value="123456" required>
				</div>
				<div class="form-group">
					<input type="text" name="captcha" maxlength="4"
						class="form-control captcha-input" placeholder="验证码" required>
					<img id="captcha_img" class="captcha-img" title="点击图片刷新验证码"
						alt="点击图片刷新验证码" />
				</div>
				<button type="submit" id="login_button"
					class="btn btn-primary block full-width m-b">登 录</button>
			</form>
			</div>
		</div>
		<p>
			<img src="${CONTEXT_PATH}/assets/img/add.png"/>
		<p>
	</div>
	<!-- Mainly scripts -->
	<script src="${CONTEXT_PATH}/assets/lib/jquery-2.1.1.min.js"></script>
	<script src="${CONTEXT_PATH}/assets/lib/bootstrap.min.js?v=3.4.0"></script>
	<!-- layer -->
	<script src="${CONTEXT_PATH}/assets/lib/layer/layer.js"></script>
	<script type="text/javascript">
		(function($) {

			var verify = {
					regexp : {
						speChar : new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$")
					},
					charLen : function(val) {
						var arr = val.split(""), len = 0;
						for (var i = 0; i < val.length; i++) {
							arr[i].charCodeAt(0) < 299 ? len++ : len += 2;
						}
						return len;
					},
					userName : function(value) {
						value = (value || "").replace(/\s/g, "");
						var namelen = this.charLen(value);
			            if(namelen < 3 || namelen > 12) return "用户名必须为3到12个字符长度";
			            if(!this.regexp.speChar.test(value)) return "用户名不能有特殊字符";
			            if(/(^\_)|(\__)|(\_+$)/.test(value)) return "用户名首尾不能出现下划线\'_\'，且最多只能出现一个下划线";
			            if(/^\d+\d+\d$/.test(value)) return "用户名不能全为数字";
					},
					password : function(value){ 
			            value = (value||"").replace(/\s/g, "");
			            if(value.length < 6 || value.length > 16) return "密码必须为6到16个字符";
			            if(/[\u4e00-\u9fa5]/.test(value)) return "密码不能出现中文";
			        },
			        captcha : function(value){
			        	value = (value||"").replace(/\s/g, "");
			            if(value.length < 4) return "验证码必须为4个字符";
			        }
			};

			$(function() {
				$("#captcha_img").attr(
						"src",
						"${CONTEXT_PATH}/login/captcha?d="
								+ (new Date()).valueOf());
				$("#captcha_img").click(
						function() {
							$(this).attr(
									"src",
									"${CONTEXT_PATH}/login/captcha?d="
											+ (new Date()).valueOf());
						});

				$("#login_form").on("submit", function() {
					var othis = $(this), action = othis.attr("action");
					var required = othis.find("[required]"), go = true;
					required.each(function(index, item) {
						var othis = $(this);
						var field = othis.attr('name'), val = othis.val();
						var msg = verify[field](val);
						if (msg) {
							layer.tips(msg, othis,{
							    tips: 4
							});
							go = false;
							return false;
						}
					});
					if (go) {
						$.ajax({
							type : "POST",
							url : action,
							dataType : "json",
							data : othis.serialize(),
							success : function(resp) {
								if (resp.code != '200') {
									 layer.msg(resp.msg, {shift: 6});
								}else{
									window.location.href = "${CONTEXT_PATH}/overview";
								}
							}
						});
					}
				});

			});
		})(jQuery);
	</script>
</body>
</html>
