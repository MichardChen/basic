<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>新增</title>
<link type="image/x-icon" rel="shortcut icon" href="${CONTEXT_PATH}/assets/img/tjico.ico" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/summernote/css/bootstrap.css">
<link href="<%=request.getContextPath()%>/summernote/dist/summernote.css" rel="stylesheet"/>
<script src="<%=request.getContextPath()%>/summernote/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/summernote/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/summernote/dist/summernote.js"></script>
<script src="<%=request.getContextPath()%>/summernote/dist/lang/summernote-zh-CN.js"></script>    <!-- 中文-->
<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/My97DatePicker/WdatePicker.js"></script>
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
			<td>参考价</td>
			<td><input type="number" step="0.01" name="referencePrice" id="referencePrice" maxlength="30" style="width: 100px;"/>(元/片)</td>
		</tr>
		<tr>
			<td>参考单价区间</td>
			<td><input type="number" step="0.01" name="fromPrice" id="fromPrice" maxlength="30" style="width: 100px;"/>&nbsp;-&nbsp;<input type="number" step="0.01" name="toPrice" id="toPrice" maxlength="30" style="width: 100px;"/>(元/片)</td>
		</tr>
		<tr>
			<td>有效截止日期</td>
			<td><input type="text" name="expireDate" id="expireDate" style="width: 120px;" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/></td>
		</tr>
		<tr>
			<td>备注</td>
			<td><input type="text" name="mark" id="mark" style="width: 200px;"/></td>
		</tr>
	</table> 
	<input type="hidden" id="teaId" name="teaId" value="${teaId}"/>
</div>

