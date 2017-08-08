package my.core.controller;

import my.core.model.User;
import my.core.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.Cookie;
import org.huadalink.plugin.shiro.CaptchaUsernamePasswordToken;
import org.huadalink.plugin.shiro.IncorrectCaptchaException;
import org.huadalink.render.kaptcha.KaptchaRender;
import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;

@ControllerBind(key = "/login", path = "/platform")
public class LoginController extends Controller {

	UserService service=Enhancer.enhance(UserService.class);
	public void index() {
		render("login.jsp");
	}

	public void captcha() {
		render(new KaptchaRender());
	}

	/**
	 * 登录
	 */
	public void checkin() {
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
			User user=service.queryByUserName(userName, password);
			setSessionAttr("agentId", user.get("agent_id"));
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

	/**
	 * 返回登录界面
	 */
	public void checkout() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			subject.logout();
		}
		redirect("/login");
	}
	private String getCookies(Cookie[] cookies) {
	    StringBuilder sb = new StringBuilder();
	    for(Cookie cookie : cookies) {
	        sb.append(cookie.getName());
	        sb.append("=");
	        sb.append(cookie.getValue());
	        sb.append(";");
	    }
	    return sb.toString();
	}
}
