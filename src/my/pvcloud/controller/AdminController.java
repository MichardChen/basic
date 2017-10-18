package my.pvcloud.controller;


import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.Role;
import my.core.model.RoleMenu;
import my.core.model.User;
import my.core.model.UserMenu;
import my.core.model.UserRole;
import my.core.vo.MemberVO;
import my.pvcloud.service.AdminService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.MD5Util;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/adminInfo", path = "/pvcloud")
public class AdminController extends Controller {

	AdminService service = Enhancer.enhance(AdminService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 会员列表
	 */
	public void index(){
		
		//清除查询条件
		removeSessionAttr("cmobile");
		Page<User> list = service.queryByPage(page, size);
		ArrayList<MemberVO> models = new ArrayList<>();
		MemberVO model = null;
		for(User member : list.getList()){
			model = new MemberVO();
			model.setId(member.getInt("user_id"));
			model.setMobile(member.getStr("mobile"));
			model.setName(member.getStr("username"));
			model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
			model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("admin.jsp");
	}
	
	/**
	 * 模糊查询条件分页
	 */
	public void queryByConditionByPage(){
			
		String cmobile = getSessionAttr("cmobile");
		
		String mobile = getPara("mobile");
		cmobile = mobile;
		
		this.setSessionAttr("cmobile",cmobile);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        removeSessionAttr("cmobile");
			Page<User> list = service.queryByPage(page, size);
			ArrayList<MemberVO> models = new ArrayList<>();
			MemberVO model = null;
			for(User member : list.getList()){
				model = new MemberVO();
				model.setId(member.getInt("user_id"));
				model.setMobile(member.getStr("mobile"));
				model.setName(member.getStr("username"));
				model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
				model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("admin.jsp");
	}
	
	/**
	 * 模糊查询底部页码分页
	 */
	public void queryByPage(){
		try {
			
			String cmobile=getSessionAttr("cmobile");
			this.setSessionAttr("cmobile",cmobile);
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        removeSessionAttr("cmobile");
			Page<User> list = service.queryByPage(page, size);
			ArrayList<MemberVO> models = new ArrayList<>();
			MemberVO model = null;
			for(User member : list.getList()){
				model = new MemberVO();
				model.setId(member.getInt("user_id"));
				model.setMobile(member.getStr("mobile"));
				model.setName(member.getStr("username"));
				model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
				model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("admin.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *修改弹窗
	 */
	public void alter(){
		int id = StringUtil.toInteger(getPara("id"));
		User model = service.queryById(id);
		UserRole uRole = UserRole.dao.queryUserRoleByUserId(id);
		if(uRole != null){
			setAttr("roleId", uRole.getInt("role_id"));
		}
		List<Role> roles = Role.dao.queryAll();
		setAttr("roles", roles);
		setAttr("model", model);
		setAttr("userId", id);
		//查询银行卡
		render("adminAlert.jsp");
	}
	
	/**
	 *新增弹窗
	 */
	public void add(){
		//查询所有角色
		List<Role> roles = Role.dao.queryAll();
		setAttr("roles", roles);
		render("addAdmin.jsp");
	}
	
	/**
	 * 更新用户
	 */
	@Transient
	public void updateAdmin(){
		int id = getParaToInt("id");
		String name = getPara("name");
		BigDecimal moneys = StringUtil.toBigDecimal(getPara("moneys"));
		UserRole ur = UserRole.dao.queryUserRoleByUserId(id);
		int roleId = StringUtil.toInteger(getPara("roleId"));
		Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新管理员:"+name+"id:"+id+"的数据");
		if(ur == null){
			setAttr("message", "用户数据不存在");
		}else{
			User user = new User();
			user.set("user_id", id);
			user.set("username", name);
			user.set("password", MD5Util.string2MD5(getPara("password")));
			user.set("create_user", getSessionAttr("agentId"));
			user.set("update_time", DateUtil.getNowTimestamp());
			user.set("moneys", moneys);
			boolean ret = User.dao.updateInfo(user);
			if(ret){
				//更新角色
				int oldRoleId = ur.getInt("role_id");
				if(oldRoleId != roleId){
					//更新角色
					UserRole uRole = new UserRole();
					uRole.set("user_role_id", ur.getInt("user_role_id"));
					uRole.set("user_id", id);
					uRole.set("role_id", roleId);
					uRole.set("update_time", DateUtil.getNowTimestamp());
					boolean update = UserRole.dao.updateInfo(uRole);
					if(update){
						setAttr("message", "保存成功");
						//菜单
						int delete = UserMenu.dao.deleteUserMenuByuserId(id);
						if(delete != 0){
							List<RoleMenu> roleMenus = RoleMenu.dao.queryByRoleId(roleId);
							for(RoleMenu rm : roleMenus){
								int menuId = rm.getInt("menu_id");
								UserMenu uMenu = new UserMenu();
								uMenu.set("user_id", id);
								uMenu.set("menu_id", menuId);
								uMenu.set("create_time", DateUtil.getNowTimestamp());
								uMenu.set("update_time", DateUtil.getNowTimestamp());
								boolean save = UserMenu.dao.saveInfo(uMenu);
								if(save){
									setAttr("message", "保存成功");
								}else{
									setAttr("message", "保存失败");
								}
							}
						}
					}else{
						setAttr("message", "保存失败");
					}
				}
			}else{
				setAttr("message", "保存失败");
			}
		}
		index();
	}
	
	//添加用户
	@Transient
	public void addAdmin(){
		String mobile = getPara("mobile");
		String name = getPara("name");
		String password = getPara("password");
		BigDecimal moneys = StringUtil.toBigDecimal(getPara("moneys"));
		int roleId = StringUtil.toInteger(getPara("roleId"));
		int createUser = getSessionAttr("agentId");
		User checkUser = User.dao.queryUser(mobile);
		if(checkUser != null){
			setAttr("message", "保存失败，此账号已经存在");
		}
		User checkUser1 = User.dao.getUserByUserName(name);
		if(checkUser1 != null){
			setAttr("message", "保存失败，此用户名已经存在");
		}
		
		User user = new User();
		user.set("username", name);
		user.set("password", MD5Util.string2MD5(password));
		user.set("mobile", mobile);
		user.set("create_user", createUser);
		user.set("effective_mark", 1);
		user.set("create_time", DateUtil.getNowTimestamp());
		user.set("update_time", DateUtil.getNowTimestamp());
		user.set("moneys", moneys);
		int userId = User.dao.saveInfos(user);
		Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "添加管理员:"+name+"id:"+userId+"的数据");
		if(userId != 0){
			//保存用户对于角色
			UserRole ur = new UserRole();
			ur.set("user_id", userId);
			ur.set("role_id", roleId);
			ur.set("create_time", DateUtil.getNowTimestamp());
			ur.set("update_time", DateUtil.getNowTimestamp());
			boolean save = UserRole.dao.saveInfo(ur);
			if(save){
				//保存用户对于的menu
				List<RoleMenu> rms = RoleMenu.dao.queryByRoleId(roleId);
				for(RoleMenu rm : rms){
					UserMenu um = new UserMenu();
					um.set("user_id", userId);
					um.set("menu_id", rm.getInt("menu_id"));
					um.set("create_time", DateUtil.getNowTimestamp());
					um.set("update_time", DateUtil.getNowTimestamp());
					UserMenu.dao.saveInfo(um);
				}
			}else{
				setAttr("message", "保存失败");
			}
		}else{
			setAttr("message", "保存失败");
		}
		setAttr("message", "保存成功");
		index();
	}
}
