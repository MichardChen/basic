package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.constants.Constants;
import my.core.model.Log;
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
		removeSessionAttr("title1");
		removeSessionAttr("mobile");
		removeSessionAttr("flg");
		removeSessionAttr("storeName");
		removeSessionAttr("content");
		Page<StoreEvaluate> list = service.queryByPage(page, size);
		ArrayList<AdminEvaluateListModel> models = new ArrayList<>();
		AdminEvaluateListModel model = null;
		for(StoreEvaluate data : list.getList()){
			model = new AdminEvaluateListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			model.setFlg(data.getInt("flg"));
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
		
		String title1=getSessionAttr("title1");
		this.setSessionAttr("title1",title1);
		
		String mobile=getSessionAttr("mobile");
		this.setSessionAttr("mobile",mobile);
		
		String flg=getSessionAttr("flg");
		this.setSessionAttr("flg",flg);
		
		String storeName=getSessionAttr("storeName");
		this.setSessionAttr("storeName",storeName);
		
		String content=getSessionAttr("content");
		this.setSessionAttr("content",content);
		
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<StoreEvaluate> list = service.queryByPageParams(page, size,title,mobile,flg,title1,storeName,content);
		ArrayList<AdminEvaluateListModel> models = new ArrayList<>();
		AdminEvaluateListModel model = null;
		for(StoreEvaluate data : list.getList()){
			model = new AdminEvaluateListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			model.setFlg(data.getInt("flg"));
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
		
		String title1 = getSessionAttr("title1");
		String ptitle1 = getPara("title1");
		title1 = ptitle1;
		
		String mobile = getSessionAttr("mobile");
		String pmobile = getPara("mobile");
		mobile = pmobile;
		
		String flg = getSessionAttr("flg");
		String pflg = getPara("flg");
		flg = pflg;
		
		String storeName = getSessionAttr("storeName");
		String pstoreName = getPara("storeName");
		storeName = pstoreName;
		
		String content = getSessionAttr("content");
		String pcontent = getPara("content");
		content = pcontent;
		
		this.setSessionAttr("title",title);
		this.setSessionAttr("title1",title1);
		this.setSessionAttr("mobile",mobile);
		this.setSessionAttr("flg",flg);
		this.setSessionAttr("storeName", storeName);
		this.setSessionAttr("content", content);
		Integer page = getParaToInt(1);
		if (page==null || page==0) {
			page = 1;
		}
		    
		Page<StoreEvaluate> list = service.queryByPageParams(page, size,title,mobile,flg,title1,storeName,content);
		ArrayList<AdminEvaluateListModel> models = new ArrayList<>();
		AdminEvaluateListModel model = null;
		for(StoreEvaluate data : list.getList()){
			model = new AdminEvaluateListModel();
			model.setCreateTime(StringUtil.toString(data.getTimestamp("create_time")));
			model.setId(data.getInt("id"));
			model.setFlg(data.getInt("flg"));
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
	
	public void update(){
		try{
			int id = getParaToInt("id");
			String status = StringUtil.checkCode(getPara("flg"));
			int ret = StoreEvaluate.dao.updateFlg(id, StringUtil.toInteger(status));
			if(ret==0){
				setAttr("message", "操作成功");
				Log.dao.saveLogInfo((Integer)getSessionAttr("agentId"), Constants.USER_TYPE.PLATFORM_USER, "删除评论id:"+id);
			}else{
				setAttr("message", "操作失败");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		index();
	}
}
