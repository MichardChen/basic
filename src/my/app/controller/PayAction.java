package my.app.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huadalink.route.ControllerBind;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

import my.core.constants.Constants;
import my.core.interceptor.RequestInterceptor;
import my.core.model.CashJournal;
import my.core.model.Member;
import my.core.model.PayRecord;
import my.core.model.ReturnData;
import my.core.pay.AlipayCore;
import my.core.pay.RequestXml;
import my.core.service.MemberService;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.PropertiesUtil;
import my.pvcloud.util.StringUtil;
import my.pvcloud.util.UtilDate;
import my.pvcloud.util.WXRequestUtil;
import net.sf.json.JSONObject;

@ControllerBind(key = "/pay", path = "/pay")
public class PayAction extends Controller{

	MemberService service = Enhancer.enhance(MemberService.class);

	//生成微信支付信息
	public void generateWxPayInfo() throws Exception{
		ReturnData data = new ReturnData();
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		BigDecimal moneys = dto.getMoney();
		int userId = dto.getUserId();
		String orderInfo = WXRequestUtil.createOrderInfo(StringUtil.getOrderNo(), moneys,userId);
		String order = WXRequestUtil.httpOrder(orderInfo);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage(order);
		renderJson(data);
	}
	
	//支付宝支付测试
	public void testAliInfo() throws Exception{
		
		ReturnData data = new ReturnData();
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		BigDecimal moneys = dto.getMoney().setScale(2);
		int userId = dto.getUserId();
		
		String orderNo = StringUtil.getOrderNo();
		
		PayRecord pr = new PayRecord();
		pr.set("member_id", userId);
		pr.set("pay_type_cd", Constants.PAY_TYPE_CD.ALI_PAY);
		pr.set("out_trade_no", orderNo);
		pr.set("moneys", moneys);
		pr.set("status",Constants.PAY_STATUS.WAIT_BUYER_PAY);
		pr.set("create_time", DateUtil.getNowTimestamp());
		pr.set("update_time", DateUtil.getNowTimestamp());
		boolean save = PayRecord.dao.saveInfo(pr);
		
		if(save){
			
			PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
			 //公共参数
	        Map<String, String> map = new HashMap<String, String>();
	        /*map.put("app_id", propertiesUtil.getProperties("ali_appid"));
	        map.put("method", "alipay.trade.app.pay");
	        map.put("format", "json");
	        map.put("charset", "utf-8");
	        map.put("sign_type", "RSA2");
	        map.put("timestamp", UtilDate.getDateFormatter());
	        map.put("version", "1.0");
	        map.put("notify_url", propertiesUtil.getProperties("ali_notify_url"));*/
	        String appID = propertiesUtil.getProperties("ali_appid");
	        String method = "alipay.trade.app.pay";
	        String format = "json";
	        String charset = "utf-8";
	        String sign_type = "RSA2";
	        String timestamp = UtilDate.getDateFormatter();
	        String version = "1.0";
	        String notify_url = propertiesUtil.getProperties("ali_notify_url");
	
	        Map<String, String> m = new HashMap<String, String>();
	
	        //业务参数
	        //m.put("body", "掌上茶宝");
	        m.put("subject", "掌上茶宝充值");
	        m.put("out_trade_no", orderNo);
	        m.put("timeout_express", "30m");
	        m.put("total_amount", StringUtil.toString(moneys));
	        //m.put("seller_id", propertiesUtil.getProperties("ali_seller_id"));
	        m.put("product_code", "QUICK_MSECURITY_PAY");
	        //业务参数字符串
	        JSONObject bizcontentJson= JSONObject.fromObject(m);
	
	        map.put("biz_content", bizcontentJson.toString());
	        
	        String content = "app_id=" + appID + "&biz_content=" + bizcontentJson + "&charset=" + charset + "&method=" + method  
	                + "&notify_url=" + notify_url + "&sign_type=" + sign_type + "&timestamp=" + timestamp + "&version="  
	                + version;
	        //对未签名原始字符串进行签名       
	        String rsaSign = AlipaySignature.rsa256Sign(content, propertiesUtil.getProperties("ali_mch_private_secret_key"), "utf-8");
	
	        Map<String, String> map4 = new HashMap<String, String>();
	
	        map4.put("app_id", propertiesUtil.getProperties("ali_appid"));
	        map4.put("method", "alipay.trade.app.pay");
	        map4.put("format", "json");
	        map4.put("charset", "utf-8");
	        map4.put("sign_type", "RSA2");
	        map4.put("alipay_sdk", "alipay-sdk-java-dynamicVersionNo");
	        map4.put("timestamp", URLEncoder.encode(UtilDate.getDateFormatter(),"UTF-8"));
	        map4.put("version", "1.0");
	        map4.put("notify_url",  URLEncoder.encode(propertiesUtil.getProperties("ali_notify_url"),"UTF-8"));
	        //最后对请求字符串的所有一级value（biz_content作为一个value）进行encode，编码格式按请求串中的charset为准，没传charset按UTF-8处理
	        map4.put("biz_content", URLEncoder.encode(bizcontentJson.toString(), "UTF-8"));
	
	        Map par = AlipayCore.paraFilter(map4); //除去数组中的空值和签名参数
	        String json4 = AlipayCore.createLinkString(map4);   //拼接后的字符串
	
	        json4=json4 + "&sign=" + URLEncoder.encode(rsaSign, "UTF-8");
	
	        System.out.println(json4.toString());
	        Map<String, Object> dataMap = new HashMap<>();
	        dataMap.put("payInfo", json4.toString());
	        //AliPayMsg apm = new AliPayMsg();
	        data.setCode(Constants.STATUS_CODE.SUCCESS);
	        data.setMessage("充值成功");
	        data.setData(dataMap);  
		}else{
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("充值失败");
		}
       renderJson(data);
	}
	
	
	//微信回调
	public void callBack(){
		try{			
			Map<String, String> map = RequestXml.parseXml(getRequest());
			System.out.print("===========微信回调===============");
			HttpServletResponse response = getResponse();
			String return_code=map.get("return_code");
			String return_msg=map.get("return_msg");
			String mobile = map.get("mobile");
			String out_trade_no=map.get("out_trade_no");
			PayRecord record = PayRecord.dao.queryByOutTradeNo(out_trade_no);
			record.set("update_time", DateUtil.getNowTimestamp());
			record.set("status", Constants.PAY_STATUS.TRADE_SUCCESS);
			PayRecord.dao.updateInfo(record);
			if(return_code.equals("SUCCESS")&&return_msg==null){
				System.out.println("微信支付成功");
				if(StringUtil.isNoneBlank(out_trade_no)){
					String total_fee=map.get("total_fee");
					LoginDTO dto = LoginDTO.getInstance(getRequest());
					dto.setMoney(StringUtil.toBigDecimal(total_fee));
					dto.setMobile(mobile);
					service.updatePayInfo(dto);
					PayRecord r = PayRecord.dao.queryByOutTradeNo(out_trade_no);
					r.set("update_time", DateUtil.getNowTimestamp());
					r.set("status", Constants.PAY_STATUS.TRADE_SUCCESS);
					PayRecord.dao.updateInfo(r);
					int count=0;
					count=Integer.valueOf(out_trade_no.substring(2,4));
					response.setContentType("text/xml");
					response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
					}else{
						System.out.println("找不到用户:订单号---"+out_trade_no);
					}
				}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//支付宝回调
	public void aliCallBack() throws Exception{
		
		PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
		HttpServletRequest request = getRequest();
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
		    String name = (String) iter.next();
		    String[] values = (String[]) requestParams.get(name);
		    String valueStr = "";
		    for (int i = 0; i < values.length; i++) {
		        valueStr = (i == values.length - 1) ? valueStr + values[i]
		                    : valueStr + values[i] + ",";
		  }
		  //乱码解决，这段代码在出现乱码时使用。
		  //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
		  params.put(name, valueStr);
		 }
		//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
		//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
		boolean flag = AlipaySignature.rsaCheckV1(params, propertiesUtil.getProperties("ali_public_secret_key"), "UTF-8", "RSA2");
		if(flag){
			String orderNo = request.getParameter("out_trade_no");
			String trade_no=request.getParameter("trade_no");
			String trade_status=request.getParameter("trade_status");
			 //交易金额
			String total_fee = request.getParameter("total_amount");
			
			 //if(AlipayNotify.verify(params)){//验证成功
				
				PayRecord payRecord = PayRecord.dao.queryByOutTradeNo(orderNo);
				int userId = 0;
				if(payRecord != null){
					userId = payRecord.getInt("member_id");
				}
				if(trade_status.equals("TRADE_FINISHED")){
					int updateFlg = Member.dao.updateCharge(userId, StringUtil.toBigDecimal(total_fee));
					if(updateFlg != 0){
						CashJournal.dao.updateStatus(orderNo, Constants.FEE_TYPE_STATUS.APPLY_FAIL,trade_no);
						PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.TRADE_FINISHED, trade_no);
						renderText("success");
					}else{
						renderText("fail");
					}
				}else if(trade_status.equals("TRADE_SUCCESS")){
					int updateFlg = Member.dao.updateCharge(userId, StringUtil.toBigDecimal(total_fee));
					if(updateFlg != 0){
						CashJournal.dao.updateStatus(orderNo, Constants.FEE_TYPE_STATUS.APPLY_SUCCESS,trade_no);
						PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.TRADE_SUCCESS, trade_no);
						renderText("success");
					}else{
						renderText("fail");
					}
					System.out.println("支付宝支付成功");
				}else if(trade_status.equals("WAIT_BUYER_PAY")){
					CashJournal.dao.updateStatus(orderNo, Constants.FEE_TYPE_STATUS.APPLY_FAIL,trade_no);
					int updateFlg = PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.WAIT_BUYER_PAY, trade_no);
					if(updateFlg != 0){
						renderText("success");
					}else{
						renderText("fail");
					}
					System.out.println("交易创建，等待买家付款");
				}else if(trade_status.equals("TRADE_CLOSED")){
					CashJournal.dao.updateStatus(orderNo, Constants.FEE_TYPE_STATUS.APPLY_FAIL,trade_no);
					int updateFlg = PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.TRADE_CLOSED, trade_no);
					if(updateFlg != 0){
						renderText("success");
					}else{
						renderText("fail");
					}
					System.out.println("未付款交易超时关闭，或支付完成后全额退款");
				}
		}else{
			renderText("fail"); 
		}
	}
	
	//生成支付宝支付信息
	@Before(RequestInterceptor.class)
	public void generateAliPayInfo() throws Exception{
		
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		BigDecimal moneys = dto.getMoney().setScale(2);
		int userId = dto.getUserId();
		
		String orderNo = CashJournal.dao.queryCurrentCashNo();
		
		PayRecord pr = new PayRecord();
		pr.set("member_id", userId);
		pr.set("pay_type_cd", Constants.PAY_TYPE_CD.ALI_PAY);
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
			cash.set("remarks", "支付宝充值"+moneys);
			cash.set("create_time", DateUtil.getNowTimestamp());
			cash.set("update_time", DateUtil.getNowTimestamp());
			CashJournal.dao.saveInfo(cash);
			
			
			//实例化客户端
			PropertiesUtil propertiesUtil = PropertiesUtil.getInstance();
			String ppk = propertiesUtil.getProperties("ali_mch_private_secret_key");
			String pk = propertiesUtil.getProperties("ali_mch_public_secret_key");
			String appId = propertiesUtil.getProperties("ali_appid");
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, ppk, "json", "UTF-8", pk, "RSA2");
			//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
			AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
			//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
			AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
			model.setBody("掌上茶宝");
			model.setSubject("掌上茶宝充值");
			model.setOutTradeNo(orderNo);
			model.setTimeoutExpress("30m");
			model.setTotalAmount(StringUtil.toString(moneys));
			model.setProductCode("QUICK_MSECURITY_PAY");
			request.setBizModel(model);
			request.setNotifyUrl(propertiesUtil.getProperties("ali_notify_url"));
			try{
			     //这里和普通的接口调用不同，使用的是sdkExecute
			     AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			     ReturnData data = new ReturnData();
			     Map<String, Object> dataMap = new HashMap<>();
			     dataMap.put("payInfo", response.getBody());
			     //AliPayMsg apm = new AliPayMsg();
			     data.setCode(Constants.STATUS_CODE.SUCCESS);
			     data.setMessage("获取支付信息成功");
			     data.setData(dataMap); 
			     System.out.println(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
			     renderJson(data);
				} catch (AlipayApiException e) {
					e.printStackTrace();
				}
		}else{
			ReturnData data = new ReturnData();
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("获取支付信息失败");
			renderJson(data);
		}
		
	}
}
