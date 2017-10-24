package my.core.vo;

import java.io.Serializable;

public class NewTeaSaleListModel implements Serializable{

	private int stock;
	private String img;
	private String name;
	private String status;
	private int teaId;
	private String statusName;
	private String unit;
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTeaId() {
		return teaId;
	}
	public void setTeaId(int teaId) {
		this.teaId = teaId;
	}
	
	
}
