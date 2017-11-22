<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>服务费记录</title>
<%@include file="../common/header.jsp"%>
<link type="image/x-icon" rel="shortcut icon" href="${CONTEXT_PATH}/assets/img/tjico.ico" />
<link href="${CONTEXT_PATH}/assets/css/animate.css" rel="stylesheet">
<link href="${CONTEXT_PATH}/assets/css/starCore.css" rel="stylesheet">
<link href="${CONTEXT_PATH}/assets/css/common.css" rel="stylesheet">
<script src="${CONTEXT_PATH}/assets/lib/jquery-2.1.1.min.js"></script>
<script src="${CONTEXT_PATH}/assets/js/common.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/My97DatePicker/WdatePicker.js"></script>
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
		url : "${CONTEXT_PATH}/warehouseInfo/alter",
		data : {},
		dataType : "html",
		success : function(result){
			$('#model').html(result);
		}
	});
}
function edit(data){
	$(".modal-title").html("修改");
	$.ajax({
		url : "${CONTEXT_PATH}/warehouseInfo/edit",
		data : {id:data},
		dataType : "html",
		success : function(result){
			$('#model1').html(result);
		}
	});
}

function check(){
	var warehouseName = $("#warehouseName").val();
	var mark = $("#mark").val();
	if(warehouseName == ""){
		alert("请输入仓库名称");
		return false;
	}
	return true;
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
	text-align: left;
}
td{
	white-space:nowrap;
	border:0!important;
	text-align: left;
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
		<div class="wrapper wrapper-content animated" style="text-align: center;">
    	<div class="" style="width:100%;color:black;font-size:15px;height:40px;line-height:40px;background: #87CEFA;text-align: center;">
	  <%--   	<div class="fl"><img src="${CONTEXT_PATH }/image/picturesfolder.ico" style="width:50px; height:50px;"/></div> --%>
	   		<div style="font-size: 30px;color: white;font-weight: bold;">服务费信息</div>
	   </div>
    	<hr/>	
    	<div class="span" style="width:100%;color:black;font-size:12px;border:2px solid #dadada;">
   		<div class="" style="margin-top:15px;margin-bottom:15px;">
    		<form method="post" action="${CONTEXT_PATH}/servicefeeInfo/queryByPage" class="form-horizontal">
    			<div style="" class="form-group">
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">注册电话</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="mobile" value="${mobile}"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">销售时间</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="time" placeholder="请选择销售时间" value="${time}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/>
    				</div>
    			<div style="" class="col-sm-1 col-xs-1 col-md-1"><input type="submit" class="ys2" value=""/></div>
       			<div style="display:inline-block;float:right;margin-right:5%;"></div>
			   </div>
    		</form>
   		</div>
	</div>
	</div>		
    <div class="container equip" style="width:100%;font-size:12px;border:1px solid #dadada;margin-top:15px;height:700px;position:relative;color:black;margin-left:0px;">
    	<div class="row">
    		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 table-responsive" style="padding-left:0px;padding-right:0px;font-size:14px;height:550px;">
    		<table class="table table-responsive" id="myTb" >
    		<thead>
    			<tr>
    				<th>序列号</th>
    				<th>销售茶叶名称</th>
    				<th>销售数量</th>
    				<th>销售价格</th>
    				<th>注册电话</th>
    				<th>用户名称</th>
    				<th>服务费</th>
    				<th>备注</th>
    				<th>销售时间</th>
    			</tr>
    		</thead>
    		<tbody>
    				<c:if test="${list.totalRow==0 }">
			    		<tr>
			    			<td colspan="7" style="font-size:30px;padding-top:18%;padding-left:45%;">没有找到相关数据</td>
			    		</tr>
		    		</c:if>
		    		<c:if test="${list.totalRow>0 }">
		    			<c:forEach var="s" items="${sList}" varStatus="status">	
		    				<tr class="bOrder">
		    					<td>${list.pageSize*(list.pageNumber-1)+status.index+1}</td>
		    					<td>${s.tea}</td>
		    					<td>${s.quanlity}</td>
		    					<td>${s.price}</td>
		    					<td>${s.mobile}</td>
		    					<td>${s.userName}</td>
		    					<td>${s.fee}</td>
		    					<td>${s.mark}</td>
		    					<td>${s.createTime}</td>
		    				</tr>
		    			</c:forEach>
					</c:if>
    		</tbody>
    		</table>
    		  
    		</div>
    		<div id="botton" style="position:absolute;bottom:50px;right:5%;color:black;margin:0 auto;font-size:12px;">
			    	<c:set var="pageNumber" scope="request" value="${list.pageNumber}" />
		            <c:set var="pageSize" scope="request" value="${list.pageSize}" />
		            <c:set var="totalPage" scope="request" value="${list.totalPage}" />
		            <c:set var="totalRow" scope="request" value="${list.totalRow}" />
					<c:set var="pageUrl" scope="request" value="${CONTEXT_PATH}/servicefeeInfo/queryByConditionByPage/-" />    	
			    	<%@include file="../common/page.jsp"%>
				</div>
    	</div>
    </div>
		</div>
	</div>
<%@include file="../common/layout.jsp"%>
</body>
</html>