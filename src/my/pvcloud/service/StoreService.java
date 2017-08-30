package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.Store;

public class StoreService {

	public Page<Store> queryByPage(int page,int size){
		return Store.dao.queryByPage(page, size);
	}
	
	public Store queryById(int teaId){
		return Store.dao.queryById(teaId);
	}
	
	public boolean updateInfo(Store tea){
		return Store.dao.updateInfo(tea);
	}
	
	public boolean saveInfo(Store tea){
		return Store.dao.saveInfo(tea);
	}
	
	public int updateFlg(int id,int flg){
		return Store.dao.updateStoreStatus(id, flg);
	}
}
