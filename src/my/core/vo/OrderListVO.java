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
	private String type;
	private String wareHouse;
	private String orderNo;
	private String amount;
	private String store;
	private String mobile;
	private String productUrl;
	private String onSale;
	private String haveSale;
	private String cancle;
	private String originStock;
	private String size;
	private String sellMobile;
	private String sellName;
	
	public String getSellMobile() {
		return sellMobile;
	}
	public void setSellMobile(String sellMobile) {
		this.sellMobile = sellMobile;
	}
	public String getSellName() {
		return sellName;
	}
	public void setSellName(String sellName) {
		this.sellName = sellName;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getOriginStock() {
		return originStock;
	}
	public void setOriginStock(String originStock) {
		this.originStock = originStock;
	}
	public String getCancle() {
		return cancle;
	}
	public void setCancle(String cancle) {
		this.cancle = cancle;
	}
	public String getOnSale() {
		return onSale;
	}
	public void setOnSale(String onSale) {
		this.onSale = onSale;
	}
	public String getHaveSale() {
		return haveSale;
	}
	public void setHaveSale(String haveSale) {
		this.haveSale = haveSale;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	public String getStore() {
		return store;
	}
	public String getMobile() {
		return mobile;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getType() {
		return type;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
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
