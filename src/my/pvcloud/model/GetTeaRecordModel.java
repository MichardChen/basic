package my.pvcloud.model;

import java.io.Serializable;

public class GetTeaRecordModel implements Serializable{

	private String name;
	private String teaName;
	private String quality;
	private String createTime;
	private String express;
	private String expressNo;
	private String status;
	private String mark;
	private int id;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public String getTeaName() {
		return teaName;
	}
	public String getQuality() {
		return quality;
	}
	public String getCreateTime() {
		return createTime;
	}
	public String getExpress() {
		return express;
	}
	public String getExpressNo() {
		return expressNo;
	}
	public String getStatus() {
		return status;
	}
	public String getMark() {
		return mark;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTeaName(String teaName) {
		this.teaName = teaName;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setExpress(String express) {
		this.express = express;
	}
	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	
}
