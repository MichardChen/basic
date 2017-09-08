package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.Store;

public class StoreService {

	public Page<Store> queryByPage(int page,int size){
		return Store.dao.queryByPage(page, size);
	}
	
	public Page<Store> queryByPageParams(int page,int size,String title){
		return Store.dao.queryByPageParams(page, size,title);
	}
	
	public Store queryById(int teaId){
		return Store.dao.queryById(teaId);
	}
	
	public boolean updateInfo(Store tea){
		return Store.dao.updateInfo(tea);
	}
	
	public Store saveInfo(Store tea){
		return Store.dao.saveInfo(tea);
	}
	
	public int updateFlg(int id,int flg){
		return Store.dao.updateStoreStatus(id, flg);
	}
}
