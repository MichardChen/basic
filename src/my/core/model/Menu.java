package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author Administrator
 *左侧菜单目录
 */
@TableBind(table = "s_menu", pk = "menu_id")
public class Menu extends Model<Menu> {
	public static final Menu dao = new Menu();

	public List<Menu> getMenuByUserId(int userId) {
		return Menu.dao.find("select m.* from s_menu m, s_role_menu rm, s_user_role ur where m.menu_id=rm.menu_id and rm.role_id=ur.role_id and m.is_show!=0 and ur.user_id=? ", userId);
	}
	public List<Menu> getUserMenuByUserId(int userId)
	{
		return Menu.dao.find("select m.* from s_menu m, s_user_menu um where m.menu_id=um.menu_id and um.user_id=?  order by m.menu_id asc", userId);
	}
	public List<Menu> getMenu() {
		return Menu.dao.find("select m.* from s_menu m  where  m.is_show!=0  order by m.menu_id asc");
	}
}
