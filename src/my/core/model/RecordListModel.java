package my.core.model;

import java.io.Serializable;

public class RecordListModel implements Serializable{

	private String type;
	private String moneys;
	private String date;
	private String content;
	private int id;
	private String tea;
	private String wareHouse;
	private String img;
	private String teaType;
	private int quality;
	private String unit;
	public String getType() {
		return type;
	}
	public String getMoneys() {
		return moneys;
	}
	public String getDate() {
		return date;
	}
	public String getContent() {
		return content;
	}
	public int getId() {
		return id;
	}
	public String getTea() {
		return tea;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public String getImg() {
		return img;
	}
	public String getTeaType() {
		return teaType;
	}
	public int getQuality() {
		return quality;
	}
	public String getUnit() {
		return unit;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setMoneys(String moneys) {
		this.moneys = moneys;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTea(String tea) {
		this.tea = tea;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setTeaType(String teaType) {
		this.teaType = teaType;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
	
}
