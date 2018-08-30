<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>店铺管理</title>
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

function check(){
	
	//门店名称
	var storeName = $("#storeName1").val();
	if(storeName == ""){
		 alert("请输入门店名称");
		 return false;
	}
	//所属地区
	/* var province = $("#province").val();
	var city = $("#city").val();
	var district = $("#district").val();
	if(province == "" || province == 0 || city == "" || city == 0 || district == "" || district == 0){
		 alert("请选择所属地区");
		 return false;
	} */
	var cityDistrict = $("#cityDistrict").val();
	if(cityDistrict == ""){
		 alert("请输入所属地区");
		 return false;
	}
	//门店地址
	var address = $("#address").val();
	if(address == ""){
		 alert("请输入门店地址");
		 return false;
	}
	//经纬度
	var longtitude = $("#longtitude").val();
	var latitude = $("#latitude").val();
	if(longtitude == "" || latitude == ""){
		 alert("请输入经纬度");
		 return false;
	}
	//主营茶叶
	var bussineeTea = $("#bussineeTea").val();
	if(bussineeTea == ""){
		 alert("请输入主营茶叶");
		 return false;
	}
	//联系电话
	var mobile = $("#mobile").val();
	if(mobile == ""){
		 alert("请输入联系电话");
		 return false;
	}
	//营业时间
	var bussineeFromTime = $("#fromTime").val();
	var bussineeToTime = $("#toTime").val();
	if(bussineeFromTime == "" || bussineeToTime == ""){
		 alert("请输入营业时间");
		 return false;
	}
	
	//门店详情
	/* var storeDetail = $("#storeDetail").val();
	if(storeDetail == ""){
		 alert("请输入门店详情");
		 return false;
	} */
	//至少输入一张图片	
	var img1 = $("#img1").val();
	var img2 = $("#img2").val();
	var img3 = $("#img3").val();
	var img4 = $("#img4").val();
	var img5 = $("#img5").val();
	var img6 = $("#img6").val();
	if((img1 == "") && (img2 == "")  && (img3 == "") && (img4 == "") && (img5 == "") && (img6 == "")){
		 alert("请至少输入一张图片");
		 return false;
	}
	return true;
}
function checkXCX(){
	var appId = $("#appId").val();
	var appName = $("#appName").val();
	if(appId==""){
		alert("请输入小程序APPID");
		return false;
	}
	if(appName==""){
		alert("请输入小程序名称");
		return false;
	}
	return true;
}

function loadProject(data){
	if(data==0){
		$(".modal-title").html("新增");
	}else{
		$(".modal-title").html("修改");
	}
	$.ajax({
		url : "${CONTEXT_PATH}/storeInfo/edit",
		data : {'id':data},
		dataType : "html",
		success : function(result){
			$('#model').html(result);
		}
	});
}

function loadProject1(data){
	$(".modal-title").html("查看");
	$.ajax({
		url : "${CONTEXT_PATH}/storeInfo/alter",
		data : {'id':data},
		dataType : "html",
		success : function(result){
			$('#model1').html(result);
		}
	});
}

function loadProject3(data){
	$(".modal-title").html("添加小程序");
	$.ajax({
		url : "${CONTEXT_PATH}/storeInfo/addXCX",
		data : {'id':data},
		dataType : "html",
		success : function(result){
			$('#model3').html(result);
		}
	});
}

//添加小程序
function loadProject2(data){
	window.open("${CONTEXT_PATH}/storeXcxInfo/bindXcx?id="+data);
}

function downloadImg(id){
	window.location.href="${CONTEXT_PATH}/storeInfo/generateQRCode?id="+id;
}

