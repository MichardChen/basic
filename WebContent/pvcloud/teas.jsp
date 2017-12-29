<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>茶叶管理</title>
<%@include file="../common/header.jsp"%>
<link type="image/x-icon" rel="shortcut icon" href="${CONTEXT_PATH}/assets/img/tjico.ico" />
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
var closeFlg='${closeFlg}';
if(closeFlg=1){
	window.close();
	window.opener.location.reload(); 
}
function loadProject(){
	window.open("${CONTEXT_PATH}/teaInfo/addTea");
}

function edit(data){
	window.open("${CONTEXT_PATH}/teaInfo/editTea?id="+data);
}
function addTeaPrice(data){
	$.ajax({
		url : "${CONTEXT_PATH}/teaInfo/addTeaPrice",
		data : {id:data},
		dataType : "html",
		success : function(result){
			$('#model1').html(result);
		}
	});
}
function editPrice(data){
	if(data==0){
		$(".modal-title").html("新增");
	}else{
		$(".modal-title").html("修改");
	}
	$.ajax({
		url : "${CONTEXT_PATH}/teaInfo/alertPrice",
		data : {'teaId':data},
		dataType : "html",
		success : function(result){
			$('#models1').html(result);
		}
	});
}
function checkPrice(){
	var price = $("#price").val();
	if(price == ""){
			alert("价格不能为空");
			return false;
	}
	if(price=="0" || price=="0.00"){
		alert("价格必须大于0");
		return false;
	}
	return true;
}

