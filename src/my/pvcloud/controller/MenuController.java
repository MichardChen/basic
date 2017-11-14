package my.pvcloud.controller;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.Log;
import my.core.model.Menu;
import my.core.model.Role;
import my.core.model.RoleMenu;
import my.core.vo.EditRoleModel;
import my.core.vo.MenuListVO;
import my.core.vo.RoleMenuVO;
import my.pvcloud.service.MenuService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;
import my.pvcloud.vo.MenuDetailVO;

@ControllerBind(key = "/menuInfo", path = "/pvcloud")
public class MenuController extends Controller {

	MenuService service = Enhancer.enhance(MenuService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		Page<Menu> list = service.queryByPage(page, size);
		ArrayList<MenuListVO> models = new ArrayList<>();
		MenuListVO model = null;
		for(Menu menu : list.getList()){
			model = new MenuListVO();
			model.setId(menu.getInt("menu_id"));
			model.setName(menu.getStr("menu_name"));
			model.setPath(menu.getStr("url"));
			model.setCreateTime(StringUtil.toString(menu.getTimestamp("create_time")));
			model.setUpdateTime(StringUtil.toString(menu.getTimestamp("update_time")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("menu.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Menu> list = service.queryByPageParams(page, size,title);
		ArrayList<MenuListVO> models = new ArrayList<>();
		MenuListVO model = null;
		for(Menu menu : list.getList()){
			model = new MenuListVO();
			model.setId(menu.getInt("menu_id"));
			model.setName(menu.getStr("menu_name"));
			model.setPath(menu.getStr("url"));
			model.setCreateTime(StringUtil.toString(menu.getTimestamp("create_time")));
			model.setUpdateTime(StringUtil.toString(menu.getTimestamp("update_time")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("menu.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("title");
		String ptitle = getPara("title");
		title = ptitle;
		
		this.setSessionAttr("title",title);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<Menu> list = service.queryByPageParams(page, size,title);
			ArrayList<MenuListVO> models = new ArrayList<>();
			MenuListVO model = null;
			for(Menu menu : list.getList()){
				model = new MenuListVO();
				model.setId(menu.getInt("menu_id"));
				model.setName(menu.getStr("menu_name"));
				model.setPath(menu.getStr("url"));
				model.setCreateTime(StringUtil.toString(menu.getTimestamp("create_time")));
				model.setUpdateTime(StringUtil.toString(menu.getTimestamp("update_time")));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("menu.jsp");
	}
	
	/**
	 *查看访问权限
	 */
	public void alter(){
		String id = getPara("id");
		Menu menu = Menu.dao.queryById(StringUtil.toInteger(id));
		MenuDetailVO model = new MenuDetailVO();
		model.setMenuId(menu.getInt("menu_id"));
		model.setName(menu.getStr("menu_name"));
		model.setPath(menu.getStr("url"));
		
		setAttr("menu", model);
		render("editMenu.jsp");
	}
	
		
	public void add(){
		render("addMenu.jsp");
	}
	

	public void saveMenu(){
		String name = StringUtil.checkCode(getPara("name"));
		String url = StringUtil.checkCode(getPara("url"));
		Menu menu = new Menu();
		menu.set("menu_name", name);
		menu.set("url", url);
		menu.set("icon", "fa-dashboard");
		menu.set("is_show", 1);
		menu.set("create_user", getSessionAttr("agentId"));
		menu.set("create_time", DateUtil.getNowTimestamp());
		menu.set("update_time", DateUtil.getNowTimestamp());
		
		boolean save = Menu.dao.saveInfo(menu);
		if(save){
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "添加菜单:"+name);
			setAttr("message","添加成功");
		}else{
			setAttr("message","添加失败");
		}
		index();
	}
	
	public void updateMenu(){
		String name = StringUtil.checkCode(getPara("name"));
		String url = StringUtil.checkCode(getPara("url"));
		int menuId = StringUtil.toInteger(getPara("menuId"));
		Menu menu = new Menu();
		menu.set("menu_id", menuId);
		menu.set("menu_name", name);
		menu.set("url", url);
		menu.set("create_user", getSessionAttr("agentId"));
		menu.set("update_time", DateUtil.getNowTimestamp());
		
		boolean save = Menu.dao.updateInfo(menu);
		if(save){
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新菜单:"+name);
			setAttr("message","修改成功");
		}else{
			setAttr("message","修改失败");
		}
		index();
	}

}
