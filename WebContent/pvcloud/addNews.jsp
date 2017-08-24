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
    height: 100, 
    toolbar: [  
              ['style', ['style']],
              ['style', ['bold', 'italic', 'underline', 'clear']],  
              ['fontsize', ['fontsize']],  
              ['fontname',['fontname']],
              ['color', ['color']],  
              ['para', ['ul', 'ol', 'paragraph']],  
              ['height', ['height']],  
              ['table',['table']],
              ['insert', ['picture','link']] ,
              ['help',['help']]
            ], 
    onImageUpload: function(files, editor, $editable) {
         sendFile(files,editor,$editable);
        },
    onblur: function(e) {
        //$('.summernote').html("");
        //$('#content').html($(this).code());
        validateContent();
    },
    onfocus:function(e){
        validateContent();
    },
    onChange: function(contents, $editable) {
        validateContent();
    },
    bFullscreen:false,

});
function sendFile(file, editor, $editable) {
    for(var i = 0;i < file.length;i++){
        data = new FormData();
        data.append("file", file[i]);
        url = "newsInfo/uploadFile";
        $.ajax({
            data: data,
            type: "POST",
            url: url,
            cache: false,
            contentType: false,
            processData: false,
            success: function (msg) {
                if(1 == msg.code){
                    editor.insertImage($editable, msg.data.imgUrl);
                }
                else{
                    bootbox.alert(msg.message);
                }
            }
        });
    }

            };
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
						
					
				
			
