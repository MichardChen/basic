package my.pvcloud.model;

import java.io.Serializable;

public class StoreModel implements Serializable{

	private int id;
	private String title;
	private String address;
	private int flg;
	private String status;
	private String statusCd;
	private String userName;
	private String mobile;
	private String keyCode;
	private int point;
	private int bindFlg;
	
	
	
	
	public int getBindFlg() {
		return bindFlg;
	}
	public void setBindFlg(int bindFlg) {
		this.bindFlg = bindFlg;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
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
	public String getStatusCd() {
		return statusCd;
	}
	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getAddress() {
		return address;
	}
	public int getFlg() {
		return flg;
	}
	public String getStatus() {
		return status;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
