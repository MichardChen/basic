package my.pvcloud.controller;


import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.ServiceFee;
import my.core.model.Tea;
import my.core.model.WarehouseTeaMember;
import my.pvcloud.service.ServiceFeeService;
import my.pvcloud.util.StringUtil;
import my.pvcloud.vo.ServiceFeeListModel;

@ControllerBind(key = "/servicefeeInfo", path = "/pvcloud")
public class ServiceFeeController extends Controller {

	ServiceFeeService service = Enhancer.enhance(ServiceFeeService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 仓库列表
	 */
	public void index(){
		
		removeSessionAttr("mobile");
		removeSessionAttr("time");
		Page<ServiceFee> list = service.queryByPage(page, size);
		ArrayList<ServiceFeeListModel> models = new ArrayList<>();
		ServiceFeeListModel model = null;
		for(ServiceFee fee : list.getList()){
			model = new ServiceFeeListModel();
			model.setId(fee.getInt("id"));
			model.setMark(fee.getStr("mark"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(fee.getInt("wtm_id"));
			if(wtm != null){
				Member member = Member.dao.queryById(wtm.getInt("member_id"));
				if(member != null){
					model.setMobile(member.getStr("mobile"));
					model.setUserName(member.getStr("name"));
				}
				Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
				if(tea != null){
					model.setTea(tea.getStr("tea_title"));
				}
			}
			
			model.setCreateTime(StringUtil.toString(fee.getTimestamp("create_time")));
			model.setFee(StringUtil.toString(fee.getBigDecimal("service_fee")));
			String unit = "";
			CodeMst unitMst = CodeMst.dao.queryCodestByCode(fee.getStr("size_type_cd"));
			if(unit != null){
				unit = unitMst.getStr("name");
			}
			model.setPrice(StringUtil.toString(fee.getBigDecimal("price"))+"元/"+unit);
			model.setQuanlity(StringUtil.toString(fee.getInt("quality"))+unit);
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("servicefee.jsp");
	}
	
	public void queryByPage(){
		String mobile = getSessionAttr("mobile");
		String pmobile = getPara("mobile");
		mobile = pmobile;
		this.setSessionAttr("mobile", mobile);
		
		String time = getSessionAttr("time");
		String ptime = getPara("time");
		time = ptime;
		this.setSessionAttr("time", time);
		
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        int userId = 0;
        if(StringUtil.isNoneBlank(mobile)){
        	Member member = Member.dao.queryMember(mobile);
        	if(member != null){
        		userId = member.getInt("id");
        	}
        }
        Page<ServiceFee> list = service.queryByPageParams(page, size,userId,time,mobile);
		ArrayList<ServiceFeeListModel> models = new ArrayList<>();
		ServiceFeeListModel model = null;
		for(ServiceFee fee : list.getList()){
			model = new ServiceFeeListModel();
			model.setId(fee.getInt("id"));
			model.setMark(fee.getStr("mark"));
			WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(fee.getInt("wtm_id"));
			if(wtm != null){
				Member member = Member.dao.queryById(wtm.getInt("member_id"));
				if(member != null){
					model.setMobile(member.getStr("mobile"));
					model.setUserName(member.getStr("name"));
				}
				Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
				if(tea != null){
					model.setTea(tea.getStr("tea_title"));
				}
			}
			
			model.setCreateTime(StringUtil.toString(fee.getTimestamp("create_time")));
			model.setFee(StringUtil.toString(fee.getBigDecimal("service_fee")));
			String unit = "";
			CodeMst unitMst = CodeMst.dao.queryCodestByCode(fee.getStr("size_type_cd"));
			if(unit != null){
				unit = unitMst.getStr("name");
			}
			model.setPrice(StringUtil.toString(fee.getBigDecimal("price"))+"元/"+unit);
			model.setQuanlity(StringUtil.toString(fee.getInt("quality"))+unit);
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("servicefee.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
			
		
			String mobile=getSessionAttr("mobile");
			this.setSessionAttr("mobile",mobile);
			String time=getSessionAttr("time");
			this.setSessionAttr("time",time);
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        int userId = 0;
	        if(StringUtil.isNoneBlank(mobile)){
	        	Member member = Member.dao.queryMember(mobile);
	        	if(member != null){
	        		userId = member.getInt("id");
	        	}
	        }
	        
	        Page<ServiceFee> list = service.queryByPageParams(page, size,userId,time,mobile);
			ArrayList<ServiceFeeListModel> models = new ArrayList<>();
			ServiceFeeListModel model = null;
			for(ServiceFee fee : list.getList()){
				model = new ServiceFeeListModel();
				model.setId(fee.getInt("id"));
				model.setMark(fee.getStr("mark"));
				WarehouseTeaMember wtm = WarehouseTeaMember.dao.queryById(fee.getInt("wtm_id"));
				if(wtm != null){
					Member member = Member.dao.queryById(wtm.getInt("member_id"));
					if(member != null){
						model.setMobile(member.getStr("mobile"));
						model.setUserName(member.getStr("name"));
					}
					Tea tea = Tea.dao.queryById(wtm.getInt("tea_id"));
					if(tea != null){
						model.setTea(tea.getStr("tea_title"));
					}
				}
				
				model.setCreateTime(StringUtil.toString(fee.getTimestamp("create_time")));
				model.setFee(StringUtil.toString(fee.getBigDecimal("service_fee")));
				String unit = "";
				CodeMst unitMst = CodeMst.dao.queryCodestByCode(fee.getStr("size_type_cd"));
				if(unit != null){
					unit = unitMst.getStr("name");
				}
				model.setPrice(StringUtil.toString(fee.getBigDecimal("price"))+"元/"+unit);
				model.setQuanlity(StringUtil.toString(fee.getInt("quality"))+unit);
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("servicefee.jsp");
	}
}
