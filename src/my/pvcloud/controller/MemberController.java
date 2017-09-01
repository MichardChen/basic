package my.pvcloud.controller;


import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.Member;
import my.core.vo.MemberVO;
import my.pvcloud.service.MemberService;
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
		
		removeSessionAttr("custInfo");
		removeSessionAttr("custValue");
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
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
			
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
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
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		Member model = service.queryById(StringUtil.toInteger(id));
		setAttr("model", model);
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
}
