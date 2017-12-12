package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.Invoice;

public class InvoiceService {

	public Page<Invoice> queryByPage(int page,int size){
		return Invoice.dao.queryByPage(page, size);
	}
	
	public Page<Invoice> queryByPageParams(int page,int size,String mobile,String date,String status){
		return Invoice.dao.queryByPageParams(page, size,mobile,date,status);
	}
}
