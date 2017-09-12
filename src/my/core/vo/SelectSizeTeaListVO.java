package my.core.vo;

import java.io.Serializable;

public class SelectSizeTeaListVO implements Serializable{

	private int id;
	private String name;
	private String price;
	private String size;
	private String stock;
	private String wareHouse;
	private String type;
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getPrice() {
		return price;
	}
	public String getSize() {
		return size;
	}
	public String getStock() {
		return stock;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public String getType() {
		return type;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
