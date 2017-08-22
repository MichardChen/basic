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
		renderJson(rt);
	}
	
	//登录
	public void loginWeb() throws Exception{
		
		ReturnData data = new ReturnData();
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.login(dto.getMobile()
								,dto.getUserPwd()
								,dto.getUserTypeCd()
								,dto.getPlatForm()
								,dto.getAccessToken()));
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
	@Before(RequestInterceptor.class)
	public void modifyPwd() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.modifyUserPwd(dto));
	}
	
	//退出
	@Before(RequestInterceptor.class)
	public void logout() throws Exception{
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(service.logout(dto));
	}
}
