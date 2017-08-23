package my.core.vo;

import java.io.Serializable;

public class NewsVO implements Serializable{

	private String img;
	private String title;
	private String type;
	private String date;
	private int hotFlg;
	private String content;
	private int newsId;
	
	public int getNewsId() {
		return newsId;
	}
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getHotFlg() {
		return hotFlg;
	}
	public void setHotFlg(int hotFlg) {
		this.hotFlg = hotFlg;
	}
}
