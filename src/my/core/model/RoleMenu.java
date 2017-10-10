package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "s_role_menu", pk = "role_menu_id")
public class RoleMenu extends Model<RoleMenu> {

	public static final RoleMenu dao = new RoleMenu();
	
	public RoleMenu queryById(int roleId){
		return RoleMenu.dao.findFirst("select * from s_role_menu where role_id = ?",roleId);
	}
}
