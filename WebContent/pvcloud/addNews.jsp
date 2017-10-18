<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<meta charset="utf-8">
<title>添加资讯</title>
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
    </script>
<form action="${CONTEXT_PATH}/newsInfo/saveNews" method="post" enctype="multipart/form-data">
<div class="m">
	<table class="table table-responsive">
		<tr>
			<td>资讯标题</td>
			<td><input type="text" name="newsTitle" maxlength="30" placeholder="标题最长30个字" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>资讯类型</td>
			<td>
						<select style="height:30px;width:120px;" name="newsTypeCd" id="newsTypeCd">
							<option value="030001">平台通知</option>
							<option value="030002">茶品资讯</option>
							<option value="030003">活动专题</option>
							<option value="030004">普洱课堂</option>
							<option value="030005">媒体报道</option>
						</select>
		</tr>
		<tr>
			<td>是否热门</td>
			<td>
						<select style="height:30px;width:120px;" name="hot" id="hot">
							<option value="1">是</option>
							<option value="0">否</option>
						</select>
		</tr>
		<tr>
			<td>资讯封面图片</td>
			<td>
					<input type="file" name="newImg"/>
			</td>
		</tr>
		<tr>
					<td>资讯内容</td>
					<td>
					</td>
		</tr>
	</table>
    <div>
    		<textarea id="content" name="content" class="summernote">
			</textarea>
    </div>
</div>
<div class="modal-footer" style="margin-top:20px;text-align: center;">
					<input type="submit" class="btn btn-success" value="保存"/>
				</div>
</form>
