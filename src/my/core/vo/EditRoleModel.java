package my.core.vo;

import java.io.Serializable;

public class EditRoleModel implements Serializable{

	private int id;
	private String roleName;
	public int getId() {
		return id;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	
}
