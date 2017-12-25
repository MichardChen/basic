<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>新茶订单记录</title>
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
		url : "${CONTEXT_PATH}/documentInfo/addDocument",
		data : {},
		dataType : "html",
		success : function(result){
			$('.modal-body').html(result);
		}
	});
}

function edit(data){
	$(".modal-title").html("修改");
	$.ajax({
		url : "${CONTEXT_PATH}/orderInfo/editOrder",
		data : {id:data},
		dataType : "html",
		success : function(result){
			$('.modal-body').html(result);
		}
	});
}

function check(){
	var saleMobile = $("#saleMobile").val();
	var saleUserTypeCd = $("#saleUserTypeCd").val();
	if(saleMobile != ""){
		if(saleUserTypeCd == ""){
			alert("请先选择卖家类型");
			return false;
		}
	}
	return true;
}

function exportData(){
	if(confirm('确认要导出数据?')){
		var title = $("#title").val();
		var saleMobile = $("#saleMobile").val();
		var buyMobile = $("#buyMobile").val();
		var params = "?title="+title+"&saleMobile="+saleMobile+"&buyMobile="+buyMobile;
		window.location.href="${CONTEXT_PATH}/sellManageInfo/exportData"+params;
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
	   		<div style="font-size: 30px;color: white;font-weight: bold;">新茶订单记录</div>
	   </div>
    	<hr/>	
	<div class="span" style="width:100%;color:black;font-size:12px;border:2px solid #dadada;">
   		<div class="" style="margin-top:15px;margin-bottom:15px;">
    		<form method="post" action="${CONTEXT_PATH}/sellManageInfo/queryByConditionByPage" class="form-horizontal" onsubmit="return check();">
    			<div style="" class="form-group">
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">订单时间</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="title" id="title" value="${title}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">经销商注册电话</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="saleMobile" id="saleMobile" value="${saleMobile}"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">买家注册电话</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="buyMobile" id="buyMobile" value="${buyMobile}"/>
    				</div>
    				<%-- <label class="col-sm-1 col-xs-1 col-md-1 control-label">付款时间</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="payTime" id="payTime" value="${payTime}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">订单号</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="orderNo" id="orderNo" value="${orderNo}"/>
    				</div>  --%>
    				<%-- <label class="col-sm-1 col-xs-1 col-md-1 control-label">买家注册电话</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="buyMobile" id="buyMobile" value="${buyMobile}"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">卖家注册电话</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="saleMobile" id="saleMobile" value="${saleMobile}"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">卖家类型</label>
	    			<div class="col-sm- col-xs-1 col-md-1">	
	    				<select name="saleUserTypeCd" style="height: 30px;width: 120px;" id="saleUserTypeCd">
		    					<option></option>
		    					<option value="010001" <c:if test="${saleUserTypeCd=='010001'}">selected="selected"</c:if>>普通卖家</option>
		    					<option value="010002" <c:if test="${saleUserTypeCd=='010002'}">selected="selected"</c:if>>平台卖家</option>
		    			</select>	
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">状态</label>
	    			<div class="col-sm- col-xs-1 col-md-1">	
	    				<select name="status" id="status" style="height: 30px;width: 120px;">
		    					<option></option>
		    					<option value="140003" <c:if test="${status=='140003'}">selected="selected"</c:if>>下单成功</option>
		    					<option value="140004" <c:if test="${status=='140004'}">selected="selected"</c:if>>下单失败</option>
		    			</select>	
    				</div> --%>
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
    				<th>订单号</th>
    				<th>产品名称</th>
    				<th>类型</th>
    				<th>仓库</th>
    				<th>经销商</th>
    				<th>经销商注册电话</th>
    				<th>买家</th>
    				<!-- <th>卖家</th>
    				<th>门店</th>
    				<th>门店联系电话</th> -->
    				<th>下单时间</th>
    				<th>付款时间</th>
    				<th>数量</th>
    				<th>单价</th>
    				<th>总金额</th>
    				<th>状态</th>
    				<!-- <th>产品详情</th> -->
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
		    					<td>${s.orderNo}</td>
		    					<td>${s.name}</td>
		    					<td>${s.type}</td>
		    					<td>${s.wareHouse}</td>
		    					<td>${s.sellName}</td>
		    					<td>${s.sellMobile}</td>
		    					<td>${s.buyUser}</td>
		    					<%-- <td>${s.saleUser}</td>
		    					<td>${s.store}</td>
		    					<td>${s.mobile}</td> --%>
		    					<td>${s.createTime}</td>
		    					<td>${s.payTime}</td>
		    					<td>${s.stock}</td>
		    					<td>${s.price}</td>
		    					<td>${s.amount}</td>
		    					<td>${s.status}</td>
		    					<%-- <td><a href="${s.productUrl}" target="_blank" style="color: blue;">产品详情</a></td> --%>
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
					<c:set var="pageUrl" scope="request" value="${CONTEXT_PATH}/sellManageInfo/queryByPage/-" />    	
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
			<form action="${CONTEXT_PATH}/orderInfo/saveOrder" method="post">
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