package my.pvcloud.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuNav implements Serializable {

	private long menuId;
	
	private String menuName;
	
	private long parentId;
	
	private List<MenuNav> childMenu=new ArrayList<MenuNav>();

	public long getMenuId() {
		return menuId;
	}

	public void setMenuId(long menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public List<MenuNav> getChildMenu() {
		return childMenu;
	}

	public void setChildMenu(List<MenuNav> childMenu) {
		this.childMenu = childMenu;
	}

	@Override
	public String toString() {
		return "MenuNav [menuId=" + menuId + ", menuName=" + menuName
				+ ", parentId=" + parentId + ", childMenu=" + childMenu + "]";
	}

}
