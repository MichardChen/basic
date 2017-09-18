package my.core.vo;

import java.io.Serializable;

public class ChooseAddressVO implements Serializable{

	private int addressId;
	private String address;
	private String receiverMan;
	private String mobile;
	public int getAddressId() {
		return addressId;
	}
	public String getAddress() {
		return address;
	}
	public String getReceiverMan() {
		return receiverMan;
	}
	public String getMobile() {
		return mobile;
	}
	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setReceiverMan(String receiverMan) {
		this.receiverMan = receiverMan;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
}
