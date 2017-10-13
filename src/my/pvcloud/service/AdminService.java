package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.User;

public class AdminService {

	public Page<User> queryUserListByPage(int page,int size,String mobile){
		return User.dao.queryUserListByPage(page, size, mobile);
	}

	public Page<User> queryByPage(int page,int size){
		return User.dao.queryByPage(page, size);
	}
	
	public User queryById(int teaId){
		return User.dao.queryById(teaId);
	}
	
	public boolean updateInfo(User tea){
		return User.dao.updateInfo(tea);
	}
	
	public boolean saveInfo(User tea){
		return User.dao.saveInfo(tea);
	}
}
