package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;


@TableBind(table="s_user_role",pk="user_role_id")
public class UserRole extends Model<UserRole> {

	public static final UserRole dao=new UserRole();
	
	public boolean saveUserRole(int userId,int roleId){
		return new UserRole().set("user_id", userId).set("role_id", roleId).save();
	}
}
