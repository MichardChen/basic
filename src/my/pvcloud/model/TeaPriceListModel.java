package my.pvcloud.model;

import java.io.Serializable;

public class TeaPriceListModel implements Serializable{

	private int id;
	private String teaName;
	private String fromPrice;
	private String toPrice;
	private String createTime;
	private String expireTime;
	private String referencePrice;
	
	public String getReferencePrice() {
		return referencePrice;
	}
	public void setReferencePrice(String referencePrice) {
		this.referencePrice = referencePrice;
	}
	public String getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}
	public int getId() {
		return id;
	}
	public String getTeaName() {
		return teaName;
	}
	public String getFromPrice() {
		return fromPrice;
	}
	public String getToPrice() {
		return toPrice;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTeaName(String teaName) {
		this.teaName = teaName;
	}
	public void setFromPrice(String fromPrice) {
		this.fromPrice = fromPrice;
	}
	public void setToPrice(String toPrice) {
		this.toPrice = toPrice;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
