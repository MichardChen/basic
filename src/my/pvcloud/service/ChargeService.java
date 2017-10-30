package my.pvcloud.service;

import my.core.model.PayRecord;

import com.jfinal.plugin.activerecord.Page;

public class ChargeService {

	public Page<PayRecord> queryByPage(int page,int size){
		return PayRecord.dao.queryByPage(page, size);
	}
	
	public Page<PayRecord> queryByPageParams(int page,int size,String date,String mobile){
		return PayRecord.dao.queryPayRecordByPage(page, size,date,mobile);
	}
	
	public boolean saveInfo(PayRecord data){
		return PayRecord.dao.saveInfo(data);
	}
}
