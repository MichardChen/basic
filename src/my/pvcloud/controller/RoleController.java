package my.pvcloud.controller;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.CodeMst;
import my.core.model.Document;
import my.core.model.FeedBack;
import my.core.model.Member;
import my.core.model.Menu;
import my.core.model.Role;
import my.core.model.RoleMenu;
import my.core.model.UserMenu;
import my.core.model.WareHouse;
import my.core.vo.EditRoleModel;
import my.core.vo.MenuListVO;
import my.core.vo.RoleListVO;
import my.core.vo.RoleMenuVO;
import my.pvcloud.model.DocumentModel;
import my.pvcloud.model.FeedBackModel;
import my.pvcloud.service.FeedBackService;
import my.pvcloud.service.MenuService;
import my.pvcloud.service.RoleService;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/roleInfo", path = "/pvcloud")
public class RoleController extends Controller {

	RoleService service = Enhancer.enhance(RoleService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		Page<Role> list = service.queryByPage(page, size);
		ArrayList<RoleListVO> models = new ArrayList<>();
		RoleListVO model = null;
		for(Role menu : list.getList()){
			model = new RoleListVO();
			model.setId(menu.getInt("role_id"));
			model.setName(menu.getStr("role_name"));
			RoleMenu roleMenu = RoleMenu.dao.queryById(menu.getInt("role_id"));
			if(roleMenu != null){
				Menu menu2 = Menu.dao.queryById(roleMenu.getInt("role_id"));
				if(menu2 != null){
					model.setPath(menu2.getStr("url"));
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("role.jsp");
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
        Page<Role> list = service.queryByPage(page, size);
		ArrayList<RoleListVO> models = new ArrayList<>();
		RoleListVO model = null;
		for(Role menu : list.getList()){
			model = new RoleListVO();
			model.setId(menu.getInt("role_id"));
			model.setName(menu.getStr("role_name"));
			RoleMenu roleMenu = RoleMenu.dao.queryById(menu.getInt("role_id"));
			if(roleMenu != null){
				Menu menu2 = Menu.dao.queryById(roleMenu.getInt("role_id"));
				if(menu2 != null){
					model.setPath(menu2.getStr("url"));
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("role.jsp");
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
	        
	        Page<Role> list = service.queryByPage(page, size);
			ArrayList<RoleListVO> models = new ArrayList<>();
			RoleListVO model = null;
			for(Role menu : list.getList()){
				model = new RoleListVO();
				model.setId(menu.getInt("role_id"));
				model.setName(menu.getStr("role_name"));
				RoleMenu roleMenu = RoleMenu.dao.queryById(menu.getInt("role_id"));
				if(roleMenu != null){
					Menu menu2 = Menu.dao.queryById(roleMenu.getInt("role_id"));
					if(menu2 != null){
						model.setPath(menu2.getStr("url"));
					}
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("role.jsp");
	}
	
	/**
	 *查看访问权限
	 */
	public void alter(){
		String id = getPara("id");
		Role role = Role.dao.queryById(StringUtil.toInteger(id));
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
	@Transient
	public void deleteRole(){
		int roleId = StringUtil.toInteger(getPara("id"));
		RoleMenu rMenu = RoleMenu.dao.queryById(roleId);
		int rId = rMenu.getInt("role_id");
		int mId = rMenu.getInt("menu_id");
		boolean deleteFlg = RoleMenu.dao.deleteById(roleId);
		if(deleteFlg){
			//删除用户菜单
			int ret = UserMenu.dao.deleteUserMenuByMenuId(mId);
			if(ret != 0){
				setAttr("message","删除成功");
			}else{
				setAttr("message","删除失败");
			}
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
	
	public void addAlert(){
		render("addRole.jsp");
	}
	

	public void addRole(){
		String name = getPara("name");
		Role role = new Role();
		role.set("role_name", name);
		int max = StringUtil.toInteger(Role.dao.queryMaxCode());
		role.set("role_code", max+1);
		boolean save = Role.dao.saveInfo(role);
		if(save){
			setAttr("message","添加成功");
		}else{
			setAttr("message","添加失败");
		}
		index();
	}
}
