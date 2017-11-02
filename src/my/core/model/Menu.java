package my.core.model;

import java.util.ArrayList;
import java.util.List;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

/**
 * @author Administrator
 *左侧菜单目录
 */
@TableBind(table = "s_menu", pk = "menu_id")
public class Menu extends Model<Menu> {
	public static final Menu dao = new Menu();

	public List<Menu> getMenuByUserId(int userId) {
		return Menu.dao.find("select m.* from s_menu m, s_role_menu rm, s_user_role ur where m.menu_id=rm.menu_id and rm.role_id=ur.role_id and m.is_show!=0 and ur.user_id=? order by CAST(m.sort as SIGNED) asc", userId);
	}
	public List<Menu> getUserMenuByUserId(int userId)
	{
		return Menu.dao.find("select m.* from s_menu m, s_user_menu um where m.menu_id=um.menu_id and um.user_id=?  order by CAST(m.sort as SIGNED) asc", userId);
	}
	
	public List<Menu> getRoleMenuByRoleId(int roleId)
	{
		return Menu.dao.find("select m.* from s_menu m, s_role_menu rm where m.menu_id=rm.menu_id and rm.role_id=?  order by CAST(m.sort as SIGNED) asc", roleId);
	}
	
	public List<Menu> getMenu() {
		return Menu.dao.find("select m.* from s_menu m  where  m.is_show!=0  order by m.menu_id asc");
	}
	
	public Page<Menu> queryMenuListByPage(int page,int size,String name){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(name)){
			strBuf.append("and menu_name like '%"+name+"%'");
		}
		
		sql=" from s_menu where 1=1 "+strBuf.toString()+" order by create_time desc";
		return Menu.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<Menu> queryByPage(int page,int size){

		String sql=" from s_menu where 1=1 order by create_time desc";
		String select="select * ";
		return Menu.dao.paginate(page, size, select, sql);
	}
	
	public Menu queryById(int id){
		return Menu.dao.findFirst("select * from s_menu where menu_id = ?",id);
	}
	
	public List<Menu> queryAllMenu(){
		return Menu.dao.find("select * from s_menu");
	}
	
	public boolean updateInfo(Menu data){
		return new Menu().setAttrs(data).update();
	}
	
	public boolean saveInfo(Menu data){
		return new Menu().setAttrs(data).save();
	}
}
