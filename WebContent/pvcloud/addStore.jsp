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

function selectCity1(){
	var options=$("#province option:selected");  
	var id = options.val();
	$("#city").empty(); 
	$.ajax({
	    type: "post",
	    async: false,
	    url : "${CONTEXT_PATH}/adminInfo/queryCity",
	    data: "type=1&parentId="+id,
	    dataType: "json",
	    success: function(data) {
	        if (data != 0) {
	            var temp = eval(data);
	            var html = " <option value=\"0\"> 请选择</option >";
	            for (var i = 0; i < temp.length; i++) {
	                html += "<option value='" + temp[i].id + "' >" + temp[i].name + "</option>";
	            }
	            $("#city").append(html);
	        }
	        else {
	            html = "<option>网络异常</option>";
	        }
	    }
});
}

function selectDistrict1(){
	var options=$("#city option:selected");  
	var id = options.val();
	$("#district").empty(); 
	$.ajax({
	    type: "post",
	    async: false,
	    url : "${CONTEXT_PATH}/adminInfo/queryCity",
	    data: "type=2&parentId="+id,
	    dataType: "json",
	    success: function(data) {
	        if (data != 0) {
	            var temp = eval(data);
	            var html = " <option value=\"0\"> 请选择</option >";
	            for (var i = 0; i < temp.length; i++) {
	                html += "<option value='" + temp[i].id + "' >" + temp[i].name + "</option>";
	            }
	            $("#district").append(html);
	        }
	        else {
	            html = "<option>网络异常</option>";
	        }
	    }
});
}
</script>
<div class="control-group">
	<table class="table table-responsive">
		<tr>
			<td>门店名称</td>
			<td><input type="text" name="storeName" id="storeName1" maxlength="30" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>所属地区</td>
			<td><input type="text" name="cityDistrict" id="cityDistrict" maxlength="30" style="width: 300px;"/></td>
		</tr>
	<%-- 	<tr>
			<td>所属地区</td>
			<td>
						<select style="height:30px;width:120px;" name="province" id="province" onchange="selectCity1()">
							<c:forEach var="s" items="${provinces}">
								<option value="${s.id}">${s.name}</option>
							</c:forEach>
						</select>
						<select name="city" id="city" onchange="selectDistrict1()">
				               <option value="0"> 请选择</option >
				      </select>
				      <select name="district" id="district">
				               <option value="0"> 请选择</option >
				      </select>
			</td>
		</tr> --%>
		<tr>
			<td>门店地址</td>
			<td><input type="text" name="address" id="address" maxlength="30" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>经纬度</td>
			<td>经度&nbsp;<input type="number" step="0.0000001" name="longtitude" id="longtitude" maxlength="30" style="width: 150px;"/>&nbsp;&nbsp;<a target="_blank" href="http://www.gpsspg.com/maps.htm">定位查看</a></td>
		</tr>
		<tr>
			<td></td>
			<td>纬度&nbsp;<input type="number" step="0.0000001"  name="latitude" id="latitude" maxlength="30" style="width: 150px;"/></td>
		</tr>
		<tr>
			<td>主营茶叶</td>
			<td><input type="text" name="bussineeTea" id="bussineeTea" maxlength="30" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>联系电话</td>
			<td><input type="text" name="mobile" id="mobile1" maxlength="30" style="width: 150px;"/></td>
		</tr>
		<tr>
			<td>营业时间:</td>
			<td><input type="text" name="fromTime" id="fromTime" maxlength="30" style="width: 100px;"/>&nbsp;-&nbsp;<input type="text" name="toTime" id="toTime" maxlength="30" style="width: 100px;"/></td>
		</tr>
		<tr>
			<td>门店详情</td>
			<td><input type="text" name="storeDetail" id="storeDetail" maxlength="30" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>状态</td>
			<td>
					<select style="height:30px;width:120px;" name="status" id="status">
							<option value="110002">待审核</option>
							<option value="110003">认证通过</option>
							<option value="110004">认证失败</option>
					</select>
			</td>
		</tr>
		<tr>
				<td>
						门店图片
				</td>
				<td>
						<input type="file" name="img1" id="img1"/>
				</td>
			</tr>
			<tr>
				<td>
				</td>
				<td>
						<input type="file" name="img2" id="img2"/>
				</td>
			</tr>
			<tr>
				<td>
				</td>
				<td>
						<input type="file" name="img3" id="img3"/>
				</td>
			</tr>
			<tr>
				<td>
				</td>
				<td>
						<input type="file" name="img4" id="img4"/>
				</td>
			</tr>
			<tr>
				<td>
				</td>
				<td>
						<input type="file" name="img5" id="img5"/>
				</td>
			</tr>
			<tr>
				<td>
				</td>
				<td>
						<input type="file" name="img6" id="img6"/>
				</td>
			</tr>
			<tr>
				<td>
				</td>
				<td>
						<label style="color: red;">请依次上传门店外部正面照片、内部全景照片、泡茶位置照片、展示架图片、营业执照、食品流通许可证，图片规格600px*600px</label>
				</td>
			</tr>
	</table>
	<input type="hidden" name="memberId" id="memberId" value="${memberId}"/>
</div>