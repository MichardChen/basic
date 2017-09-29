package my.pvcloud.vo;

import java.io.Serializable;

public class StoreDetailVO implements Serializable{

	private int storeId;
	private String name;
	private String address;
	private String mobile;
	private String time;
	private String businessTea;
	public int getStoreId() {
		return storeId;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public String getMobile() {
		return mobile;
	}
	public String getTime() {
		return time;
	}
	public String getBusinessTea() {
		return businessTea;
	}
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setBusinessTea(String businessTea) {
		this.businessTea = businessTea;
	}
	
	
}
