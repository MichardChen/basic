package my.app.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.huadalink.route.ControllerBind;
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
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.HttpRequest;
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
}
