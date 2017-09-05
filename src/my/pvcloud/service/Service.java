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
	
	public Document queryById(int teaId){
		return Document.dao.queryById(teaId);
	}
	
	public boolean updateInfo(Document tea){
		return Document.dao.updateInfo(tea);
	}
	
	public boolean saveInfo(Document tea){
		return Document.dao.saveInfo(tea);
	}
	
	public int updateFlg(int id,int flg){
		return Document.dao.updateDocumentStatus(id, flg);
	}
}