package my.core.service;

import java.util.Date;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.User;
import my.core.model.UserRole;

public class UserService {

	public User getUserByUserName(String userName){
		return User.dao.getUserByUserName(userName);
	}
	
	public String regUser(String userName, String password){
		User user = User.dao.getUserByUserName(userName);
		if(user != null){
			return "该用户名已被注册";
		}
		if(new User().set("username", userName).set("password", password).save()){
			return "";
		}else{
			return "注册失败";
		}
	}
	public User queryByUserName(String userName,String pwd){
		return User.dao.queryByUserName(userName, pwd);
	}
	
	public Page<User> queryAllByPage(int pageNum,int size,String roleName,int agentId){
		return User.dao.queryAllByPage(pageNum, size, roleName, agentId);
	}
	public Page<User> queryByCondition(int pageNum,int size,String roleName,int agentId,String codeName,String realname){
		return User.dao.queryByCondition(pageNum, size, roleName, agentId, codeName, realname);
	}
	
	public boolean updateEffMark(int userId){
		return User.dao.updateEffMark(userId);
	}
	public boolean saveAgentStaff(int agentId,String realname,String postionCode,String mobile,Date joinDate){
		return User.dao.saveAgentStaff(agentId,realname, postionCode, mobile, joinDate);
	}
	public boolean updateAgentStaff(int userId,String realname,String postionCode,String mobile,Date joinDate){
		return User.dao.updateAgentStaff(userId, realname, postionCode, mobile, joinDate);
	}
	public boolean saveUserRole(int userId,int roleId){
		return UserRole.dao.saveUserRole(userId, roleId);
	}
	public User queryByRealnameMobile(String realname,String mobile){
		return User.dao.queryByRealnameMobile(realname, mobile);
	}
	public User queryByPosition(int agentId,String status,String postionCode){
		return User.dao.queryByPosition(agentId, status, postionCode);
	}
	public Page<User> queryUserInfoByAgentId(int pageNum,int size,int agentId,String status,String postionCode){
		return User.dao.queryUserInfoByAgentId(pageNum,size,agentId, status, postionCode);
	}
	public boolean saveInfo(String username,String password,String mobile,String email,int agentId,Date joinDate ){
		return User.dao.saveInfo(username, password, mobile, email, agentId, joinDate);
	}
	public User queryUserInfo(String username,String mobile,int agentId){
		return User.dao.queryUserInfo(username, mobile, agentId);
	}
	public boolean updateInfo(int userId,String username,String password,String mobile,String email,Date joinDate ){
		return User.dao.updateInfo(userId, username, password, mobile, email, joinDate);
	}
	public boolean updateUInfo(int userId,String username,String mobile,String email,Date joinDate ){
		return User.dao.updateUInfo(userId, username, mobile, email, joinDate);
	}
	public User queryById(int userId){
		return User.dao.queryById(userId);
	}
	public User queryByUserName(String username){
		return User.dao.queryByUserName(username);
	}
}