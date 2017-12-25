<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>修改开票详情</title>
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
			<td>申请人电话</td>
			<td class="td_class">
				${model.mobile}
			</td>
		</tr>
		<tr>
			<td>发票类型</td>
			<td class="td_class">
					<select name="typeCd" style="height: 30px;width: 150px;">
	    					<option></option>
	    					<option value="320001" <c:if test="${model.invoice_type_cd=='320001'}">selected="selected"</c:if>>电子发票</option>
	    					<option value="320002" <c:if test="${model.invoice_type_cd=='320002'}">selected="selected"</c:if>>纸质发票</option>
	    				</select>	
			</td>
		</tr>
		<tr>
			<td>抬头类型</td>
			<td class="td_class">
						<select name="titleTypeCd" style="height: 30px;width: 150px;">
	    					<option></option>
	    					<option value="330001" <c:if test="${model.title_type_cd=='330001'}">selected="selected"</c:if>>企业</option>
	    					<option value="330002" <c:if test="${model.title_type_cd=='330002'}">selected="selected"</c:if>>个人/非企业单位</option>
	    				</select>	
			</td>
		</tr>
		<tr>
			<td>发票抬头</td>
			<td class="td_class">
					<input type="text" name="title" value="${model.title}"/>
			</td>
		</tr>
		<tr>
			<td>申请状态</td>
			<td class="td_class">
						<select name="status" style="height: 30px;width: 150px;">
	    					<option></option>
	    					<option value="340001" <c:if test="${model.status=='340001'}">selected="selected"</c:if>>待处理</option>
	    					<option value="340003" <c:if test="${model.status=='340003'}">selected="selected"</c:if>>未开票</option>
	    					<option value="340004" <c:if test="${model.status=='340004'}">selected="selected"</c:if>>已开票</option>
	    				</select>	
			</td>
		</tr>
		<tr>
			<td>发票号</td>
			<td class="td_class">
					<input type="text" name="invoiceNo" value="${model.invoice_no}"/>
			</td>
		</tr>
		<tr>
			<td>税务单号</td>
			<td class="td_class">
					<input type="text" name="taxNo" value="${model.tax_no}"/>
			</td>
		</tr>
		<tr>
			<td>发票内容</td>
			<td class="td_class">
					<input type="text" name="content" value="${model.content}"/>
			</td>
		</tr>
		<tr>
			<td>发票金额</td>
			<td class="td_class">
						<input type="number" step="0.01" name="moneys" value="${model.moneys}"/>
			</td>
		</tr>
		<tr>
			<td>备注</td>
			<td class="td_class">
						<input type="text" name="mark" value="${model.mark}"/>
			</td>
		</tr>
		<%-- <tr>
			<td>仓储费</td>
			<td class="td_class">${data.warehouse_fee}</td>
		</tr> --%>
		<tr>
			<td>申请时间</td>
			<td class="td_class">${model.create_time}</td>
		</tr>
		
		<tr>
			<td>开户行</td>
			<td class="td_class">
					<input type="text" name="bank" value="${model.bank}"/>
			</td>
		</tr>
		<tr>
			<td>账户</td>
			<td class="td_class">
					<input type="text" name="account" value="${model.account}"/>
			</td>
		</tr>
		<tr>
			<td>邮箱</td>
			<td class="td_class">
					<input type="text" name="mail" value="${model.mail}"/>
			</td>
		</tr>
		<tr style="border-bottom: 1px solid grey;">
			<td>邮寄信息</td>
			<td class="td_class">
					${address}
			</td>
		</tr>
		<tr>
			<td>快递名称</td>
			<td class="td_class">
					<select style="height:30px;width:120px;" name="expressName" id="expressName" >
						<c:forEach var="s" items="${express}">
							<option <c:if test="${s.name==model.express_company}">selected="selected"</c:if>>${s.name}</option>
						</c:forEach>
					</select>
			</td>
		</tr>
		<tr>
			<td>快递单号</td>
			<td class="td_class"><input type="text" name="expressNo" maxlength="30" id="expressNo" style="width: 300px;" value="${model.express_no}"/></td>
		</tr>
	</table>
		<input type="hidden" name="id" value="${model.id}"/>
</div>

