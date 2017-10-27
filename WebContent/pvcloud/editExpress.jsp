<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>修改信息</title>
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
        .td_class{
        	text-align: left;
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
			<td>申请人</td>
			<td class="td_class">${member}</td>
		</tr>
		<tr>
			<td>茶叶名称</td>
			<td class="td_class">${tea.tea_title}</td>
		</tr>
		<tr>
			<td>数量</td>
			<td class="td_class">${data.quality}</td>
		</tr>
		<tr>
			<td>仓储费</td>
			<td class="td_class">${data.warehouse_fee}</td>
		</tr>
		<tr>
			<td>申请时间</td>
			<td class="td_class">${data.create_time}</td>
		</tr>
		<tr>
			<td>快递名称</td>
			<td class="td_class"><input type="text" name="expressName" maxlength="30" id="expressName" style="width: 300px;" value="${data.express_company}"/></td>
		</tr>
		<tr>
			<td>快递单号</td>
			<td class="td_class"><input type="text" name="expressNo" maxlength="30" id="expressNo" style="width: 300px;" value="${data.express_no}"/></td>
		</tr>
		<tr>
			<td>状态</td>
			<td class="td_class">
						<select name="status" style="height: 30px;">
	    					<option></option>
	    					<option value="280001" <c:if test="${data.status=='280001'}">selected="selected"</c:if>>申请中</option>
	    					<option value="280002" <c:if test="${data.status=='280002'}">selected="selected"</c:if>>申请失败</option>
	    					<option value="280003" <c:if test="${data.status=='280003'}">selected="selected"</c:if>>申请成功，待发货</option>
	    				 	<option value="280004" <c:if test="${data.status=='280004'}">selected="selected"</c:if>>已收货</option>
	    					<option value="280005" <c:if test="${data.status=='280005'}">selected="selected"</c:if>>异常</option>
	    				</select>	
			</td>
		</tr>
		<tr>
			<td>备注</td>
			<td class="td_class">
						<input type="text" name="mark" maxlength="30" id="mark" style="width: 300px;" value="${data.mark}"/>
						<input type="hidden" name="id" value="${data.id}"/>
		</tr>
	</table>
</div>

