package my.core.vo;

import java.io.Serializable;

public class CarouselVO implements Serializable{

	private String imgUrl;
	private String realUrl;
	private int id;
	private int flg;
	private String createTime;
	private String updateTime;
	
	public String getCreateTime() {
		return createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public String getRealUrl() {
		return realUrl;
	}
	public int getId() {
		return id;
	}
	public int getFlg() {
		return flg;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public void setRealUrl(String realUrl) {
		this.realUrl = realUrl;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
	
	
}
