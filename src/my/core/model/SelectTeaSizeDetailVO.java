package my.core.model;

import java.io.Serializable;

public class SelectTeaSizeDetailVO implements Serializable{

	private int teaId;
	private String img;
	private String name;
	private String type;
	private String wareHouse;
	private String size;
	private String stock;
	private String wareHouseUrl;
	public int getTeaId() {
		return teaId;
	}
	public String getImg() {
		return img;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public String getSize() {
		return size;
	}
	public String getStock() {
		return stock;
	}
	public String getWareHouseUrl() {
		return wareHouseUrl;
	}
	public void setTeaId(int teaId) {
		this.teaId = teaId;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public void setWareHouseUrl(String wareHouseUrl) {
		this.wareHouseUrl = wareHouseUrl;
	}
}
