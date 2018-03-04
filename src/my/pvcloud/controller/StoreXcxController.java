package my.pvcloud.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.huadalink.route.ControllerBind;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.druid.sql.visitor.functions.Function;
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
			model.setAppSecret(xcx.getStr("appsecret"));
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
			model.setAppSecret(xcx.getStr("appsecret"));
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
			model.setAppSecret(xcx.getStr("appsecret"));
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
	
	public void bindXcx(){
		int id = StringUtil.toInteger(getPara("id"));
		setAttr("storeId", id);
		render("bindXcx.jsp");
	}
	
	public void updateAuth(){
		//错误代码40001的时候，要重新获取access_token
		int storeId = StringUtil.toInteger(getPara("id"));
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
			System.out.println("请求令牌发送参数json："+postJson.toString());
			String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, postJson.toString());
			
			JSONObject retJson1 = new JSONObject(accessTokenReturnMsg);
			
			String component_access_token = retJson1.getString("component_access_token");
			System.out.println("请求返回component_access_token:"+component_access_token);
			
			//获取预授权码pre_auth_code
			String preAuthCodeUrl="https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token="+component_access_token;
			JSONObject postJson1 = new JSONObject();
			postJson1.put("component_appid", appId);
			String returnMsg = HttpRequest.sendPostJson(preAuthCodeUrl, postJson1.toString());
			System.out.println("获取预授权码pre_auth_code:"+returnMsg);
			
			//解析预授权码
			JSONObject retJson = new JSONObject(returnMsg);
			String preAuthCode = retJson.getString("pre_auth_code");
			//返回信息给前端，打开授权页面
			String url = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid="+appId+"&pre_auth_code="+preAuthCode+"&redirect_uri=https://www.yibuwangluo.cn/zznj/storeXcxInfo/redirectCall?storeId="+storeId;
			System.out.println("url："+url);	
			setAttr("data", url);
			renderJson();
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
		queryApiAuthorizerToken(storeXcx.getStr("appid"));
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
	
	//获取可选择的类目
	public void getCategory(){
		int id = StringUtil.toInteger(getPara("id"));
		StoreXcx storeXcx = StoreXcx.dao.queryById(id);
		if(storeXcx == null){
			setAttr("msg", "数据出错");
			renderJson();
		}
		queryApiAuthorizerToken(storeXcx.getStr("appid"));
		try {
			String accessToken = storeXcx.getStr("authorizer_access_token");
			System.out.println("authorizer_access_token："+accessToken);
			String accessTokenUrl="https://api.weixin.qq.com/wxa/get_category";
			String accessTokenReturnMsg = HttpRequest.sendGet(accessTokenUrl, "access_token="+accessToken);
			System.out.println("可选择类目："+accessTokenReturnMsg);
			//{"errcode":0,"errmsg":"ok",
			//"category_list":[{"first_class":"旅游","second_class":"旅游攻略","first_id":231,"second_id":640},
			//{"first_class":"工具","second_class":"信息查询","first_id":287,"second_id":612},
			//{"first_class":"餐饮","second_class":"点评与推荐","first_id":220,"second_id":221}]}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//先将域名登记到第三方平台的小程序服务器域名中，才可以调用接口进行配置
	public int modifyDomain(String appid){
		StoreXcx storeXcx = StoreXcx.dao.queryByAppId(appid);
		if(storeXcx == null){
			return 0;
		}
		queryApiAuthorizerToken(storeXcx.getStr("appid"));
		try {
			CodeMst storeXcxMst = CodeMst.dao.queryCodestByCode("210011");
			if(storeXcxMst == null){
				return 0;
			}
			String domainUrl = storeXcxMst.getStr("data5");
			String accessToken = storeXcx.getStr("authorizer_access_token");
			String url="https://api.weixin.qq.com/wxa/modify_domain?access_token="+accessToken;
			String[] domain={domainUrl,domainUrl};
			JSONObject data = new JSONObject();
			data.put("action", "add");
			data.put("requestdomain", domain);
			data.put("wsrequestdomain", domain);
			data.put("uploaddomain", domain);
			data.put("downloaddomain", domain);
			System.out.println("json:"+data.toString());
			String returnMsg = HttpRequest.sendPostJson(url, data.toString());
			System.out.println(returnMsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	//查看最近提交版本的审核状态
	public void getLatestAuditstatus(){
		int id = StringUtil.toInteger(getPara("id"));
		StoreXcx storeXcx = StoreXcx.dao.queryById(id);
		if(storeXcx == null){
			setAttr("msg", "数据出错");
			renderJson();
		}
		
		queryApiAuthorizerToken(storeXcx.getStr("appid"));
		try {
			String accessToken = storeXcx.getStr("authorizer_access_token");
			System.out.println("authorizer_access_token："+accessToken);
			String accessTokenUrl="https://api.weixin.qq.com/wxa/get_latest_auditstatus";
			String accessTokenReturnMsg = HttpRequest.sendGet(accessTokenUrl, "access_token="+accessToken);
			
			JSONObject retJson1 = new JSONObject(accessTokenReturnMsg);
			int status = retJson1.getInt("status");
			System.out.println("审核结果："+accessTokenReturnMsg+",status："+status);
			if(status == 0){
				setAttr("msg", "审核成功");
			}else if(status == 1){
				setAttr("msg", "审核失败");
			}else{
				setAttr("msg", "审核中");
			}
			renderJson();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//获取小程序相关信息
	public JSONObject getAuthInfo(String appid){
		
		CodeMst storeXcxMst = CodeMst.dao.queryCodestByCode("210011");
		if(storeXcxMst == null){
			return null;
		}
		String authAppId = appid;
		String appId = storeXcxMst.getStr("data2");
		String appSecret = storeXcxMst.getStr("data3");
		String ticket = storeXcxMst.getStr("data4");
		try {
			String accessTokenUrl="https://api.weixin.qq.com/cgi-bin/component/api_component_token";
			JSONObject postJson1 = new JSONObject();
			postJson1.put("component_appid", appId);
			postJson1.put("component_appsecret", appSecret);
			postJson1.put("component_verify_ticket", ticket);
			
			System.out.println("json:"+postJson1.toString());
			String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, postJson1.toString());
			System.out.println("请求令牌access_token:"+accessTokenReturnMsg);
			
			JSONObject retJson1 = new JSONObject(accessTokenReturnMsg);
			String component_access_token = retJson1.getString("component_access_token");
			
			String authTokenUrl = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token="+component_access_token;
			JSONObject postJson2 = new JSONObject();
			postJson2.put("component_appid", appId);
			postJson2.put("authorizer_appid", authAppId);
			String returnMsg = HttpRequest.sendPostJson(authTokenUrl, postJson2.toString());
			System.out.println("小程序相关信息:"+returnMsg);
			return new JSONObject(returnMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//提交审核
	public void submitCode(){
		int id = StringUtil.toInteger(getPara("id"));
		StoreXcx storeXcx = StoreXcx.dao.queryById(id);
		if(storeXcx == null){
			setAttr("msg", "数据出错");
			renderJson();
		}
		
		queryApiAuthorizerToken(storeXcx.getStr("appid"));
		
		String extJson = FileReadUtil.readFile("D:\\itemlist.json");
		System.out.println("extJson:"+extJson);
		try {
			String accessToken = storeXcx.getStr("authorizer_access_token");
			String url="https://api.weixin.qq.com/wxa/submit_audit?access_token="+accessToken;
			JSONObject postJson = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("address", "pages/index/index");
			data.put("tag", "旅游");
			data.put("first_class", "旅游");
			data.put("second_class", "旅游攻略");
			data.put("first_id", 231);
			data.put("second_id", 640);
			data.put("title", "首页");
			JSONArray array = new JSONArray();
			array.put(data);
			/*WxSubmitModel model = new WxSubmitModel();
			model.setAddress("pages/index/index");
			model.setTag("生活");
			model.setFirst_class("文娱");
			model.setSecond_class("资讯");
			model.setFirst_id(1);
			model.setSecond_id(2);
			model.setTitle("首页");
			List<WxSubmitModel> models = new ArrayList<>();
			models.add(model);*/
			postJson.put("item_list", array);
			//postJson.put("item_list", "[{\"address\":\"pages/index/index\",\"tag\":\"生活\",\"first_class\": \"文娱\",\"second_class\": \"资讯\",\"first_id\":1,\"second_id\":2,\"title\": \"首页\"}]");
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
	
	//获取（刷新）授权公众号或小程序的接口调用凭据（令牌），刷新authorizer_refresh_token令牌
	public void queryApiAuthorizerToken(String authAppId){
		
		CodeMst storeXcx = CodeMst.dao.queryCodestByCode("210011");
		if(storeXcx == null){
			return;
		}
		String appId = storeXcx.getStr("data2");
		String appSecret = storeXcx.getStr("data3");
		String ticket = storeXcx.getStr("data4");
		try {
			StoreXcx xcx = StoreXcx.dao.queryByAppId(authAppId);
			if(xcx == null){
				return;
			}
			if(xcx.getTimestamp("expire_time") != null){
				if(DateUtil.getNowTimestamp().compareTo(xcx.getTimestamp("expire_time"))<=0){
					System.out.println("now:"+DateUtil.getNowTimestamp()+",expire_time:"+xcx.getTimestamp("expire_time"));
					//没过期
					return;
				}
			}
			
			String accessTokenUrl="https://api.weixin.qq.com/cgi-bin/component/api_component_token";
			JSONObject postJson1 = new JSONObject();
			postJson1.put("component_appid", appId);
			postJson1.put("component_appsecret", appSecret);
			postJson1.put("component_verify_ticket", ticket);
			
			System.out.println("json:"+postJson1.toString());
			String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, postJson1.toString());
			System.out.println("请求令牌access_token:"+accessTokenReturnMsg);
			
			JSONObject retJson1 = new JSONObject(accessTokenReturnMsg);
			String component_access_token = retJson1.getString("component_access_token");
			
			String authTokenUrl = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token="+component_access_token;
			JSONObject postJson2 = new JSONObject();
			postJson2.put("component_appid", appId);
			postJson2.put("authorizer_appid", authAppId);
			postJson2.put("authorizer_refresh_token", xcx.getStr("authorizer_refresh_token"));
			String returnMsg = HttpRequest.sendPostJson(authTokenUrl, postJson2.toString());
			System.out.println("请求刷新调用凭据:"+returnMsg);
			
			JSONObject retJson = new JSONObject(returnMsg);
			String ret_authorizer_access_token = retJson.getString("authorizer_access_token");
			String ret_expires_in = retJson.getString("expires_in");
			String ret_authorizer_refresh_token = retJson.getString("authorizer_refresh_token");
			Timestamp expireTime = new Timestamp(DateUtil.getNowTimestamp().getTime()+StringUtil.toInteger(retJson.getString("expires_in"))*1000);
			int retUpdate = StoreXcx.dao.updateStoreXcxRefresh(authAppId
															  ,expireTime
															  ,ret_authorizer_access_token
															  ,ret_authorizer_refresh_token);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//扫码回调
	public void redirectCall(){
		try {
			int storeId = StringUtil.toInteger(getPara("storeId"));
			String auth_code = getPara("auth_code");
			String expires_in = getPara("expires_in");
			System.out.println("auth_code:"+auth_code+",expires_in:"+expires_in+",storeId:"+storeId);
			CodeMst storeXcx = CodeMst.dao.queryCodestByCode("210011");
			if(storeXcx == null){
				setAttr("message", "数据出错");
				render("bindXcx.jsp");
			}
			String appId = storeXcx.getStr("data2");
			String appSecret = storeXcx.getStr("data3");
			String ticket = storeXcx.getStr("data4");
			
			//请求accessToken
			String accessTokenUrl="https://api.weixin.qq.com/cgi-bin/component/api_component_token";
			JSONObject postJson1 = new JSONObject();
			postJson1.put("component_appid", appId);
			postJson1.put("component_appsecret", appSecret);
			postJson1.put("component_verify_ticket", ticket);
			
			System.out.println("json:"+postJson1.toString());
			String accessTokenReturnMsg = HttpRequest.sendPostJson(accessTokenUrl, postJson1.toString());
			System.out.println("请求令牌access_token:"+accessTokenReturnMsg);
			
			JSONObject retJson1 = new JSONObject(accessTokenReturnMsg);
			String component_access_token = retJson1.getString("component_access_token");
			//获取接口调用凭据和授权信息
			String url="https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="+component_access_token;
			JSONObject postJson = new JSONObject();
			postJson.put("component_appid", appId);
			postJson.put("authorization_code", auth_code);
			//使用授权码换取公众号或小程序的接口调用凭据和授权信息
			String returnMsg = HttpRequest.sendPostJson(url, postJson.toString());
			System.out.println("返回接口调用凭据和授权信息:"+returnMsg);
			JSONObject retJson = new JSONObject(returnMsg);
			String authorizationInfo = retJson.getString("authorization_info");
			JSONObject authorizationInfoObj = new JSONObject(authorizationInfo);
			String authorizerAppid = authorizationInfoObj.getString("authorizer_appid");
			String authorizerAccesToken = authorizationInfoObj.getString("authorizer_access_token");
			String expiresIn = authorizationInfoObj.getString("expires_in");
			String authorizerRefreshToken = authorizationInfoObj.getString("authorizer_refresh_token");
			
			JSONObject xcxInfo = getAuthInfo(authorizerAppid);
			String nickName = "";
			if(xcxInfo != null){
				JSONObject authorizer_info = new JSONObject(xcxInfo.getString("authorizer_info"));
				nickName = authorizer_info.getString("nick_name");
			}
			System.out.println("nick_name:"+nickName);
			StoreXcx storeXcx2 = StoreXcx.dao.queryByAppId(authorizerAppid);
			Timestamp expireTime = new Timestamp(DateUtil.getNowTimestamp().getTime()+StringUtil.toInteger(expiresIn)*1000);
			if(storeXcx2 != null){
				//更新小程序调用凭证
				int ret = StoreXcx.dao.updateStoreXcx(authorizerAppid
													 ,auth_code
													 ,expireTime
													 ,authorizerAccesToken
													 ,authorizerRefreshToken
													 ,nickName);
				if(ret != 0){
					StoreXcx xcx = StoreXcx.dao.queryByAppId(authorizerAppid);
					setAttr("xcx", xcx);
					setAttr("message", "绑定成功");
					render("bindXcx.jsp");
				}else{
					setAttr("message", "数据失败");
					render("bindXcx.jsp");
				}
			}else{
				//保存小程序调用凭证
				StoreXcx storeXcx3 = new StoreXcx();
				storeXcx3.set("appid", authorizerAppid);
				storeXcx3.set("appname", nickName);
				int memberId = 0;
				Store store = Store.dao.queryById(storeId);
				if(store != null){
					memberId = store.getInt("member_id");
				}
				storeXcx3.set("member_id", memberId);
				storeXcx3.set("create_time", DateUtil.getNowTimestamp());
				storeXcx3.set("update_time", DateUtil.getNowTimestamp());
				storeXcx3.set("auth_code", auth_code);
				storeXcx3.set("expire_time", expireTime);
				storeXcx3.set("store_id", storeId);
				storeXcx3.set("authorizer_access_token", authorizerAccesToken);
				storeXcx3.set("authorizer_refresh_token", authorizerRefreshToken);
				boolean ret = StoreXcx.dao.saveInfo(storeXcx3);
				if(ret){
					//为小程序登记服务器域名
					modifyDomain(appId);
					StoreXcx xcx = StoreXcx.dao.queryByAppId(authorizerAppid);
					setAttr("xcx", xcx);
					setAttr("message", "绑定成功");
					render("bindXcx.jsp");
				}else{
					setAttr("message", "绑定失败");
					render("bindXcx.jsp");
				}
			}
		} catch (Exception e1) {
			setAttr("message", "绑定失败");
			render("bindXcx.jsp");
			e1.printStackTrace();
		}
    }
}
