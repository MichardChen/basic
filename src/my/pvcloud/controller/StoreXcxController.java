package my.pvcloud.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import my.core.model.CodeMst;
import my.core.model.Store;
import my.core.model.StoreXcx;
import my.core.model.WxSubmitModel;
import my.pvcloud.model.StoreXcxListModel;
import my.pvcloud.service.StoreXcxService;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.FileReadUtil;
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
			Store store = Store.dao.queryById(xcx.getInt("store_id")==null?0:xcx.getInt("store_id"));
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
		/*if(retJson1.getString("errcode")==""){
			
		}*/
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
			
		String url = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid="+appId+"&pre_auth_code="+preAuthCode+"&redirect_uri=https://www.yibuwangluo.cn/zznj/storeXcxInfo/redirectCall";
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
	
	public void upload(){
		//上传小程序
		int id = StringUtil.toInteger(getPara("id"));
		StoreXcx storeXcx = StoreXcx.dao.queryById(id);
		if(storeXcx == null){
			setAttr("msg", "数据出错");
			renderJson();
		}
		
		String extJson = FileReadUtil.readFile("D:\\app.json");
		System.out.println("extJson:"+extJson);
		try {
			String accessToken = storeXcx.getStr("authorizer_access_token");
			String accessTokenUrl="https://api.weixin.qq.com/wxa/commit?access_token="+accessToken;
			JSONObject postJson = new JSONObject();
			postJson.put("template_id", 0);
			postJson.put("ext_json", extJson);
			postJson.put("user_version", "V1.0");
			postJson.put("user_desc", "同记平台代发布");
			System.out.println("json:"+postJson.toString());
			String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, postJson.toString());
			System.out.println(accessTokenReturnMsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	//提交审核
	public void submitCode(){
		int id = StringUtil.toInteger(getPara("id"));
		StoreXcx storeXcx = StoreXcx.dao.queryById(id);
		if(storeXcx == null){
			setAttr("msg", "数据出错");
			renderJson();
		}
		
		String extJson = FileReadUtil.readFile("D:\\itemlist.json");
		System.out.println("extJson:"+extJson);
		try {
			String accessToken = storeXcx.getStr("authorizer_access_token");
			String url="https://api.weixin.qq.com/wxa/submit_audit?access_token="+accessToken;
			JSONObject postJson = new JSONObject();
			WxSubmitModel model = new WxSubmitModel();
			model.setAddress("pages/index/index");
			model.setTag("生活");
			model.setFirst_class("文娱");
			model.setSecond_class("资讯");
			model.setFirst_id(1);
			model.setSecond_id(2);
			model.setTitle("首页");
			List<WxSubmitModel> models = new ArrayList<>();
			models.add(model);
			postJson.put("item_list", "{\"address\":\"pages/index/index\",\"tag\":\"生活\",\"first_class\": \"文娱\",\"second_class\": \"资讯\",\"first_id\":1,\"second_id\":2,\"title\": \"首页\"}");
			System.out.println("json:"+postJson.toString());
			String returnMsg = HttpRequest.sendPostJson(url, postJson.toString());
			System.out.println(returnMsg);
			setAttr("msg", "发布成功");
			renderJson();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void test(){
		int id = StringUtil.toInteger(getPara("id"));
		StoreXcx storeXcx = StoreXcx.dao.queryById(id);
		if(storeXcx == null){
			setAttr("msg", "数据出错");
			renderJson();
		}
		
		try {
			String accessToken = storeXcx.getStr("authorizer_access_token");
			String url="https://api.weixin.qq.com/wxa/get_qrcode?access_token="+accessToken;
			String returnMsg = HttpRequest.sendPostJson(url, "");
			System.out.println(returnMsg);
			setAttr("msg", "发布成功");
			renderJson();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void redirectCall(){
		try {
			String auth_code = getPara("auth_code");
			String expires_in = getPara("expires_in");
			System.out.println("auth_code:"+auth_code+",expires_in:"+expires_in);
			CodeMst storeXcx = CodeMst.dao.queryCodestByCode("210011");
			if(storeXcx == null){
				setAttr("message", "数据出错");
				render("xcxauth.jsp");
			}
			String appId = storeXcx.getStr("data2");
			String appSecret = storeXcx.getStr("data3");
			String ticket = storeXcx.getStr("data4");
			
			String accessTokenUrl="https://api.weixin.qq.com/cgi-bin/component/api_component_token";
			JSONObject postJson1 = new JSONObject();
			postJson1.put("component_appid", appId);
			postJson1.put("component_appsecret", appSecret);
			postJson1.put("component_verify_ticket", ticket);
			//String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, accessTokenParam);
			System.out.println("json:"+postJson1.toString());
			String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, postJson1.toString());
			System.out.println("请求令牌access_token:"+accessTokenReturnMsg);
			
			JSONObject retJson1 = new JSONObject(accessTokenReturnMsg);
			/*if(retJson1.getString("errcode")==""){
				
			}*/
			String component_access_token = retJson1.getString("component_access_token");
			
			String url="https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="+component_access_token;
			JSONObject postJson = new JSONObject();
			postJson.put("component_appid", appId);
			postJson.put("authorization_code", auth_code);
			System.out.println("json:"+postJson.toString());
			String returnMsg = HttpRequest.sendPostJson(url, postJson.toString());
			System.out.println("returnMsg:"+returnMsg);
			JSONObject retJson = new JSONObject(returnMsg);
			String authorizationInfo = retJson.getString("authorization_info");
			JSONObject authorizationInfoObj = new JSONObject(authorizationInfo);
			String authorizerAppid = authorizationInfoObj.getString("authorizer_appid");
			String authorizerAccesToken = authorizationInfoObj.getString("authorizer_access_token");
			String expiresIn = authorizationInfoObj.getString("expires_in");
			String authorizerRefreshToken = authorizationInfoObj.getString("authorizer_refresh_token");
			StoreXcx storeXcx2 = StoreXcx.dao.queryByAppId(appId);
			Timestamp expireTime = new Timestamp(DateUtil.getNowTimestamp().getTime()+StringUtil.toInteger(expiresIn)*1000);
			if(storeXcx2 != null){
				//更新
				int ret = StoreXcx.dao.updateStoreXcx(appId,auth_code, expireTime, authorizerAccesToken, authorizerRefreshToken);
				if(ret != 0){
					setAttr("message", "绑定成功");
					render("xcxauth.jsp");
				}else{
					setAttr("message", "数据失败");
					render("xcxauth.jsp");
				}
			}else{
				StoreXcx storeXcx3 = new StoreXcx();
				storeXcx3.set("appid", appId);
				storeXcx3.set("appname", "");
				storeXcx3.set("create_time", DateUtil.getNowTimestamp());
				storeXcx3.set("update_time", DateUtil.getNowTimestamp());
				storeXcx3.set("auth_code", auth_code);
				storeXcx3.set("expire_time", expireTime);
				storeXcx3.set("authorizer_access_token", authorizerAccesToken);
				storeXcx3.set("authorizer_refresh_token", authorizerRefreshToken);
				boolean ret = StoreXcx.dao.saveInfo(storeXcx3);
				if(ret){
					setAttr("message", "绑定成功");
					render("xcxauth.jsp");
				}else{
					setAttr("message", "绑定失败");
					render("xcxauth.jsp");
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
}
