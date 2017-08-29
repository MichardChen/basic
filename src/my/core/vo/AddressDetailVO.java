package my.core.vo;

import java.io.Serializable;

public class AddressDetailVO implements Serializable{

	private int id;
	private int provinceId;
	private int cityId;
	private int districtId;
	private String address;
	private String receiverMan;
	private int defaultFlg;
	private String mobile;
	public int getId() {
		return id;
	}
	public int getProvinceId() {
		return provinceId;
	}
	public int getCityId() {
		return cityId;
	}
	public int getDistrictId() {
		return districtId;
	}
	public String getAddress() {
		return address;
	}
	public String getReceiverMan() {
		return receiverMan;
	}
	public int getDefaultFlg() {
		return defaultFlg;
	}
	public String getMobile() {
		return mobile;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setReceiverMan(String receiverMan) {
		this.receiverMan = receiverMan;
	}
	public void setDefaultFlg(int defaultFlg) {
		this.defaultFlg = defaultFlg;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
