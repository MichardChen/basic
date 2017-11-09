package my.core.vo;

import java.io.Serializable;

public class MessageListDetailVO implements Serializable{
	
	private String title;
	private String price;
	private String quality;
	private String bargainAmount;
	private String payAmount;
	private String createTime;
	private String payTime;
	public String getTitle() {
		return title;
	}
	public String getPrice() {
		return price;
	}
	public String getQuality() {
		return quality;
	}
	public String getBargainAmount() {
		return bargainAmount;
	}
	public String getPayAmount() {
		return payAmount;
	}
	public String getCreateTime() {
		return createTime;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public void setBargainAmount(String bargainAmount) {
		this.bargainAmount = bargainAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
}
