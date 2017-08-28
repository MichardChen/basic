package my.core.vo;

import java.io.Serializable;

public class AddressVO implements Serializable{

	private String linkMan;
	private String linkTel;
	private int defaultFlg;
	private String address;
	private int addressId;
	
	public String getLinkMan() {
		return linkMan;
	}
	public String getLinkTel() {
		return linkTel;
	}
	public int getDefaultFlg() {
		return defaultFlg;
	}
	public String getAddress() {
		return address;
	}
	public int getAddressId() {
		return addressId;
	}
	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}
	public void setLinkTel(String linkTel) {
		this.linkTel = linkTel;
	}
	public void setDefaultFlg(int defaultFlg) {
		this.defaultFlg = defaultFlg;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
}
