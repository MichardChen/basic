package my.pvcloud.model;

import java.io.Serializable;

public class WarehouseTeaMemberVO implements Serializable{

	private String teaName;
	private String stock;
	private String type;
	private String warehouse;
	private String saleUser;
	private String mobile;
	private String saleUserType;
	private String createTime;
	private String store;
	private String onSale;
	
	public String getOnSale() {
		return onSale;
	}
	public void setOnSale(String onSale) {
		this.onSale = onSale;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public String getTeaName() {
		return teaName;
	}
	public String getStock() {
		return stock;
	}
	public String getType() {
		return type;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public String getSaleUser() {
		return saleUser;
	}
	public String getMobile() {
		return mobile;
	}
	public String getSaleUserType() {
		return saleUserType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setTeaName(String teaName) {
		this.teaName = teaName;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public void setSaleUser(String saleUser) {
		this.saleUser = saleUser;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setSaleUserType(String saleUserType) {
		this.saleUserType = saleUserType;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
