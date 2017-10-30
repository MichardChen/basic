package my.pvcloud.model;

import java.io.Serializable;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

public class FeedBackModel implements Serializable{

	private String mobile;
	private String name;
	private String content;
	private int id;
	private int flg;
	private String createTime;
	private String updateTime;
	private String operateUser;
	
	public String getCreateTime() {
		return createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public String getOperateUser() {
		return operateUser;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}
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
