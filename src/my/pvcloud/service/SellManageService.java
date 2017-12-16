package my.pvcloud.service;

import my.core.model.OrderItem;

import com.jfinal.plugin.activerecord.Page;

public class SellManageService {

	public Page<OrderItem> queryByPage(int page,int size){
		return OrderItem.dao.querySellOrderItemList(size, page);
	}
	
	public Page<OrderItem> queryOrderItemByParam(int page,int size,String createDate,String buyMobile,String sellMobile,int sellId){
		return OrderItem.dao.querySellOrderItemListByParams(size, page, createDate, buyMobile, sellMobile,sellId);
	}
}
