package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.StoreXcx;

public class StoreXcxService {

	public Page<StoreXcx> queryByPage(int page,int size){
		return StoreXcx.dao.queryByPage(page, size);
	}
	
	public Page<StoreXcx> queryByPageParams(int page,int size,String appId){
		return StoreXcx.dao.queryListByPage(page, size,appId);
	}
	
	public StoreXcx queryById(int id){
		return StoreXcx.dao.queryById(id);
	}
}
