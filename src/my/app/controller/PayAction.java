package my.app.controller;

import java.beans.Transient;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.huadalink.route.ControllerBind;

import com.alipay.api.domain.Data;
import com.alipay.api.internal.util.AlipaySignature;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

import my.app.service.LoginService;
import my.core.constants.Constants;
import my.core.model.Member;
import my.core.model.PayRecord;
import my.core.model.ReturnData;
import my.core.pay.AlipayCore;
import my.core.pay.AlipayNotify;
import my.core.pay.RequestXml;
import my.core.service.MemberService;
import my.pvcloud.dto.LoginDTO;
import my.pvcloud.model.PayModel;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.PropertiesUtil;
import my.pvcloud.util.StringUtil;
import my.pvcloud.util.UtilDate;
import my.pvcloud.util.WXRequestUtil;

@ControllerBind(key = "/pay", path = "/pay")
public class PayAction extends Controller{

	MemberService service = Enhancer.enhance(MemberService.class);

	//回调
	public void Vertify() throws Exception{
		
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
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
			params.put(name, valueStr);
		}
		
		response.setContentType("text/html;charset=utf-8");   		
	    PrintWriter out = response.getWriter(); 
		
	    PayModel model = new PayModel();
	    model.setOutTradeNo(request.getParameter("out_trade_no"));
		model.setTradeNo(request.getParameter("trade_no"));
		model.setTradeStatus(request.getParameter("trade_status"));
		
		//交易金额
		model.setTotalFee(request.getParameter("total_fee"));
		
		model.setBuyerEmail(request.getParameter("buyer_email"));
		int count=0;
		count=Integer.valueOf(model.getOutTradeNo().substring(2,4));

