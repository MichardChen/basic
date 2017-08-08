package my.core.security;

import my.core.model.User;
import my.core.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.huadalink.plugin.shiro.CaptchaUsernamePasswordToken;
import org.huadalink.plugin.shiro.IncorrectCaptchaException;
import org.huadalink.util.ToolKit;

import com.jfinal.aop.Enhancer;

public class ShiroDbRealm extends AuthorizingRealm {

	UserService service = Enhancer.enhance(UserService.class);

	// 授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		return null;
	}

	// 认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		CaptchaUsernamePasswordToken authToken = (CaptchaUsernamePasswordToken) token;
		String md5Captcha = (String)SecurityUtils.getSubject().getSession().getAttribute("captcha");
		if(authToken.getCaptcha()==null || !ToolKit.validateCaptcha(md5Captcha, authToken.getCaptcha())){
			throw new IncorrectCaptchaException("验证码错误");
		}
		String userName = authToken.getUsername();
		User user = service.getUserByUserName(userName);
		if(user != null){
		AuthenticationInfo authinfo = new SimpleAuthenticationInfo(user.getStr("username"), user.getStr("password"),
				getName());
		return authinfo;
		}else{
			return null;
		}
	}
}