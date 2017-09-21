package my.app.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huadalink.route.ControllerBind;

import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;

import my.app.service.LoginService;
import my.core.pay.AlipayNotify;
import my.pvcloud.model.PayModel;

@ControllerBind(key = "/pay", path = "/pay")
public class PayAction extends Controller{

	LoginService service = Enhancer.enhance(LoginService.class);

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
}
