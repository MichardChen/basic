<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>编辑角色</title>
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
</style>
<script>
    var str='${message}';
    if(str!=''){
      alert(str);
    }

    function addAuth(){
    	var roleId = $("#roleId").val();
    	var menuId = $("#menuId").val();
    	window.location.href="${CONTEXT_PATH}/roleInfo/addAuth?roleId="+roleId+"&menuId="+menuId;
    }
    </script>
<div class="m">
	<table class="table table-responsive">
		<tr>
			<td>角色</td>
			<td>
					<input type="text" name="name" maxlength="30" style="width: 300px;" value="${role.roleName}"/>
					<input type="hidden" name="roleId" id="roleId" maxlength="30" style="width: 300px;" value="${role.id}"/>
			</td>
		</tr>
		<tr>
			<td>增加角色</td>
			<td>
					<select style="height:30px;width:120px;" name="menuId" id="menuId">
						<c:forEach var="s" items="${menus}">
							<option value="${s.menu_id}">${s.menu_name}</option>
						</c:forEach>
					</select>&nbsp;&nbsp;
					<input type="button" class="ys3" onclick="addAuth()" style="width:100px;" value="添加选中权限"/>
			</td>
		</tr>
		<tr>
			<td>访问路径</td>
			<td></td>
		</tr>
	</table>
					<c:forEach var="s" items="${menu}">
						<div style="margin-bottom: 10px;">
						${s.path} &nbsp;&nbsp;&nbsp;<input type="button" value="删除" class="ys3" onclick="if(confirm('确认要提交数据?')){window.location='${CONTEXT_PATH}/roleInfo/deleteRole?id=${s.id}';}"/>
						</div>
			</c:forEach>
	
</div>

