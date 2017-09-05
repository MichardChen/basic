package my.pvcloud.service;

import my.core.model.Order;

import com.jfinal.plugin.activerecord.Page;

public class OrderService {

	public Page<Order> queryByPage(int page,int size){
		return Order.dao.queryByPage(page, size);
	}
	
	public Page<Order> queryByPageParams(int page,int size,String title){
		return Order.dao.queryByPageParams(page, size,title);
	}
	
	public Order queryById(int orderId){
		return Order.dao.queryById(orderId);
	}
	
	public boolean updateInfo(Order order){
		return Order.dao.updateInfo(order);
	}
	
	public boolean saveInfo(Order order){
		return Order.dao.saveInfo(order);
	}
}
