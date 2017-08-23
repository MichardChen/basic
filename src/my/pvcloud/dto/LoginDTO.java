package my.pvcloud.dto;

import javax.servlet.http.HttpServletRequest;

import my.pvcloud.util.StringUtil;

public class LoginDTO extends BaseDTO{

	public LoginDTO(){}
	
	public static LoginDTO getInstance(HttpServletRequest request){
		LoginDTO dto = new LoginDTO();
		dto.setMobile(request.getParameter("mobile"));
		dto.setUserTypeCd(request.getParameter("userTypeCd"));
		dto.setUserPwd(request.getParameter("userPwd"));
		dto.setUserId(StringUtil.toInteger(request.getParameter("userId")));
		dto.setToken(request.getParameter("token"));
		dto.setAccessToken(request.getParameter("accessToken"));
		dto.setCode(request.getParameter("code"));
		dto.setOldPwd(request.getParameter("oldPwd"));
		dto.setNewPwd(request.getParameter("newPwd"));
		dto.setUserName(request.getParameter("userName"));
		dto.setUserPwd(request.getParameter("userPwd"));
		dto.setRememberMe(StringUtil.toInteger(request.getParameter("rememberMe")));
		dto.setNickName(request.getParameter("nickName"));
		dto.setSex(StringUtil.toInteger(request.getParameter("sex")));
		dto.setStore(StringUtil.toInteger(request.getParameter("store")));
		dto.setPageNum(StringUtil.toInteger(request.getParameter("pageNum")));
		dto.setPageSize(StringUtil.toInteger(request.getParameter("pageSize")));
		dto.setNewsId(StringUtil.toInteger(request.getParameter("newsId")));
		return dto;
	}
	
	private int sex;
	private String userName;
	private String userPwd;
	private int rememberMe;
	private String code;
	private String oldPwd;
	private String newPwd;
	private String nickName;
	private int adminId;
	private int store;
	private int newsId;
	
	public int getNewsId() {
		return newsId;
	}

	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}

	public int getStore() {
		return store;
	}

	public void setStore(int store) {
		this.store = store;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public int getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(int rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
