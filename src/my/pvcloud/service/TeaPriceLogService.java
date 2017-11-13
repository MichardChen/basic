package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.TeaPrice;
import my.core.model.TeapriceLog;

public class TeaPriceLogService {

	public Page<TeapriceLog> queryByPage(int page,int size){
		return TeapriceLog.dao.queryByPage(page, size);
	}
	
	public Page<TeapriceLog> queryByPageParams(int page,int size,String name){
		return TeapriceLog.dao.queryByPageParams(page, size,name);
	}
}
