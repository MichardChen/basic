package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "s_user_menu", pk = "user_menu_id")
public class UserMenu  extends Model<UserMenu>{

	public static final UserMenu dao = new UserMenu();
	
	public void deleteUserMenuByuserId(String userId){
		 Db.update("delete from s_user_menu where user_id="+userId);
	}
	
	public int deleteUserMenuByuserId(int userId){
		 return Db.update("delete from s_user_menu where user_id="+userId);
	}
	
	public int deleteUserMenuByMenuId(int menuId){
		 return Db.update("delete from s_user_menu where menu_id="+menuId);
	}
	
	public boolean saveInfo(UserMenu data){
		return new UserMenu().setAttrs(data).save();
	}
}
