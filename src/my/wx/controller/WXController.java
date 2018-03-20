package my.wx.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.huadalink.plugin.shiro.CaptchaUsernamePasswordToken;
import org.huadalink.plugin.shiro.IncorrectCaptchaException;
import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;

import my.core.constants.Constants;
import my.core.model.Log;
import my.core.model.Member;
import my.wx.service.WXService;

@ControllerBind(key = "/wxmrest", path = "/wx")
public class WXController extends Controller{

	WXService service=Enhancer.enhance(WXService.class);
	
	public void index() {
		render("/wx/login.html");
	}
	public void login() {
		String userName = getPara("userName");
		String pword = getPara("password");
		String password=HashKit.md5(pword);
		String captcha = getPara("captcha");
		//登陆验证
		CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(userName, password, captcha);
		
		Subject subject = SecurityUtils.getSubject();
		String code = "500";
		String msg;
		try {
			if (!subject.isAuthenticated()) {
				subject.login(token);
			}
			setSessionAttr("userName", userName);
			code = "200";
			msg = "登录成功";
			Member user=service.queryMember(userName, password);
			setSessionAttr("agentId", user.get("user_id"));
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "登录");
		} catch (IncorrectCaptchaException e) {
			msg = "验证码错误!";
		} catch (UnknownAccountException e) {
			msg = "账号不存在!";
		} catch (IncorrectCredentialsException e) {
			msg = "用户名密码错误!";
		} catch (LockedAccountException e) {
			msg = "账号被锁定!";
		} catch (ExcessiveAttemptsException e) {
			msg = "尝试次数过多 请明天再试!";
		} catch (AuthenticationException e) {
			msg = "对不起 没有权限访问!";
		} catch (Exception e) {
			e.printStackTrace();
			msg = "请重新登录!";
		}
		renderJson("{\"code\":" + code + ",\"msg\":\" " + msg + " \"}");
	}
}
