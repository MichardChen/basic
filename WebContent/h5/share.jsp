<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <meta content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport" /> 
	<link rel="stylesheet" href="${CONTEXT_PATH}/assets/bootstrap/css/bootstrap.css" type="text/css" media="screen"/>
	<script src="${CONTEXT_PATH}/assets/summernote/jquery-2.1.1.min.js"></script>
   	<style>
   	.container {
	    margin-right: 0px;
	    margin-left: 0px;
	    }
   	</style>
   	<script type="text/javascript">
	   	var layer=document.createElement("div");
	   	layer.id="layer";
	   	function toast(msg){
	   	    var style=
	   	    {
	   	        background:"#121212",
	   	        position:"absolute",
	   	        zIndex:10,
	   	        width:"60%",
	   	        //height:"6%",
	   			 //lineHeight:"6%",
	   		   padding:"2%",
	   	        left:"20%",
	   	        bottom:"50%",
	   			borderRadius:"10px",
	   			color:"#fff",
	   			textAlign:"center",
	   	    }
	   	    for(var i in style)
	   	        layer.style[i]=style[i];   
	   	    if(document.getElementById("layer")==null)
	   	    {
	   	        document.body.appendChild(layer);
	   			layer.innerHTML=msg;
	   	        setTimeout("document.body.removeChild(layer)",2000)
	   	    }
	   	}
	   	
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
	   	
	   	window.onload=function(){
			var Request = new Object();
			Request = GetRequest();
			var businessId = Request['businessId'];
			document.getElementById("businessId").value=businessId;
		}
	   	
	   	function bind(){
	   		var mobile = $("#mobile").val();
	   		if(mobile == ""){
	   			toast("手机号码不能为空");
	   		}
	   	}
   	</script>
    <title>绑定门店</title>
</head>
<body style="margin: 0px 0px;">
	<div>
	<img src="http://119.29.100.17:8080//craft4j/admin/images/aboutus.png" class="img-responsive"  alt="Responsive image" width="100%">
	</div>
	<div style="margin: 0px 0px;text-align: center;margin-top: 10px;">
	<input type="text" class="form-control" style="width: 50%;display: inline;" name="mobile" id="mobile" placeholder="请输入绑定手机号码"/>&nbsp;&nbsp;&nbsp;&nbsp;<button class="btn btn-primary" type="button" onclick="bind()">绑定</button>
	</div>
	<div style="margin: 0px 0px;text-align: center;margin-top: 10px;">
			<a href="https://www.baidu.com"><button type="button" class="btn btn-default" style="border-radius:30px;">Android客户端</button></a>
			<a href="https://www.baidu.com"><button type="button" class="btn btn-default" style="border-radius:30px;">IOS客户端</button></a>
	</div>
	<div style="margin: 0px 0px;text-align: center;margin-top: 10px;height: 100px;">
	</div>
	<input type="hidden" id="businessId" name="businessId"/>
</body>
</html>