package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.Member;
import my.core.model.Store;
import my.core.model.StoreEvaluate;
import my.core.vo.AdminEvaluateListModel;
import my.pvcloud.service.StoreEvaluateService;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/storeEvaluateInfo", path = "/pvcloud")
public class StoreEvaluateController extends Controller {

	StoreEvaluateService service = Enhancer.enhance(StoreEvaluateService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("title");
		Page<StoreEvaluate> list = service.queryByPage(page, size);
		ArrayList<AdminEvaluateListModel> models = new ArrayList<>();
		AdminEvaluateListModel model = null;
		for(StoreEvaluate data : list.getList()){
			model = new AdminEvaluateListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			model.setPoint(StringUtil.toString(data.getInt("service_point")));
			int memberId = data.getInt("member_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setCommentUser(member.getStr("nick_name"));
				model.setCommentUserMobile(member.getStr("mobile"));
			}
			model.setComment(data.getStr("mark"));
			int storeId = data.getInt("store_id");
			Store store = Store.dao.queryById(storeId);
			if(store != null){
				model.setStore(store.getStr("store_name"));
				int storeMemberId = store.getInt("member_id");
				Member sMember = Member.dao.queryById(storeMemberId);
				if(sMember != null){
					model.setStoreMobile(sMember.getStr("mobile"));
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("evaluate.jsp");
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
        Page<StoreEvaluate> list = service.queryByPageParams(page, size,title);
		ArrayList<AdminEvaluateListModel> models = new ArrayList<>();
		AdminEvaluateListModel model = null;
		for(StoreEvaluate data : list.getList()){
			model = new AdminEvaluateListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			model.setComment(data.getStr("mark"));
			model.setPoint(StringUtil.toString(data.getInt("service_point")));
			int memberId = data.getInt("member_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setCommentUser(member.getStr("nick_name"));
				model.setCommentUserMobile(member.getStr("mobile"));
			}
			
			int storeId = data.getInt("store_id");
			Store store = Store.dao.queryById(storeId);
			if(store != null){
				model.setStore(store.getStr("store_name"));
				int storeMemberId = store.getInt("member_id");
				Member sMember = Member.dao.queryById(storeMemberId);
				if(sMember != null){
					model.setStoreMobile(sMember.getStr("mobile"));
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("evaluate.jsp");
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
		    
		Page<StoreEvaluate> list = service.queryByPageParams(page, size,title);
		ArrayList<AdminEvaluateListModel> models = new ArrayList<>();
		AdminEvaluateListModel model = null;
		for(StoreEvaluate data : list.getList()){
			model = new AdminEvaluateListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			model.setComment(data.getStr("mark"));
			model.setPoint(StringUtil.toString(data.getInt("service_point")));
			int memberId = data.getInt("member_id");
			Member member = Member.dao.queryById(memberId);
			if(member != null){
				model.setCommentUser(member.getStr("nick_name"));
				model.setCommentUserMobile(member.getStr("mobile"));
			}
			
			int storeId = data.getInt("store_id");
			Store store = Store.dao.queryById(storeId);
			if(store != null){
				model.setStore(store.getStr("store_name"));
				int storeMemberId = store.getInt("member_id");
				Member sMember = Member.dao.queryById(storeMemberId);
				if(sMember != null){
					model.setStoreMobile(sMember.getStr("mobile"));
				}
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("evaluate.jsp");
	}
}
