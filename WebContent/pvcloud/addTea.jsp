<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>添加茶叶</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/summernote/css/bootstrap.css">
<link href="<%=request.getContextPath()%>/summernote/dist/summernote.css" rel="stylesheet"/>
<script src="<%=request.getContextPath()%>/summernote/js/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/summernote/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/summernote/dist/summernote.js"></script>
<script src="<%=request.getContextPath()%>/summernote/dist/lang/summernote-zh-CN.js"></script>    <!-- 中文-->
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
                height: 200,
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
                    url: "teaInfo/uploadFile",//图片上传的url（指定action），返回的是图片上传后的路径，http格式
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
    </script>
<div class="m">
	<table class="table table-responsive">
		<tr>
			<td>茶叶标题</td>
			<td><input type="text" name="title" maxlength="30" placeholder="标题最长30个字" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>茶叶类型</td>
			<td>
						<select style="height:30px;width:120px;" name="typeCd" id="typeCd">
							<option value="050001">普洱</option>
							<option value="050002">铁观音</option>
						</select>
		</tr>
		<tr>
			<td>价格</td>
			<td>
						<input type="number" name="price" maxlength="30" placeholder="请输入价格" style="width: 300px;"/>
		</tr>
		<tr>
			<td>茶叶封面图片</td>
			<td>
					<input type="file" name="coverImg"/>
			</td>
		</tr>
		<tr>
					<td>茶叶详情</td>
					<td>
					</td>
		</tr>
	</table>
    <div>
    		<textarea id="content" name="content" class="summernote">
			</textarea>
    </div>
</div>

