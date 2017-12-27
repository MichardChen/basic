package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.StoreEvaluate;

public class StoreEvaluateService {

	public Page<StoreEvaluate> queryByPage(int page,int size){
		return StoreEvaluate.dao.queryByPage(page, size);
	}
	
	public Page<StoreEvaluate> queryByPageParams(int page,int size,String date,String mobile,String flg,String title1,String storeName,String content){
		return StoreEvaluate.dao.queryByPageParams(page, size,date,mobile,flg,title1,storeName,content);
	}
}
