package my.pvcloud.model;

import java.io.Serializable;

public class NewsModel implements Serializable{

	private int id;
	private String title;
	private String type;
	private String createUser;
	private String status;
	private String createTime;
	private String url;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	public String getCreateUser() {
		return createUser;
	}
	public String getStatus() {
		return status;
	}
	public String getCreateTime() {
		return createTime;
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
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
}
