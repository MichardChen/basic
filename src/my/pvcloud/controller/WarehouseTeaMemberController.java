package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.Order;
import my.core.model.OrderItem;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.OrderListVO;
import my.pvcloud.model.WarehouseTeaMemberVO;
import my.pvcloud.service.OrderService;
import my.pvcloud.service.WarehouseTeaMemberService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/wtmInfo", path = "/pvcloud")
public class WarehouseTeaMemberController extends Controller {

	WarehouseTeaMemberService service = Enhancer.enhance(WarehouseTeaMemberService.class);
	
	int page=1;
	int size=10;
	
	public void index(){
		
		removeSessionAttr("date");
		removeSessionAttr("saleMobile");
		removeSessionAttr("saleUserTypeCd");
		removeSessionAttr("tea");
		
		Page<WarehouseTeaMember> list = service.queryByPage(page, size);
		ArrayList<WarehouseTeaMemberVO> models = new ArrayList<>();
		WarehouseTeaMemberVO model = null;
		for(WarehouseTeaMember order : list.getList()){
			model = new WarehouseTeaMemberVO();
			model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if(tea != null){
				model.setTeaName(tea.getStr("tea_title"));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if(type != null){
					model.setType(type.getStr("name"));
				}
			}
			model.setStock(StringUtil.toString(order.getInt("stock"))+"片");
			WareHouse wareHouse = WareHouse.dao.queryById(order.getInt("warehouse_id"));
			if(wareHouse != null){
				model.setWarehouse(wareHouse.getStr("warehouse_name"));
			}
			if(StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
				User user = User.dao.queryById(order.getInt("member_id"));
				if(user != null){
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("username"));
					model.setSaleUserType("平台");
				}
			}
			if(StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)){
				Member user = Member.dao.queryById(order.getInt("member_id"));
				if(user != null){
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("name"));
					model.setSaleUserType("用户");
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("wtm.jsp");
	}
	
	/**
	 * 模糊查询(分页)
	 */
	public void queryByPage(){
		
		String pdate = getPara("date");
		this.setSessionAttr("date",pdate);
		
		String psaleMobile = getPara("saleMobile");
		this.setSessionAttr("saleMobile",psaleMobile);

		String psaleUserTypeCd = getPara("saleUserTypeCd");
		this.setSessionAttr("saleUserTypeCd",psaleUserTypeCd);
		
		String ptea = getPara("tea");
		this.setSessionAttr("tea",ptea);
		
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
	
		Page<WarehouseTeaMember> list = service.queryWarehouseTeaMemberByParam(page, size, pdate, psaleMobile, psaleUserTypeCd,ptea);
		ArrayList<WarehouseTeaMemberVO> models = new ArrayList<>();
		WarehouseTeaMemberVO model = null;
		for(WarehouseTeaMember order : list.getList()){
			model = new WarehouseTeaMemberVO();
			model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if(tea != null){
				model.setTeaName(tea.getStr("tea_title"));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if(type != null){
					model.setType(type.getStr("name"));
				}
			}
			model.setStock(StringUtil.toString(order.getInt("stock"))+"片");
			WareHouse wareHouse = WareHouse.dao.queryById(order.getInt("warehouse_id"));
			if(wareHouse != null){
				model.setWarehouse(wareHouse.getStr("warehouse_name"));
			}
			if(StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
				User user = User.dao.queryById(order.getInt("member_id"));
				if(user != null){
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("username"));
					model.setSaleUserType("平台");
				}
			}
			if(StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)){
				Member user = Member.dao.queryById(order.getInt("member_id"));
				if(user != null){
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("name"));
					model.setSaleUserType("用户");
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("wtm.jsp");
	}
	
	/**
	 * 模糊查询，搜索
	 */
	public void queryByConditionByPage(){
		
		String pdate = getPara("date");
		this.setSessionAttr("date",pdate);
		
		String ptea = getPara("tea");
		this.setSessionAttr("tea",ptea);
		
		String psaleMobile = getPara("saleMobile");
		this.setSessionAttr("saleMobile",psaleMobile);

		String psaleUserTypeCd = getPara("saleUserTypeCd");
		this.setSessionAttr("saleUserTypeCd",psaleUserTypeCd);
		
		Integer page = getParaToInt(1);
	    if (page==null || page==0) {
	    	page = 1;
	    }
		
		Page<WarehouseTeaMember> list = service.queryWarehouseTeaMemberByParam(page, size, pdate, psaleMobile, psaleUserTypeCd,ptea);
		ArrayList<WarehouseTeaMemberVO> models = new ArrayList<>();
		WarehouseTeaMemberVO model = null;
		for(WarehouseTeaMember order : list.getList()){
			model = new WarehouseTeaMemberVO();
			model.setCreateTime(StringUtil.toString(order.getTimestamp("create_time")));
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if(tea != null){
				model.setTeaName(tea.getStr("tea_title"));
				CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
				if(type != null){
					model.setType(type.getStr("name"));
				}
			}
			model.setStock(StringUtil.toString(order.getInt("stock"))+"片");
			WareHouse wareHouse = WareHouse.dao.queryById(order.getInt("warehouse_id"));
			if(wareHouse != null){
				model.setWarehouse(wareHouse.getStr("warehouse_name"));
			}
			if(StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.PLATFORM_USER)){
				User user = User.dao.queryById(order.getInt("member_id"));
				if(user != null){
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("username"));
					model.setSaleUserType("平台");
				}
			}
			if(StringUtil.equals(order.getStr("member_type_cd"), Constants.USER_TYPE.USER_TYPE_CLIENT)){
				Member user = Member.dao.queryById(order.getInt("member_id"));
				if(user != null){
					model.setMobile(user.getStr("mobile"));
					model.setSaleUser(user.getStr("name"));
					model.setSaleUserType("用户");
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("wtm.jsp");
	}
}
