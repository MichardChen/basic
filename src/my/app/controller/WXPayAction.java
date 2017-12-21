package my.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.huadalink.route.ControllerBind;

import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayConstants.SignType;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

import my.core.constants.Constants;
import my.core.interceptor.RequestInterceptor;
import my.core.model.CashJournal;
import my.core.model.Member;
import my.core.model.PayRecord;
import my.core.model.ReturnData;
import my.core.pay.RequestXml;
import my.core.service.MemberService;
import my.core.vo.WXPrepayModel;
import my.core.wxpay.WXPayUtil;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.PropertiesUtil;
import my.pvcloud.util.StringUtil;

@ControllerBind(key = "/wxpay", path = "/pay")
public class WXPayAction extends Controller{

	MemberService service = Enhancer.enhance(MemberService.class);

	//微信支付回调
	public void wxCallBack() throws Exception{
		
		HttpServletRequest request = getRequest();
		PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
		Map<String,String> params = RequestXml.parseXml(request);
	
		//
		StringBuffer sb = new StringBuffer();
	    InputStream is = request.getInputStream();
	    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
	    BufferedReader br = new BufferedReader(isr);
	    String s = "";
	    while ((s = br.readLine()) != null) {
	        sb.append(s);
	    }
	    Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
	    System.out.println("=======回调的参数========");
	    while(iterator.hasNext()){
	    	Map.Entry<String, String> entry = iterator.next();
	    	System.out.println("key:"+entry.getKey()+"==value:"+entry.getValue());
	    }
	    System.out.println("========================");
	        
		//返回状态码、返回信息
		String returnCode = params.get("return_code");
		String returnMsg = params.get("return_msg");
		System.out.println("return_code:"+returnCode+"--return_msg:"+returnMsg);
		//验证签名
		boolean checkSign = WXPayUtil.isSignatureValid(params, propertiesUtil.getProperties("wx_key"));
		if(checkSign&&(StringUtil.equals(returnCode, "SUCCESS"))&&(StringUtil.isBlank(returnMsg))){
			System.out.println("签名有效");
			//签名有效
			String orderNo = params.get("out_trade_no");
			String trade_no=params.get("transaction_id");
			 //交易金额
			BigDecimal total_fee = new BigDecimal("0");
			//params.get("total_fee");
			
			PayRecord payRecord = PayRecord.dao.queryByOutTradeNo(orderNo);
			int userId = 0;
			if(payRecord != null){
				userId = payRecord.getInt("member_id");
				total_fee = payRecord.getBigDecimal("moneys");
			}
			//更新状态
			int updateFlg = Member.dao.updateCharge(userId, total_fee);
			if(updateFlg != 0){
				CashJournal.dao.updateStatus(orderNo, Constants.FEE_TYPE_STATUS.APPLY_SUCCESS,trade_no);
				PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.TRADE_SUCCESS, trade_no);
				renderText("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
			}else{
				renderText("fail");
			}
			System.out.println("微信支付回调成功");
		}else if(checkSign&&(StringUtil.equals(returnCode, "FAIL"))){
			System.out.println("微信支付回调，支付失败");
			//签名有效，失败
			String orderNo = params.get("out_trade_no");
			String trade_no=params.get("transaction_id");
			CashJournal.dao.updateStatus(orderNo, Constants.FEE_TYPE_STATUS.APPLY_FAIL,trade_no);
			int updateFlg = PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.TRADE_FAIL, trade_no);
			if(updateFlg != 0){
				renderText("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
				renderText("success");
			}else{
				renderText("fail");
			}
		}else{
			System.out.println("微信回调，签名错误");
		}
	}
	
