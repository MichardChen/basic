package my.pvcloud.model;

import java.io.Serializable;

public class SystemModel implements Serializable{

	private String type;
	private int id;
	private String mark;
	private String data1;
	private String data2;
	private String createTime;
	private String version;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getType() {
		return type;
	}
	public int getId() {
		return id;
	}
	public String getMark() {
		return mark;
	}
	public String getData1() {
		return data1;
	}
	public String getData2() {
		return data2;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public void setData1(String data1) {
		this.data1 = data1;
	}
	public void setData2(String data2) {
		this.data2 = data2;
	}
	
	
}
