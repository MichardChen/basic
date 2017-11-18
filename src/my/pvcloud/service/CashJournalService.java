package my.pvcloud.service;

import my.core.model.CashJournal;

import com.jfinal.plugin.activerecord.Page;

public class CashJournalService {

	public boolean updateInfo(CashJournal tea){
		return CashJournal.dao.updateInfo(tea);
	}
	
	public boolean saveInfo(CashJournal tea){
		return CashJournal.dao.saveInfo(tea);
	}
	
	public Page<CashJournal> queryByPage(int page,int size){
		return CashJournal.dao.queryByPage(page, size);
	}
	
	public Page<CashJournal> queryByPageParams(int page,int size,String piType,String status,String time,String mobile,String name){
		return CashJournal.dao.queryByPageParams(page, size,piType,status,time,mobile,name);
	}
}
