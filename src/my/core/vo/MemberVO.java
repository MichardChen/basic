package my.core.vo;

import java.io.Serializable;

public class MemberVO implements Serializable{

	private int id;
	private String name;
	private String mobile;
	private String createTime;
	private String moneys;
	private String sex;
	private String userName;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getMobile() {
		return mobile;
	}
	public String getCreateTime() {
		return createTime;
	}
	public String getMoneys() {
		return moneys;
	}
	public String getSex() {
		return sex;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setMoneys(String moneys) {
		this.moneys = moneys;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	
}
