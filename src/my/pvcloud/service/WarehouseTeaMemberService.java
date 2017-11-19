package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.WarehouseTeaMember;

public class WarehouseTeaMemberService {

	public Page<WarehouseTeaMember> queryByPage(int page,int size){
		return WarehouseTeaMember.dao.queryByPage(page, size);
	}
	
	public Page<WarehouseTeaMember> queryWarehouseTeaMemberByParam(int page
																	,int size
																	,String date
																	,String mobile
																	,String saleUserTypeCd
																	,String tea){
		return WarehouseTeaMember.dao.queryByPageParams(page, size,date,mobile,saleUserTypeCd,tea);
	}
}
