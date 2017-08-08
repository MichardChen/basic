package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.model.CustInfo;

public class CustInfoService{

	public Page<CustInfo> queryByPage(int page,int size,String custInfo,String custValue){
		return CustInfo.dao.queryByPage(page, size, custInfo, custValue);
	}
	
	public CustInfo queryById(int custId){
		return CustInfo.dao.queryById(custId);
	}
	
	public boolean updateInfo(CustInfo custInfo){
		return CustInfo.dao.updateInfo(custInfo);
	}
	
	public CustInfo login(String phoneNum,String password){
		return CustInfo.dao.login(phoneNum, password);
	}
	
	public boolean saveInfo(CustInfo custInfo){
		return CustInfo.dao.saveInfo(custInfo);
	}
	
	public CustInfo queryByPhoneNum(String phoneNum){
		return CustInfo.dao.queryByPhoneNum(phoneNum);
	}
	
	public boolean del(int custId){
		return CustInfo.dao.del(custId);
	}
}