	//生成微信预支付信息
	//@Before(RequestInterceptor.class)
	public void generateWXPayInfo() throws Exception{
		
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		BigDecimal moneys = dto.getMoney().setScale(2);
		int userId = dto.getUserId();
		
		String orderNo = CashJournal.dao.queryCurrentCashNo();
		
		PayRecord pr = new PayRecord();
		pr.set("member_id", userId);
		pr.set("pay_type_cd", Constants.PAY_TYPE_CD.WX_PAY);
		pr.set("out_trade_no", orderNo);
		pr.set("moneys", moneys);
		pr.set("status",Constants.PAY_STATUS.WAIT_BUYER_PAY);
		pr.set("create_time", DateUtil.getNowTimestamp());
		pr.set("update_time", DateUtil.getNowTimestamp());
		boolean save = PayRecord.dao.saveInfo(pr);
		if(save){
			//成功充值记录
			CashJournal cash = new CashJournal();
			cash.set("cash_journal_no", orderNo);
			cash.set("member_id", userId);
			cash.set("pi_type", Constants.PI_TYPE.RECHARGE);
			cash.set("fee_status", Constants.FEE_TYPE_STATUS.APPLING);
			cash.set("occur_date", new Date());
			cash.set("act_rev_amount", moneys);
			cash.set("act_pay_amount", moneys);
			Member member = Member.dao.queryById(userId);
			cash.set("opening_balance", member.getBigDecimal("moneys"));
			cash.set("closing_balance", member.getBigDecimal("moneys").add(moneys));
			cash.set("remarks", "微信充值"+moneys);
			cash.set("create_time", DateUtil.getNowTimestamp());
			cash.set("update_time", DateUtil.getNowTimestamp());
			CashJournal.dao.saveInfo(cash);
			
			int moneyInt = moneys.multiply(new BigDecimal("100")).intValue();
			//生成微信支付信息
			PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
	        String wx_appid = propertiesUtil.getProperties("wx_appid");
	        String wx_mch_id = propertiesUtil.getProperties("wx_mch_id");
	        String wx_key = propertiesUtil.getProperties("wx_key");
	        String wx_unifiedorder = propertiesUtil.getProperties("wx_unifiedorder");
	        String wx_notify_url = propertiesUtil.getProperties("wx_notify_url");
	        String nonStr = WXPayUtil.generateNonceStr();
	        String UTF8 = "UTF-8";
	        String stringA="appid="+wx_appid
	        			  +"&body=掌上茶宝-充值"
	        			  +"&mch_id="+wx_mch_id
	        			  +"&nonce_str="+nonStr
	        			  +"&notify_url="+wx_notify_url
	        			  +"&out_trade_no="+orderNo
	        			  +"&spbill_create_ip=120.41.149.248"
	        			  +"&total_fee="+moneyInt
	        			  +"&trade_type=APP";
	        String md5StringA = WXPayUtil.MD5(stringA+"&key="+wx_key);
	        String reqBody = "<xml>"
	        				+"<appid>"+wx_appid+"</appid>"
	        				+"<body>掌上茶宝-充值</body>"
	        				+"<mch_id>"+wx_mch_id+"</mch_id>"
	        				+"<nonce_str>"+nonStr+"</nonce_str>"
	        				+"<notify_url>"+wx_notify_url+"</notify_url>"
	        				+"<out_trade_no>"+orderNo+"</out_trade_no>"
	        				+"<spbill_create_ip>120.41.149.248</spbill_create_ip>"
	        				+"<total_fee>"+moneyInt+"</total_fee>"
	        				+"<trade_type>APP</trade_type>"
	        				+"<sign>"+md5StringA+"</sign>"
	        				+"</xml>";
	        URL httpUrl = new URL(wx_unifiedorder);
	        HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
	       // httpURLConnection.setRequestProperty("Host", "api.mch.weixin.qq.com");
	        httpURLConnection.setDoOutput(true);
	        httpURLConnection.setRequestMethod("POST");
	        httpURLConnection.setConnectTimeout(10*1000);
	        httpURLConnection.setReadTimeout(10*1000);
	        httpURLConnection.connect();
	        OutputStream outputStream = httpURLConnection.getOutputStream();
	        outputStream.write(reqBody.getBytes(UTF8));

	        //获取内容
	        InputStream inputStream = httpURLConnection.getInputStream();
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
	        final StringBuffer stringBuffer = new StringBuffer();
	        String line = null;
	        while ((line = bufferedReader.readLine()) != null) {
	            stringBuffer.append(line);
	        }
	        String resp = stringBuffer.toString();
	        if (stringBuffer!=null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if (inputStream!=null) {
	            try {
	                inputStream.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        if (outputStream!=null) {
	            try {
	                outputStream.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

	        Map<String, String> retMap = processResponseXml(resp);
	        WXPrepayModel model = new WXPrepayModel();
	        if(!retMap.isEmpty()){
	        	//重新生成签名，二次生成签名
		        String wx_appid2 = propertiesUtil.getProperties("wx_appid");
		        String wx_mch_id2 = propertiesUtil.getProperties("wx_mch_id");
		        String nonStr2 = WXPayUtil.generateNonceStr();
		        String stringA2="appid="+wx_appid2
		        			  +"&noncestr="+nonStr2
		        			  +"&package=Sign=WXPay"
		        			  +"&partnerid="+wx_mch_id2
		        			  +"&prepayid="+retMap.get("prepay_id")
		        			  +"&timestamp="+StringUtil.getTimeStamp();
		        String md5StringA2 = WXPayUtil.MD5(stringA2+"&key="+wx_key);
		        
	        	model.setAppId(retMap.get("appid"));
	        	model.setPartnerId(retMap.get("mch_id"));
	        	model.setPrepayId(retMap.get("prepay_id"));
	        	model.setPackageValue("Sign=WXPay");
	        	model.setNonceStr(nonStr2);
	        	model.setTimeStamp(StringUtil.getTimeStamp());
	        	model.setSign(md5StringA2);
	        	
	        	ReturnData data = new ReturnData();
			    Map<String, Object> dataMap = new HashMap<>();
			    dataMap.put("payInfo", model);
			    data.setCode(Constants.STATUS_CODE.SUCCESS);
			    data.setMessage("获取微信预支付信息成功");
			    data.setData(dataMap); 
			    renderJson(data);
	        }else{
	        	ReturnData data = new ReturnData();
			    Map<String, Object> dataMap = new HashMap<>();
			    dataMap.put("payInfo", model);
			    data.setCode(Constants.STATUS_CODE.FAIL);
			    data.setMessage("获取微信预支付信息失败");
			    data.setData(dataMap); 
			    renderJson(data);
	        }
		}
	}
	
	public Map<String, String> processResponseXml(String xmlStr) throws Exception{
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        }
        else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(WXPayConstants.FAIL)) {
            return respData;
        }
        else if (return_code.equals(WXPayConstants.SUCCESS)) {
           if (this.isResponseSignatureValid(respData)) {
               return respData;
           }
           else {
               throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
           }
        }
        else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }
	
	/**
     * 判断xml数据的sign是否有效，必须包含sign字段，否则返回false。
     *
     * @param reqData 向wxpay post的请求数据
     * @return 签名是否有效
     * @throws Exception
     */
    public boolean isResponseSignatureValid(Map<String, String> reqData) throws Exception {
    	PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
        String wx_key = propertiesUtil.getProperties("wx_key");
        // 返回数据的签名方式和请求中给定的签名方式是一致的
    	SignType signType = SignType.MD5;
        return WXPayUtil.isSignatureValid(reqData, wx_key, signType);
    }
    
    /**
     * 判断支付结果通知中的sign是否有效
     *
     * @param reqData 向wxpay post的请求数据
     * @return 签名是否有效
     * @throws Exception
     */
    public boolean isPayResultNotifySignatureValid(Map<String, String> reqData) throws Exception {
    	PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
        String wx_key = propertiesUtil.getProperties("wx_key");
        String signTypeInData = reqData.get(WXPayConstants.FIELD_SIGN_TYPE);
        SignType signType;
        if (signTypeInData == null) {
            signType = SignType.MD5;
        }
        else {
            signTypeInData = signTypeInData.trim();
            if (signTypeInData.length() == 0) {
                signType = SignType.MD5;
            }
            else if (WXPayConstants.MD5.equals(signTypeInData)) {
                signType = SignType.MD5;
            }
            else if (WXPayConstants.HMACSHA256.equals(signTypeInData)) {
                signType = SignType.HMACSHA256;
            }
            else {
                throw new Exception(String.format("Unsupported sign_type: %s", signTypeInData));
            }
        }
        return WXPayUtil.isSignatureValid(reqData, wx_key, signType);
    }
}
