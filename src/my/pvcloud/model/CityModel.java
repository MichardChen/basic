package my.pvcloud.model;

import java.io.Serializable;

public class CityModel implements Serializable{

	private int id;
	private String name;
	private int pid;
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public int getPid() {
		return pid;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	
}
