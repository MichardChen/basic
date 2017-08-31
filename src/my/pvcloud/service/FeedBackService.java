package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.FeedBack;
import my.core.model.FeedBack;

public class FeedBackService {

	public Page<FeedBack> queryByPage(int page,int size){
		return FeedBack.dao.queryByPage(page, size);
	}
	
	public FeedBack queryById(int id){
		return FeedBack.dao.queryById(id);
	}
	
	public boolean updateInfo(FeedBack data){
		return FeedBack.dao.updateInfo(data);
	}
	
	public boolean saveInfo(FeedBack data){
		return FeedBack.dao.saveInfo(data);
	}
	
	public int updateFlg(int id,int flg){
		return FeedBack.dao.updateFeedBackStatus(id, flg);
	}
}
