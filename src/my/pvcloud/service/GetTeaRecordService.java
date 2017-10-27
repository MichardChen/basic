package my.pvcloud.service;

import my.core.model.GetTeaRecord;

import com.jfinal.plugin.activerecord.Page;
import com.sun.org.apache.regexp.internal.recompile;

public class GetTeaRecordService {

	public Page<GetTeaRecord> queryByPage(int page,int size){
		return GetTeaRecord.dao.queryByPage(page, size);
	}
	
	public Page<GetTeaRecord> queryByPageParams(int page,int size,String time1,String time2,String mobile,String status){
		return GetTeaRecord.dao.queryByPageParams(page, size,time1,time2,mobile,status);
	}
	
	public GetTeaRecord queryById(int id){
		return GetTeaRecord.dao.queryById(id);
	}
	
	public boolean updateInfo(GetTeaRecord data){
		return GetTeaRecord.dao.updateInfo(data);
	}
	
	public int updateRecord(int id,String expressName,String expressNo,String mark,String status){
		return GetTeaRecord.dao.updateMsg(id, expressName, expressNo, mark, status);
	}
}
