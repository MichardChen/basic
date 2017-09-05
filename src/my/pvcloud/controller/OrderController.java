package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.constants.Constants;
import my.core.model.Member;
import my.core.model.Order;
import my.core.model.Tea;
import my.core.model.User;
import my.core.vo.OrderListVO;
import my.pvcloud.service.OrderService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/orderInfo", path = "/pvcloud")
public class OrderController extends Controller {

	OrderService service = Enhancer.enhance(OrderService.class);
	
	int page=1;
	int size=10;
	
	public void index(){
		
		removeSessionAttr("title");
		String flg = getPara(0);
		if(StringUtil.equals(flg,"1")){
			//默认发售说明
			
		}
		Page<Order> list = service.queryByPage(page, size);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(Order order : list.getList()){
			model = new OrderListVO();
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if(tea == null){
				continue;
			}
			model.setName(tea.getStr("tea_title"));
			model.setId(order.getInt("id"));
			model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
			model.setPayTime(DateUtil.formatTimestampForDate(order.getTimestamp("pay_time")));
			String saleUserType = order.getStr("sale_user_type");
			if(StringUtil.isBlank(saleUserType)){
				continue;
			}
			int saleId = order.getInt("sale_user_id");
			if(StringUtil.equals(saleUserType, Constants.USER_TYPE.USER_TYPE_CLIENT)){
				Member member = Member.dao.queryById(saleId);
				if(member != null){
					model.setSaleUser(member.getStr("name"));
				}
			}else{
				User user = User.dao.queryById(saleId);
				if(user != null){
					model.setSaleUser(user.getStr("username"));
				}
			}
			int buyId = order.getInt("buy_user_id");
			Member m = Member.dao.queryById(buyId);
			if(m != null){
				model.setBuyUser(m.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("order.jsp");
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
        Page<Order> list = service.queryByPageParams(page, size,title);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(Order order : list.getList()){
			model = new OrderListVO();
			Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
			if(tea == null){
				continue;
			}
			model.setName(tea.getStr("tea_title"));
			model.setId(order.getInt("id"));
			model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
			model.setPayTime(DateUtil.formatTimestampForDate(order.getTimestamp("pay_time")));
			String saleUserType = order.getStr("sale_user_type");
			if(StringUtil.isBlank(saleUserType)){
				continue;
			}
			int saleId = order.getInt("sale_user_id");
			if(StringUtil.equals(saleUserType, Constants.USER_TYPE.USER_TYPE_CLIENT)){
				Member member = Member.dao.queryById(saleId);
				if(member != null){
					model.setSaleUser(member.getStr("name"));
				}
			}else{
				User user = User.dao.queryById(saleId);
				if(user != null){
					model.setSaleUser(user.getStr("username"));
				}
			}
			int buyId = order.getInt("buy_user_id");
			Member m = Member.dao.queryById(buyId);
			if(m != null){
				model.setBuyUser(m.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("order.jsp"); 
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
	        Page<Order> list = service.queryByPageParams(page, size,title);
			ArrayList<OrderListVO> models = new ArrayList<>();
			OrderListVO model = null;
			for(Order order : list.getList()){
				model = new OrderListVO();
				Tea tea = Tea.dao.queryById(order.getInt("tea_id"));
				if(tea == null){
					continue;
				}
				model.setName(tea.getStr("tea_title"));
				model.setId(order.getInt("id"));
				model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
				model.setPayTime(DateUtil.formatTimestampForDate(order.getTimestamp("pay_time")));
				String saleUserType = order.getStr("sale_user_type");
				if(StringUtil.isBlank(saleUserType)){
					continue;
				}
				int saleId = order.getInt("sale_user_id");
				if(StringUtil.equals(saleUserType, Constants.USER_TYPE.USER_TYPE_CLIENT)){
					Member member = Member.dao.queryById(saleId);
					if(member != null){
						model.setSaleUser(member.getStr("name"));
					}
				}else{
					User user = User.dao.queryById(saleId);
					if(user != null){
						model.setSaleUser(user.getStr("username"));
					}
				}
				int buyId = order.getInt("buy_user_id");
				Member m = Member.dao.queryById(buyId);
				if(m != null){
					model.setBuyUser(m.getStr("name"));
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("order.jsp"); 
	     
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		int orderId = 0;
		if(!("").equals(id) && id!=null){
			orderId = getParaToInt("id");
		}
		Order order = service.queryById(orderId);
		setAttr("model", order);
		render("orderAlter.jsp");
	}
	
	//增加
	public void addDocument(){
		render("addDocument.jsp");
	}
	
	
	
	public void editOrder(){
		Order model = service.queryById(StringUtil.toInteger(getPara("id")));
		setAttr("model", model);
		render("orderAlert.jsp");
	}
}
