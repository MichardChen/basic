package my.pvcloud.vo;

import java.io.Serializable;

public class ServiceFeeListModel implements Serializable{

	private int id;
	private String tea;
	private String quanlity;
	private String price;
	private String fee;
	private String mark;
	private String createTime;
	private String userName;
	private String mobile;
	
	public int getId() {
		return id;
	}
	public String getTea() {
		return tea;
	}
	public String getQuanlity() {
		return quanlity;
	}
	public String getPrice() {
		return price;
	}
	public String getFee() {
		return fee;
	}
	public String getMark() {
		return mark;
	}
	public String getCreateTime() {
		return createTime;
	}
	public String getUserName() {
		return userName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTea(String tea) {
		this.tea = tea;
	}
	public void setQuanlity(String quanlity) {
		this.quanlity = quanlity;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
