package my.core.controller;

import my.core.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.huadalink.route.ControllerBind;
import org.huadalink.util.ToolKit;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

@ControllerBind(key = "/reg", path = "/platform")
public class RegisterController extends Controller {

	UserService service = Enhancer.enhance(UserService.class);

	public void index() {
		render("register.jsp");
	}

	/**
	 * 注册
	 */
	public void save() {
		String userName = getPara("userName");
		String password = getPara("password");
		String captcha = getPara("captcha");
		String code = "500";
		String msg;
		if (!ToolKit.validateCaptcha((String) getSessionAttr("captcha"), captcha)) {
			msg = "验证码错误";
		} else {
			msg = service.regUser(userName, password);
			if (StringUtils.isBlank(msg)) {
				code = "200";
				msg = "注册成功";
			}
		}
		System.out.println("code:"+code+"msg:"+msg);
		renderJson("{\"code\":" + code + ",\"msg\":\" " + msg + " \"}");
	}
}
