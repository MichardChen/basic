package my.pvcloud.model;

import java.io.Serializable;

public class GetTeaRecordListModel implements Serializable{

	private String userName;
	private String mobile;
	private String tea;
	private String quality;
	private String address;
	private String createTime;
	private int id;
	private String status;
	private String express;
	private String mark;
	
	
	public String getExpress() {
		return express;
	}
	public String getMark() {
		return mark;
	}
	public void setExpress(String express) {
		this.express = express;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTea() {
		return tea;
	}
	public void setTea(String tea) {
		this.tea = tea;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
}