package my.pvcloud.service;

import my.core.model.Order;
import my.core.model.OrderItem;

import com.jfinal.plugin.activerecord.Page;

public class OrderService {

	public Page<Order> queryByPage(int page,int size){
		return Order.dao.queryByPage(page, size);
	}
	
	public Page<OrderItem> queryOrderItemByPage(int page,int size){
		return OrderItem.dao.queryByPage(page, size);
	}
	
	public Page<OrderItem> queryOrderItemByParam(int page,int size,String createDate,String orderNo,String status,String payTime,int userId,String saleUserTypeCd,int buyUserId){
		return OrderItem.dao.queryByPageParams(page, size,createDate,orderNo,status,payTime,userId,saleUserTypeCd,buyUserId);
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
