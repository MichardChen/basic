<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<title>修改用户信息</title>
<script type="text/javascript">
var str='${message}';
if(str!=''){
  alert(str);
}
</script>
<div class="control-group">
	<table class="table table-responsive">
		<tr>
			<td>用户名</td>
			<td><input type="text" name="name" value="${model.name}"/></td>
		</tr>
		<tr>
			<td>注册手机</td>
			<td><input type="text" name="mobile" value="${model.mobile}" style="IME-MODE: disabled;"/></td>
		</tr>
		<tr>
			<td>账号余额</td>
			<td><input type="text" name="moneys" value="${model.moneys}" style="IME-MODE: disabled;"/></td>
		</tr>
		<tr>
			<td>用户状态</td>
			<td>
				<select style="height:30px;width:120px;" name="status" id="status">
					<option value="040001" <c:if test="${model.status=='040001'}">selected="selected"</c:if>>未认证</option>
					<option value="040002" <c:if test="${model.status=='040002'}">selected="selected"</c:if>>已认证</option>
				</select>
			</td>
		</tr>
		<input type="hidden" name="id" value="${model.id}"/>
	</table>
</div>