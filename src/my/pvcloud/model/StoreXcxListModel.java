package my.pvcloud.model;

import java.io.Serializable;

public class StoreXcxListModel implements Serializable{

	private int id;
	private String appId;
	private String appName;
	private String store;
	private String appSecret;
	private String createTime;
	
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public int getId() {
		return id;
	}
	public String getAppId() {
		return appId;
	}
	public String getAppName() {
		return appName;
	}
	public String getStore() {
		return store;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
