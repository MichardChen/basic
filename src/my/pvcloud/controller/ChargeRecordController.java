package my.pvcloud.controller;

import java.util.ArrayList;

import my.core.model.CodeMst;
import my.core.model.Member;
import my.core.model.PayRecord;
import my.pvcloud.service.ChargeService;
import my.pvcloud.util.StringUtil;
import my.pvcloud.vo.PayRecordListVO;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@ControllerBind(key = "/chargeInfo", path = "/pvcloud")
public class ChargeRecordController extends Controller {

	ChargeService service = Enhancer.enhance(ChargeService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		removeSessionAttr("mobile");
		Page<PayRecord> list = service.queryByPage(page, size);
		ArrayList<PayRecordListVO> models = new ArrayList<>();
		PayRecordListVO model = null;
		for(PayRecord record : list.getList()){
			model = new PayRecordListVO();
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setId(record.getInt("id"));
			int memberId = record.getInt("member_id") == null ? 0 : record.getInt("member_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setUserName(member.getStr("name"));
				model.setMobile(member.getStr("mobile"));
			}
			model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			if(status != null){
				model.setStatus(status.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("charge.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
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
        Page<PayRecord> list = service.queryByPageParams(page, size,title,mobile);
		ArrayList<PayRecordListVO> models = new ArrayList<>();
		PayRecordListVO model = null;
		for(PayRecord record : list.getList()){
			model = new PayRecordListVO();
			model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
			model.setId(record.getInt("id"));
			int memberId = record.getInt("member_id") == null ? 0 : record.getInt("member_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setUserName(member.getStr("name"));
				model.setMobile(member.getStr("mobile"));
			}
			model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
			CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
			if(status != null){
				model.setStatus(status.getStr("name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("charge.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("title");
		String ptitle = getPara("title");
		title = ptitle;
		this.setSessionAttr("title",title);
		
		String mobile = getSessionAttr("mobile");
		String pmobile = getPara("mobile");
		mobile = pmobile;
		
		this.setSessionAttr("mobile",mobile);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<PayRecord> list = service.queryByPageParams(page, size,title,mobile);
			ArrayList<PayRecordListVO> models = new ArrayList<>();
			PayRecordListVO model = null;
			for(PayRecord record : list.getList()){
				model = new PayRecordListVO();
				model.setCreateTime(StringUtil.toString(record.getTimestamp("create_time")));
				model.setId(record.getInt("id"));
				int memberId = record.getInt("member_id") == null ? 0 : record.getInt("member_id");
				Member member = Member.dao.queryById(memberId);
				if(member != null){
					model.setUserName(member.getStr("name"));
					model.setMobile(member.getStr("mobile"));
				}
				model.setMoneys(StringUtil.toString(record.getBigDecimal("moneys")));
				CodeMst status = CodeMst.dao.queryCodestByCode(record.getStr("status"));
				if(status != null){
					model.setStatus(status.getStr("name"));
				}
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("charge.jsp");
	}
}
