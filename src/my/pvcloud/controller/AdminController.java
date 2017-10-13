package my.pvcloud.controller;


import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

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
		Page<Member> custInfoList = new Page<Member>(null, 0, 0, 0, 0);
		
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
	public void updateMember(){
		int id = getParaToInt("id");
		String mobile = getPara("mobile");
		String name = getPara("name");
		BigDecimal moneys = StringUtil.toBigDecimal(getPara("moneys"));
		String statusString = getPara("status");
		Member member = new Member();
		member.set("id", id);
		member.set("mobile", mobile);
		member.set("name", name);
		member.set("moneys", moneys);
		member.set("status", statusString);
		boolean ret = Member.dao.updateInfo(member);
		if(ret){
			setAttr("message", "保存成功");
		}else{
			setAttr("message", "保存失败");
		}
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
