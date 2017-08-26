<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<title>添加资讯</title>
<!-- 富文本编辑器 -->
<link href="<%=request.getContextPath()%>/assets/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="<%=request.getContextPath()%>/assets/summernote/summernote/summernote.css" type="text/css" rel="stylesheet" />
<link href="<%=request.getContextPath()%>/assets/summernote/font-awesome/css/font-awesome.min.css" type="text/css" rel="stylesheet" />
<script src="<%=request.getContextPath()%>/assets/summernote/jquery-2.1.1.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/bootstrap.min.js"></script> 
<script src="<%=request.getContextPath()%>/assets/summernote/summernote/summernote.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/summernote/summernote-zh-CN.js"></script>
<script type="text/javascript">
var str='${message}';
if(str!=''){
  alert(str);
}
$('#content').summernote({
	 /* callbacks: { */
        onImageUpload: function(files) {
               // img = sendFile(files[0]);  
            	//上传图片到服务器，使用了formData对象，至于兼容性...据说对低版本IE不太友好
                var formData = new FormData();
                formData.append('file',files[0]);
                var url = "";
                $.ajax({
                  url : 'newsInfo/uploadFile', //后台文件上传接口
                  type : 'POST',
                  data : formData,
                  processData : false,
                  contentType : false,
                  success : function(data) {
                	  var temp = eval(data);
                	   //editor.insertImage($editable, temp.data.imgUrl,"img");
                	 //alert(temp.data.imgUrl);
                	 $('#content').summernote('insertImage',temp.data.imgUrl,'lindkdk');
                  },  
                  error:function(){  
                      alert("上传失败");  
                  }  
                });
        }
/* 	 } */
	});
</script>
<div class="control-group">
	<table class="table table-responsive">
		<tr>
			<td>资讯标题</td>
			<td><input type="text" name="newsTitle" maxlength="30" placeholder="标题最长30个字" style="width: 300px;"/></td>
		</tr>
		<tr>
			<td>资讯类型</td>
			<td>
						<select style="height:30px;width:120px;" name="newsTypeCd">
							<option value="030001">平台通知</option>
							<option value="030002">茶品资讯</option>
							<option value="030003">活动专题</option>
							<option value="030004">普洱课堂</option>
							<option value="030005">媒体报道</option>
						</select>
		</tr>
		<tr>
			<td>资讯封面图片</td>
			<td>
					<input type="file" name="img"/>
			</td>
		</tr>
		<tr>
					<td>资讯内容</td>
					<td>
							<textarea id="content" name="content">
							</textarea>
					</td>
		</tr>
	</table>
</div>
						
					
				
			
