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
	private int flg;
	private int hotFlg;
	private String content;
	private String updateTime;
	private String updateUser;
	private int topFlg;
	
	public int getTopFlg() {
		return topFlg;
	}
	public void setTopFlg(int topFlg) {
		this.topFlg = topFlg;
	}
	public int getHotFlg() {
		return hotFlg;
	}
	public String getContent() {
		return content;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setHotFlg(int hotFlg) {
		this.hotFlg = hotFlg;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public int getFlg() {
		return flg;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
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
