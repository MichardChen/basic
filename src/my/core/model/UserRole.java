package my.core.model;

import java.util.List;

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
	
	public List<UserRole> queryUserRoleByUserListId(int userId){
		return UserRole.dao.find("select * from s_user_role where user_id=?",userId);
	}
	
	public List<UserRole> queryUserRoleByRoleId(int roleId){
		return UserRole.dao.find("select * from s_user_role where role_id=?",roleId);
	}
	
	public UserRole queryUserRoleByUserRoleId(int userId,int roleId){
		return UserRole.dao.findFirst("select * from s_user_role where user_id=? and role_id=?",userId,roleId);
	}
}
