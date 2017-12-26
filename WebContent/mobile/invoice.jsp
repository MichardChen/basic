<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <meta content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport" /> 
	<link rel="stylesheet" href="${CONTEXT_PATH}/assets/bootstrap/css/bootstrap.css" type="text/css" media="screen" />
	<script src="${CONTEXT_PATH}/assets/lib/jquery-2.1.1.min.js"></script>
   	<style>
   	.container {
	    margin-right: 0px;
	    margin-left: 0px;
	    }
	.row-height{
		height: 40px;
	}
	.content{
		color: #FF5400;
	}
	.title{
		color: #333333;
	}
	.word{
		word-wrap:break-word;
	}
   	</style>
    <title>发票详情</title>
</head>
<body style="margin: 0px 0px;">
	<div class="container" style="background-color: white;">
	   <div class="row row-height" style="margin-top: 20px;">
	      <div class="col-xs-3 col-sm-3 row-height color">
	         	申请人电话
	      </div>
	      <div class="col-xs-9 col-sm-3 word row-height content">
	         	${model.mobile}
	      </div>
	   </div>
	   <div class="row row-height">
		 	<div class="col-xs-3 col-sm-3 row-height color">
		        	发票类型
		      </div>
		      <div class="col-xs-9 col-sm-3 word row-height content">
		         	<c:if test="${model.invoice_type_cd=='320001'}">电子发票</c:if>
	    			<c:if test="${model.invoice_type_cd=='320002'}">纸质发票</c:if>
		      </div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			       抬头类型
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			       <c:if test="${model.title_type_cd=='330001'}">企业</c:if>
	    			<c:if test="${model.title_type_cd=='330002'}">个人/非企业单位</c:if>
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        发票抬头
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.title}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			       申请状态
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			       <c:if test="${model.status=='340001'}">待处理</c:if>
	    		   <c:if test="${model.status=='340003'}">未开票</c:if>
	    		   <c:if test="${model.status=='340004'}">已开票</c:if>
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        发票号
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.invoice_no}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			       税务单号
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.tax_no}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        发票内容
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.content}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        发票金额
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.moneys}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        备注
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.mark}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        申请时间
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.create_time}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        开户行
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.bank}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        账户
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.account}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        邮箱
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.mail}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        邮寄信息
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${address}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        快递名称
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
					${model.express_company}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			        快递单号
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.express_no}
			</div>
		</div>
	</div>
</body>
</html>