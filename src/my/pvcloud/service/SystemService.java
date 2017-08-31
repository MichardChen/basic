package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.SystemVersionControl;

public class SystemService {

	public Page<SystemVersionControl> queryByPage(int page,int size){
		return SystemVersionControl.dao.queryByPage(page, size);
	}
	
	public SystemVersionControl queryById(int id){
		return SystemVersionControl.dao.querySystemVersionControlById(id);
	}
	
	public boolean updateInfo(SystemVersionControl data){
		return SystemVersionControl.dao.updateInfo(data);
	}
	
	public boolean saveInfo(SystemVersionControl data){
		return SystemVersionControl.dao.saveInfo(data);
	}
}
