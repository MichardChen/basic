package my.pvcloud.service;

import my.core.model.Log;

import com.jfinal.plugin.activerecord.Page;

public class LogService {

	public Page<Log> queryByPage(int page,int size){
		return Log.dao.queryByPage(page, size);
	}
	
	public Page<Log> queryByPageParams(int page,int size,String date,String operation){
		return Log.dao.queryLogByPage(page, size,date,operation);
	}
	
	public boolean saveInfo(Log data){
		return Log.dao.saveInfo(data);
	}
}
