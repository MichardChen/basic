package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.model.BankCardRecord;
import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.PayRecord;
import my.core.vo.RechargeListVO;
import my.pvcloud.model.BankRecordModel;
import my.pvcloud.service.RechargeService;
import my.pvcloud.service.WithDrawService;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/rechargeInfo", path = "/pvcloud")
public class RechargeController extends Controller {

	RechargeService service = Enhancer.enhance(RechargeService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("time");
		removeSessionAttr("status");
		removeSessionAttr("mobile");
		Page<PayRecord> list = service.queryByPage(page, size);
		ArrayList<RechargeListVO> models = new ArrayList<>();
		RechargeListVO model = null;
		for(PayRecord record : list.getList()){
			model = new RechargeListVO();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if(member != null){
				model.setName(member.getStr("name"));
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
			}
			model.setTradeNo(record.getStr("trade_no"));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			model.setStatus(status==null?"":status.getStr("name"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("payrecord.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String time=getSessionAttr("time");
		this.setSessionAttr("time",time);
		String mobile=getSessionAttr("mobile");
		this.setSessionAttr("mobile",mobile);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
		Page<PayRecord> list = service.queryByPageParams(page, size,time,mobile);
		ArrayList<RechargeListVO> models = new ArrayList<>();
		RechargeListVO model = null;
		for(PayRecord record : list.getList()){
			model = new RechargeListVO();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if(member != null){
				model.setName(member.getStr("name"));
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
			}
			model.setTradeNo(record.getStr("trade_no"));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			model.setStatus(status==null?"":status.getStr("name"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("payrecord.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("time");
		String ptitle = getPara("time");
		title = ptitle;
		
		//String mobile = getSessionAttr("mobile");
		String mobile = getPara("mobile");
		
		this.setSessionAttr("time",title);
		this.setSessionAttr("mobile", mobile);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
			Page<PayRecord> list = service.queryByPageParams(page, size,title,mobile);
			ArrayList<RechargeListVO> models = new ArrayList<>();
			RechargeListVO model = null;
			for(PayRecord record : list.getList()){
				model = new RechargeListVO();
				model.setId(record.getInt("id"));
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
				Member member = Member.dao.queryById(record.getInt("member_id"));
				if(member != null){
					model.setName(member.getStr("name"));
					model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
					model.setMobile(member.getStr("mobile"));
				}
				model.setTradeNo(record.getStr("trade_no"));
				CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
				model.setStatus(status==null?"":status.getStr("name"));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("payrecord.jsp");
	}
}
