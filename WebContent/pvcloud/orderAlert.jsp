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
			<td>数据项1</td>
			<td><input type="text" name="data1" value="${model.id}"/></td>
		</tr>
		<tr>
			<td>数据项2</td>
			<td><input type="text" name="data2" value="${model.tea_id}" style="IME-MODE: disabled;"/></td>
		</tr>
		<tr>
			<td>备注</td>
			<td><input type="text" name="mark" value="${model.pay_time}" style="IME-MODE: disabled;"/></td>
		</tr>
		<input type="hidden" name="id" value="${model.id}"/>
	</table>
</div>