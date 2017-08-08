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
			<td>手机号码</td>
			<td><input type="text" name="phoneNum" value="${custInfo.phonenum }"/></td>
		</tr>
		<tr>
			<td>手机归属地</td>
			<td><input type="text" name="addrname" value="${custInfo.addrname }"/></td>
		</tr>
		<tr>
			<td>积分</td>
			<td><input type="text" name="integral" value="${custInfo.integral }" style="IME-MODE: disabled;" onkeyup="this.value=this.value.replace(/\D/g,'')"  onafterpaste="this.value=this.value.replace(/\D/g,'')"/></td>
		</tr>
		<input type="hidden" name="custId" value="${custInfo.cust_id }"/>
	</table>
</div>
						
					
				
			