function updateAuth(id){
	$.ajax({
	    type: "post",
	    async: false,
	    url : "${CONTEXT_PATH}/storeXcxInfo/updateAuth",
	    data : {'id':id},
	    dataType: "json",
	    success: function(data) {
	        if (data.msg != null || data.msg != "") {
	        	window.open(data.data);
	        }
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
	text-align: left;
}
td{
	white-space:nowrap;
	text-align: left;
}
tr{
	border: 1px solid #F2F2F2;
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
    	<div class="" style="width:100%;color:black;font-size:15px;height:40px;line-height:40px;background: #F3F3F3;text-align: center;border: 1px solid #FFFFFF;">
	  <%--   	<div class="fl"><img src="${CONTEXT_PATH }/image/picturesfolder.ico" style="width:50px; height:50px;"/></div> --%>
	   		<div style="font-size: 15px;color: #686868;font-family:Georgia, serif;text-align: left;padding-left: 20px;font-weight: bold;"><span class="glyphicon glyphicon-home"></span>&nbsp;门店信息</div>
	   </div>
    	<hr/>	
    
	<div class="span" style="width:100%;color:black;font-size:12px;border:2px solid #dadada;">
   		<div class="" style="margin-top:15px;margin-bottom:15px;">
    		<form method="post" action="${CONTEXT_PATH}/storeInfo/queryByConditionByPage" class="form-horizontal">
    			<div style="" class="form-group">
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">门店名称</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="title" value="${title}"/>
    				</div>
    				<label class="col-sm-2 col-xs-2 col-md-2 control-label">商家注册手机号</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="mobile" value="${mobile}"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">状态</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">
	    				<select name="status" style="height: 30px;width: 120px;">
		    					<option></option>
		    					<option value="110001" <c:if test="${status=='110001'}">selected="selected"</c:if>>未认证</option>
		    					<option value="110002" <c:if test="${status=='110002'}">selected="selected"</c:if>>待审核</option>
		    					<option value="110003" <c:if test="${status=='110003'}">selected="selected"</c:if>>认证通过</option>
		    					<option value="110004" <c:if test="${status=='110004'}">selected="selected"</c:if>>认证失败</option>
		    			</select>	
	    			</div>
    			<div style="" class="col-sm-1 col-xs-1 col-md-1"><input type="submit" class="btn btn-info" value="查询"/></div>
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
    				<th>名称</th>
    				<th>店铺编码</th>
    				<th>本月有效评分</th>
    				<th>用户名</th>
    				<th>注册手机号</th>
    				<th>地址</th>
    				<th>描述</th>
    				<th>状态</th>
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
		    					<td>${s.title}</td>
		    					<td>${s.keyCode}</td>
		    					<td>${s.point}</td>
		    					<td>${s.userName}</td>
		    					<td>${s.mobile}</td>
		    					<td>${s.address}</td>
		    					<td>${s.title }</td>
		    					<td>${s.status}</td>
		    					<td>
		    							<input type="button" value="审核未通过" class="btn btn-primary" style="width: 100px;" onclick="if(confirm('确认要提交数据?')){window.location='${CONTEXT_PATH}/storeInfo/update?flg=110004&id=${s.id}';}"/>
		    							<input type="button" value="审核通过" class="btn btn-primary" onclick="if(confirm('确认要提交数据?')){window.location='${CONTEXT_PATH}/storeInfo/update?flg=110003&id=${s.id}';}"/>
		    							<input type="button" value="下载二维码" class="btn btn-primary" style="width: 100px;" onclick="downloadImg(${s.id})"/>
										<input type="button" value="修改门店" class="btn btn-primary" data-toggle="modal" data-target="#myModal" onclick="loadProject(${s.id})"/>
										<input type="button" value="查看" class="btn btn-primary" data-toggle="modal" data-target="#myModal1" onclick="loadProject1(${s.id})"/>
										<input type="button" value="查看会员" class="btn btn-primary" onclick="javascript:window.location='${CONTEXT_PATH}/storeInfo/queryMemberList?flg=1&storeId=${s.id}';"/>
										<input type="button" value="查看评价" class="btn btn-primary" onclick="javascript:window.location='${CONTEXT_PATH}/storeEvaluateInfo/queryByConditionByPage?mobile=${s.mobile}';"/>
										<input type="button" value="绑定小程序" class="btn btn-primary" style="width: 100px;" data-toggle="modal" data-target="#myModal3" onclick="loadProject3(${s.id})"/>
										<button type="button" class="btn btn-primary" onclick="updateAuth(${s.id})">授权小程序</button>
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
					<c:set var="pageUrl" scope="request" value="${CONTEXT_PATH}/storeInfo/queryByPage/-" />    	
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
			<form action="${CONTEXT_PATH}/storeInfo/updateStoreImages" method="post" enctype="multipart/form-data" onsubmit="return check();">
				<div class="modal-body" id="model">
				</div>
				<div class="modal-footer" style="margin-top:20px;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<input type="submit" class="btn btn-primary" value="保存"/>
				</div>
			</form>
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
				<div class="modal-body" id="model1">
				</div>
		</div>
	</div>
</div>
<div class="modal fade bs-example-modal-lg" id="myModal3" role="dialog" aria-label="myModalDialog" aria-hidden="true" style="">
	<div class="modal-dialog modal-lg">
		<div class="modal-content" style="width: 120%;margin-left:-10%;">
			<div class="modal-header">
				<button type="button" data-dismiss="modal" class="close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">添加小程序</h4>
			</div>
			<form action="${CONTEXT_PATH}/storeInfo/submitXCX" method="post" onsubmit="return checkXCX();">
				<div class="modal-body" id="model3">
				</div>
				<div class="modal-footer" style="margin-top:20px;">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<input type="submit" class="btn btn-primary" value="保存"/>
				</div>
			</form>
		</div>
	</div>
</div>
</body>
</html>