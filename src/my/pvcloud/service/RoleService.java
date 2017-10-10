package my.pvcloud.service;

import my.core.model.Menu;
import my.core.model.Role;

import com.jfinal.plugin.activerecord.Page;

public class RoleService {

	public Page<Role> queryByPage(int page,int size){
		return Role.dao.queryByPage(page, size);
	}
	
	public Page<Role> queryByPageParams(int page,int size,String date){
		return Role.dao.queryRoleListByPage(page, size,date);
	}
	
	public Role queryById(int id){
		return Role.dao.queryById(id);
	}
	
	public boolean updateInfo(Role data){
		return Role.dao.updateInfo(data);
	}
	
	public boolean saveInfo(Role data){
		return Role.dao.saveInfo(data);
	}
}
