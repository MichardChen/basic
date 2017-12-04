/**
 * 登录、注册模块公共js
 */
var localObj = window.location;
var contextPath = localObj.pathname.split("/")[1];
var basePath = localObj.protocol+"//"+localObj.host+"/"+contextPath;
var server_context=basePath;

function loginClick(){
	$("#loginRegisterFlg").val(1);
	$("#toggleLogin").css("color", "#10A1F1");
	$("#toggleRegister").css("color", "gray");
	$("#loginLine").css("background", "#10A1F1");
	$("#registerLine").css("background", "transparent");
	document.getElementById('modelFrame').src = server_context+"/website/login.jsp";
	document.getElementById('btnLogOrReg').value = "登录";
	$(function() {
		$('#homeModal').modal({
			keyboard : true
		});
	});
}
//Show the model and change toggle style for Register
function registerClick() {
	$("#loginRegisterFlg").val(0);
	$("#toggleRegister").css("color", "#10A1F1");
	$("#toggleLogin").css("color", "gray");
	$("#registerLine").css("background", "#10A1F1");
	$("#loginLine").css("background", "transparent");
	document.getElementById('modelFrame').src = server_context+"/website/register.jsp";
	document.getElementById('btnLogOrReg').value = "注册";
	$(function() {
	 	$('#homeModal').modal({
			keyboard : true
		});
	}); 
}

// Change button's text to Submit
function changeTextToSubmit() {
	$("#loginRegisterFlg").val(2);
	document.getElementById('btnLogOrReg').value = "提交";
}
// Change button's text to Login
function changeTextToLogin() {
	$("#loginRegisterFlg").val(1);
	document.getElementById('btnLogOrReg').value = "登录";
}

function isphone2(inputString){
    var partten = /^1[3,5,8]\d{9}$/;
    var fl=false;
    if(partten.test(inputString))
    {
         //alert('是手机号码');
         return true;
    }
    else
    {
         return false;
         //alert('不是手机号码');
    }
}
function checkMobile(){
	//验证手机号码
	var mobile =document.getElementById('txtPhone');
	var mobilemsg = document.getElementById("mobilemsg");
	var br1 = document.getElementById('br1');
	if(!isphone2(mobile.value)){
		  br1.style.display = "none";
		  mobilemsg.style.display="block";
		  return false;
	  }else{
		  br1.style.display = "block";
		  mobilemsg.style.display="none";
		  return true;
	  }
}

function checkFrameMobile(){
	//验证手机号码
	var mobile = document.getElementById('modelFrame').contentWindow.document.getElementById('txtPhone');
	var mobilemsg = document.getElementById('modelFrame').contentWindow.document.getElementById("mobilemsg");
	var br1 = document.getElementById('modelFrame').contentWindow.document.getElementById('br1');
	if(!isphone2(mobile.value)){
		  br1.style.display = "none";
		  mobilemsg.style.display="block";
		  return false;
	  }else{
		  br1.style.display = "block";
		  mobilemsg.style.display="none";
		  return true;
	  }
}

function checkPwd(){
	//验证手机号码
	var pwd =document.getElementById('txtPassword');
	var pwdmsg = document.getElementById("pwdmsg");
	var br2 = document.getElementById('br2');
	if(pwd.value.length < 6 || pwd.value.length > 16){
		  br2.style.display = "none";
		  pwdmsg.style.display="block";
		  return false;
	  }else{
		  br2.style.display = "block";
		  pwdmsg.style.display="none";
		  return true;
	  }
}

function checkFramePwd(){
	//验证手机号码
	var pwd =document.getElementById('modelFrame').contentWindow.document.getElementById('txtPassword');
	var pwdmsg = document.getElementById('modelFrame').contentWindow.document.getElementById("pwdmsg");
	var br2 = document.getElementById('modelFrame').contentWindow.document.getElementById('br2');
	if(pwd.value.length < 6){
		  br2.style.display = "none";
		  pwdmsg.style.display="block";
		  return false;
	  }else{
		  br2.style.display = "block";
		  pwdmsg.style.display="none";
		  return true;
	  }
}

function checkFrameCode(){
	//验证手机号码
	var txtCode =document.getElementById('modelFrame').contentWindow.document.getElementById('txtCode');
	var pwdmsg = document.getElementById('modelFrame').contentWindow.document.getElementById("pwdmsg");
	var br2 = document.getElementById('modelFrame').contentWindow.document.getElementById('br2');
	var pwdLabel =document.getElementById('modelFrame').contentWindow.document.getElementById('pwdLabel');
	if(txtCode.value.length == 0){
		  br2.style.display = "none";
		  pwdmsg.style.display="block";
		  pwdLabel.textContent = "验证码不能为空！";
		  return false;
	  }else{
		  br2.style.display = "block";
		  pwdmsg.style.display="none";
		  return true;
	  }
}

