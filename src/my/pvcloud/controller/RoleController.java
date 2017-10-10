package my.pvcloud.controller;

import java.util.ArrayList;

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
import my.core.vo.MenuListVO;
import my.core.vo.RoleListVO;
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
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		//Menu model = service.queryById(StringUtil.toInteger(id));
		//setAttr("model", model);
		//render("feedbackAlter.jsp");
	}
}
