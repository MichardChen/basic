package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.Tea;

public class TeaService{

	public Page<Tea> queryByPage(int page,int size){
		return Tea.dao.queryByPage(page, size);
	}
	
	public Page<Tea> queryByPageParams(int page,int size,String title,String status){
		return Tea.dao.queryByPage(page, size,title,status);
	}
	
	public Tea queryById(int teaId){
		return Tea.dao.queryById(teaId);
	}
	
	public boolean updateInfo(Tea tea){
		return Tea.dao.updateInfo(tea);
	}
	
	public boolean saveInfo(Tea tea){
		return Tea.dao.saveInfo(tea);
	}
	
	public int updateFlg(int id,int flg){
		return Tea.dao.updateTeaStatus(id, flg);
	}
	
	public int updateStatus(int id,String status){
		return Tea.dao.updateStatus(id, status);
	}
}