<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>修改角色</title>
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
			<td>管理员名称</td>
			<td><input type="text" name="name"  value="${model.username}" maxlength="30" style="width: 300px;" placeholder="请输入英文名字"/></td>
		</tr>
			<tr>
			<td>密码</td>
			<td><input type="password" name="password"  value="${model.password}" maxlength="30" style="width: 300px;" placeholder="请输入6-18位密码"/></td>
		</tr>
			<tr>
			<td>手机</td>
			<td><input type="text" name="mobile"  value="${model.mobile}" maxlength="30" style="width: 300px;" placeholder="请输入11位手机号"/></td>
		</tr>
			<tr>
			<td>账号金额</td>
			<td><input type="number" name="moneys"  step="0.01" value="${model.moneys}" maxlength="30" style="width: 300px;" placeholder="请输入账号金额"/></td>
		</tr>
			<tr>
			<td>角色</td>
			<td>
				<select style="height:30px;width:120px;" name="roleId" id="roleId">
						<c:forEach var="s" items="${roles}">
							<option value="${s.role_id}"  <c:if test="${roleId == s.role_id }">selected="selected"</c:if>>${s.role_name}</option>
						</c:forEach>
					</select>
			</td>
		</tr>
	</table>
	<input type="hidden" name="id" value="${userId}"/>
</div>
