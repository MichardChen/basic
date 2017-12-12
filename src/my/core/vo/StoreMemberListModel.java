package my.core.vo;

import java.io.Serializable;

public class StoreMemberListModel implements Serializable{

	private String icon;
	private int sex;
	private int id;
	private String nickName;
	private String mobile;
	private String createTime;
	private String role;
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getIcon() {
		return icon;
	}
	public int getSex() {
		return sex;
	}
	public int getId() {
		return id;
	}
	public String getNickName() {
		return nickName;
	}
	public String getMobile() {
		return mobile;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
}
