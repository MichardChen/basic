<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>查看茶叶</title>
<link type="image/x-icon" rel="shortcut icon" href="${CONTEXT_PATH}/assets/img/tjico.ico" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/summernote/css/bootstrap.css">
<link href="<%=request.getContextPath()%>/summernote/dist/summernote.css" rel="stylesheet"/>
<script src="<%=request.getContextPath()%>/summernote/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/summernote/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/summernote/dist/summernote.js"></script>
<script src="<%=request.getContextPath()%>/summernote/dist/lang/summernote-zh-CN.js"></script>    <!-- 中文-->
<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/My97DatePicker/WdatePicker.js"></script>
<style>
        .m {
            width: 800px;
            margin-left: auto;
            margin-right: auto;
        }
</style>
<script>
    var str='${message}';
    if(str!=''){
      alert(str);
    }
    $(function () {
            $('.summernote').summernote({
                height: 400,
                toolbar: [
                          ['style', ['style']],
                          ['fontsize',['fontsize']],
                          ['font', ['bold', 'underline', 'clear']],
                          ['fontname', ['fontname']],
                          ['color', ['color']],
                          ['para', ['ul', 'ol', 'paragraph']],
                          ['table', ['table']],
                          ['insert', ['link', 'picture', 'video']],
                          ['view', ['fullscreen', 'codeview', 'help']]
                        ],
                tabsize: 2,
                lang: 'zh-CN',
                codemirror: {
                    theme: 'monokai'
                },
                focus: true,
                callbacks: {
                    onImageUpload: function (files, editor, $editable) {
                        sendFile(files);
                    }
                }
            });
        });
        // 选择图片时把图片上传到服务器再读取服务器指定的存储位置显示在富文本区域内
        function sendFile(files, editor, $editable) {
            var $files = $(files);
            $files.each(function () {
                var file = this;
                // FormData，新的form表单封装，具体可百度，但其实用法很简单，如下
                var data = new FormData();
                // 将文件加入到file中，后端可获得到参数名为“file”
                data.append("file", file);
                // ajax上传
                $.ajax({
                    async: false, // 设置同步
                    data: data,
                    type: "POST",
                    url: "uploadFile",//图片上传的url（指定action），返回的是图片上传后的路径，http格式
                    cache: false,
                    contentType: false,
                    processData: false,
                    // 成功时调用方法，后端返回json数据
                    success: function (data) {
                    	var temp = eval(data);
                        $('.summernote').summernote('insertImage',temp.data.imgUrl);
                    },
                    // ajax请求失败时处理
                    error: function () {
                        alert("上传失败");
                    }
                });
            });
        }
        
        function check(){
        	var title = $("#title").val();
        	if(title == ""){
        		alert("请输入茶叶名称");
        		return false;
        	}
        	var brand = $("#brand").val();
        	if(brand == ""){
        		alert("请输入茶叶品牌");
        		return false;
        	}
        	var place = $("#place").val();
        	if(place == ""){
        		alert("请输入茶叶产地");
        		return false;
        	}
        	var birthday = $("#birthday").val();
        	if(birthday == ""){
        		alert("请输入生产日期");
        		return false;
        	}
        	var size1 = $("#size1").val();
        	if(size1 == ""){
        		alert("请输入规格");
        		return false;
        	}
        	var size2 = $("#size2").val();
        	if(size2 == ""){
        		alert("请输入规格");
        		return false;
        	}
        	var amount = $("#amount").val();
        	if(amount == ""){
        		alert("请输入出厂总量");
        		return false;
        	}
        	var price = $("#price").val();
        	if(price == ""){
        		alert("请输入发行单价");
        		return false;
        	}
        	var fromtime = $("#fromtime").val();
        	if(fromtime == ""){
        		alert("请输入发行时间");
        		return false;
        	}
        	var warehouse = $("#warehouse").val();
        	if(warehouse == ""){
        		alert("请输入库存");
        		return false;
        	}
        	return true;
        }
        
    </script>
