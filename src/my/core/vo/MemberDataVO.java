package my.core.vo;

import java.io.Serializable;

public class MemberDataVO implements Serializable{

	private String icon;
	private String mobile;
	private String wxNo;
	private String qqNo;
	private String nickName;
	private int storeFlg;
	private int sex;
	
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getStoreFlg() {
		return storeFlg;
	}
	public void setStoreFlg(int storeFlg) {
		this.storeFlg = storeFlg;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getWxNo() {
		return wxNo;
	}
	public void setWxNo(String wxNo) {
		this.wxNo = wxNo;
	}
	public String getQqNo() {
		return qqNo;
	}
	public void setQqNo(String qqNo) {
		this.qqNo = qqNo;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	
}
