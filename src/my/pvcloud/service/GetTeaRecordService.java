package my.pvcloud.service;

import my.core.model.GetTeaRecord;

import com.jfinal.plugin.activerecord.Page;

public class GetTeaRecordService {

	public Page<GetTeaRecord> queryByPage(int page,int size){
		return GetTeaRecord.dao.queryByPage(page, size);
	}
	
	public Page<GetTeaRecord> queryByPageParams(int page,int size,String time1,String time2){
		return GetTeaRecord.dao.queryByPageParams(page, size,time1,time2);
	}
	
	public GetTeaRecord queryById(int id){
		return GetTeaRecord.dao.queryById(id);
	}
	
	public boolean updateInfo(GetTeaRecord data){
		return GetTeaRecord.dao.updateInfo(data);
	}
}
