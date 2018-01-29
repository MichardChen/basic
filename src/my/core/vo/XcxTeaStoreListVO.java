package my.core.vo;

import java.io.Serializable;

public class XcxTeaStoreListVO implements Serializable{

	private int storeId;
	private String img;
	private String name;
	private String address;
	private String businessTea;
	private String distance;
	private int businessId;
	private String appId;
	public int getStoreId() {
		return storeId;
	}
	public String getImg() {
		return img;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public String getBusinessTea() {
		return businessTea;
	}
	public String getDistance() {
		return distance;
	}
	public int getBusinessId() {
		return businessId;
	}
	public String getAppId() {
		return appId;
	}
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setBusinessTea(String businessTea) {
		this.businessTea = businessTea;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
}
