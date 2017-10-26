package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.News;

public class NewsInfoService{

	public Page<News> queryByPage(int page,int size){
		return News.dao.queryByAdminPage(page, size);
	}
	
	public News queryById(int newId){
		return News.dao.queryById(newId);
	}
	
	public boolean updateInfo(News news){
		return News.dao.updateInfo(news);
	}
	
	public boolean saveInfo(News news){
		return News.dao.saveInfo(news);
	}
	
	public int updateFlg(int newId,int flg,int operateUserId){
		return News.dao.updateNewsStatus(newId, flg,operateUserId);
	}
}