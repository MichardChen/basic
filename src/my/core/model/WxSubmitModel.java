package my.core.model;

import java.io.Serializable;

public class WxSubmitModel implements Serializable{

	private String address;
	private String tag;
	private String first_class;
	private String second_class;
	private int first_id;
	private int second_id;
	private String title;
	public String getAddress() {
		return address;
	}
	public String getTag() {
		return tag;
	}
	public String getFirst_class() {
		return first_class;
	}
	public String getSecond_class() {
		return second_class;
	}
	public int getFirst_id() {
		return first_id;
	}
	public int getSecond_id() {
		return second_id;
	}
	public String getTitle() {
		return title;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public void setFirst_class(String first_class) {
		this.first_class = first_class;
	}
	public void setSecond_class(String second_class) {
		this.second_class = second_class;
	}
	public void setFirst_id(int first_id) {
		this.first_id = first_id;
	}
	public void setSecond_id(int second_id) {
		this.second_id = second_id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
}
