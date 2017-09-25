package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class SaleOrderListVO implements Serializable{

	private String orderNo;
	private String img;
	private String name;
	private BigDecimal price;
	private int quality;
	private BigDecimal amount;
	private String size;
	public String getOrderNo() {
		return orderNo;
	}
	public String getImg() {
		return img;
	}
	public String getName() {
		return name;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public int getQuality() {
		return quality;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public String getSize() {
		return size;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
}
