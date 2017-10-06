package my.pvcloud.service;

import my.core.model.BankCardRecord;

import com.jfinal.plugin.activerecord.Page;

public class WithDrawService {

	public Page<BankCardRecord> queryByPage(int page,int size){
		return BankCardRecord.dao.queryByPage(page, size);
	}
	
	public Page<BankCardRecord> queryByPageParams(int page,int size,String time,String status,String mobile){
		return BankCardRecord.dao.queryByPageParams(page, size,time,status,mobile);
	}
	
	public BankCardRecord queryById(int teaId){
		return BankCardRecord.dao.queryById(teaId);
	}
	
	public boolean updateInfo(BankCardRecord tea){
		return BankCardRecord.dao.updateInfo(tea);
	}
	
	public int updateFlg(int id,String status){
		return BankCardRecord.dao.updateStoreStatus(id, status);
	}
}
