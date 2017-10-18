package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.constants.Constants;
import my.core.model.BankCardRecord;
import my.core.model.CodeMst;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.Store;
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
				model.setName(member.getStr("name"));
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
			}
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
				model.setName(member.getStr("name"));
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				model.setMobile(member.getStr("mobile"));
			}
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
					model.setName(member.getStr("name"));
					model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
					model.setMobile(member.getStr("mobile"));
				}
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
				setAttr("message", "操作成功");
			}else{
				setAttr("message", "操作失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
