package my.core.vo;

import java.io.Serializable;

public class AdminInvoiceListModel implements Serializable{

	private int id;
	private String userName;
	private String userMobile;
	private String createTime;
	private String moneys;
	private String status;
	private String title;
	
	private String type;
	private String mark;
	private String taxNo;
	
	
	public String getType() {
		return type;
	}
	public String getMark() {
		return mark;
	}
	public String getTaxNo() {
		return taxNo;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}
	public int getId() {
		return id;
	}
	public String getUserName() {
		return userName;
	}
	public String getUserMobile() {
		return userMobile;
	}
	public String getCreateTime() {
		return createTime;
	}
	public String getMoneys() {
		return moneys;
	}
	public String getStatus() {
		return status;
	}
	public String getTitle() {
		return title;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setMoneys(String moneys) {
		this.moneys = moneys;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
