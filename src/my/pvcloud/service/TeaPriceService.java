package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.TeaPrice;

public class TeaPriceService {

	public Page<TeaPrice> queryByPage(int page,int size){
		return TeaPrice.dao.queryByPage(page, size);
	}
	
	public Page<TeaPrice> queryByPageParams(int page,int size,String name){
		return TeaPrice.dao.queryByPageParams(page, size,name);
	}
}
