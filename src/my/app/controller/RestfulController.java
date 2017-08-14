package my.app.controller;

import java.util.ArrayList;
import java.util.List;

import javax.el.ArrayELResolver;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.huadalink.plugin.shiro.CaptchaUsernamePasswordToken;
import org.huadalink.route.ControllerBind;
import org.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

import my.app.service.LoginService;
import my.app.service.RestService;
import my.core.constants.Constants;
import my.core.interceptor.RequestInterceptor;
import my.core.model.Member;
import my.core.model.ReturnData;
import my.core.model.User;
import my.pvcloud.dto.IndexDTO;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.MD5Util;
import my.pvcloud.util.StringUtil;
import my.pvcloud.util.VertifyUtil;

@ControllerBind(key = "/rest", path = "/rest")
public class RestfulController extends Controller{

	LoginService service = Enhancer.enhance(LoginService.class);
    RestService restService = Enhancer.enhance(RestService.class);
    
    @Before(RequestInterceptor.class)
    public void login(){
    	ReturnData data = new ReturnData();
    	data.setCode(Constants.STATUS_CODE.SUCCESS);
    	data.setMessage("查询成功");
    	List<String> d = new ArrayList<String>();
    	d.add("1");
    	d.add("2");
    	d.add("3");
    	data.setData(d);
		renderJson(data);
	}
	
	//获取验证码
	public void getCheckCode() throws Exception{
		LoginDTO dto =  LoginDTO.getInstance(getRequest());
		String code = VertifyUtil.getVertifyCode();
		dto.setCode(code);
		renderJson(service.getCheckCode(dto));
	}
	
	//注册
	public void register() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		ReturnData rt = service.register(dto);
		if(StringUtil.equals(rt.getCode(), Constants.STATUS_CODE.SUCCESS)){
			JSONObject data=(JSONObject)rt.getData();
			setSessionAttr("userId", data.get("userId"));
			setSessionAttr("userTypeCd", Constants.USER_TYPE_CD.CLIENT);
			setSessionAttr("userName", dto.getMobile());
		}
		renderJson(rt);
	}
	
	//登录
	public void loginWeb() throws Exception{
		
		ReturnData data = new ReturnData();
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		//removeSessionAttr("store");
		//登陆验证
		CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(dto.getUserName(),MD5Util.string2MD5(dto.getUserPwd()),dto.getUserTypeCd());
		Subject subject = SecurityUtils.getSubject();
		String code = "5600";
		String msg = null;
		try {
			if (!subject.isAuthenticated()){
				subject.login(token);
			}
			setSessionAttr("userName", dto.getUserName());
			msg = "登录成功";
			data.setMessage("登录成功");
			data.setCode(Constants.STATUS_CODE.SUCCESS);
			IndexDTO indexDTO = IndexDTO.getInstance(getRequest());
			if(StringUtil.equals(dto.getUserTypeCd(), Constants.USER_TYPE_CD.CLIENT)){
				Member member=service.queryMember(dto.getUserName(), MD5Util.string2MD5(dto.getUserPwd()));
				setSessionAttr("userId", member.getInt("id"));
				indexDTO.setUserId(member.getInt("id"));
				setSessionAttr("mobile",member.getStr("mobile"));
				//保存账号密码
				setCookie("clientName", dto.getUserName(), 1000*60*60*24*90);
				setCookie("clientPwd", dto.getUserPwd(), 1000*60*60*24*90);
			}else{
				User admin = User.dao.queryByUserName(dto.getUserName(), MD5Util.string2MD5(dto.getUserPwd()));
				setSessionAttr("userId", admin.getInt("user_id"));
				setSessionAttr("mobile", admin.getStr("mobile"));
			}
			
			setSessionAttr("userName", dto.getUserName());
			setSessionAttr("userTypeCd", dto.getUserTypeCd());
			setSessionAttr("store", dto.getStore());
		} catch (UnknownAccountException e) {
			msg = "账号不存在";
			data.setCode(Constants.STATUS_CODE.FAIL);
		} catch (IncorrectCredentialsException e) {
			msg = "用户名密码错误";
			data.setCode(Constants.STATUS_CODE.FAIL);
		} catch (LockedAccountException e) {
			msg = "账号被锁定";
			data.setCode(Constants.STATUS_CODE.FAIL);
		} catch (ExcessiveAttemptsException e) {
			msg = "尝试次数过多 请明天再试";
			data.setCode(Constants.STATUS_CODE.FAIL);
		} catch (AuthenticationException e) {
			msg = "对不起 没有权限访问";
			data.setCode(Constants.STATUS_CODE.FAIL);
		} catch (Exception e) {
			e.printStackTrace();
			data.setCode(Constants.STATUS_CODE.FAIL);
			msg = "请重新登录";
		}
		data.setMessage(msg);
		renderJson(data);
	}
	
	//获取忘记密码验证码
	public void getForgetCheckCode() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.getForgetCheckCode(dto));
	}
	
	//保存忘记密码
	public void saveForgetPwd() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.saveForgetPwd(dto));
	}
	
	//客户修改密码
	public void modifyPwd() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		dto.setMobile((String)getSessionAttr("mobile"));
		renderJson(service.modifyUserPwd(dto));
	}
	
	//退出
	public void logout() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.logout(dto));
	}
}
