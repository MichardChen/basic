package my.pvcloud.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.Member;
import my.core.model.Store;

public class StoreService {

	public Page<Store> queryByPage(int page,int size){
		return Store.dao.queryByPage(page, size);
	}
	
	public Page<Store> queryByPageParams(int page,int size,String title,String status,int memberId){
		return Store.dao.queryByPageParams(page, size,title,status,memberId);
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
	
	public int updateFlg(int id,String status){
		return Store.dao.updateStoreStatus(id, status);
	}
	
	public Page<Member> queryStoreMemberList(int page,int size,int storeId,String name,String mobile){
		return Member.dao.queryStoreMemberList(page, size, storeId, name, mobile);
	}
}
