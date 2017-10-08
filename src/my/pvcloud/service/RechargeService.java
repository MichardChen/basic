package my.pvcloud.service;

import my.core.model.PayRecord;

import com.jfinal.plugin.activerecord.Page;

public class RechargeService{

	public Page<PayRecord> queryByPage(int page,int size){
		return PayRecord.dao.queryByPage(page, size);
	}
	
	public Page<PayRecord> queryByPageParams(int page,int size,String time,String mobile){
		return PayRecord.dao.queryByPageParams(page, size,time,mobile);
	}
	
	public PayRecord queryById(int teaId){
		return PayRecord.dao.queryById(teaId);
	}
	
	public boolean updateInfo(PayRecord tea){
		return PayRecord.dao.updateInfo(tea);
	}
}
