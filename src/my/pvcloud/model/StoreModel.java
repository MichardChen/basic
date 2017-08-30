package my.pvcloud.model;

import java.io.Serializable;

public class StoreModel implements Serializable{

	private int id;
	private String title;
	private String address;
	private int flg;
	private String status;
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getAddress() {
		return address;
	}
	public int getFlg() {
		return flg;
	}
	public String getStatus() {
		return status;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
