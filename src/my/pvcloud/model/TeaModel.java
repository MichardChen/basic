package my.pvcloud.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TeaModel implements Serializable{

	private String name;
	private int id;
	private BigDecimal price;
	private String type;
	private String createTime;
	private String url;
	private String status;
	private int flg;
	
	public int getFlg() {
		return flg;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public String getType() {
		return type;
	}
	public String getCreateTime() {
		return createTime;
	}
	public String getUrl() {
		return url;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
