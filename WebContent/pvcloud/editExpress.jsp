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
			<td class="td_class">${model.name}</td>
		</tr>
		<tr>
			<td>茶叶名称</td>
			<td class="td_class">${model.teaName}</td>
		</tr>
		<tr>
			<td>数量</td>
			<td class="td_class">${model.quality}</td>
		</tr>
		<%-- <tr>
			<td>仓储费</td>
			<td class="td_class">${data.warehouse_fee}</td>
		</tr> --%>
		<tr>
			<td>申请时间</td>
			<td class="td_class">${model.createTime}</td>
		</tr>
		<tr>
			<td>快递名称</td>
			<td class="td_class">
					<select style="height:30px;width:120px;" name="expressName" id="expressName" >
						<c:forEach var="s" items="${express}">
							<option <c:if test="${s.name==model.express}">selected="selected"</c:if>>${s.name}</option>
						</c:forEach>
					</select>
			</td>
		</tr>
		<tr>
			<td>快递单号</td>
			<td class="td_class"><input type="text" name="expressNo" maxlength="30" id="expressNo" style="width: 300px;" value="${model.expressNo}"/></td>
		</tr>
		<tr>
			<td>状态</td>
			<c:if test="${model.status=='280002' || model.status=='280005'}">
				<td class="td_class">
		    				<c:if test="${model.status=='280002'}">申请失败</c:if>
		    				 <c:if test="${model.status=='280005'}">异常</c:if>
				</td>
			</c:if>
			<c:if test="${model.status!='280002' and model.status!='280005'}">
				<td class="td_class">
						<select name="status" style="height: 30px;width: 150px;">
	    					<option></option>
	    					<option value="280001" <c:if test="${model.status=='280001'}">selected="selected"</c:if>>申请中</option>
	    					<option value="280002" <c:if test="${model.status=='280002'}">selected="selected"</c:if>>申请失败</option>
	    					<option value="280003" <c:if test="${model.status=='280003'}">selected="selected"</c:if>>申请成功，待发货</option>
	    				 	<option value="280004" <c:if test="${model.status=='280004'}">selected="selected"</c:if>>已收货</option>
	    					<option value="280005" <c:if test="${model.status=='280005'}">selected="selected"</c:if>>异常</option>
	    				</select>	
	    			</td>
			</c:if>
		</tr>
		<tr>
			<td>备注</td>
			<td class="td_class">
						<input type="text" name="mark" maxlength="30" id="mark" style="width: 300px;" value="${model.mark}"/>
						<input type="hidden" name="id" value="${model.id}"/>
		</tr>
	</table>
</div>

