package my.core.vo;

import java.io.Serializable;

public class OrderListVO implements Serializable{

	private String name;
	private String createTime;
	private String payTime;
	private String buyUser;
	private String saleUser;
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getBuyUser() {
		return buyUser;
	}
	public void setBuyUser(String buyUser) {
		this.buyUser = buyUser;
	}
	public String getSaleUser() {
		return saleUser;
	}
	public void setSaleUser(String saleUser) {
		this.saleUser = saleUser;
	}
}
