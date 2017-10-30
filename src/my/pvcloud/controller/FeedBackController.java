package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.Document;
import my.core.model.FeedBack;
import my.core.model.Log;
import my.core.model.Member;
import my.core.model.User;
import my.pvcloud.model.DocumentModel;
import my.pvcloud.model.FeedBackModel;
import my.pvcloud.service.FeedBackService;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/feedbackInfo", path = "/pvcloud")
public class FeedBackController extends Controller {

	FeedBackService service = Enhancer.enhance(FeedBackService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		Page<FeedBack> list = service.queryByPage(page, size);
		ArrayList<FeedBackModel> models = new ArrayList<>();
		FeedBackModel model = null;
		for(FeedBack feedBack : list.getList()){
			model = new FeedBackModel();
			model.setContent(feedBack.getStr("feedback"));
			model.setId(feedBack.getInt("id"));
			model.setCreateTime(StringUtil.toString(feedBack.getTimestamp("create_time")));
			model.setUpdateTime(StringUtil.toString(feedBack.getTimestamp("update_time")));
			int operateUserId = feedBack.getInt("operate_user_id")==null?0:feedBack.getInt("operate_user_id");
			User admin = User.dao.queryById(operateUserId);
			if(admin != null){
				model.setOperateUser(admin.getStr("username"));
			}else{
				model.setOperateUser("");
			}
			
			
			Integer userId = feedBack.getInt("user_id");
			if(userId != null){
				Member member = Member.dao.queryMemberById(userId);
				if(member != null){
					model.setMobile(member.getStr("mobile"));
					model.setName(member.getStr("name"));
				}
			}
			model.setFlg(feedBack.getInt("readed"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("feedback.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String title=getSessionAttr("title");
		this.setSessionAttr("title",title);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<FeedBack> list = service.queryByPageParams(page, size,title);
		ArrayList<FeedBackModel> models = new ArrayList<>();
		FeedBackModel model = null;
		for(FeedBack feedBack : list.getList()){
			model = new FeedBackModel();
			model.setContent(feedBack.getStr("feedback"));
			model.setId(feedBack.getInt("id"));
			Integer userId = feedBack.getInt("user_id");
			model.setCreateTime(StringUtil.toString(feedBack.getTimestamp("create_time")));
			model.setUpdateTime(StringUtil.toString(feedBack.getTimestamp("update_time")));
			int operateUserId = feedBack.getInt("operate_user_id")==null?0:feedBack.getInt("operate_user_id");
			User admin = User.dao.queryById(operateUserId);
			if(admin != null){
				model.setOperateUser(admin.getStr("username"));
			}else{
				model.setOperateUser("");
			}
			if(userId != null){
				Member member = Member.dao.queryMemberById(userId);
				if(member != null){
					model.setMobile(member.getStr("mobile"));
					model.setName(member.getStr("name"));
				}
			}
			model.setFlg(feedBack.getInt("readed"));
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("feedback.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		String title = getSessionAttr("title");
		String ptitle = getPara("title");
		title = ptitle;
		
		this.setSessionAttr("title",title);
		
			Integer page = getParaToInt(1);
	        if (page==null || page==0) {
	            page = 1;
	        }
	        
	        Page<FeedBack> list = service.queryByPageParams(page, size,title);
			ArrayList<FeedBackModel> models = new ArrayList<>();
			FeedBackModel model = null;
			for(FeedBack feedBack : list.getList()){
				model = new FeedBackModel();
				model.setContent(feedBack.getStr("feedback"));
				model.setId(feedBack.getInt("id"));
				Integer userId = feedBack.getInt("user_id");
				model.setCreateTime(StringUtil.toString(feedBack.getTimestamp("create_time")));
				model.setUpdateTime(StringUtil.toString(feedBack.getTimestamp("update_time")));
				int operateUserId = feedBack.getInt("operate_user_id")==null?0:feedBack.getInt("operate_user_id");
				User admin = User.dao.queryById(operateUserId);
				if(admin != null){
					model.setOperateUser(admin.getStr("username"));
				}else{
					model.setOperateUser("");
				}
				if(userId != null){
					Member member = Member.dao.queryMemberById(userId);
					if(member != null){
						model.setMobile(member.getStr("mobile"));
						model.setName(member.getStr("name"));
					}
				}
				model.setFlg(feedBack.getInt("readed"));
				models.add(model);
			}
			setAttr("list", list);
			setAttr("sList", models);
			render("feedback.jsp");
	}
	
	/**
	 *新增/修改弹窗
	 */
	public void alter(){
		String id = getPara("id");
		FeedBack model = service.queryById(StringUtil.toInteger(id));
		setAttr("model", model);
		render("feedbackAlter.jsp");
	}
	
	/**
	 * 更新
	 */
	public void update(){
		try{
			int id = getParaToInt("id");
			int flg = StringUtil.toInteger(getPara("flg"));
			int ret = service.updateFlg(id, flg,(Integer)getSessionAttr("agentId"));
			if(ret!=0){
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "查看反馈消息");
				setAttr("message", "保存成功");
			}else{
				setAttr("message", "保存失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