function check(){
	var referencePrice = $("#referencePrice").val();
	var fromPrice = $("#fromPrice").val();
	var toPrice = $("#toPrice").val();
	var expireDate = $("#expireDate").val();
	if(referencePrice == ""){
		alert("参考价不能为空");
		return false;
	}
	if(fromPrice == ""){
		alert("最低参考价不能为空");
		return false;
	}
	 if(expireDate == ""){
		alert("有效截止日期不能为空");
		return false;
	}
	return true;
}
function exportData(){
	if(confirm('确认要导出数据?')){
		var newStatus = $("#newStatus").val();
		var title = $("#title").val();
		var params = "?newStatus="+newStatus+"&title="+title;
		window.location.href="${CONTEXT_PATH}/teaInfo/exportData"+params;
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
	   		<div style="font-size: 30px;color: white;font-weight: bold;">茶信息</div>
	   </div>
    	<hr/>	
	<div class="span" style="width:100%;color:black;font-size:12px;border:2px solid #dadada;">
   		<div class="" style="margin-top:15px;margin-bottom:15px;">
    		<form method="post" action="${CONTEXT_PATH}/teaInfo/queryByConditionByPage" class="form-horizontal">
    			<div style="" class="form-group">
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">茶叶名称</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="title" id="title" value="${title}"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">发行状态</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">
	    				<select name="newStatus" id="newStatus" style="height: 30px;width: 120px;">
		    					<option></option>
		    					<option value="090001" <c:if test="${newStatus=='090001'}">selected="selected"</c:if>>待售</option>
		    					<option value="090002" <c:if test="${newStatus=='090002'}">selected="selected"</c:if>>发行中</option>
		    					<option value="090003" <c:if test="${newStatus=='090003'}">selected="selected"</c:if>>发行结束</option>
		    			</select>	
	    			</div>
    			<div style="" class="col-sm-1 col-xs-1 col-md-1">
    				<input type="submit" class="ys2" value=""/>
    			</div>
    			<div style="" class="col-sm-1 col-xs-1 col-md-1">
    				<button type="button" class="btn btn-primary" onclick="exportData()">导出</button>
    			</div>
       			<div style="display:inline-block;float:right;margin-right:5%;"><input type="button" value="新茶发行" class="ys3" onclick="loadProject(0)"/></div>
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
    				<th>茶名称</th>
    				<th>茶编码</th>
    				<th>茶类型</th>
    				<th>茶价格</th>
    				<th>参考价</th>
    				<th>茶叶发行状态</th>
    				<!-- 新增 -->
    				<th>品牌</th>
    				<th>产地</th>
    				<th>规格</th>
    				<th>生产商</th>
    				<th>出品商</th>
    				<!-- 四个值 -->
    				<th>发行件数</th>
    				<th>剩余件数</th>
    				<th>发行总量</th>
    				<th>剩余库存</th>
    				<!--  -->
    				<!-- <th>状态</th> -->
    				<th>是否删除</th>
    				<!-- 新增完 -->
    				<th>创建时间</th>
    				<th>操作</th>
    			</tr>
    		</thead>
    		<tbody>
    				<c:if test="${teaList.totalRow==0 }">
			    		<tr>
			    			<td colspan="7" style="font-size:30px;padding-top:18%;padding-left:45%;">没有找到相关数据</td>
			    		</tr>
		    		</c:if>
		    		<c:if test="${teaList.totalRow>0 }">
		    			<c:forEach var="s" items="${sList}" varStatus="status">	
		    				<tr class="bOrder">
		    					<td>${teaList.pageSize*(teaList.pageNumber-1)+status.index+1}</td>
		    					<td>${s.name }</td>
		    					<td>${s.keyCode}</td>
		    					<td>${s.type}</td>
		    					<td>${s.price}</td>
		    					<td>${s.referencePrice}</td>
		    					<td>${s.status}</td>
		    					<!-- 新增 -->
		    					<td>${s.brand}</td>
		    					<td>${s.productPlace}</td>
		    					<td>${s.size}</td>
		    					<td>${s.productBusiness}</td>
		    					<td>${s.makeBusiness}</td>
		    						<!-- 四个 -->
		    					<td>${s.saleItems}件</td>
		    					<td>${s.stock}</td>
		    					<td>${s.amount}片</td>
		    					<td>${s.syPiece}</td>
		    					<!-- 新增完 -->
		    					<%-- <td>${s.status}</td> --%>
		    					<td>
		    							<c:if test="${s.flg==1}">
		    							否
				    					</c:if>
				    					<c:if test="${s.flg!=1}">
				    					是
				    					</c:if>
		    					</td>
		    					<td>${s.createTime}</td>
		    					<td>
		    							<c:if test="${s.flg==1}">
		    									<input type="button" value="删除" class="ys3" onclick="if(confirm('确认要删除数据?')){window.location='${CONTEXT_PATH}/teaInfo/del?id=${s.id}';}"/>
		    							</c:if>
		    							<!-- 停售 -->
		    							<c:if test="${s.statusCd == '090001' }">
		    									<input type="button" value="发行" class="ys3" onclick="if(confirm('确认要发行?')){window.location='${CONTEXT_PATH}/teaInfo/updateStatus?id=${s.id}&status=090002';}"/>
		    									<input type="button" value="结束" class="ys3" onclick="if(confirm('确认要结束?')){window.location='${CONTEXT_PATH}/teaInfo/updateStatus?id=${s.id}&status=090003';}"/>
		    							</c:if>
		    							<!-- 发行 -->
		    							<c:if test="${s.statusCd == '090002' }">
		    									<input type="button" value="停售" class="ys3" onclick="if(confirm('确认要停售?')){window.location='${CONTEXT_PATH}/teaInfo/updateStatus?id=${s.id}&status=090001';}"/>
		    									<input type="button" value="结束" class="ys3" onclick="if(confirm('确认要结束?')){window.location='${CONTEXT_PATH}/teaInfo/updateStatus?id=${s.id}&status=090003';}"/>
		    							</c:if>
		    		      		 <%-- <a href="${s.url}" target="_blank"><input type="button" value="查看编辑内容" style="width: 100px;" class="ys3"/></a>  --%>
		    		      		 <input type="button" value="查看" class="ys3"  onclick="edit(${s.id})"/>
		    		      		 <input type="button" value="设置参考价" style="width: 100px;" class="ys3" data-toggle="modal" data-target="#myModal1" onclick="addTeaPrice(${s.id})"/>
		    					</td>
		    				</tr>
		    			</c:forEach>
					</c:if>
    		</tbody>
    		</table>
    		  
    		</div>
    		<div id="botton" style="position:absolute;bottom:50px;right:5%;color:black;margin:0 auto;font-size:12px;">
			    	<c:set var="pageNumber" scope="request" value="${teaList.pageNumber}" />
		            <c:set var="pageSize" scope="request" value="${teaList.pageSize}" />
		            <c:set var="totalPage" scope="request" value="${teaList.totalPage}" />
		            <c:set var="totalRow" scope="request" value="${teaList.totalRow}" />
					<c:set var="pageUrl" scope="request" value="${CONTEXT_PATH}/teaInfo/queryByPage/-" />    	
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
			<form action="${CONTEXT_PATH}/teaInfo/saveTea" method="post" enctype="multipart/form-data">
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
<!-- 设置区间价格 -->
<div class="modal fade bs-example-modal-lg" id="myModal1" role="dialog" aria-label="myModalDialog" aria-hidden="true" style="">
	<div class="modal-dialog modal-lg">
		<div class="modal-content" style="width: 120%;margin-left:-10%;">
			<div class="modal-header">
				<button type="button" data-dismiss="modal" class="close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">修改区间价格</h4>
			</div>
			<form action="${CONTEXT_PATH}/teaInfo/saveTeaPrice" method="post" onsubmit="return check();">
				<div class="modal-body" id="model1">
				</div>
				<div class="modal-footer" style="margin-top:20px;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<input type="submit" class="btn btn-success" value="保存"/>
				</div>
			</form>
		</div>
	</div>
</div>

<div class="modal fade bs-example-modal-lg" id="myModalDialog1" role="dialog" aria-label="myModalDialog" aria-hidden="true" style="">
	<div class="modal-dialog modal-lg">
		<div class="modal-content" style="width: 120%;margin-left:-10%;">
			<div class="modal-header">
				<button type="button" data-dismiss="modal" class="close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">修改</h4>
			</div>
			<form action="${CONTEXT_PATH}/teaInfo/updateTeaPrice" method="post" onsubmit="return checkPrice();">
				<div class="modal-body" id="models1">
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