<form action="${CONTEXT_PATH}/teaInfo/updateTea" method="post" enctype="multipart/form-data" onsubmit="return check();">
<div class="m">
	<table class="table table-responsive">
		<tr>
			<td>茶叶名称</td>
			<td>${teaInfo.tea_title}<%-- <input type="text" name="title" id="title" maxlength="30" value="${teaInfo.tea_title}" style="width: 300px;"/> --%></td>
		</tr>
		<tr>
			<td>茶叶品牌</td>
			<td>${teaInfo.brand}<%-- <input type="text" name="brand" id="brand" maxlength="30" value="${teaInfo.brand}" style="width: 300px;"/> --%></td>
		</tr>
		<tr>
			<td>茶叶产地</td>
			<td>${teaInfo.product_place}<%-- <input type="text" name="place" id="place" value="${teaInfo.product_place}" maxlength="30" style="width: 300px;"/> --%></td>
		</tr>
		<tr>
			<td>生产日期</td>
			<td>${teaInfo.product_date}<%-- <input type="text" name="birthday" id="birthday" value="${teaInfo.product_date}" style="width: 120px;" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/> --%></td>
		</tr>
		<tr>
			<td>规格</td>
			<td><%-- <input type="number" name="size1" id="size1" maxlength="30" style="width: 100px;" value="${teaInfo.weight}"/> --%>${teaInfo.weight}&nbsp;(克/片)&nbsp;&nbsp;
			<%-- <input type="number" name="size2" id="size2" maxlength="30" style="width: 100px;" value="${teaInfo.size}"/> --%>${teaInfo.size}&nbsp;(片/件)</td>
		</tr>
		<tr>
			<td>出厂总量</td>
			<td><%-- <input type="number" name="amount" id="amount" maxlength="30" style="width: 100px;" value="${teaInfo.total_output}"/> --%>${teaInfo.total_output}&nbsp;(饼)</td>
		</tr>
		<tr>
			<td>茶叶类型</td>
			<td>
						<select style="height:30px;width:120px;" name="typeCd" id="typeCd">
							<option value="050001" <c:if test="${teaInfo.type_cd=='050001'}">selected="selected"</c:if>>普洱</option>
							<option value="050002" <c:if test="${teaInfo.type_cd=='050002'}">selected="selected"</c:if>>铁观音</option>
						</select>
			</td>
		</tr>
		<tr>
			<td>仓库</td>
			<td>${warehouse}</td>
		</tr>
		<tr>
			<td>库存</td>
			<td>${stock}&nbsp;(件)</td>
		</tr>
		<tr>
			<td>茶叶状态</td>
			<td>
						<select style="height:30px;width:120px;" name="status" id="status">
							 	<option value="090001" <c:if test="${teaInfo.status=='090001'}">selected="selected"</c:if>>待售</option>
								<option value="090002" <c:if test="${teaInfo.status=='090002'}">selected="selected"</c:if>>发行中</option>
								<option value="090003" <c:if test="${teaInfo.status=='090003'}">selected="selected"</c:if>>已结束</option>
						</select>
			</td>
		</tr>
		<tr>
			<td>官方茶叶正品保障</td>
			<td>
						<select style="height:30px;width:120px;" name="certificate" id="certificate">
							<option value="1" <c:if test="${teaInfo.certificate_flg==1}">selected="selected"</c:if>>是</option>
							<option value="0" <c:if test="${teaInfo.certificate_flg==0}">selected="selected"</c:if>>否</option>
						</select>
			</td>
		</tr>
		<tr>
			<td>发行单价</td>
			<td>
				<%-- <input type="number" name="price" id="price" maxlength="30" style="width: 100px;" value="${teaInfo.tea_price}"/> --%>${teaInfo.tea_price}&nbsp;(元/件)
			</td>
		</tr>
		<tr>
			<td>参考价</td>
			<td>
				<input type="number" name="referencePrice" id="referencePrice" maxlength="30" style="width: 100px;" value="${teaPrice.referencePrice}"/>&nbsp;(元/件)
			</td>
		</tr>
		<tr>
			<td>参考单价区间</td>
			<td>
				<input type="number" name="fromPrice" id="fromPrice" maxlength="30" style="width: 100px;" value="${teaPrice.fromPrice}"/>&nbsp;-
				<input type="number" name="toPrice" id="toPrice" maxlength="30" style="width: 100px;" value="${teaPrice.toPrice}"/>&nbsp;(元/件)
			</td>
		</tr>
		<tr>
			<td>有效截止日期</td>
			<td><input type="text" name="expireDate" id="expireDate" style="width: 120px;" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})" value="${teaPrice.date}"/></td>
		</tr>
		<tr>
			<td>发行时间</td>
			<td>
				<input type="text" value="${teaInfo.sale_from_date}" name="fromtime" id="fromtime" maxlength="30" style="width: 120px;" class="Wdate"  onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})"/>&nbsp;-&nbsp;<input type="text" value="${teaInfo.sale_to_date}" name="totime" maxlength="30" class="Wdate"  onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true})" style="width: 120px;"/>
			</td>
		</tr>
		<!-- <tr>
			<td style="color: red;font-weight: bold;">是否更新图片</td>
			<td>
						<select style="height:30px;width:120px;" name="reset">
							<option value="0">否</option>
							<option value="1">是</option>
						</select>
			</td>
		</tr> -->
		<tr>
			<td>茶叶图片</td>
			<td>
			</td>
		</tr>
		<c:forEach var="s" items="${list}">
			<tr>
				<td><img src="${s}" width="50%"/></td>
				<td>
				</td>
			</tr>
		</c:forEach>
	<!-- 	<tr>
			<td>茶叶图片2</td>
			<td>
					<input type="file" name="coverImg2"/>
			</td>
		</tr>
		<tr>
			<td>茶叶图片3</td>
			<td>
					<input type="file" name="coverImg3"/>
			</td>
		</tr>
		<tr>
			<td>茶叶图片4</td>
			<td>
					<input type="file" name="coverImg4"/>
			</td>
		</tr> -->
		<tr>
					<td>茶叶详情</td>
					<td>
					</td>
		</tr>
	</table>
	<input type="hidden" name="id" value="${teaInfo.id}"/>
    <div>
    		<textarea id="content" name="content" class="summernote">
    			${teaInfo.tea_desc}
			</textarea>
    </div>
</div>
<div class="modal-footer" style="margin-top:20px;text-align: center;">
					<!-- <input type="submit" class="btn btn-success" value="保存"/> -->
				</div>
			</form>
