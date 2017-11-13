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
			<td>所属地区</td>
			<td>${model.city_district }</td>
		</tr>
		<tr>
			<td>门店地址</td>
			<td>${model.store_address }</td>
		</tr>
		<tr>
			<td>经纬度</td>
			<td>经度：${model.longitude }，纬度：${model.latitude}</td>
		</tr>
		<tr>
			<td>主营茶叶</td>
			<td>${model.business_tea}</td>
		</tr>
		<tr>
			<td>营业时间</td>
			<td>${model.business_fromtime}-${model.business_totime}</td>
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
		<c:forEach var="s" items="${imgs}" varStatus="status">
			<tr>
				<td>
					<c:if test="${status.index==0}">
						门店图片<br><label style="color: red;">（可选择文件，保存更新图片）</label>
					</c:if>
				</td>
				<td>
						<a href="${s}" target="blank">查看图片</a><input type="file" name="img${status.index+1}"/>
				</td>
			</tr>
		</c:forEach>
	</table>
	<input type="hidden" name="storeId" id="storeId" value="${model.id}"/>
</div>