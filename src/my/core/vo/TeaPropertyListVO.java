package my.core.vo;

import java.io.Serializable;

public class TeaPropertyListVO implements Serializable{

	private int teaId;
	private String name;
	private String img;
	private String type;
	private String stock;
	private String size;
	private String warehouse;
	
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public int getTeaId() {
		return teaId;
	}
	public String getName() {
		return name;
	}
	public String getImg() {
		return img;
	}
	public String getType() {
		return type;
	}
	public String getStock() {
		return stock;
	}
	public void setTeaId(int teaId) {
		this.teaId = teaId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
}
