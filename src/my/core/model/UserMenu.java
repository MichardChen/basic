package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "s_user_menu", pk = "usermenu_id")
public class UserMenu  extends Model<UserMenu>{

	public static final UserMenu dao = new UserMenu();
	
	public void deleteUserMenuByuserId(String userId)
	{
		 Db.update("delete from s_user_menu where user_id="+userId);
	}
}
