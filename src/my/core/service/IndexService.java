package my.core.service;

import java.util.ArrayList;
import java.util.List;

import my.core.model.Menu;
import my.core.model.User;
import my.core.model.UserMenu;
import my.core.tx.TxProxy;
import my.core.tx.TxService;
import my.pvcloud.vo.MenuNav;


import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

public class IndexService {

	public static final IndexService service = TxProxy.newProxy(IndexService.class);
	public User getProfile(String userName){
		return User.dao.getRoleByUserId(userName);
	}
	
	public List<Menu> getMenuList(int userId){
		return Menu.dao.getMenuByUserId(userId);
	}
	public List<Menu> getUserMenuByUserId(int userId)
	{
		return Menu.dao.getUserMenuByUserId(userId);
	}
	
	
	public List<MenuNav> getAllMenuNav()
	{
		 List<Menu> menus=Menu.dao.getMenu();
		 //List<Menu> usermenus=getMenuList(userId);
		 List<MenuNav> menNavs=getRootMenuNav(menus);
		 if(menNavs!=null && menNavs.size()>0)
		 {
			 for(int i=menNavs.size()-1;i>=0;i--)
			 {
				 MenuNav item=menNavs.get(i);
				 curvMenuNa(item,menus);
			 }
			 
		 }
		 return menNavs;
	}
	private void curvMenuNa(MenuNav parentNav,List<Menu> menus)
	{
		List<MenuNav> menNavs=new ArrayList<MenuNav>();
		//for(int i=menus.size()-1;i>=0;i--)
		if(menus!=null && menus.size()>0)
		{
			for(int i=menus.size()-1;i>0;i--)
			{
				Menu item=menus.get(i);
				if(item.get("p_menu_id")!=null && item.getLong("p_menu_id")==parentNav.getMenuId())
				{
					MenuNav nva=new MenuNav();
					nva.setMenuId(item.getLong("menu_id"));
					nva.setMenuName(item.getStr("menu_name"));
					nva.setParentId(item.getLong("p_menu_id"));
					menus.remove(i);
					//curvMenuNa(nva,menus);
					menNavs.add(nva);
					//ischild=true;
				}
			}
		}
		parentNav.setChildMenu(menNavs);
	}
	
	private List<MenuNav> getRootMenuNav(List<Menu> menus)
	{
		List<MenuNav> menNavs=new ArrayList<MenuNav>();
		for(int i=menus.size()-1;i>=0;i--)
		{
			Menu item=menus.get(i);
			if(item.get("p_menu_id")==null ||item.get("p_menu_id").toString().equals(""))
			{
				MenuNav nva=new MenuNav();
				nva.setMenuId(item.getLong("menu_id"));
				nva.setMenuName(item.getStr("menu_name"));
				nva.setParentId(0);
				menNavs.add(nva);
				menus.remove(i);
			}
		}
		return menNavs;
	}
	@TxService
	public void updateUserMenu(String userId,String[] menuIds)
	{
		UserMenu dao=UserMenu.dao;
		dao.deleteUserMenuByuserId(userId);
		for(int i=0;i<menuIds.length;i++)
		{
			UserMenu model=new UserMenu();
			model.set("user_id",userId);
			model.set("menu_id", menuIds[i]);
			model.save();
			
		}
		
	}
}
