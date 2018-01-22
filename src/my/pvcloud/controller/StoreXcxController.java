package my.pvcloud.controller;

import java.util.ArrayList;

import org.huadalink.route.ControllerBind;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.CodeMst;
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
		CodeMst storeXcx = CodeMst.dao.queryCodestByCode("210011");
		if(storeXcx == null){
			setAttr("msg", "数据出错");
			renderJson();
		}
		String appId = storeXcx.getStr("data2");
		String appSecret = storeXcx.getStr("data3");
		String ticket = storeXcx.getStr("data4");
		if(StringUtil.isBlank(ticket)){
			setAttr("msg", "数据出错，ticket为空");
			renderJson();
		}
		try{
		//请求令牌access_token
		String accessTokenUrl="https://api.weixin.qq.com/cgi-bin/component/api_component_token";
		JSONObject postJson = new JSONObject();
		postJson.put("component_appid", appId);
		postJson.put("component_appsecret", appSecret);
		postJson.put("component_verify_ticket", ticket);
		//String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, accessTokenParam);
		System.out.println("json:"+postJson.toString());
		String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, postJson.toString());
		System.out.println("请求令牌access_token:"+accessTokenReturnMsg);
		
		
		JSONObject retJson1 = new JSONObject(accessTokenReturnMsg);
		String component_access_token = retJson1.getString("component_access_token");
		System.out.println("component_access_token:"+component_access_token);
		
		//获取预授权码pre_auth_code
		String preAuthCodeUrl="https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token="+component_access_token;
		JSONObject postJson1 = new JSONObject();
		postJson1.put("component_appid", appId);
		//postJson1.put("component_access_token", component_access_token);
		String returnMsg = HttpRequest.sendPostJson(preAuthCodeUrl, postJson1.toString());
		System.out.println("获取预授权码pre_auth_code:"+returnMsg);
		
		//解析预授权码
		JSONObject retJson = new JSONObject(returnMsg);
		String preAuthCode = retJson.getString("pre_auth_code");
		System.out.println("preAuthCode:"+preAuthCode);
			
		String url = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid="+appId+"&pre_auth_code="+preAuthCode+"&redirect_uri=www.yibuwangluo.cn/zznj/wxrest/redirectCall";
		System.out.println("url："+url);	
		setAttr("data", url);
		renderJson();
			//使用授权码换取公众号或小程序的接口调用凭据和授权信息
			/*String authUrl = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="+component_access_token;
			JSONObject postJson2 = new JSONObject();
			postJson2.put("component_appid", "wxad9f8af413348c26");
			postJson2.put("authorization_code", preAuthCode);
			String returnMsg1 = HttpRequest.sendPostJson(authUrl, postJson2.toString());
			System.out.println("接口调用凭据和授权信息:"+returnMsg1);*/
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
