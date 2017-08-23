package my.core.vo;

import java.io.Serializable;

public class CarouselVO implements Serializable{

	private String imgUrl;
	private String realUrl;
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getRealUrl() {
		return realUrl;
	}
	public void setRealUrl(String realUrl) {
		this.realUrl = realUrl;
	}
}
