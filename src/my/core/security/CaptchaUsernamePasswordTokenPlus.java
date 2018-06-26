package my.core.security;

import org.huadalink.plugin.shiro.CaptchaUsernamePasswordToken;

public class CaptchaUsernamePasswordTokenPlus extends CaptchaUsernamePasswordToken{

	private String userTypeCd;
	public CaptchaUsernamePasswordTokenPlus(String username
										   ,String password
										   ,String captcha
										   ,String userTypeCd) {
		super(username, password,captcha);
		this.userTypeCd = userTypeCd;
	}
	
	public String getUserTypeCd() {
		return userTypeCd;
	}
	public void setUserTypeCd(String userTypeCd) {
		this.userTypeCd = userTypeCd;
	}
}
