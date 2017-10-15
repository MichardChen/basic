package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;


@TableBind(table="s_user_role",pk="user_role_id")
public class UserRole extends Model<UserRole> {

	public static final UserRole dao=new UserRole();
	
	public boolean saveUserRole(int userId,int roleId){
		return new UserRole().set("user_id", userId).set("role_id", roleId).save();
	}
	
	public boolean saveInfo(UserRole data){
		return new UserRole().setAttrs(data).save();
	}
	
	public boolean updateInfo(UserRole data){
		return new UserRole().setAttrs(data).update();
	}
	
	public UserRole queryUserRoleByUserId(int userId){
		return UserRole.dao.findFirst("select * from s_user_role where user_id=?",userId);
	}
}
