package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.model.CashJournal;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.pvcloud.model.CashListModel;
import my.pvcloud.service.CashJournalService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/cashJournalInfo", path = "/pvcloud")
public class CashJournalController extends Controller {

	CashJournalService service = Enhancer.enhance(CashJournalService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("status");
		removeSessionAttr("type");
		removeSessionAttr("time");
		Page<CashJournal> list = service.queryByPage(page, size);
		ArrayList<CashListModel> models = new ArrayList<>();
		CashListModel model = null;
		for(CashJournal record : list.getList()){
			model = new CashListModel();
			model.setClosingBalance(StringUtil.toString(record.getBigDecimal("closing_balance")));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setOccurDate(DateUtil.format(record.getDate("occur_date")));
			Member member = Member.dao.queryById(record.getInt("member_id")==null?0:record.getInt("member_id"));
			if(member != null){
				model.setCreateBy(record.getStr("name"));
				model.setMemberName(member.getStr("name"));
			}
			CodeMst feeStatus = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
			if(feeStatus != null){
				model.setFeeStatus(feeStatus.getStr("name"));
			}
			//
			CodeMst piType = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
			if(piType != null){
				model.setPiType(piType.getStr("name"));
			}
			model.setOpeningBalance(StringUtil.toString(record.getBigDecimal("opening_balance")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("cash.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String s=getSessionAttr("status");
		this.setSessionAttr("status",s);
		String type=getSessionAttr("type");
		this.setSessionAttr("type",type);
		String time=getSessionAttr("time");
		this.setSessionAttr("time",time);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<CashJournal> list = service.queryByPageParams(page, size,type,s,time);
		ArrayList<CashListModel> models = new ArrayList<>();
		CashListModel model = null;
		for(CashJournal record : list.getList()){
			model = new CashListModel();
			model.setClosingBalance(StringUtil.toString(record.getBigDecimal("closing_balance")));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setOccurDate(DateUtil.format(record.getDate("occur_date")));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if(member != null){
				model.setCreateBy(record.getStr("name"));
				model.setMemberName(member.getStr("name"));
			}
			CodeMst feeStatus = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
			if(feeStatus != null){
				model.setFeeStatus(feeStatus.getStr("name"));
			}
			CodeMst piType = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
			if(piType != null){
				model.setPiType(piType.getStr("name"));
			}
			model.setOpeningBalance(StringUtil.toString(record.getBigDecimal("opening_balance")));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("cash.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String status = getSessionAttr("status");
		String pstatus = getPara("status");
		status = pstatus;
		this.setSessionAttr("status",status);
		
		String type = getSessionAttr("type");
		String ptype = getPara("type");
		type = ptype;
		this.setSessionAttr("type",type);
		
		String time = getSessionAttr("time");
		String ptime = getPara("time");
		time = ptime;
		this.setSessionAttr("time",time);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<CashJournal> list = service.queryByPageParams(page, size,status,type,time);
			ArrayList<CashListModel> models = new ArrayList<>();
			CashListModel model = null;
			for(CashJournal record : list.getList()){
				model = new CashListModel();
				model.setClosingBalance(StringUtil.toString(record.getBigDecimal("closing_balance")));
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
				model.setOccurDate(DateUtil.format(record.getDate("occur_date")));
				Member member = Member.dao.queryById(record.getInt("member_id"));
				if(member != null){
					model.setCreateBy(record.getStr("name"));
					model.setMemberName(member.getStr("name"));
				}
				CodeMst feeStatus = CodeMst.dao.queryCodestByCode(record.getStr("fee_status"));
				if(feeStatus != null){
					model.setFeeStatus(feeStatus.getStr("name"));
				}
				CodeMst piType = CodeMst.dao.queryCodestByCode(record.getStr("pi_type"));
				if(piType != null){
					model.setPiType(piType.getStr("name"));
				}
				model.setOpeningBalance(StringUtil.toString(record.getBigDecimal("opening_balance")));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("cash.jsp");
	}
	
	
	/**
	 * 更新
	 */
	public void update(){
		
	}
}