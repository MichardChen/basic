package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "s_role_menu", pk = "role_menu_id")
public class RoleMenu extends Model<RoleMenu> {

	public static final RoleMenu dao = new RoleMenu();
	
	public RoleMenu queryById(int roleId){
		return RoleMenu.dao.findFirst("select * from s_role_menu where role_id = ?",roleId);
	}
	
	public RoleMenu queryByKeyId(int id){
		return RoleMenu.dao.findFirst("select * from s_role_menu where role_menu_id = ?",id);
	}
	
	public RoleMenu queryByRoleMenuId(int roleId,int menuId){
		return RoleMenu.dao.findFirst("select * from s_role_menu where role_id = ? and menu_id=?",roleId,menuId);
	}
	
	public List<RoleMenu> queryByRoleId(int roleId){
		return RoleMenu.dao.find("select * from s_role_menu where role_id = ?",roleId);
	}
	
	public boolean saveInfo(RoleMenu data){
		return new RoleMenu().setAttrs(data).save();
	}
}
