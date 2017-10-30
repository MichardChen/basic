package my.core.vo;

import java.io.Serializable;

public class WareHouseVO implements Serializable{

	private int id;
	private String name;
	private String mark;
	private String createTime;
	private int flg;
	private String updateTime;
	private String createUser;
	private String updateUser;
	private int stock;
	
	
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
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
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getMark() {
		return mark;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
