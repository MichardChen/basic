package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.ServiceFee;

public class ServiceFeeService {

	public Page<ServiceFee> queryByPage(int page,int size){
		return ServiceFee.dao.queryByPage(page, size);
	}
	
	public Page<ServiceFee> queryByPageParams(int page,int size,int userId,String time,String mobile){
		return ServiceFee.dao.queryByPageParams(page, size,userId,time,mobile);
	}
}
