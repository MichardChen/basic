package my.pvcloud.service;

import my.core.model.FeedBack;
import my.core.model.Menu;

import com.jfinal.plugin.activerecord.Page;

public class MenuService {

	public Page<Menu> queryByPage(int page,int size){
		return Menu.dao.queryByPage(page, size);
	}
	
	public Page<Menu> queryByPageParams(int page,int size,String name){
		return Menu.dao.queryMenuListByPage(page, size,name);
	}
	
	public Menu queryById(int id){
		return Menu.dao.queryById(id);
	}
	
	public boolean updateInfo(Menu data){
		return Menu.dao.updateInfo(data);
	}
	
	public boolean saveInfo(Menu data){
		return Menu.dao.saveInfo(data);
	}
}
