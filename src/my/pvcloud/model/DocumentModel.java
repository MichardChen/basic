package my.pvcloud.model;

import java.io.Serializable;

public class DocumentModel implements Serializable{
	
	private int id;
	private String title;
	private String type;
	private String content;
	private int flg;
	private String status;
	private String url;
	
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getType() {
		return type;
	}
	public String getContent() {
		return content;
	}
	public int getFlg() {
		return flg;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
}
