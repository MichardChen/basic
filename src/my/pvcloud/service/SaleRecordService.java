package my.pvcloud.service;

import my.core.model.SaleOrder;

import com.jfinal.plugin.activerecord.Page;

//卖茶记录
public class SaleRecordService {

	public Page<SaleOrder> queryByPage(int page,int size){
		return SaleOrder.dao.queryByPage(page, size);
	}
	
	public Page<SaleOrder> querySaleOrderByParam(int page,int size,String createDate,int userId){
		return SaleOrder.dao.queryByPageParams(page, size,createDate,userId);
	}
	
	public SaleOrder queryById(int id){
		return SaleOrder.dao.queryById(id);
	}
	
	public boolean updateInfo(SaleOrder data){
		return SaleOrder.dao.updateInfo(data);
	}
	
	public boolean saveInfo(SaleOrder data){
		return SaleOrder.dao.saveInfo(data);
	}
}
