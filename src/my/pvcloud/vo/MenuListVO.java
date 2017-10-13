package my.pvcloud.vo;

import java.io.Serializable;

public class MenuListVO implements Serializable{

	private int id;
	private String name;
	private String path;
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
