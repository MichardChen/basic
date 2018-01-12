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
			<td><input type="text" name="storeName" id="storeName" maxlength="30" style="width: 300px;" value="${model.store_name }"/>
			</td>
		</tr>
		<tr>
			<td>所属地区</td>
			<td><input type="text" name="cityDistrict" id="cityDistrict" maxlength="30" style="width: 300px;" value="${model.city_district }"/></td>
		</tr>
		<tr>
			<td>门店地址</td>
			<td><input type="text" name="address" id="address" maxlength="30" style="width: 300px;" value="${model.store_address }"/></td>
		</tr>
		
		
		<tr>
			<td>经纬度</td>
			<td>经度&nbsp;<input type="number" step="0.0000001" value="${model.longitude }" name="longtitude" id="longtitude" maxlength="30" style="width: 150px;"/></td>
		</tr>
		<tr>
			<td></td>
			<td>纬度&nbsp;<input type="number" step="0.0000001" value="${model.latitude}"  name="latitude" id="latitude" maxlength="30" style="width: 150px;"/>&nbsp;&nbsp;<a target="_blank" href="http://www.gpsspg.com/maps.htm">定位查看</a></td>
		</tr>
		<tr>
			<td>主营茶叶</td>
			<td><input type="text" name="bussineeTea" id="bussineeTea" value="${model.business_tea}" maxlength="30" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>联系电话</td>
			<td><input type="text" name="mobile" id="mobile" maxlength="30" value="${model.link_phone}" style="width: 150px;"/></td>
		</tr>
		<tr>
			<td>营业时间:</td>
			<td><input type="text" name="fromTime" id="fromTime" value="${model.business_fromtime}" maxlength="30" style="width: 100px;"/>&nbsp;-&nbsp;<input type="text" name="toTime" value="${model.business_totime}" id="toTime" maxlength="30" style="width: 100px;"/></td>
		</tr>
		<tr>
			<td>门店详情</td>
			<td><input type="text" name="storeDetail" id="storeDetail" value="${model.store_desc }" maxlength="30" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>状态</td>
			<td>
					<select style="height:30px;width:120px;" name="status" id="status">
							<option value="110002" <c:if test="${model.status=='110002'}">selected="selected"</c:if>>待审核</option>
							<option value="110003" <c:if test="${model.status=='110003'}">selected="selected"</c:if>>认证通过</option>
							<option value="110004" <c:if test="${model.status=='110004'}">selected="selected"</c:if>>认证失败</option>
					</select>
			</td>
		</tr>
		<c:forEach var="s" items="${imgs}" varStatus="status">
			<tr>
				<td>
					<c:if test="${status.index==0}">
						门店图片<br><label style="color: red;">（可选择文件，保存更新图片）<br/>&nbsp;&nbsp;注：图片大小不要超过1M，规格600px*600px</label>
					</c:if>
				</td>
				<td>
						<a href="${s}" target="blank">查看图片</a><input type="file" name="img${status.index+1}"/>
				</td>
			</tr>
		</c:forEach>
		<c:forEach var="i" begin="${imgSize+1}" end="6" step="1" varStatus="status">
			<tr>
				<td>
				</td>
				<td>
						<input type="file" name="img${i}"/>
				</td>
			</tr>
		</c:forEach>
	</table>
	<input type="hidden" name="storeId" id="storeId" value="${model.id}"/>
</div>