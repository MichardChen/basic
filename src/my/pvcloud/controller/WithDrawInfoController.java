package my.pvcloud.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.CashJournal;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.Store;
import my.core.model.User;
import my.pvcloud.model.BankRecordModel;
import my.pvcloud.model.StoreModel;
import my.pvcloud.service.WithDrawService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/withdrawInfo", path = "/pvcloud")
public class WithDrawInfoController extends Controller {

	WithDrawService service = Enhancer.enhance(WithDrawService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("time");
		removeSessionAttr("status");
		removeSessionAttr("mobile");
		Page<BankCardRecord> list = service.queryByPage(page, size);
		ArrayList<BankRecordModel> models = new ArrayList<>();
		BankRecordModel model = null;
		for(BankCardRecord record : list.getList()){
			model = new BankRecordModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if(member != null){
				model.setMemberId(member.getInt("id"));
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(member.getInt("id"));
				if(memberBankcard != null){
					model.setName(memberBankcard.getStr("owner_name"));
				}
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
			}
			model.setBalance(StringUtil.toString(record.getBigDecimal("balance")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			model.setStatus(status==null?"":status.getStr("name"));
			model.setStatusCd(record.getStr("status"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("withdraw.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String time=getSessionAttr("time");
		this.setSessionAttr("time",time);
		String s=getSessionAttr("status");
		this.setSessionAttr("status",s);
		String mobile=getSessionAttr("mobile");
		this.setSessionAttr("mobile",mobile);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<BankCardRecord> list = service.queryByPageParams(page, size,time,s,mobile);
		ArrayList<BankRecordModel> models = new ArrayList<>();
		BankRecordModel model = null;
		for(BankCardRecord record : list.getList()){
			model = new BankRecordModel();
			model.setId(record.getInt("id"));
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			Member member = Member.dao.queryById(record.getInt("member_id"));
			if(member != null){
				model.setMemberId(member.getInt("id"));
				model.setName(member.getStr("mobile"));
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(member.getInt("id"));
				if(memberBankcard != null){
					model.setName(memberBankcard.getStr("owner_name"));
				}
			}
			model.setBalance(StringUtil.toString(record.getBigDecimal("balance")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			model.setStatus(status==null?"":status.getStr("name"));
			model.setStatusCd(record.getStr("status"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("withdraw.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("time");
		String ptitle = getPara("time");
		String s = getSessionAttr("status");
		String st = getPara("status");
		title = ptitle;
		
		//String mobile = getSessionAttr("mobile");
		String mobile = getPara("mobile");
		
		this.setSessionAttr("time",title);
		this.setSessionAttr("status",st);
		this.setSessionAttr("mobile", mobile);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<BankCardRecord> list = service.queryByPageParams(page, size,title,st,mobile);
			ArrayList<BankRecordModel> models = new ArrayList<>();
			BankRecordModel model = null;
			for(BankCardRecord record : list.getList()){
				model = new BankRecordModel();
				model.setId(record.getInt("id"));
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
				Member member = Member.dao.queryById(record.getInt("member_id"));
				if(member != null){
					model.setMemberId(member.getInt("id"));
					model.setName(member.getStr("mobile"));
					model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
					model.setMobile(member.getStr("mobile"));
					MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(member.getInt("id"));
					if(memberBankcard != null){
						model.setName(memberBankcard.getStr("owner_name"));
					}
				}
				model.setBalance(StringUtil.toString(record.getBigDecimal("balance")));
				CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
				model.setStatus(status==null?"":status.getStr("name"));
				model.setStatusCd(record.getStr("status"));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("withdraw.jsp");
	}
	
	
	/**
	 * 更新
	 */
	public void update(){
		try{
			int id = getParaToInt("id");
			String flg = getPara("status");
			int ret = service.updateFlg(id, flg);
			if(ret!=0){
				CodeMst status = CodeMst.dao.queryCodestByCode(flg);
				if(status != null){
					Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "处理提现申请id:"+id+","+status.getStr("name"));
				}
				if(StringUtil.equals(flg, Constants.WITHDRAW_STATUS.FAIL)){
					//失败，要把金额还回到账号
					BankCardRecord bankCardRecord = BankCardRecord.dao.queryById(id);
					if(bankCardRecord != null){
						Member member = Member.dao.queryById(bankCardRecord.getInt("member_id"));
						BigDecimal moneys = bankCardRecord.getBigDecimal("moneys");
						if(moneys != null){
							int updateFlg = Member.dao.updateCharge(bankCardRecord.getInt("member_id"), moneys);
							if(updateFlg != 0){
								//提现失败
								CashJournal cash = new CashJournal();
								cash.set("cash_journal_no", StringUtil.getOrderNo());
								cash.set("member_id", bankCardRecord.getInt("member_id"));
								cash.set("pi_type", Constants.PI_TYPE.GET_CASH);
								cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_FAIL);
								cash.set("occur_date", new Date());
								cash.set("act_rev_amount", moneys);
								cash.set("act_pay_amount", moneys);
								cash.set("opening_balance", member.getBigDecimal("moneys"));
								cash.set("closing_balance", member.getBigDecimal("moneys").add(moneys));
								cash.set("remarks", "申请提现"+moneys+"，后台审核失败");
								User admin = User.dao.queryById((Integer)getSessionAttr("agentId"));
								if(admin != null){
									cash.set("create_by", admin.getInt("user_id"));
									cash.set("update_by", admin.getInt("user_id"));
								}
								cash.set("create_time", DateUtil.getNowTimestamp());
								cash.set("update_time", DateUtil.getNowTimestamp());
								CashJournal.dao.saveInfo(cash);
								setAttr("message", "操作成功");
							}else{
								setAttr("message", "操作失败");
							}
						}else{
							setAttr("message", "操作失败");
						}
					}else{
						setAttr("message", "操作失败");
					}
				}
				
				if(StringUtil.equals(flg, Constants.WITHDRAW_STATUS.SUCCESS)){
					//后台审核提现成功
					BankCardRecord bankCardRecord = BankCardRecord.dao.queryById(id);
					if(bankCardRecord != null){
						Member member = Member.dao.queryById(bankCardRecord.getInt("member_id"));
						BigDecimal moneys = bankCardRecord.getBigDecimal("moneys");
						if(moneys != null){
								//提现失败
								CashJournal cash = new CashJournal();
								cash.set("cash_journal_no", StringUtil.getOrderNo());
								cash.set("member_id", bankCardRecord.getInt("member_id"));
								cash.set("pi_type", Constants.PI_TYPE.GET_CASH);
								cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLY_SUCCESS);
								cash.set("occur_date", new Date());
								cash.set("act_rev_amount", moneys);
								cash.set("act_pay_amount", moneys);
								cash.set("opening_balance", member.getBigDecimal("moneys"));
								cash.set("closing_balance", member.getBigDecimal("moneys"));
								cash.set("remarks", "申请提现"+moneys+"，后台审核成功");
								User admin = User.dao.queryById((Integer)getSessionAttr("agentId"));
								if(admin != null){
									cash.set("create_by", admin.getInt("user_id"));
									cash.set("update_by", admin.getInt("user_id"));
								}
								cash.set("create_time", DateUtil.getNowTimestamp());
								cash.set("update_time", DateUtil.getNowTimestamp());
								CashJournal.dao.saveInfo(cash);
								setAttr("message", "操作成功");
							}else{
								setAttr("message", "操作失败");
							}
					}else{
						setAttr("message", "操作失败");
					}
				}
			}else{
				setAttr("message", "操作失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
