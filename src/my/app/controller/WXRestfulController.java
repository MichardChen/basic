package my.app.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.huadalink.route.ControllerBind;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

import my.app.service.LoginService;
import my.app.service.WXRestService;
import my.app.xcxdemo.WXBizMsgCrypt;
import my.core.constants.Constants;
import my.core.model.CodeMst;
import my.core.model.ReturnData;
import my.core.pay.RequestXml;
import my.pvcloud.dto.LoginDTO;

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
    
    public void callBack(){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getRequest().getInputStream()));
			String body = IOUtils.read(reader);
			String signature = getPara("signature");
			System.out.println("signature:"+signature);
			String timestamp = getPara("timestamp");
			System.out.println("timestamp:"+timestamp);
			String nonce = getPara("nonce");
			System.out.println("nonce:"+nonce);
			String msg_signature = getPara("msg_signature");
			System.out.println("msg_signature:"+msg_signature);
			String encrypt_type = getPara("encrypt_type");
			System.out.println("body:"+body);
	    	WXBizMsgCrypt pc = new WXBizMsgCrypt("tongji1688", "tongji1688TONGJI1688tongji1688TONGJI1688abc", "wxad9f8af413348c26");
			String result2 = pc.decryptMsg(msg_signature, timestamp, nonce, body);
			System.out.println("result:"+result2);
			Map<String, String> params = RequestXml.getXml(result2);
			int ret = CodeMst.dao.updateCodeMst("210011", params.get("ComponentVerifyTicket"));
			System.out.println("ComponentVerifyTicket:"+params.get("ComponentVerifyTicket"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
   
    public void redirectCall(){
		try {
			String auth_code = getPara("auth_code");
			System.out.println("auth_code:"+auth_code);
			String expires_in = getPara("expires_in");
			System.out.println("expires_in:"+expires_in);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
}
