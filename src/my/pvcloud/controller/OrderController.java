package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.Order;
import my.core.model.OrderItem;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
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
		Page<OrderItem> list = service.queryOrderItemByPage(page, size);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(OrderItem order : list.getList()){
			model = new OrderListVO();
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(order.getInt("wtm_item_id"));
			if(wtmItem != null){
				int wtmId = wtmItem.getInt("warehouse_tea_member_id");
				model.setPrice(wtmItem.getBigDecimal("price"));
				String sizeTypeCd = wtmItem.getStr("size_type_cd");
				CodeMst size = CodeMst.dao.queryCodestByCode(sizeTypeCd);
				if(size != null){
					model.setStock(order.getInt("quality")+size.getStr("name"));
				}else{
					model.setStock(StringUtil.toString(order.getInt("quality")));
				}
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmId);
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					Order order2 = Order.dao.queryById(order.getInt("order_id"));
					if(order2 != null){
						model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
						model.setPayTime(DateUtil.formatTimestampForDate(order2.getTimestamp("pay_time")));
						String saleUserType = order.getStr("sale_user_type");
						if(StringUtil.isBlank(saleUserType)){
							continue;
						}
						int saleId = order.getInt("sale_id");
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
						int buyId = order2.getInt("member_id");
						Member m = Member.dao.queryById(buyId);
						if(m != null){
							model.setBuyUser(m.getStr("name"));
						}
					}else{
						continue;
					}
					models.add(model);
				}else{
					continue;
				}
			}else{
				continue;
			}
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("order.jsp");
	}
	
	/**
	 * 模糊查询(分页)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
		String flg = getPara(0);
		if(StringUtil.equals(flg,"1")){
			//默认发售说明
			
		}
		Page<OrderItem> list = service.queryOrderItemByParam(page, size,title);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(OrderItem order : list.getList()){
			model = new OrderListVO();
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(order.getInt("wtm_item_id"));
			if(wtmItem != null){
				int wtmId = wtmItem.getInt("warehouse_tea_member_id");
				String sizeTypeCd = wtmItem.getStr("size_type_cd");
				CodeMst size = CodeMst.dao.queryCodestByCode(sizeTypeCd);
				model.setPrice(wtmItem.getBigDecimal("price"));
				if(size != null){
					model.setStock(order.getInt("quality")+size.getStr("name"));
				}else{
					model.setStock(StringUtil.toString(order.getInt("quality")));
				}
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmId);
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					Order order2 = Order.dao.queryById(order.getInt("order_id"));
					if(order2 != null){
						model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
						model.setPayTime(DateUtil.formatTimestampForDate(order2.getTimestamp("pay_time")));
						String saleUserType = order.getStr("sale_user_type");
						if(StringUtil.isBlank(saleUserType)){
							continue;
						}
						int saleId = order.getInt("sale_id");
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
						int buyId = order2.getInt("member_id");
						Member m = Member.dao.queryById(buyId);
						if(m != null){
							model.setBuyUser(m.getStr("name"));
						}
					}else{
						continue;
					}
					models.add(model);
				}else{
					continue;
				}
			}else{
				continue;
			}
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("order.jsp");

	}
	
	/**
	 * 模糊查询，搜索
	 */
	public void queryByConditionByPage(){
		
		String ptitle = getPara("title");
		this.setSessionAttr("title",ptitle);
		
		Integer page = getParaToInt(1);
	    if (page==null || page==0) {
	    	page = 1;
	    }
		String flg = getPara(0);
		if(StringUtil.equals(flg,"1")){
			//默认发售说明
			
		}
		Page<OrderItem> list = service.queryOrderItemByParam(page, size,ptitle);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(OrderItem order : list.getList()){
			model = new OrderListVO();
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryById(order.getInt("wtm_item_id"));
			if(wtmItem != null){
				int wtmId = wtmItem.getInt("warehouse_tea_member_id");
				String sizeTypeCd = wtmItem.getStr("size_type_cd");
				CodeMst size = CodeMst.dao.queryCodestByCode(sizeTypeCd);
				if(size != null){
					model.setStock(order.getInt("quality")+size.getStr("name"));
				}else{
					model.setStock(StringUtil.toString(order.getInt("quality")));
				}
				model.setPrice(wtmItem.getBigDecimal("price"));
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(wtmId);
				if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					Order order2 = Order.dao.queryById(order.getInt("order_id"));
					if(order2 != null){
						model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
						model.setPayTime(DateUtil.formatTimestampForDate(order2.getTimestamp("pay_time")));
						String saleUserType = order.getStr("sale_user_type");
						if(StringUtil.isBlank(saleUserType)){
							continue;
						}
						int saleId = order.getInt("sale_id");
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
						int buyId = order2.getInt("member_id");
						Member m = Member.dao.queryById(buyId);
						if(m != null){
							model.setBuyUser(m.getStr("name"));
						}
					}else{
						continue;
					}
					models.add(model);
				}else{
					continue;
				}
			}else{
				continue;
			}
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
