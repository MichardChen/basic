package my.core.vo;

import java.io.Serializable;

public class WantSaleTeaListVO implements Serializable{

	private int teaId;
	private String name;
	private int quality;
	private String size;
	private String img;
	
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public int getTeaId() {
		return teaId;
	}
	public String getName() {
		return name;
	}
	public int getQuality() {
		return quality;
	}
	public String getSize() {
		return size;
	}
	public void setTeaId(int teaId) {
		this.teaId = teaId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	
}
