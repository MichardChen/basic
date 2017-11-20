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
	<%-- 	<tr>
			<td>用户名</td>
			<td><input type="text" name="userName" value="${model.name}"/></td>
		</tr> --%>
		<tr>
			<td>用户昵称</td>
			<td><input type="text" name="name" value="${model.nick_name}"/></td>
		</tr>
		<tr>
			<td>QQ</td>
			<td>${model.qq}</td>
		</tr>
			<tr>
			<td>微信</td>
			<td>${model.wx}</td>
		</tr>
		<tr>
			<td>唯一识别码</td>
			<td>${model.id_code}</td>
		</tr>
		<tr>
			<td>注册手机</td>
			<td>${model.mobile}</td>
		</tr>
		<tr>
			<td>注册时间</td>
			<td>${model.create_time}</td>
		</tr>
		<tr>
			<td>最近一次更新时间</td>
			<td>${model.update_time}</td>
		</tr>
		<tr>
			<td>账号余额</td>
			<td>${model.moneys}</td>
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
		<tr>
			<td>加盟店名称</td>
			<td>${openStore}</td>
		</tr>
		<tr>
			<td>绑定会员门店</td>
			<td>${store}</td>
		</tr>
		<tr style="border-top: 1px solid grey;">
			<td><label style="color:red;">银行卡信息</label></td>
			<td></td>
		</tr>
		<tr>
			<td>银行卡号</td>
			<td>${bankCard.card_no}</td>
		</tr>
		<tr>
			<td>银行卡</td>
			<td>
			<c:if test="${bankCard != null}">
			<a href="${bankCard.card_img}" target="blank"><img src="${bankCard.card_img}" width="100px;" height="50px;"/></a>
			</c:if>
			</td>
		</tr>
		<tr>
			<td>身份证</td>
			<td>
			<c:if test="${model != null}">
			<a href="${model.id_card_img}" target="blank"><img src="${model.id_card_img}" width="100px;" height="50px;"/></a>
			</c:if>
			</td>
		</tr>
		<tr>
			<td>开户预留手机</td>
			<td>${bankCard.stay_mobile}</td>
		</tr>
		<tr>
			<td>身份证号码</td>
			<td>${bankCard.id_card_no}</td>
		</tr>
		<tr>
			<td>开户名</td>
			<td>${bankCard.owner_name}</td>
		</tr>
		<tr>
			<td>开户行</td>
			<td>${bankCard.bank_name_cd}</td>
		</tr>
		<tr>
			<td>开户支行</td>
			<td>${bankCard.open_bank_name}</td>
		</tr>
			<tr>
				<td>审核结果</td>
				<td>
					<c:if test="${bankCard.status=='240001'}">审核中</c:if>
					<c:if test="${bankCard.status=='240002'}">审核成功</c:if>
					<c:if test="${bankCard.status=='240003'}">审核失败</c:if>
				</td>
			</tr>
		<c:if test="${!empty bankCard}">
		<tr>
			<td>银行卡审核操作</td>
			<td>
				<input type="button" value="通过" class="ys3" style="width: 100px;" onclick="if(confirm('确认要审核通过?')){window.location='${CONTEXT_PATH}/memberInfo/updateStatus?id=${bankCard.id}&status=240002';}"/>
				<input type="button" value="不通过" class="ys3" style="width:100px;" onclick="if(confirm('确认要审核不通过?')){window.location='${CONTEXT_PATH}/memberInfo/updateStatus?id=${bankCard.id}&status=240003';}"/>
			</td>
		</tr>
		</c:if>
		<input type="hidden" name="id" value="${model.id}"/>
	</table>
</div>