		if(AlipayNotify.verify(params)){//验证成功
			
			if(model.getTradeStatus().equals("TRADE_FINISHED")){
		
			} else if (model.getTradeStatus().equals("TRADE_SUCCESS")){
				
				System.out.println("支付宝支付成功");
				/*UserInfo userInfo=userInfoBiz.queryAlipayTradeNo(out_trade_no);	
				List<Alipay> alipayList=alipayBiz.queryByOutTradeNo(out_trade_no);
				if(userInfo!=null && alipayList.size()==0){
					
					userInfo.setTotalCount(userInfo.getTotalCount()+count);
					userInfoBiz.update(userInfo);
					Alipay alipay=new Alipay();
					alipay.setBuyerEmail(buyerEmail);
					alipay.setTotalFee(total_fee);
					alipay.setOutTradeNo(out_trade_no);
					alipay.setTradeNo(trade_no);
					alipay.setTradeStatus(trade_status);
					alipay.setCreateDate(new Date());
					alipayBiz.add(alipay);	
			}*/
		}
			 out.println("success");	//请不要修改或删除
		}else{//验证失败
		    out.println("fail"); 	//请不要修改或删除
		}
	}
	
	public void generateWxPayInfo() throws Exception{
		ReturnData data = new ReturnData();
		LoginDTO dto = LoginDTO.getInstance(getRequest());
		BigDecimal moneys = dto.getMoney();
		int userId = dto.getUserId();
		String orderInfo = WXRequestUtil.createOrderInfo(StringUtil.getOrderNo(), "1",userId);
		String order = WXRequestUtil.httpOrder(orderInfo);
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		data.setMessage(order);
		renderJson(data);
	}
	
	public void generateAliPayInfo() throws Exception{
		
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
	        map.put("app_id", propertiesUtil.getProperties("ali_appid"));
	        map.put("method", "alipay.trade.app.pay");
	        map.put("format", "json");
	        map.put("charset", "utf-8");
	        map.put("sign_type", "RSA");
	        map.put("timestamp", UtilDate.getDateFormatter());
	        map.put("version", "1.0");
	        map.put("notify_url", propertiesUtil.getProperties("ali_notify_url"));
	
	        Map<String, String> m = new HashMap<String, String>();
	
	        
	        m.put("body", "支付宝充值");
	        m.put("subject", "支付宝充值"+moneys);
	        m.put("out_trade_no", orderNo);
	        m.put("timeout_express", "30m");
	        m.put("total_amount", StringUtil.toString(moneys));
	        m.put("seller_id", propertiesUtil.getProperties("ali_seller_id"));
	        m.put("product_code", "QUICK_MSECURITY_PAY");
	
	        JSONObject bizcontentJson= JSONObject.fromObject(m);
	
	        map.put("biz_content", bizcontentJson.toString());
	        //对未签名原始字符串进行签名       
	        String rsaSign = AlipaySignature.rsaSign(map, propertiesUtil.getProperties("ali_mch_private_secret_key"), "utf-8");
	
	        Map<String, String> map4 = new HashMap<String, String>();
	
	        map4.put("app_id", propertiesUtil.getProperties("ali_appid"));
	        map4.put("method", "alipay.trade.app.pay");
	        map4.put("format", "json");
	        map4.put("charset", "utf-8");
	        map4.put("sign_type", "RSA");
	        map4.put("timestamp", URLEncoder.encode(UtilDate.getDateFormatter(),"UTF-8"));
	        map4.put("version", "1.0");
	        map4.put("notify_url",  URLEncoder.encode(propertiesUtil.getProperties("ali_notify_url"),"UTF-8"));
	        //最后对请求字符串的所有一级value（biz_content作为一个value）进行encode，编码格式按请求串中的charset为准，没传charset按UTF-8处理
	        map4.put("biz_content", URLEncoder.encode(bizcontentJson.toString(), "UTF-8"));
	
	        Map par = AlipayCore.paraFilter(map4); //除去数组中的空值和签名参数
	        String json4 = AlipayCore.createLinkString(map4);   //拼接后的字符串
	
	        json4=json4 + "&sign=" + URLEncoder.encode(rsaSign, "UTF-8");
	
	        System.out.println(json4.toString());
	
	        //AliPayMsg apm = new AliPayMsg();
	        data.setCode(Constants.STATUS_CODE.SUCCESS);
	        data.setMessage("充值成功");
	        data.setData(json4.toString());  
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
	@Transient
	public void aliCallBack() throws Exception{
		
		HttpServletRequest request = getRequest();
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]:valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		
		HttpServletResponse response = getResponse();
		response.setContentType("text/html;charset=utf-8");   		
	    PrintWriter out = response.getWriter(); 
		
		String orderNo = request.getParameter("out_trade_no");
		String trade_no=request.getParameter("trade_no");
		String trade_status=request.getParameter("trade_status");
		 //交易金额
		String total_fee = request.getParameter("total_amount");
		
		 if(AlipayNotify.verify(params)){//验证成功
			
			PayRecord payRecord = PayRecord.dao.queryByOutTradeNo(orderNo);
			int userId = 0;
			if(payRecord != null){
				userId = payRecord.getInt("member_id");
			}
			if(trade_status.equals("TRADE_FINISHED")){
				int updateFlg = Member.dao.updateCharge(userId, StringUtil.toBigDecimal(total_fee));
				if(updateFlg != 0){
					PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.TRADE_FINISHED, trade_no);
					out.println("success");
				}else{
					out.println("fail");
				}
			}else if(trade_status.equals("TRADE_SUCCESS")){
				int updateFlg = Member.dao.updateCharge(userId, StringUtil.toBigDecimal(total_fee));
				if(updateFlg != 0){
					PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.TRADE_SUCCESS, trade_no);
					out.println("success");
				}else{
					out.println("fail");
				}
				System.out.println("支付宝支付成功");
			}else if(trade_status.equals("WAIT_BUYER_PAY")){
				int updateFlg = PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.WAIT_BUYER_PAY, trade_no);
				if(updateFlg != 0){
					out.println("success");
				}else{
					out.println("fail");
				}
				System.out.println("交易创建，等待买家付款");
			}else if(trade_status.equals("TRADE_CLOSED")){
				int updateFlg = PayRecord.dao.updatePay(orderNo, Constants.PAY_STATUS.TRADE_CLOSED, trade_no);
				if(updateFlg != 0){
					out.println("success");
				}else{
					out.println("fail");
				}
				System.out.println("未付款交易超时关闭，或支付完成后全额退款");
			}
		}else{//验证失败
		    out.println("fail"); 	//请不要修改或删除
		}
	}
}
