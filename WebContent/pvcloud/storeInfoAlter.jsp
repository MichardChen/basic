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
			<td>门店名称</td>
			<td>${model.store_name }</td>
		</tr>
			<tr>
			<td>门店地址</td>
			<td>${model.store_address }</td>
		</tr>
			<tr>
			<td>联系电话</td>
			<td>${model.link_phone }</td>
		</tr>
			<tr>
			<td>营业时间:</td>
			<td>${model.business_fromtime }-${model.business_totime }</td>
		</tr>
			<tr>
			<td>门店详情</td>
			<td>${model.store_desc }</td>
		</tr>
	</table>
</div>
						
					

			
