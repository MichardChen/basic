package my.pvcloud.service;

import my.core.model.WarehouseTeaMemberItem;

import com.jfinal.plugin.activerecord.Page;

//WarehouseTeaMember
public class SaleOrderService {

	public Page<WarehouseTeaMemberItem> queryByPage(int page,int size){
		return WarehouseTeaMemberItem.dao.queryByPage(page, size);
	}
	
	public Page<WarehouseTeaMemberItem> queryWtmItemByPage(int page,int size){
		return WarehouseTeaMemberItem.dao.queryByPage(page, size);
	}
	
	public Page<WarehouseTeaMemberItem> queryWtmItemByParam(int page,int size,String createDate){
		return WarehouseTeaMemberItem.dao.queryByPageParams(page, size,createDate);
	}
	
	public WarehouseTeaMemberItem queryById(int id){
		return WarehouseTeaMemberItem.dao.queryById(id);
	}
	
	public boolean updateInfo(WarehouseTeaMemberItem wtmItem){
		return WarehouseTeaMemberItem.dao.updateInfo(wtmItem);
	}
	
	public boolean saveInfo(WarehouseTeaMemberItem wtmItem){
		return WarehouseTeaMemberItem.dao.saveInfo(wtmItem);
	}
}
