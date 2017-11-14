<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>用户管理</title>
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
	window.open("${CONTEXT_PATH}/newsInfo/addNews");
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
		<div class="wrapper wrapper-content animated" style="text-align: center;">
    	<div class="" style="width:100%;color:black;font-size:15px;height:40px;line-height:40px;background: #87CEFA;text-align: center;">
	  <%--   	<div class="fl"><img src="${CONTEXT_PATH }/image/picturesfolder.ico" style="width:50px; height:50px;"/></div> --%>
	   		<div style="font-size: 30px;color: white;font-weight: bold;">资讯信息</div>
	   </div>
    	<hr/>	
    
	<div class="span" style="width:100%;color:black;font-size:12px;border:2px solid #dadada;">
   		<div class="" style="margin-top:15px;margin-bottom:15px;">
    		<form method="post" action="${CONTEXT_PATH}/newsInfo/queryByCondition" class="form-horizontal">
    			<div style="" class="form-group">
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">资讯标题</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="title" value="${title}"/>
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">资讯类型</label>
	    			<div class="col-sm-1 col-xs-1 col-md-1">	
	    				<select name="type" style="height: 30px;">
		    					<option></option>
		    					<option value="030001" <c:if test="${type=='030001'}">selected="selected"</c:if>>平台通知</option>
		    					<option value="030002" <c:if test="${type=='030002'}">selected="selected"</c:if>>茶品资讯</option>
		    					<option value="030003" <c:if test="${type=='030003'}">selected="selected"</c:if>>活动专题</option>
		    					<option value="030004" <c:if test="${type=='030004'}">selected="selected"</c:if>>普洱课堂</option>
		    					<option value="030005" <c:if test="${type=='030005'}">selected="selected"</c:if>>媒体报道</option>
		    			</select>	
    				</div>
    				</div>
    				<div style="" class="form-group">
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">是否热门</label>
	    			<div class="col-sm-1 col-xs-1 col-md-1">	
	    				<select name="hot" style="height: 30px;">
		    					<option></option>
		    					<option value="1" <c:if test="${hot=='1'}">selected="selected"</c:if>>是</option>
		    					<option value="0" <c:if test="${hot=='0'}">selected="selected"</c:if>>否</option>
		    			</select>	
    				</div>
    				<label class="col-sm-1 col-xs-1 col-md-1 control-label">创建期间</label>
	    			<div class="col-sm-2 col-xs-2 col-md-2">	
	    				<input type="text" class="form-control" name="createTime1" placeholder="请选择起始时间" value="${createTime1}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/>
    				</div>
    				<div class="col-sm-2 col-xs-2 col-md-2">	
    					   <input type="text" class="form-control" name="createTime2" placeholder="请选择结束时间" value="${createTime2}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/>
    				</div>
    			<div style="" class="col-sm-1 col-xs-1 col-md-1"><input type="submit" class="ys2" value=""/></div>
   			   <div style="display:inline-block;float:right;margin-right:5%;"><input type="button" value="新增" class="ys3" onclick="loadProject(0)"/></div>
			   </div>
    		</form>
   		</div>
	</div>
    <div class="container equip" style="width:100%;font-size:12px;border:1px solid #dadada;margin-top:15px;height:690px;position:relative;color:black;margin-left:0px;">
    	<div class="row">
    		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 table-responsive" style="padding-left:0px;padding-right:0px;font-size:14px;height:705px;">
    		<table class="table table-responsive" id="myTb">
    		<thead>
    			<tr>
    				<th>序列号</th>
    				<th>资讯标题</th>
    				<th>资讯类型</th>
    				<th>创建者</th>
    				<th>最近一次更新者</th>
    				<th>是否热门</th>
    				<th>状态</th>
    				<th>创建时间</th>
    				<th>更新时间</th>
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
		    			<c:forEach var="s" items="${sList}" varStatus="status">	
		    				<tr class="bOrder">
		    					<td>${list.pageSize*(list.pageNumber-1)+status.index+1}</td>
		    					<td style="border: 1px solid red;width: 20px;">${s.title }</td>
		    					<td>${s.type }</td>
		    					<td>${s.createUser}</td>
		    					<td>${s.updateUser}</td>
		    					<td>
		    						<c:if test="${s.hotFlg==1 }">是</c:if>
		    						<c:if test="${s.hotFlg==0 }">否</c:if>
		    					</td>
		    					<td>${s.status }</td>
		    					<td>${s.createTime}</td>
		    						<td>${s.updateTime}</td>
		    					<td>
		    						<%-- <input type="button" value="推送" class="ys3" data-toggle="modal" data-target="#myModal" onclick="if(confirm('确认要发布这条资讯?')){window.location='${CONTEXT_PATH}/newsInfo/push?newsId=${s.id}';}"/> --%>
		    						<c:if test="${s.flg ==1}">
			    							<input type="button" value="删除" class="ys3" onclick="if(confirm('确认要删除数据?')){window.location='${CONTEXT_PATH}/newsInfo/del?newsId=${s.id}';}"/>
			    						<c:if test="${s.topFlg ==0}">
			    							<input type="button" value="置顶" class="ys3" onclick="if(confirm('确认要置顶?')){window.location='${CONTEXT_PATH}/newsInfo/saveTop?top=1&newsId=${s.id}';}"/>
			    						</c:if>
			    						<c:if test="${s.topFlg !=1}">
			    							<input type="button" value="取消置顶" class="ys3" onclick="if(confirm('确认要取消置顶?')){window.location='${CONTEXT_PATH}/newsInfo/saveTop?top=0&newsId=${s.id}';}"/>
			    						</c:if>
		    						</c:if>
		    						<a href="${s.url}" target="_blank"><input type="button" value="查看" class="ys3"/></a>
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
					<c:set var="pageUrl" scope="request" value="${CONTEXT_PATH}/newsInfo/queryByPage/-" />    	
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