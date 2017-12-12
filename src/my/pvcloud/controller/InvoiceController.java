package my.pvcloud.controller;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.sun.org.apache.bcel.internal.classfile.Code;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.GetTeaRecord;
import my.core.model.Invoice;
import my.core.model.Member;
import my.core.model.Tea;
import my.core.vo.AdminInvoiceListModel;
import my.pvcloud.model.GetTeaRecordModel;
import my.pvcloud.service.InvoiceService;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/invoiceInfo", path = "/pvcloud")
public class InvoiceController extends Controller {

	InvoiceService service = Enhancer.enhance(InvoiceService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		removeSessionAttr("mobile");
		removeSessionAttr("status");
		Page<Invoice> list = service.queryByPage(page, size);
		ArrayList<AdminInvoiceListModel> models = new ArrayList<>();
		AdminInvoiceListModel model = null;
		for(Invoice data : list.getList()){
			model = new AdminInvoiceListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			int memberId = data.getInt("user_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setUserName(member.getStr("nick_name"));
				model.setUserMobile(member.getStr("mobile"));
			}
			
			model.setMark(data.getStr("mark"));
			model.setTaxNo(data.getStr("tax_no"));
			CodeMst typeMst = CodeMst.dao.queryCodestByCode(data.getStr("title_type_cd"));
			if(typeMst != null){
				model.setType(typeMst.getStr("name"));
			}
			model.setTitle(data.getStr("title"));
			model.setMoneys(StringUtil.toString(data.getBigDecimal("moneys")));
			CodeMst status = CodeMst.dao.queryCodestByCode(data.getStr("status"));
			if(status != null){
				model.setStatus(status.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("invoice.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		String mobile = getSessionAttr("mobile");
		this.setSessionAttr("mobile", mobile);
		
		String status = getSessionAttr("status");
		this.setSessionAttr("status", status);
		
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<Invoice> list = service.queryByPageParams(page, size,mobile,title,status);
		ArrayList<AdminInvoiceListModel> models = new ArrayList<>();
		AdminInvoiceListModel model = null;
		for(Invoice data : list.getList()){
			model = new AdminInvoiceListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			int memberId = data.getInt("user_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setUserName(member.getStr("nick_name"));
				model.setUserMobile(member.getStr("mobile"));
			}
			
			model.setMark(data.getStr("mark"));
			model.setTaxNo(data.getStr("tax_no"));
			CodeMst typeMst = CodeMst.dao.queryCodestByCode(data.getStr("title_type_cd"));
			if(typeMst != null){
				model.setType(typeMst.getStr("name"));
			}
			model.setTitle(data.getStr("title"));
			model.setMoneys(StringUtil.toString(data.getBigDecimal("moneys")));
			CodeMst st = CodeMst.dao.queryCodestByCode(data.getStr("status"));
			if(st != null){
				model.setStatus(st.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("invoice.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("title");
		String ptitle = getPara("title");
		title = ptitle;
		
		String mobile = getSessionAttr("mobile");
		String pmobile = getPara("mobile");
		mobile = pmobile;
		
		String status = getSessionAttr("status");
		String pstatus = getPara("status");
		status = pstatus;
		
		this.setSessionAttr("title",title);
		this.setSessionAttr("status",status);
		this.setSessionAttr("mobile", mobile);
		Integer page = getParaToInt(1);
		if (page==null || page==0) {
			page = 1;
		}
		    
		Page<Invoice> list = service.queryByPageParams(page, size, mobile, title,status);
		ArrayList<AdminInvoiceListModel> models = new ArrayList<>();
		AdminInvoiceListModel model = null;
		for(Invoice data : list.getList()){
			model = new AdminInvoiceListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			int memberId = data.getInt("user_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setUserName(member.getStr("nick_name"));
				model.setUserMobile(member.getStr("mobile"));
			}
			model.setMark(data.getStr("mark"));
			model.setTaxNo(data.getStr("tax_no"));
			CodeMst typeMst = CodeMst.dao.queryCodestByCode(data.getStr("title_type_cd"));
			if(typeMst != null){
				model.setType(typeMst.getStr("name"));
			}
			model.setTitle(data.getStr("title"));
			model.setMoneys(StringUtil.toString(data.getBigDecimal("moneys")));
			CodeMst st = CodeMst.dao.queryCodestByCode(data.getStr("status"));
			if(st != null){
				model.setStatus(st.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("invoice.jsp");
	}
	
	public void editInt(){
		int id = StringUtil.toInteger(getPara("id"));
		Invoice record = Invoice.dao.queryInvoiceById(id);
		setAttr("model", record);
		//List<CodeMst> express = CodeMst.dao.queryCodestByPcode(Constants.EXPRESS.EXPRESS);
		//setAttr("express", express);
		render("editinvoice.jsp");
	}
}
