package my.pvcloud.controller;

import java.math.BigDecimal;
import java.util.ArrayList;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.SaleOrder;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WareHouse;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.OrderListVO;
import my.pvcloud.service.SaleRecordService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/salerecordInfo", path = "/pvcloud")
public class SaleRecordController extends Controller {

	SaleRecordService service = Enhancer.enhance(SaleRecordService.class);
	
	int page=1;
	int size=10;
	
	public void index(){
		
		removeSessionAttr("title");
		String flg = getPara(0);
		if(StringUtil.equals(flg,"1")){
			//默认发售说明
			
		}
		Page<SaleOrder> list = service.queryByPage(page, size);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(SaleOrder order : list.getList()){
			model = new OrderListVO();
			model.setOrderNo(order.getStr("order_no"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			String size = "";
			if(sizeCodeMst != null){
				size = sizeCodeMst.getStr("name");
				model.setStock(order.getInt("quality")+sizeCodeMst.getStr("name"));
			}else{
				model.setStock(StringUtil.toString(order.getInt("quality")));
			}
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(order.getInt("wtm_item_id"));
			if(wtmItem != null){
				int cancle = wtmItem.getInt("cancle_quality") == null ? 0 : wtmItem.getInt("cancle_quality");
				int onSale = wtmItem.getInt("quality") == null ? 0 : wtmItem.getInt("quality");
				int originStock = wtmItem.getInt("origin_stock") == null ? 0 : wtmItem.getInt("origin_stock");
				model.setOnSale(StringUtil.toString(onSale)+size);
				model.setCancle(StringUtil.toString(cancle)+size);
				model.setHaveSale(StringUtil.toString(originStock-cancle-onSale)+size);
				model.setOriginStock(originStock+size);
			}
			BigDecimal amount = order.getBigDecimal("price").multiply(new BigDecimal(order.getInt("quality")));
			model.setAmount(StringUtil.toString(amount));
			CodeMst status = CodeMst.dao.queryCodestByCode(order.getStr("status"));
			if(status != null){
				model.setStatus(status.getStr("name"));
			}else{
				model.setStatus("");
			}
			if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
					if(type != null){
						model.setType(type.getStr("name"));
					}
					
					WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
					if(house != null){
						model.setWareHouse(house.getStr("warehouse_name"));
					}
					if(StringUtil.equals(Constants.USER_TYPE.USER_TYPE_CLIENT, wtm.getStr("member_type_cd"))){
						Store store = Store.dao.queryMemberStore(wtm.getInt("member_id"));
						if(store != null){
							model.setStore(store.getStr("store_name"));
							model.setMobile(store.getStr("link_phone"));
						}
					}else{
						model.setStore("平台");
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					model.setCreateTime(StringUtil.toString(order.getTimestamp("update_time")));
					String memberTypeCd = wtm.getStr("member_type_cd");
					if(StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)){
						Member member = Member.dao.queryById(wtm.getInt("member_id"));
						if(member != null){
							model.setSaleUser(member.getStr("mobile"));
						}else{
							continue;
						}
					}else{
						User user = User.dao.queryById(wtm.getInt("member_id"));
						if(user != null){
							model.setSaleUser(user.getStr("username"));
						}else{
							continue;
						}
					}
					models.add(model);
				}else{
					continue;
				}
			
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("salerecord.jsp");
	}
	
	/**
	 * 模糊查询(分页)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		String mobile=getSessionAttr("mobile");
		this.setSessionAttr("mobile",mobile);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
		String flg = getPara(0);
		if(StringUtil.equals(flg,"1")){
			//默认发售说明
			
		}
		Member m = Member.dao.queryMember(mobile);
		int userId = 0;
		if(m != null){
			userId=m.getInt("id");
		}
		Page<SaleOrder> list = service.querySaleOrderByParam(page, size,title,userId);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(SaleOrder order : list.getList()){
			model = new OrderListVO();
			model.setOrderNo(order.getStr("order_no"));
			model.setAmount(StringUtil.toString(order.getBigDecimal("item_amount")));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			String size = "";
			if(sizeCodeMst != null){
				size = sizeCodeMst.getStr("name");
				model.setStock(order.getInt("quality")+sizeCodeMst.getStr("name"));
			}else{
				model.setStock(StringUtil.toString(order.getInt("quality")));
			}
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(order.getInt("wtm_item_id"));
			if(wtmItem != null){
				int cancle = wtmItem.getInt("cancle_quality") == null ? 0 : wtmItem.getInt("cancle_quality");
				int onSale = wtmItem.getInt("quality") == null ? 0 : wtmItem.getInt("quality");
				int originStock = wtmItem.getInt("origin_stock") == null ? 0 : wtmItem.getInt("origin_stock");
				model.setOnSale(StringUtil.toString(onSale)+size);
				model.setCancle(StringUtil.toString(cancle)+size);
				model.setHaveSale(StringUtil.toString(originStock-cancle-onSale)+size);
				model.setOriginStock(originStock+size);
			}
			BigDecimal amount = order.getBigDecimal("price").multiply(new BigDecimal(order.getInt("quality")));
			model.setAmount(StringUtil.toString(amount));
			CodeMst status = CodeMst.dao.queryCodestByCode(order.getStr("status"));
			if(status != null){
				model.setStatus(status.getStr("name"));
			}else{
				model.setStatus("");
			}
			if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
					if(type != null){
						model.setType(type.getStr("name"));
					}
					
					WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
					if(house != null){
						model.setWareHouse(house.getStr("warehouse_name"));
					}
					if(StringUtil.equals(Constants.USER_TYPE.USER_TYPE_CLIENT, wtm.getStr("member_type_cd"))){
						Store store = Store.dao.queryMemberStore(wtm.getInt("member_id"));
						if(store != null){
							model.setStore(store.getStr("store_name"));
							model.setMobile(store.getStr("link_phone"));
						}
					}else{
						model.setStore("平台");
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					model.setCreateTime(StringUtil.toString(order.getTimestamp("update_time")));
					String memberTypeCd = wtm.getStr("member_type_cd");
					if(StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)){
						Member member = Member.dao.queryById(wtm.getInt("member_id"));
						if(member != null){
							model.setSaleUser(member.getStr("mobile"));
						}else{
							continue;
						}
					}else{
						User user = User.dao.queryById(wtm.getInt("member_id"));
						if(user != null){
							model.setSaleUser(user.getStr("username"));
						}else{
							continue;
						}
					}
					models.add(model);
				}else{
					continue;
				}
			
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("salerecord.jsp");
	}
	
	/**
	 * 模糊查询，搜索
	 */
	public void queryByConditionByPage(){
		
		String ptitle = getPara("title");
		this.setSessionAttr("title",ptitle);
		String pmobile = getPara("mobile");
		this.setSessionAttr("mobile",pmobile);
		
		Integer page = getParaToInt(1);
	    if (page==null || page==0) {
	    	page = 1;
	    }
		String flg = getPara(0);
		if(StringUtil.equals(flg,"1")){
			//默认发售说明
			
		}
		Member m = Member.dao.queryMember(pmobile);
		int userId = 0;
		if(m != null){
			userId=m.getInt("id");
		}
		Page<SaleOrder> list = service.querySaleOrderByParam(page, size,ptitle,userId);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(SaleOrder order : list.getList()){
			model = new OrderListVO();
			model.setOrderNo(order.getStr("order_no"));
			model.setAmount(StringUtil.toString(order.getBigDecimal("item_amount")));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			BigDecimal amount = order.getBigDecimal("price").multiply(new BigDecimal(order.getInt("quality")));
			model.setAmount(StringUtil.toString(amount));
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			String size = "";
			if(sizeCodeMst != null){
				size = sizeCodeMst.getStr("name");
				model.setStock(order.getInt("quality")+sizeCodeMst.getStr("name"));
			}else{
				model.setStock(StringUtil.toString(order.getInt("quality")));
			}
			CodeMst status = CodeMst.dao.queryCodestByCode(order.getStr("status"));
			if(status != null){
				model.setStatus(status.getStr("name"));
			}else{
				model.setStatus("");
			}
			WarehouseTeaMemberItem wtmItem = WarehouseTeaMemberItem.dao.queryByKeyId(order.getInt("wtm_item_id"));
			if(wtmItem != null){
				int cancle = wtmItem.getInt("cancle_quality") == null ? 0 : wtmItem.getInt("cancle_quality");
				int onSale = wtmItem.getInt("quality") == null ? 0 : wtmItem.getInt("quality");
				int originStock = wtmItem.getInt("origin_stock") == null ? 0 : wtmItem.getInt("origin_stock");
				model.setOnSale(StringUtil.toString(onSale)+size);
				model.setCancle(StringUtil.toString(cancle)+size);
				model.setHaveSale(StringUtil.toString(originStock-cancle-onSale)+size);
				model.setOriginStock(originStock+size);
			}
			if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					CodeMst type = CodeMst.dao.queryCodestByCode(tea.getStr("type_cd"));
					if(type != null){
						model.setType(type.getStr("name"));
					}
					
					WareHouse house = WareHouse.dao.queryById(wtm.getInt("warehouse_id"));
					if(house != null){
						model.setWareHouse(house.getStr("warehouse_name"));
					}
					if(StringUtil.equals(Constants.USER_TYPE.USER_TYPE_CLIENT, wtm.getStr("member_type_cd"))){
						Store store = Store.dao.queryMemberStore(wtm.getInt("member_id"));
						if(store != null){
							model.setStore(store.getStr("store_name"));
							model.setMobile(store.getStr("link_phone"));
						}
					}else{
						model.setStore("平台");
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					model.setCreateTime(StringUtil.toString(order.getTimestamp("update_time")));
					String memberTypeCd = wtm.getStr("member_type_cd");
					if(StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)){
						Member member = Member.dao.queryById(wtm.getInt("member_id"));
						if(member != null){
							model.setSaleUser(member.getStr("mobile"));
						}else{
							continue;
						}
					}else{
						User user = User.dao.queryById(wtm.getInt("member_id"));
						if(user != null){
							model.setSaleUser(user.getStr("username"));
						}else{
							continue;
						}
					}
					models.add(model);
				}else{
					continue;
				}
			
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("salerecord.jsp");
	        
	}
}
