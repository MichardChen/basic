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
   	<script type="text/javascript">
		   	function GetRequest() {
		   	    var url = location.search; //获取url中"?"符后的字串
		   	    var theRequest = new Object();
		   	    if (url.indexOf("?") != -1) {
		   	        var str = url.substr(1);
		   	        strs = str.split("&");
		   	        for(var i = 0; i < strs.length; i ++) {
		   	            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
		   	        }
		   	    }
		   	    return theRequest;
		   	}
		   	
		   	function isphone2(inputString){
		   	    var partten = /^1[3,4,5,8,7]\d{9}$/;
		   	    var fl=false;
		   	    if(partten.test(inputString)){
		   	         //alert('是手机号码');
		   	         return true;
		   	    }else{
		   	         return false;
		   	         //alert('不是手机号码');
		   	    }
		   	}
   			function bind(){
   				var mobile=$("#mobile").val();
   				var businessId=$("#businessId").val();
   				if(mobile==""){
   					alert("请输入手机号码");
   					return;
   				}
   				
   				if(!isphone2(mobile)){
   					alert("请输入正确的手机号码");
   					return false;
   				}
   				
   				if(window.confirm('你确定关联门店吗？')){
   					$.ajax({
   				        type: "post",
   				        async: false,
   				        url: "<%=request.getContextPath()%>/rest/bindStoreByMobile",
   				        data: "businessId="+businessId+"&mobile="+mobile,
   				        dataType: "json",
   				        success: function(data) {
   				        	var temp = eval(data);
   				        	alert(temp.message);
   				        }
   				    });
   			     }
   			}
   			
   			window.onload=function(){
   			      var Request = new Object();
   				  Request = GetRequest();
   				  var businessId = Request['businessId'];
   				  $("#businessId").val(businessId);
   				  //判断是否提交绑定门店，如果没有，隐藏手机号
	   			  $.ajax({
					       type: "post",
					       async: false,
					       url: "<%=request.getContextPath()%>/rest/queryBusinessStore",
					       data: "businessId="+businessId,
					       dataType: "json",
					       success: function(data) {
					       	var temp = eval(data);
					       	if(temp.code=="5700"){
					       		$("#bindDiv").css("display","none");
					       	}
					       }
					});
   			}
   	</script>
    <title>关联门店</title>
</head>
<body style="margin: 0px 0px;">
	<div style="color:red;text-align: center;word-wrap:break-word;">
			<img src="../assets/img/guide.png" class="img-responsive"  alt="Responsive image" width="100%">
	</div>
	<div>
	<img src="http://app.tongjichaye.com:88/common/download.jpg" class="img-responsive"  alt="Responsive image" width="100%">
	</div>
	<div style="margin: 0px 0px;text-align: center;margin-top: 10px;" id="bindDiv">
	<input type="text" class="form-control" style="width: 50%;display: inline;" name="mobile" id="mobile" placeholder="请输入手机号码"/>&nbsp;&nbsp;&nbsp;&nbsp;<button class="btn btn-primary" type="button" onclick="bind()">免费喝茶</button>
	</div>
	<div style="margin: 0px 0px;text-align: center;margin-top: 10px;">
			<a href="http://app.tongjichaye.com:88/app/tj_android.apk"><button type="button" class="btn btn-default" style="border-radius:30px;">Android客户端</button></a>
			&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://itunes.apple.com/us/app/%E6%8E%8C%E4%B8%8A%E8%8C%B6%E5%AE%9D/id1311337970?l=zh&ls=1&mt=8"><button type="button" class="btn btn-default" style="border-radius:30px;">IOS客户端</button></a>
	</div>
	<%-- <div style="margin: 0px 0px;text-align: center;margin-top: 10px;width: 100%;">
			<img src="<%=request.getContextPath()%>/image/yyb.png"  alt="Responsive image" width="50%" style="text-align: center;">		
	</div> --%>
	<div style="margin: 0px 0px;text-align: center;margin-top: 10px;height: 100px;">
	</div>
	<input type="hidden" id="businessId"/>
</body>
</html>