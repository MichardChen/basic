<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>用户管理</title>
<%@include file="../common/header.jsp"%>
<link href="${CONTEXT_PATH}/assets/css/animate.css" rel="stylesheet">
<link href="${CONTEXT_PATH}/assets/css/starCore.css" rel="stylesheet">
<link href="${CONTEXT_PATH}/assets/css/common.css" rel="stylesheet">
<script src="${CONTEXT_PATH}/assets/lib/jquery-2.1.1.min.js"></script>
<script src="${CONTEXT_PATH}/assets/js/common.js"></script>
<script type="text/javascript">
var str='${message}';
if(str!=''){
  alert(str);
}

function loadProject(data){
	if(data==0){
		$(".modal-title").html("新增");
	}else{
		$(".modal-title").html("修改");
	}
	$.ajax({
		url : "${CONTEXT_PATH}/newsInfo/addNews",
		data : {},
		dataType : "html",
		success : function(result){
			$('.modal-body').html(result);
		}
	});
}

</script>
<style>
.ys1{
	height:30px;
}
.ys2{
	background-image:url("${CONTEXT_PATH}/image/search.png");
	width:43px;
	height:32px;
	border:0;
}
.ys3{
	width:80px;
	height:32px;
	border:1px solid #dddddd;
	background-color:#0099cc;
	color:#fff;
}
a:hover{
	text-decoration:none!important;
}
.control-group td{
	border:0!important;
}
.span a{
	color:black!important;
}
.btn-color{
	background-color:transparent;
	border:0;
}
th{
	white-space:nowrap;
}
td{
	white-space:nowrap;
	border:0!important;
}
.table thead tr{
	background-color:#F5F6FA;
	color:#999;
}
.table thead tr a{
	color:#999;
}
.table thead tr a:hover{
	text-decoration:underline!important;
}
#botton a{
	color:#000;
}
.table tbody{
	margin-top:5px;
}
.bOrder:hover{
	background-color:#e4f6f6;
}
.bg{
	background-color:#e4f6f6;
}
.fl{
	float:left;
}
</style>
</head>
<body class="fixed-nav fixed-sidebar">
<div id="wrapper">
	<div id="page-wrapper" class="gray-bg dashbard-1" style="background-color:#fff;margin-top:50px;">
		<div class="wrapper wrapper-content animated fadeInRightBig">
    	<div class="" style="width:100%;color=black;font-size:15px;height:50px;line-height:50px;margin-bottom:20px;">
	    	<div class="fl"><img src="${CONTEXT_PATH }/image/picturesfolder.ico" style="width:50px; height:50px;"/></div>
	   		<div class="fl">资讯信息</div>
	   </div>
	  
    	<hr/>			
    <div class="span" style="width:100%;color:black;font-size:12px;border:2px solid #dadada;">
   		<div class="" style="margin-top:15px;margin-bottom:15px;">
    		<form method="post" action="${CONTEXT_PATH}/custInfo/queryByCondition">
    			<div style="margin-left:10px;display: inline-block;">
    				<select style="height:30px;width:120px;" name="cInfo">
    					<option></option>
    					<c:if test="${empty custInfo }">
	    					<option value="addrName">用户姓名</option>
							<option value="phoneNum">手机</option>
						</c:if>
						<c:if test="${custInfo=='addrName' }">
							<option value="addrName" selected="selected">用户姓名</option>
							<option value="phoneNum">手机</option>
						</c:if>
						<c:if test="${custInfo=='phoneNum' }">
							<option value="addrName">用户姓名</option>
							<option value="phoneNum" selected="selected">手机</option>
						</c:if>
    				</select>
    			</div>
    			<div style="margin-left:10px;display: inline-block;">
   					<input type="text" class="ys1" autocomplete="off" placeholder="" name="cValue" value="${custValue }"/>
   				</div>
    			<div style="display: inline-block;"><input type="submit" class="ys2" value=""/></div>
    			<div style="display:inline-block;float:right;margin-right:5%;"><input type="button" value="新增" class="ys3" data-toggle="modal" data-target="#myModal" onclick="loadProject(0)"/></div>
    		</form>
    		
   		</div>
	</div>
    <div class="container equip" style="width:100%;font-size:12px;border:1px solid #dadada;margin-top:15px;height:690px;position:relative;color:black;margin-left:0px;">
    	<div class="row">
    		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 table-responsive" style="padding-left:0px;padding-right:0px;font-size:14px;height:705px;">
    		<table class="table table-responsive" id="myTb" >
    		<thead>
    			<tr>
    				<th>资讯标题</th>
    				<th>资讯类型</th>
    				<th>创建者</th>
    				<th>创建时间</th>
    				<th>状态</th>
    				<th>操作</th>
    			</tr>
    		</thead>
    		
    		<tbody>
    				<c:if test="${newsList.totalRow==0 }">
			    		<tr>
			    			<td colspan="7" style="font-size:30px;padding-top:18%;padding-left:45%;">没有找到相关数据</td>
			    		</tr>
		    		</c:if>
		    		<c:if test="${newsList.totalRow>0 }">
		    			<c:forEach var="newsList" items="${newsList.list}">	
		    				<tr class="bOrder">
		    					<td style="display:none;">${newsList.news_title }</td>
		    					<td>${newsList.hot_flg }</td>
		    					<td>${newsList.news_title }</td>
		    					<td>${newsList.create_user }</td>
		    					<td><fmt:formatDate value="${newsList.create_time }" pattern="yyyy-MM-dd hh:mm:ss"/></td>
		    					<td><fmt:formatDate value="${newsList.update_time }" pattern="yyyy-MM-dd hh:mm:ss"/></td>
		    					<td>
		    						<input type="button" value="修改" class="ys3" data-toggle="modal" data-target="#myModal" onclick="loadProject(${newsList.id})"/>
		    						<input type="button" value="删除" class="ys3" onclick="if(confirm('确认要删除数据?')){window.location='${CONTEXT_PATH}/newsInfo/del?newsId=${newsList.id}';}"/>
		    					</td>
		    				</tr>
		    			</c:forEach>
					</c:if>
    		</tbody>
    		</table>
    		  
    		</div>
    		<div id="botton" style="position:absolute;bottom:5px;right:5%;color:black;margin:0 auto;font-size:12px;">
			    	<c:set var="pageNumber" scope="request" value="${newsList.pageNumber}" />
		            <c:set var="pageSize" scope="request" value="${newsList.pageSize}" />
		            <c:set var="totalPage" scope="request" value="${newsList.totalPage}" />
		            <c:set var="totalRow" scope="request" value="${newsList.totalRow}" />
					<c:set var="pageUrl" scope="request" value="${CONTEXT_PATH}/custInfo/queryByConditionByPage/-" />    	
			    	<%@include file="../common/page.jsp"%>
				</div>
    	</div>
    </div>
		</div>
	</div>
</div>
<%@include file="../common/layout.jsp"%>
<div class="modal fade bs-example-modal-lg" id="myModal" role="dialog" aria-label="myModalDialog" aria-hidden="true" style="">
	<div class="modal-dialog modal-lg">
		<div class="modal-content" style="width: 120%;margin-left:-10%;">
			<div class="modal-header">
				<button type="button" data-dismiss="modal" class="close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">修改</h4>
			</div>
			<form action="${CONTEXT_PATH}/newsInfo/saveNews" method="post" enctype="multipart/form-data">
				<div class="modal-body">
				</div>
				<div class="modal-footer" style="margin-top:20px;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<input type="submit" class="btn btn-success" value="保存"/>
				</div>
			</form>
		</div>
	</div>
</div>
</body>
</html>