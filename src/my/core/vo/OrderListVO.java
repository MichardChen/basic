package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderListVO implements Serializable{

	private String name;
	private String createTime;
	private String payTime;
	private String buyUser;
	private String saleUser;
	private int id;
	private String stock;
	private String status;
	private BigDecimal price;
	
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
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
