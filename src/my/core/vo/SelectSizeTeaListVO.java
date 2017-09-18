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
	private String img;
	private String wareHouseMarkUrl;
	private int sizeNum;
	
	public int getSizeNum() {
		return sizeNum;
	}
	public void setSizeNum(int sizeNum) {
		this.sizeNum = sizeNum;
	}
	public String getWareHouseMarkUrl() {
		return wareHouseMarkUrl;
	}
	public void setWareHouseMarkUrl(String wareHouseMarkUrl) {
		this.wareHouseMarkUrl = wareHouseMarkUrl;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
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
