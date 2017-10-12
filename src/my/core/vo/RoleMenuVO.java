package my.core.vo;

import java.io.Serializable;

public class RoleMenuVO implements Serializable{

	private int id;
	private String path;
	public int getId() {
		return id;
	}
	public String getPath() {
		return path;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
