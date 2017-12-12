package my.pvcloud.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.MemberStore;
import my.core.model.Message;
import my.core.model.PayRecord;
import my.core.model.Store;
import my.core.model.Tea;
import my.core.vo.MemberVO;
import my.pvcloud.model.CustInfo;
import my.pvcloud.service.MemberService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/memberInfo", path = "/pvcloud")
public class MemberController extends Controller {

	MemberService service = Enhancer.enhance(MemberService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 会员列表
	 */
	public void index(){
		
		//清除查询条件
		removeSessionAttr("cmobile");
		removeSessionAttr("cname");
		removeSessionAttr("storeName");
		removeSessionAttr("type");
		Page<Member> list = service.queryByPage(page, size);
		ArrayList<MemberVO> models = new ArrayList<>();
		MemberVO model = null;
		for(Member member : list.getList()){
			model = new MemberVO();
			model.setKeyCode(member.getStr("id_code"));
			model.setId(member.getInt("id"));
			model.setMobile(member.getStr("mobile"));
			model.setName(member.getStr("nick_name"));
			model.setUserName(member.getStr("name"));
			CodeMst roleMst = CodeMst.dao.queryCodestByCode(member.getStr("role_cd"));
			if(roleMst != null){
				model.setRole(roleMst.getStr("name"));
			}
			model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
			//查询用户已提现金额和提现中的金额
			BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
			model.setApplingMoneys(StringUtil.toString(applying));
			BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
			model.setApplyedMoneys(StringUtil.toString(applySuccess));
			BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
			model.setRechargeMoneys(StringUtil.toString(paySuccess));
			MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(model.getId());
			if(memberBankcard == null){
				model.setBankStatus("暂未绑定银行卡");
			}else{
				String bankStatus = memberBankcard.getStr("status");
				CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(bankStatus);
				if(sCodeMst != null){
					model.setBankStatus(sCodeMst.getStr("name"));
				}
			}
			
			model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
			model.setSex(member.getInt("sex")==1?"男":"女");
			
			Store store1 = Store.dao.queryMemberStore(member.getInt("id"));
			if(store1 != null){
				model.setStoreId(store1.getInt("id"));
				model.setOpenStore(1);
			}else{
				model.setOpenStore(0);
			}
			
			
			
			Store store = Store.dao.queryById(member.getInt("store_id"));
			if(store != null){
				model.setStore(store.getStr("store_name"));
			}else{
				model.setStore("");
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("member.jsp");
	}
	
	/**
	 * 模糊查询条件分页
	 */
	public void queryByConditionByPage(){
			
		String cmobile = getSessionAttr("cmobile");
		String cname = getSessionAttr("cname");
		String storeName = getSessionAttr("storeName");
		String ctype = getSessionAttr("type");
		
		String mobile = getPara("mobile");
		cmobile = mobile;
		this.setSessionAttr("cmobile",cmobile);
		
		String name = getPara("cname");
		cname = name;
		this.setSessionAttr("cname",cname);
		
		String type = getPara("type");
		ctype = type;
		this.setSessionAttr("type",ctype);
		
		String storeNames = getPara("storeName");
		storeName = storeNames;
		this.setSessionAttr("storeName",storeName);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        Page<Member> list = service.queryMemberListByPage(page, size,mobile,name,storeName,ctype);
			ArrayList<MemberVO> models = new ArrayList<>();
			MemberVO model = null;
			for(Member member : list.getList()){
				model = new MemberVO();
				model.setId(member.getInt("id"));
				model.setKeyCode(member.getStr("id_code"));
				model.setMobile(member.getStr("mobile"));
				model.setName(member.getStr("nick_name"));
				model.setUserName(member.getStr("name"));
				model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
				model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
				model.setSex(member.getInt("sex")==1?"男":"女");
				CodeMst roleMst = CodeMst.dao.queryCodestByCode(member.getStr("role_cd"));
				if(roleMst != null){
					model.setRole(roleMst.getStr("name"));
				}
				//查询用户已提现金额和提现中的金额
				BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
				model.setApplingMoneys(StringUtil.toString(applying));
				BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
				model.setApplyedMoneys(StringUtil.toString(applySuccess));
				BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
				model.setRechargeMoneys(StringUtil.toString(paySuccess));
				
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(model.getId());
				if(memberBankcard == null){
					model.setBankStatus("暂未绑定银行卡");
				}else{
					String bankStatus = memberBankcard.getStr("status");
					CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(bankStatus);
					if(sCodeMst != null){
						model.setBankStatus(sCodeMst.getStr("name"));
					}
				}
				
				Store store1 = Store.dao.queryMemberStore(member.getInt("id"));
				if(store1 != null){
					model.setStoreId(store1.getInt("id"));
					model.setOpenStore(1);
				}else{
					model.setOpenStore(0);
				}
				
				
				Store store = Store.dao.queryById(member.getInt("store_id"));
				if(store != null){
					model.setStore(store.getStr("store_name"));
				}else{
					model.setStore("");
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("member.jsp");
	}
	
	/**
	 * 模糊查询底部页码分页
	 */
	public void queryByPage(){
		try {
			
			String cmobile=getSessionAttr("cmobile");
			this.setSessionAttr("cmobile",cmobile);
			
			String cname=getSessionAttr("cname");
			this.setSessionAttr("cname",cname);
			
			String storeName=getSessionAttr("storeName");
			this.setSessionAttr("storeName",storeName);
			
			String ctype=getSessionAttr("type");
			this.setSessionAttr("type",ctype);
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<Member> list = service.queryMemberListByPage(page, size,cmobile,cname,storeName,ctype);
			ArrayList<MemberVO> models = new ArrayList<>();
			MemberVO model = null;
			for(Member member : list.getList()){
				model = new MemberVO();
				model.setId(member.getInt("id"));
				model.setMobile(member.getStr("mobile"));
				model.setKeyCode(member.getStr("id_code"));
				model.setName(member.getStr("nick_name"));
				model.setUserName(member.getStr("name"));
				CodeMst roleMst = CodeMst.dao.queryCodestByCode(member.getStr("role_cd"));
				if(roleMst != null){
					model.setRole(roleMst.getStr("name"));
				}
				model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
				model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
				model.setSex(member.getInt("sex")==1?"男":"女");
				//查询用户已提现金额和提现中的金额
				BigDecimal applying = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.APPLYING);
				model.setApplingMoneys(StringUtil.toString(applying));
				BigDecimal applySuccess = BankCardRecord.dao.sumApplying(model.getId(), Constants.BANK_MANU_TYPE_CD.WITHDRAW, Constants.WITHDRAW_STATUS.SUCCESS);
				model.setApplyedMoneys(StringUtil.toString(applySuccess));
				BigDecimal paySuccess = PayRecord.dao.sumPay(model.getId(), Constants.PAY_TYPE_CD.ALI_PAY, Constants.PAY_STATUS.TRADE_SUCCESS);
				model.setRechargeMoneys(StringUtil.toString(paySuccess));
				
				MemberBankcard memberBankcard = MemberBankcard.dao.queryByMemberId(model.getId());
				if(memberBankcard == null){
					model.setBankStatus("暂未绑定银行卡");
				}else{
					String bankStatus = memberBankcard.getStr("status");
					CodeMst sCodeMst = CodeMst.dao.queryCodestByCode(bankStatus);
					if(sCodeMst != null){
						model.setBankStatus(sCodeMst.getStr("name"));
					}
				}
				
				Store store1 = Store.dao.queryMemberStore(member.getInt("id"));
				if(store1 != null){
					model.setStoreId(store1.getInt("id"));
					model.setOpenStore(1);
				}else{
					model.setOpenStore(0);
				}
				
				Store store = Store.dao.queryById(member.getInt("store_id"));
				if(store != null){
					model.setStore(store.getStr("store_name"));
				}else{
					model.setStore("");
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("member.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		int id = StringUtil.toInteger(getPara("id"));
		Member model = service.queryById(id);
		setAttr("model", model);
		//查询银行卡
		MemberBankcard bankCard = MemberBankcard.dao.queryByMemberId(id);
		if((bankCard != null)&&(StringUtil.isBlank(bankCard.getStr("card_img")))){
			bankCard.set("card_img", "#");
		}
		if(bankCard != null){
			String bankCd = bankCard.getStr("bank_name_cd");
			CodeMst bank = CodeMst.dao.queryCodestByCode(bankCd);
			if(bank != null){
				bankCard.set("bank_name_cd", bank.getStr("name"));
			}
			setAttr("bankCard", bankCard);
		}
		
		Store store = Store.dao.queryById(model.getInt("store_id"));
		if(store != null){
			setAttr("store", store.getStr("store_name"));
		}else{
			setAttr("store", "");
		}
		Store openStore = Store.dao.queryMemberStore(id);
		if(openStore != null){
			setAttr("openStore", openStore.getStr("store_name"));
		}else{
			setAttr("openStore", "");
		}
		render("memberAlert.jsp");
	}
	
	public void see(){
		int id = StringUtil.toInteger(getPara("id"));
		Member model = service.queryById(id);
		setAttr("model", model);
		//查询银行卡
		MemberBankcard bankCard = MemberBankcard.dao.queryByMemberId(id);
		if((bankCard !=null)&&(StringUtil.isBlank(bankCard.getStr("card_img")))){
			bankCard.set("card_img", "#");
		}
		if(bankCard != null){
			String bankCd = bankCard.getStr("bank_name_cd");
			CodeMst bank = CodeMst.dao.queryCodestByCode(bankCd);
			if(bank != null){
				bankCard.set("bank_name_cd", bank.getStr("name"));
			}
			setAttr("bankCard", bankCard);
		}
		
		Store store = Store.dao.queryById(model.getInt("store_id"));
		if(store != null){
			setAttr("store", store.getStr("store_name"));
		}else{
			setAttr("store", "");
		}
		Store openStore = Store.dao.queryMemberStore(id);
		if(openStore != null){
			setAttr("openStore", openStore.getStr("store_name"));
		}else{
			setAttr("openStore", "");
		}
		render("memberseeAlert.jsp");
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int id = StringUtil.toInteger(getPara("id"));
			if(id==0){
				setAttr("message", "用户还没有绑定门店");
			}else{
				int ret = service.updateStatus(id, getPara("status"));
				if(ret==0){
					setAttr("message", "修改成功");
				}else{
					setAttr("message", "修改失败");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
	
	/**
	 * 更新用户
	 */
	public void updateMember(){
		int id = StringUtil.toInteger(getPara("id"));
		if(id==0){
			setAttr("message", "用户数据不存在");
		}else{
			String mobile = StringUtil.checkCode(getPara("mobile"));
			String name = StringUtil.checkCode(getPara("name"));
			String statusString = StringUtil.checkCode(getPara("status"));
			Member member = new Member();
			member.set("id", id);
			member.set("nick_name", name);
			member.set("status", statusString);
			member.set("name", StringUtil.checkCode(getPara("userName")));
			boolean ret = Member.dao.updateInfo(member);
			if(ret){
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新用户"+mobile+"的信息");
				setAttr("message", "保存成功");
			}else{
				setAttr("message", "保存失败");
			}
			index();
		}
	}
	
	public void updateStatus(){
		int id = StringUtil.toInteger(getPara("id"));
		if(id==0){
			setAttr("message", "用户还未绑定银行卡，无法审核");
		}else{
			String status = StringUtil.checkCode(getPara("status"));
			MemberBankcard member = new MemberBankcard();
			member.set("id", id);
			member.set("status", status);
			member.set("update_time", DateUtil.getNowTimestamp());
			
			//消息
			String stStr = "";
			if(StringUtil.equals(status, "240002")){
				stStr = "您的银行卡已审核通过";
			}
			if(StringUtil.equals(status, "240003")){
				stStr = "您的银行卡审核未通过，请重新提交";
			}
		
			int userId = 0;
			MemberBankcard mbc = MemberBankcard.dao.queryById(id);
			if(mbc != null){
				userId = mbc.getInt("member_id") == null ? 0 : mbc.getInt("member_id");
			}
			
			/*Message message = new Message();
			message.set("message_type_cd", Constants.MESSAGE_TYPE.BANK_REVIEW_MSG);
			message.set("message",stStr);
			message.set("title","绑定银行卡审核");
			message.set("params", "{id:"+id+"}");
			message.set("create_time", DateUtil.getNowTimestamp());
			message.set("update_time", DateUtil.getNowTimestamp());
			message.set("user_id", userId);
			boolean messageSave = Message.dao.saveInfo(message);*/
			
			boolean ret = MemberBankcard.dao.updateInfo(member);
			if(ret){
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新用户id:"+id+"的银行卡状态");
				setAttr("message", "保存成功");
			}else{
				setAttr("message", "保存失败");
			}
		}
		index();
	}
}
