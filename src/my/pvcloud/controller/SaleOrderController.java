package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.Tea;
import my.core.model.User;
import my.core.model.WarehouseTeaMember;
import my.core.model.WarehouseTeaMemberItem;
import my.core.vo.OrderListVO;
import my.pvcloud.service.SaleOrderService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/saleorderInfo", path = "/pvcloud")
public class SaleOrderController extends Controller {

	SaleOrderService service = Enhancer.enhance(SaleOrderService.class);
	
	int page=1;
	int size=10;
	
	public void index(){
		
		removeSessionAttr("title");
		String flg = getPara(0);
		if(StringUtil.equals(flg,"1")){
			//默认发售说明
			
		}
		Page<WarehouseTeaMemberItem> list = service.queryWtmItemByPage(page, size);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(WarehouseTeaMemberItem order : list.getList()){
			model = new OrderListVO();
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			if(sizeCodeMst != null){
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
			if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
					String memberTypeCd = wtm.getStr("member_type_cd");
					if(StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)){
						Member member = Member.dao.queryById(wtm.getInt("member_id"));
						if(member != null){
							model.setSaleUser(member.getStr("name"));
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
		render("saleorder.jsp");
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
		Page<WarehouseTeaMemberItem> list = service.queryWtmItemByParam(page, size,title);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(WarehouseTeaMemberItem order : list.getList()){
			model = new OrderListVO();
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			if(sizeCodeMst != null){
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
			if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
					String memberTypeCd = wtm.getStr("member_type_cd");
					if(StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)){
						Member member = Member.dao.queryById(wtm.getInt("member_id"));
						if(member != null){
							model.setSaleUser(member.getStr("name"));
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
		render("saleorder.jsp");
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
		Page<WarehouseTeaMemberItem> list = service.queryWtmItemByParam(page, size,ptitle);
		ArrayList<OrderListVO> models = new ArrayList<>();
		OrderListVO model = null;
		for(WarehouseTeaMemberItem order : list.getList()){
			model = new OrderListVO();
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(order.getInt("warehouse_tea_member_id"));
			model.setPrice(order.getBigDecimal("price"));
			String sizeTypeCd = order.getStr("size_type_cd");
			CodeMst sizeCodeMst = CodeMst.dao.queryCodestByCode(sizeTypeCd);
			if(sizeCodeMst != null){
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
			
			if(wtm != null){
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea == null){
						continue;
					}
					model.setName(tea.getStr("tea_title"));
					model.setId(order.getInt("id"));
					model.setCreateTime(DateUtil.formatTimestampForDate(order.getTimestamp("create_time")));
					String memberTypeCd = wtm.getStr("member_type_cd");
					if(StringUtil.equals(memberTypeCd, Constants.USER_TYPE.USER_TYPE_CLIENT)){
						Member member = Member.dao.queryById(wtm.getInt("member_id"));
						if(member != null){
							model.setSaleUser(member.getStr("name"));
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
		render("saleorder.jsp");
	        
	}
}
