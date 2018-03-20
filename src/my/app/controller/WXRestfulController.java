package my.app.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.huadalink.route.ControllerBind;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

import my.app.service.LoginService;
import my.app.service.WXRestService;
import my.app.xcxdemo.WXBizMsgCrypt;
import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.ReturnData;
import my.core.model.StoreXcx;
import my.core.pay.RequestXml;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.HttpRequest;
import my.pvcloud.util.SHA1;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/wxrest", path = "/wxrest")
public class WXRestfulController extends Controller{

    WXRestService restService = Enhancer.enhance(WXRestService.class);
    LoginService service = Enhancer.enhance(LoginService.class);
    
    public void index(){
    	ReturnData data = new ReturnData();
    	data.setCode(Constants.STATUS_CODE.SUCCESS);
    	data.setMessage("查询成功");
    	CodeMst androidMst = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.XCX_ANDROID);
    	CodeMst iosMst = CodeMst.dao.queryCodestByCode(Constants.COMMON_SETTING.XCX_IOS);
    	Map<String, Object> map = new HashMap<>();
    	if(androidMst != null){
    		map.put("android", androidMst.getStr("data2"));
    	}
    	if(iosMst != null){
    		map.put("ios", iosMst.get("data2"));
    	}
    	map.put("advertisement", "http://app.tongjichaye.com:88/common/download.jpg");
    	data.setData(map);
		renderJson(data);
	}
    
    public void queryStoreDetail(){
    	LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(restService.queryTeaStoreDetail(dto));
	}
    
    public void queryTeaStoreList(){
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		renderJson(restService.queryTeaStoreList(dto));
	}
    
    //微信每个10分钟会推送ComponentVerifyTicket
    public void callBack(){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getRequest().getInputStream()));
			String body = IOUtils.read(reader);
			//参数
			String signature = getPara("signature");
			String timestamp = getPara("timestamp");
			String nonce = getPara("nonce");
			String msg_signature = getPara("msg_signature");
			String encrypt_type = getPara("encrypt_type");
			
			System.out.println("signature:"+signature+",timestamp："+timestamp+",nonce:"+nonce+",msg_signature:"+msg_signature+",encrypt_type:"+encrypt_type);
	    	
			CodeMst msgSettingMst = CodeMst.dao.queryCodestByCode("210012");
			if(msgSettingMst != null){
				WXBizMsgCrypt pc = new WXBizMsgCrypt(msgSettingMst.getStr("data2")
												    ,msgSettingMst.getStr("data3")
												    ,msgSettingMst.getStr("data4"));
				
				String result2 = pc.decryptMsg(msg_signature, timestamp, nonce, body);
				System.out.println("result:"+result2);
				Map<String, String> params = RequestXml.getXml(result2);
				int ret = 0;
				if(StringUtil.isNoneBlank(params.get("ComponentVerifyTicket"))){
					ret = CodeMst.dao.updateCodeMst("210011", params.get("ComponentVerifyTicket"));
				}
				System.out.println("接收到ComponentVerifyTicket:"+params.get("ComponentVerifyTicket"));
				if(ret != 0){
					renderText("success");
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
    
    //小程序消息回调
    public void checkSignature(){
    	
    	 String signature = getPara("signature");
    	 String timestamp = getPara("timestamp");
    	 String nonce = getPara("nonce");
    	 String echostr = getPara("echostr");

    	 String token = "jflajkljljoa752822ABCAKLK";
    	 //组成字符串
    	 String[] tmpArr={token,timestamp,nonce};
    	 //数组内对象排序，字符串从小到大
    	 Arrays.sort(tmpArr);
    	 //数组内的对象拼接组合
    	 String tmpStr = tmpArr[0]+tmpArr[1]+tmpArr[2];
    	 //sha1加密
    	 String tmpStrEncode = SHA1.encode(tmpStr);

    	 if(StringUtil.equals(tmpStrEncode,signature)){
    		 renderText(echostr);
    	 }else{
    		 renderText("false");
    	}
    }
    
    //发送微信接口，请求消息，code有效
    public void sendClientMsg(){
    	
    	ReturnData data = new ReturnData();
    	//获取用户的openID
    	String code = getPara("code");
    	String appId = getPara("appId");
    	String secret = "";
    	if(StringUtil.isBlank(appId)){
    		data.setCode(Constants.STATUS_CODE.FAIL);
    		data.setMessage("请求失败");
    		renderJson(data);
    		return;
    	}
    	StoreXcx xcx = StoreXcx.dao.queryByAppId(appId);
    	if(xcx==null){
    		data.setCode(Constants.STATUS_CODE.FAIL);
    		data.setMessage("请求失败");
    		renderJson(data);
    		return;
    	}
    	secret=xcx.getStr("appsecret");
    	String userUrl = "https://api.weixin.qq.com/sns/jscode2session?appid="+appId+"&secret="+secret+"&js_code="+code+"&grant_type=authorization_code";
    	String retMsg1 = HttpRequest.sendGet(userUrl, "");
    	try {
	    	JSONObject retJson1 = new JSONObject(retMsg1);
	    	if(retJson1.has("errcode")&&StringUtil.equals(retJson1.getString("errcode"), "40029")){
				//请求失败
	    		data.setCode("40029");
	    		data.setMessage("code无效");
	    		renderJson(data);
	    		return;
			}else{
		    	//获取accessToken
		    	String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
		    	String retMsg2 = HttpRequest.sendGet(accessTokenUrl, "grant_type=client_credential&appid="+appId+"&secret="+secret);
		    	JSONObject retJson2 = new JSONObject(retMsg2);
		    	if(retJson2.has("access_token")&&retJson1.has("openid")){
			    	String openId = retJson1.getString("openid");
			    	Map<String, String> map = new HashMap<>();
			    	map.put("openId", openId);
			    	data.setData(map);
					data.setCode(Constants.STATUS_CODE.SUCCESS);
			    	data.setMessage("请求成功");
			    	renderJson(data);
			    	return;
		    	}else{
		    		data.setCode(Constants.STATUS_CODE.FAIL);
		    		data.setMessage("access_token请求失败");
		    		renderJson(data);
		    		return;
		    	}
			}
    	} catch (JSONException e) {
    		data.setCode(Constants.STATUS_CODE.FAIL);
    		data.setMessage("请求失败");
    		renderJson(data);
			e.printStackTrace();
		}
    }
    
    //code失效
    public void sendClientMsgCodeInvalid(){
    	
    	ReturnData data = new ReturnData();
    	//获取用户的openID
    	String openId = getPara("openId");
    	String appId = getPara("appId");
    	String secret = "";
    	if(StringUtil.isBlank(appId)){
    		data.setCode(Constants.STATUS_CODE.FAIL);
    		data.setMessage("请求失败");
    		renderJson(data);
    		return;
    	}
    	StoreXcx xcx = StoreXcx.dao.queryByAppId(appId);
    	if(xcx==null){
    		data.setCode(Constants.STATUS_CODE.FAIL);
    		data.setMessage("请求失败");
    		renderJson(data);
    		return;
    	}
    	secret=xcx.getStr("appsecret");
    	try {
	    	//获取accessToken
	    	String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";
	    	String retMsg2 = HttpRequest.sendGet(accessTokenUrl, "grant_type=client_credential&appid="+appId+"&secret="+secret);
	    	JSONObject retJson2 = new JSONObject(retMsg2);
	    	if(retJson2.has("access_token")){
		    	String accessToken = retJson2.getString("access_token");
		    	String postUrl="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+accessToken;
		    	JSONObject postJson = new JSONObject();
		    	postJson.put("touser", openId);
		    	postJson.put("msgtype", "link");
		    	JSONObject postJson1 = new JSONObject();
		    	postJson1.put("title", "APP下载");
		    	postJson1.put("description", "让全天下有免费的茶喝");
		    	postJson1.put("url", "http://a.app.qq.com/o/simple.jsp?pkgname=com.tea.tongji");
				postJson1.put("picurl", "https://app.tongjichaye.com/ios80.png");
				postJson1.put("thumb_url", "https://app.tongjichaye.com/ios80.png");
				postJson.put("link", postJson1);
				String retMsg3 = HttpRequest.sendPostJson(postUrl, postJson.toString());
				JSONObject retJson3 = new JSONObject(retMsg3);
				if(StringUtil.equals(retJson3.getString("errcode"), "0")){
					data.setCode(Constants.STATUS_CODE.SUCCESS);
		    		data.setMessage("请求成功");
		    		renderJson(data);
		    		return;
				}else{
					data.setCode(Constants.STATUS_CODE.FAIL);
		    		data.setMessage("请求失败");
		    		renderJson(data);
		    		return;
				}
	    	}else{
	    		data.setCode(Constants.STATUS_CODE.FAIL);
	    		data.setMessage("access_token请求失败");
	    		renderJson(data);
	    		return;
	    	}
    	} catch (JSONException e) {
    		data.setCode(Constants.STATUS_CODE.FAIL);
    		data.setMessage("请求失败");
    		renderJson(data);
			e.printStackTrace();
		}
    }
}