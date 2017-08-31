package my.core.vo;

import java.io.Serializable;

public class MessageListVO implements Serializable{

	private String type;
	private String title;
	private String date;
	private String typeCd;
	private int id;
	private String params;
	
	
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getType() {
		return type;
	}
	public String getTitle() {
		return title;
	}
	public String getDate() {
		return date;
	}
	public String getTypeCd() {
		return typeCd;
	}
	public int getId() {
		return id;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}
	public void setId(int id) {
		this.id = id;
	}
}
