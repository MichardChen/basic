package my.pvcloud.service;

import my.core.model.CodeMst;

import com.jfinal.plugin.activerecord.Page;

public class CodemstService {

	public Page<CodeMst> queryByPage(int page,int size){
		return CodeMst.dao.queryByPage(page, size);
	}
	
	public Page<CodeMst> queryByPageParams(int page,int size,String name,String pcode){
		return CodeMst.dao.queryLogByPage(page, size,name,pcode);
	}
}
