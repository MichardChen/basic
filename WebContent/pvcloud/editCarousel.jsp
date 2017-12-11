<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>修改跳转链接</title>
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
    </script>
<div class="m">
	<table class="table table-responsive">
		<tr>
			<td>跳转链接</td>
			<td><input type="text" name="realUrl" maxlength="100" placeholder="地址最长100个字" style="width: 300px;" value="${data.real_url}"/>
			<input type="hidden" name="id" value="${data.id}"/>
			</td>
		</tr>
		<tr>
			<td>备注</td>
			<td><input type="text" name="mark" maxlength="30" placeholder="备注最长30个字" style="width: 300px;" value="${data.mark}"/>
			</td>
		</tr>
	</table>
</div>

