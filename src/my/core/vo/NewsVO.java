package my.core.vo;

import java.io.Serializable;

public class NewsVO implements Serializable{

	private String img;
	private String title;
	private String type;
	private String date;
	private int hotFlg;
	private int newsId;
	private String shareUrl;
	private String shareLogo;
	public String getShareLogo() {
		return shareLogo;
	}
	public void setShareLogo(String shareLogo) {
		this.shareLogo = shareLogo;
	}
	public String getImg() {
		return img;
	}
	public String getTitle() {
		return title;
	}
	public String getType() {
		return type;
	}
	public String getDate() {
		return date;
	}
	public int getHotFlg() {
		return hotFlg;
	}
	public int getNewsId() {
		return newsId;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setHotFlg(int hotFlg) {
		this.hotFlg = hotFlg;
	}
	public void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	
	
}
