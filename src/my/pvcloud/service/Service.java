package my.pvcloud.service;

import com.jfinal.plugin.activerecord.Page;

import my.core.model.Document;

public class Service{

	public Page<Document> queryByPage(int page,int size){
		return Document.dao.queryByPage(page, size);
	}
	
	public Page<Document> queryByPageParams(int page,int size,String title){
		return Document.dao.queryByPageParams(page, size,title);
	}
	
	public Document queryById(int id){
		return Document.dao.queryById(id);
	}
	
	public boolean updateInfo(Document data){
		return Document.dao.updateInfo(data);
	}
	
	public boolean saveInfo(Document data){
		return Document.dao.saveInfo(data);
	}
	
	public int updateFlg(int id,int flg){
		return Document.dao.updateDocumentStatus(id, flg);
	}
}