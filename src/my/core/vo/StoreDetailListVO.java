package my.core.vo;

import java.io.Serializable;
import java.util.List;

public class StoreDetailListVO implements Serializable{

	private String name;
	private String address;
	private String mobile;
	private String businessFromTime;
	private String businessToTime;
	private String storeDesc;
	private Float longitude;
	private Float latitude;
	private List<String> imgs;
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public String getMobile() {
		return mobile;
	}
	public String getBusinessFromTime() {
		return businessFromTime;
	}
	public String getBusinessToTime() {
		return businessToTime;
	}
	public String getStoreDesc() {
		return storeDesc;
	}
	public Float getLongitude() {
		return longitude;
	}
	public Float getLatitude() {
		return latitude;
	}
	public List<String> getImgs() {
		return imgs;
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
	public void setBusinessFromTime(String businessFromTime) {
		this.businessFromTime = businessFromTime;
	}
	public void setBusinessToTime(String businessToTime) {
		this.businessToTime = businessToTime;
	}
	public void setStoreDesc(String storeDesc) {
		this.storeDesc = storeDesc;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	public void setImgs(List<String> imgs) {
		this.imgs = imgs;
	}
}
