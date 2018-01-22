<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>查看小程序</title>
<link type="image/x-icon" rel="shortcut icon" href="${CONTEXT_PATH}/assets/img/tjico.ico" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/summernote/css/bootstrap.css">
<link href="<%=request.getContextPath()%>/summernote/dist/summernote.css" rel="stylesheet"/>
<script src="<%=request.getContextPath()%>/summernote/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/summernote/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/summernote/dist/summernote.js"></script>
<script src="<%=request.getContextPath()%>/summernote/dist/lang/summernote-zh-CN.js"></script>    <!-- 中文-->
<style>
        .m {
            width: 800px;
            margin-left: auto;
            margin-right: auto;
        }
</style>
<script>
    var str='${message}';
    if(str!=''){
      alert(str);
    }
    
    function updateAuth(id){
    	/* $.ajax({
    		url : "${CONTEXT_PATH}/storeXcxInfo/updateAuth",
    		data : {'id':id},
    		success : function(result){
    			alert(1);
    			alert(result);
    			//window.open();
    		}
    	}); */
    	$.ajax({
    	    type: "post",
    	    async: false,
    	    url : "${CONTEXT_PATH}/storeXcxInfo/updateAuth",
    	    data : {'id':id},
    	    dataType: "json",
    	    success: function(data) {
    	        if (data.msg == null || data.msg == "") {
    	        	window.open(data.data);
    	        }
    	    }
    });
    }
    </script>
<div class="m">
	<table class="table table-responsive">
		<tr>
			<td colspan="2"><label style="color: #3399ff;">小程序尚未授权，授权后将可避免复杂的配置，并能更安全、更便捷地使用我们的系统。</label>
					<button type="button" class="btn btn-primary" onclick="updateAuth(${xcx.id})">立即授权</button>
			</td>
		</tr>
		<tr>
			<td>小程序名称</td>
			<td><input type="text" name="appName" id="appName" value="${xcx.appname}" maxlength="30" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>小程序APPID</td>
			<td><input type="text" name="appId" id="appId" value="${xcx.appid}" maxlength="30" style="width: 300px;"/></td>
		</tr>
	</table>
	<input type="hidden" name="id" value="${xcx.id}"/>
</div>

