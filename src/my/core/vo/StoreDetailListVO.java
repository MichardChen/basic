package my.core.vo;

import java.io.Serializable;

public class StoreDetailListVO implements Serializable{

	private String name;
	private String address;
	private String mobile;
	private String img1;
	private String img2;
	private String img3;
	private String businessFromTime;
	private String businessToTime;
	private String storeDesc;
	private Float longitude;
	private Float latitude;
	
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public String getMobile() {
		return mobile;
	}
	public String getImg1() {
		return img1;
	}
	public String getImg2() {
		return img2;
	}
	public String getImg3() {
		return img3;
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
	public void setName(String name) {
		this.name = name;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setImg1(String img1) {
		this.img1 = img1;
	}
	public void setImg2(String img2) {
		this.img2 = img2;
	}
	public void setImg3(String img3) {
		this.img3 = img3;
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
}
