package my.pvcloud.controller;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.Menu;
import my.core.model.Role;
import my.core.model.RoleMenu;
import my.core.vo.EditRoleModel;
import my.core.vo.MenuListVO;
import my.core.vo.RoleMenuVO;
import my.pvcloud.service.MenuService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

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
		Menu role = Menu.dao.queryById(StringUtil.toInteger(id));
		EditRoleModel model = new EditRoleModel();
		model.setId(role.getInt("role_id"));
		model.setRoleName(role.getStr("role_name"));
		List<RoleMenu> list = RoleMenu.dao.queryByRoleId(role.getInt("role_id"));
		List<RoleMenuVO> vlist = new ArrayList<>();
		RoleMenuVO vo = null;
		for(RoleMenu rm : list){
			vo = new RoleMenuVO();
			vo.setId(rm.getInt("role_menu_id"));
			Menu menu = Menu.dao.queryById(rm.getInt("menu_id"));
			if(menu != null){
				vo.setPath(menu.getStr("menu_name"));
				if(StringUtil.isBlank(menu.getStr("url"))){
					continue;
				}
			}else{
				continue;
			}
			vlist.add(vo);
		}
		List<Menu> menus = Menu.dao.queryAllMenu();
		setAttr("menus", menus);
		setAttr("role", model);
		setAttr("menu", vlist);
		render("editRole.jsp");
	}
	
	//保存角色
	public void saveRole(){
		String roleName = getPara("name");
		int roleId = StringUtil.toInteger(getPara("roleId"));
		Role role = new Role();
		role.set("role_id", roleId);
		role.set("role_name", roleName);
		boolean save = Role.dao.updateInfo(role);
		if(save){
			setAttr("message","修改成功");
		}else{
			setAttr("message","修改失败");
		}
		index();
	}
	
	//删除角色对应的权限
	public void deleteRole(){
		int roleId = StringUtil.toInteger(getPara("id"));
		boolean deleteFlg = RoleMenu.dao.deleteById(roleId);
		if(deleteFlg){
			setAttr("message","删除成功");
		}else{
			setAttr("message","删除失败");
		}
		index();
	}
	
	public void addAuth(){
		int roleId = StringUtil.toInteger(getPara("roleId"));
		int menuId = StringUtil.toInteger(getPara("menuId"));
		RoleMenu rm = RoleMenu.dao.queryByRoleMenuId(roleId,menuId);
		if(rm != null){
			setAttr("message","此角色已添加此权限，无需再次添加");
		}else{
			RoleMenu rm1 = new RoleMenu();
			rm1.set("role_id", roleId);
			rm1.set("menu_id", menuId);
			boolean save = RoleMenu.dao.saveInfo(rm1);
			if(save){
				setAttr("message","添加成功");
			}else{
				setAttr("message","添加失败");
			}
		}
		index();
	}
	
	public void add(){
		render("addMenu.jsp");
	}
	

	public void saveMenu(){
		String name = getPara("name");
		String url = getPara("url");
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
			setAttr("message","添加成功");
		}else{
			setAttr("message","添加失败");
		}
		index();
	}
}
