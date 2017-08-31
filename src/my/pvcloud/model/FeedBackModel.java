package my.pvcloud.model;

import java.io.Serializable;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

public class FeedBackModel implements Serializable{

	private String mobile;
	private String name;
	private String content;
	private int id;
	private int flg;
	
	
	public int getFlg() {
		return flg;
	}
	public void setFlg(int flg) {
		this.flg = flg;
	}
	public String getMobile() {
		return mobile;
	}
	public String getName() {
		return name;
	}
	public String getContent() {
		return content;
	}
	public int getId() {
		return id;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
