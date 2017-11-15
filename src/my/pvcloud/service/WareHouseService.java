package my.pvcloud.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.sun.org.apache.regexp.internal.recompile;

import my.core.model.WareHouse;

public class WareHouseService {

	public Page<WareHouse> queryByPage(int page,int size){
		return WareHouse.dao.queryByPage(page, size);
	}
	
	public Page<WareHouse> queryByPageParams(int page,int size,String title){
		return WareHouse.dao.queryByPageParams(page, size,title);
	}
	
	public WareHouse queryById(int dataId){
		return WareHouse.dao.queryById(dataId);
	}
	
	public boolean updateInfo(WareHouse data){
		return WareHouse.dao.updateInfo(data);
	}
	
	public boolean saveInfo(WareHouse data){
		return WareHouse.dao.saveInfo(data);
	}
	
	public int updateFlg(int id,int flg,int updateUserId){
		return WareHouse.dao.updateWareHouseStatus(id, flg,updateUserId);
	}
	
	public List<WareHouse> queryAllHouse(){
		return WareHouse.dao.queryAllHouse();
	}
}
