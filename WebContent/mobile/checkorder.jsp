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
		height: 50px;
		border: 1px solid red;
		line-height: 50px;
	}
	
	.word{
		word-wrap:break-word;
	}
   	</style>
    <title>账单详情</title>
</head>
<body style="margin: 0px 0px;">
	<div class="container">
	   <div class="row row-height">
	      <div class="col-xs-3 col-sm-3 row-height">
	         	账单类型
	      </div>
	      <div class="col-xs-9 col-sm-3 word row-height">
	         	买茶
	      </div>
	   </div>
	   <div class="row row-height">
		 	<div class="col-xs-3 col-sm-3 row-height">
		        	账单详情
		      </div>
		      <div class="col-xs-9 col-sm-3 word row-height">
		         	单价：￥1500.00/件 已售2件
		      </div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height">
			         账单日期
			</div>
			<div class="col-xs-9 col-sm-3 word row-height">
			         2017-12-07
			</div>
		</div>
		<div class="row row-height">
			<div class="col-xs-3 col-sm-3 row-height">
			         备注
			</div>
			<div class="col-xs-9 col-sm-3 word row-height">
			         卖茶成功 金鸡报晓普洱茶x2件福建省龙卷风抗裂砂浆斐林试剂风口浪尖
			</div>
		</div>
	</div>
</body>
</html>