package my.core.controller;

import java.util.ArrayList;
import java.util.List;

import my.core.model.Menu;
import my.core.model.User;
import my.core.service.IndexService;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

@ControllerBind(key = "/homepage", path = "/platform")
public class IndexController extends Controller {

	IndexService service = Enhancer.enhance(IndexService.class);

	public void index() {
		render("index.jsp");
	}
	/**
	 * 获取菜单列表
	 */
	public void nav(){
		String userName=getSessionAttr("userName");
		User user = service.getProfile(userName);
		//User user =User.dao.getUserByUserName(userName);
		int userId=user.get("user_id");
		int isAdmin=user.get("isadmin") !=null ? user.getInt("isadmin") :0;
		List<Menu> list=new ArrayList<Menu>();
		if(isAdmin==1){	
			//管理员获取所有菜单列表
			list = service.getMenuList(userId);
		}else{
			//获取部分菜单列表
			list=service.queryUserMenuByUserId(userId);
		}
		user.put("menu", list);
		renderJson(user);
	}
	
	
	
}