function register(){
	  var mobile =document.getElementById('modelFrame').contentWindow.document.getElementById('txtPhone');
	  var userPwd = document.getElementById('modelFrame').contentWindow.document.getElementById('txtPassword');
	  var flag = $("#loginRegisterFlg").val();
	  var code =  document.getElementById('modelFrame').contentWindow.document.getElementById('txtCode');
	  var label =  document.getElementById('pwdLabel');
	  var chkArgee = document.getElementById('modelFrame').contentWindow.document.getElementById('chkArgee');
	  var topMsg = document.getElementById('topMsg');
	  if(flag == 0){
		  if(!checkFrameMobile()){
				return;
			}
		  if(!checkFramePwd()){
				return;
			}
		  if(!checkFrameCode()){
			  return;
		  }  
		  if(code.value.length == 0){
				var pwdmsg = document.getElementById("pwdmsg");
				var br2 = document.getElementById('br2');
				br2.style.display = "none";
				pwdmsg.style.display="block";
				label.textContent = "验证码不能为空！";
				return;
		  }
		  if(chkArgee.checked == false){
			  topMsg.style.display="block";
			  topMsg.innerText = "您还没有同意协议！";
			  setTimeout(function(){
				  topMsg.style.display="none";
			  }, 2000);
			  return;
		  }
	  }
	  
	  //提交表单
	  if(flag == 0){
		  $.ajax({
				 type: "post",
				 async: false,
				 url: server_context+"/website/nonAuth/register.do",
				 data: "userTypeCd=010001&mobile="+mobile.value+"&userPwd="+userPwd.value+"&checkCode="+code.value,
				 dataType: "json",
				 success: function(data) {
				       if (data != 0) {
				               var temp = eval(data);
				               var retTopMsg = document.getElementById("topMsg");
		            		   retTopMsg.textContent=temp.message;
		            		   retTopMsg.style.display="block";
				               if(temp.code == "5600"){
				            	   setTimeout(function(){
			            				  topMsg.style.display="none";
			            				  window.location.href=server_context+"/website/index.do";
			            			  }, 2000);
				            	   }else{
				            		   setTimeout(function(){
				            				  topMsg.style.display="none";
				            			  }, 2000);
				            	   }
				               }
				       }
				});
	  }else if(flag == 1){
		  //登陆
		  if(mobile.value == ""){
			  var retTopMsg = document.getElementById("topMsg");
			  retTopMsg.textContent="账户不能为空！";
			  retTopMsg.style.display="block";
			  setTimeout(function(){
				  topMsg.style.display="none";
			  }, 2000);
			  return;
		  }
		  if(userPwd.value == ""){
			  var retTopMsg = document.getElementById("topMsg");
			  retTopMsg.textContent="密码不能为空！";
			  retTopMsg.style.display="block";
			  setTimeout(function(){
				  topMsg.style.display="none";
			  }, 2000);
			  return;
		  }
		  if(code.value == ""){
			  var retTopMsg = document.getElementById("topMsg");
			  retTopMsg.textContent="验证码不能为空！";
			  retTopMsg.style.display="block";
			  setTimeout(function(){
				  topMsg.style.display="none";
			  }, 2000);
			  return;
		  }
		  $.ajax({
				 type: "post",
				 async: false,
				 url: server_context+"/website/nonAuth/login.do",
				 data: "userTypeCd=010001&mobile="+mobile.value+"&userPwd="+userPwd.value+"&checkCode="+code.value,
				 dataType: "json",
				 success: function(data) {
				       if (data != 0) {
				               var temp = eval(data);
				               var retTopMsg = document.getElementById("topMsg");
		            		   retTopMsg.textContent=temp.message;
		            		   retTopMsg.style.display="block";
				               if(temp.code == "5600"){
				            	   setTimeout(function(){
			            				  topMsg.style.display="none";
			            				  window.location.href=server_context+"/website/index.do";
			            			  }, 2000);
				            	   }else{
				            		   setTimeout(function(){
				            				  topMsg.style.display="none";
				            			  }, 2000);
				            	   }
				               }
				       }
				});
	  }else{
		  $.ajax({
				 type: "post",
				 async: false,
				 url: server_context+"/rest/nonAuth/saveForgetPwd.do",
				 data: "userTypeCd=010001&mobile="+mobile.value+"&userPwd="+userPwd.value+"&checkCode="+code.value,
				 dataType: "json",
				 success: function(data) {
				       if (data != 0) {
				               var temp = eval(data);
				               var retTopMsg = document.getElementById("topMsg");
		            		   retTopMsg.textContent=temp.message;
		            		   retTopMsg.style.display="block";
				               if(temp.code == "5600"){
				            	   setTimeout(function(){
			            				  topMsg.style.display="none";
			            				  window.location.href=server_context+"/website/index.do";
			            			  }, 2000);
				            	   }else{
				            		   setTimeout(function(){
				            				  topMsg.style.display="none";
				            			  }, 2000);
				            	   }
				               }
				       }
				});
	  }
}