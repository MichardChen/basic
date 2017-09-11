package my.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class BuyTeaListVO implements Serializable{

	private int id;
	private String name;
	private String size;
	private BigDecimal price;
	private String type;
	private String img;
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getSize() {
		return size;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public String getType() {
		return type;
	}
	public String getImg() {
		return img;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setImg(String img) {
		this.img = img;
	}
	
	
}
