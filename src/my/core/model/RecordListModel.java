package my.core.model;

import java.io.Serializable;

public class RecordListModel implements Serializable{

	private String type;
	private String moneys;
	private String date;
	private String content;
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
}
