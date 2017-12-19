<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
    <title>账单详情</title>
</head>
<body style="margin: 0px 0px;">
	<div class="container" style="background-color: white;">
	   <div class="row row-height" style="margin-top: 20px;">
	      <div class="col-xs-3 col-sm-3 row-height color">
	         	账单类型
	      </div>
	      <div class="col-xs-9 col-sm-3 word row-height content">
	         	${model.type}
	      </div>
	   </div>
	   <div class="row row-height">
		 	<div class="col-xs-3 col-sm-3 row-height color">
		        	账单详情
		      </div>
		      <div class="col-xs-9 col-sm-3 word row-height content">
		         	${model.moneys}
		      </div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			         账单日期
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			       ${model.date}
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height color">
			         备注
			</div>
			<div class="col-xs-9 col-sm-3 word row-height content">
			        ${model.content}
			</div>
		</div>
	</div>
</body>
</html>