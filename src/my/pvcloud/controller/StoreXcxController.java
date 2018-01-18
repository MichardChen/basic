package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.Store;
import my.core.model.StoreXcx;
import my.pvcloud.model.StoreXcxListModel;
import my.pvcloud.service.StoreXcxService;
import my.pvcloud.util.HttpRequest;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/storeXcxInfo", path = "/pvcloud")
public class StoreXcxController extends Controller{

	StoreXcxService service = Enhancer.enhance(StoreXcxService.class);
	
	int page=1;
	int size=10;
	
	/**
	 * 门店列表
	 */
	public void index(){
		
		removeSessionAttr("appId");
		Page<StoreXcx> list = service.queryByPage(page, size);
		ArrayList<StoreXcxListModel> models = new ArrayList<>();
		StoreXcxListModel model = null;
		for(StoreXcx xcx : list.getList()){
			model = new StoreXcxListModel();
			model.setAppId(xcx.getStr("appid"));
			model.setAppName(xcx.getStr("appname"));
			model.setCreateTime(StringUtil.toString(xcx.getTimestamp("create_time")));
			model.setId(xcx.getInt("id"));
			Store store = Store.dao.queryById(xcx.getInt("store_id"));
			if(store != null){
				model.setStore(store.getStr("store_name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("xcx.jsp");
	}
	
	/**
	 * 模糊查询(文本框)
	 */
	public void queryByPage(){
		String title=getSessionAttr("appId");
		this.setSessionAttr("appId",title);
		Integer page = getParaToInt(1);
        if (page==null || page==0) {
            page = 1;
        }
        Page<StoreXcx> list = service.queryByPageParams(page, size, title);
		ArrayList<StoreXcxListModel> models = new ArrayList<>();
		StoreXcxListModel model = null;
		for(StoreXcx xcx : list.getList()){
			model = new StoreXcxListModel();
			model.setAppId(xcx.getStr("appid"));
			model.setAppName(xcx.getStr("appname"));
			model.setCreateTime(StringUtil.toString(xcx.getTimestamp("create_time")));
			model.setId(xcx.getInt("id"));
			Store store = Store.dao.queryById(xcx.getInt("store_id"));
			if(store != null){
				model.setStore(store.getStr("store_name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("xcx.jsp");
	}
	
	/**
	 * 模糊查询分页
	 */
	public void queryByConditionByPage(){
		
		String appId = getSessionAttr("appId");
		String pappId = getPara("appId");
		appId = pappId;
		
		this.setSessionAttr("appId",appId);
		
		Integer page = getParaToInt(1);
	    if(page==null || page==0) {
	    	page = 1;
	    }
	        
	    Page<StoreXcx> list = service.queryByPageParams(page, size, appId);
		ArrayList<StoreXcxListModel> models = new ArrayList<>();
		StoreXcxListModel model = null;
		for(StoreXcx xcx : list.getList()){
			model = new StoreXcxListModel();
			model.setAppId(xcx.getStr("appid"));
			model.setAppName(xcx.getStr("appname"));
			model.setCreateTime(StringUtil.toString(xcx.getTimestamp("create_time")));
			model.setId(xcx.getInt("id"));
			Store store = Store.dao.queryById(xcx.getInt("store_id"));
			if(store != null){
				model.setStore(store.getStr("store_name"));
			}
			models.add(model);
		}
		setAttr("list", list);
		setAttr("sList", models);
		render("xcx.jsp");
	}
	
	public void alter(){
		int id = StringUtil.toInteger(getPara("id"));
		StoreXcx xcx = service.queryById(id);
		setAttr("xcx", xcx);
		render("xcxAlter.jsp");
	}
	
	public void updateAuth(){
		int id = StringUtil.toInteger(getPara("id"));
		HttpRequest request = HttpRequest.sendPost(url, param)
	}
}
