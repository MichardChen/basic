<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>提现申请</title>
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
function loadProject1(data){
	$(".modal-title").html("查看");
	$.ajax({
		url : "${CONTEXT_PATH}/memberInfo/see",
		data : {id:data},
		dataType : "html",
		success : function(result){
			$('.modal-body').html(result);
		}
	});
}
function loadProject2(data){
	$(".modal-title").html("编辑备注");
	$.ajax({
		url : "${CONTEXT_PATH}/withdrawInfo/updateMarkInit",
		data : {id:data},
		dataType : "html",
		success : function(result){
			$('#m1').html(result);
		}
	});
}
function loadProject(data){
	if(data==0){
		$(".modal-title").html("新增");
	}else{
		$(".modal-title").html("修改");
	}
	$.ajax({
		url : "${CONTEXT_PATH}/storeInfo/alter",
		data : {'id':data},
		dataType : "html",
		success : function(result){
			$('.modal-body').html(result);
		}
	});
}
function exportData(){
	if(confirm('确认要导出数据?')){
		var time = $("#time").val();
		var mobile = $("#mobile").val();
		var status = $("#status").val();
		var params = "?time="+time+"&mobile="+mobile+"&status="+status;
		window.location.href="${CONTEXT_PATH}/withdrawInfo/exportData"+params;
	}else{
		return false;
	}
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
	   		<div style="font-size: 30px;color: white;font-weight: bold;">提现申请信息</div>
	   </div>
    	<hr/>	
    
	<div class="span" style="width:100%;color:black;font-size:12px;border:2px solid #dadada;">
   		<div class="" style="margin-top:15px;margin-bottom:15px;">
    		<form method="post" action="${CONTEXT_PATH}/withdrawInfo/queryByConditionByPage" class="form-horizontal">
    			<div style="" class="form-group">
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">申请时间</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="time" id="time" value="${time}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">注册手机号</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="mobile" id="mobile" value="${mobile}"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">状态</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">
	    				<select name="status" id="status" style="height: 30px;width: 150px;">
	    					<option></option>
	    					<option value="190001" <c:if test="${status=='190001'}">selected="selected"</c:if>>审核中</option>
	    					<option value="190002" <c:if test="${status=='190002'}">selected="selected"</c:if>>审核通过并转账</option>
	    					<option value="190003" <c:if test="${status=='190003'}">selected="selected"</c:if>>审核失败</option>
	    				</select>	
    				</div>
    			<div style="" class="col-sm-1 col-xs-1 col-md-1">
    					<input type="submit" class="ys2" value=""/>
    			</div>
    			<div style="" class="col-sm-1 col-xs-1 col-md-1">
    					<button type="button" class="btn btn-primary" onclick="exportData()">导出</button>	
    			</div>
			   </div>
    		</form>
   		</div>
	</div>
    <div class="container equip" style="width:100%;font-size:12px;border:1px solid #dadada;margin-top:15px;height:700px;position:relative;color:black;margin-left:0px;">
    	<div class="row">
    		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 table-responsive" style="padding-left:0px;padding-right:0px;font-size:14px;height:550px;">
    		<table class="table table-responsive" id="myTb" >
    		<thead>
    			<tr>
    				<th>序列号</th>
    				<th>申请人</th>
    				<th>注册号码</th>
    				<th>提现金额</th>
    				<th>账号余额</th>
    				<th>申请时间</th>
    				<th>状态</th>
    				<th>备注</th>
    				<th>结果截图</th>
    				<th>操作</th>
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
		    					<td>${s.name}</td>
		    					<td>${s.mobile}</td>
		    					<td>${s.moneys}</td>
		    					<td>${s.balance}</td>
		    					<td>${s.createTime}</td>
		    					<td>${s.status}</td>
		    					<td>${s.mark}</td>
		    					<td>
		    						<a href="${s.markImg}" target="_blank" style="color: blue;">查看</a>
		    					</td>
		    					<td>
		    							<c:if test="${s.statusCd=='190001'}">
		    									<input type="button" value="审核成功" class="ys3" onclick="if(confirm('确认要提交数据?')){window.location='${CONTEXT_PATH}/withdrawInfo/update?status=190002&id=${s.id}';}"/>
		    									<input type="button" value="审核失败" class="ys3" onclick="if(confirm('确认要提交数据?')){window.location='${CONTEXT_PATH}/withdrawInfo/update?status=190003&id=${s.id}';}"/>
		    							</c:if>
		    							<input type="button" value="查看用户" class="ys3" data-toggle="modal" data-target="#myModal1" onclick="loadProject1(${s.memberId})"/>
		    							<input type="button" value="编辑备注" class="ys3" data-toggle="modal" data-target="#myModal2" onclick="loadProject2(${s.id})"/>
		    					</td>
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
					<c:set var="pageUrl" scope="request" value="${CONTEXT_PATH}/withdrawInfo/queryByPage/-" />    	
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
			<%-- <form action="${CONTEXT_PATH}/teaInfo/saveTea" method="post" enctype="multipart/form-data">
				<div class="modal-body">
				</div>
				<div class="modal-footer" style="margin-top:20px;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<input type="submit" class="btn btn-success" value="保存"/>
				</div>
			</form> --%>
		</div>
	</div>
</div>
<div class="modal fade bs-example-modal-lg" id="myModal1" role="dialog" aria-label="myModalDialog" aria-hidden="true" style="">
	<div class="modal-dialog modal-lg">
		<div class="modal-content" style="width: 120%;margin-left:-10%;">
			<div class="modal-header">
				<button type="button" data-dismiss="modal" class="close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">查看</h4>
			</div>
			<form action="${CONTEXT_PATH}/memberInfo/updateMember" method="post">
				<div class="modal-body">
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade bs-example-modal-lg" id="myModal2" role="dialog" aria-label="myModalDialog" aria-hidden="true" style="">
	<div class="modal-dialog modal-lg">
		<div class="modal-content" style="width: 120%;margin-left:-10%;">
			<div class="modal-header">
				<button type="button" data-dismiss="modal" class="close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">编辑备注</h4>
			</div>
			<form action="${CONTEXT_PATH}/withdrawInfo/updateMark" method="post" enctype="multipart/form-data">
				<div class="modal-body" id="m1">
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