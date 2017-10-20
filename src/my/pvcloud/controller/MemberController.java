package my.pvcloud.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.MemberBankcard;
import my.core.model.MemberStore;
import my.core.model.Store;
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
		Page<Member> list = service.queryByPage(page, size);
		ArrayList<MemberVO> models = new ArrayList<>();
		MemberVO model = null;
		for(Member member : list.getList()){
			model = new MemberVO();
			model.setId(member.getInt("id"));
			model.setMobile(member.getStr("mobile"));
			model.setName(member.getStr("name"));
			model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
			model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
			model.setSex(member.getInt("sex")==1?"男":"女");
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
		Page<Member> custInfoList = new Page<Member>(null, 0, 0, 0, 0);
		
		String mobile = getPara("mobile");
		cmobile = mobile;
		
		this.setSessionAttr("cmobile",cmobile);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        Page<Member> list = service.queryMemberListByPage(page, size,mobile);
			ArrayList<MemberVO> models = new ArrayList<>();
			MemberVO model = null;
			for(Member member : list.getList()){
				model = new MemberVO();
				model.setId(member.getInt("id"));
				model.setMobile(member.getStr("mobile"));
				model.setName(member.getStr("name"));
				model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
				model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
				model.setSex(member.getInt("sex")==1?"男":"女");
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
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<Member> list = service.queryMemberListByPage(page, size,cmobile);
			ArrayList<MemberVO> models = new ArrayList<>();
			MemberVO model = null;
			for(Member member : list.getList()){
				model = new MemberVO();
				model.setId(member.getInt("id"));
				model.setMobile(member.getStr("mobile"));
				model.setName(member.getStr("name"));
				model.setCreateTime(StringUtil.toString(member.getTimestamp("create_time")));
				model.setMoneys(StringUtil.toString(member.getBigDecimal("moneys")));
				model.setSex(member.getInt("sex")==1?"男":"女");
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
		setAttr("bankCard", bankCard);
		Store store = Store.dao.queryById(model.getInt("store_id"));
		if(store != null){
			setAttr("store", store.getStr("store_name"));
		}else{
			setAttr("store", "");
		}
		render("memberAlert.jsp");
	}
	
	/**
	 * 删除
	 */
	public void del(){
		try{
			int id = getParaToInt("id");
			int ret = service.updateStatus(id, getPara("status"));
			if(ret==0){
				setAttr("message", "修改成功");
			}else{
				setAttr("message", "修改失败");
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
		int id = getParaToInt("id");
		String mobile = StringUtil.checkCode(getPara("mobile"));
		String name = StringUtil.checkCode(getPara("name"));
		String statusString = StringUtil.checkCode(getPara("status"));
		Member member = new Member();
		member.set("id", id);
		member.set("name", name);
		member.set("status", statusString);
		boolean ret = Member.dao.updateInfo(member);
		if(ret){
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新用户"+mobile+"的信息");
			setAttr("message", "保存成功");
		}else{
			setAttr("message", "保存失败");
		}
	}
	
	public void updateStatus(){
		int id = getParaToInt("id");
		String status = StringUtil.checkCode(getPara("status"));
		MemberBankcard member = new MemberBankcard();
		member.set("id", id);
		member.set("status", status);
		member.set("update_time", DateUtil.getNowTimestamp());
		boolean ret = MemberBankcard.dao.updateInfo(member);
		if(ret){
			Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "更新用户id:"+id+"的银行卡状态");
			setAttr("message", "保存成功");
		}else{
			setAttr("message", "保存失败");
		}
		index();
	}
}
