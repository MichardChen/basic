package my.core.interceptor;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import my.core.constants.Constants;
import my.core.model.AcceessToken;
import my.core.model.ReturnData;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

public class RequestInterceptor implements Interceptor{

	private AcceessToken tokenDao = new AcceessToken();
	
	@Override
	public void intercept(Invocation invocation){
		ReturnData data = vertifyToken(invocation);
		if(StringUtil.equals(data.getCode(),Constants.STATUS_CODE.FAIL)){
			invocation.getController().renderJson(data);
			return;
		}
		invocation.invoke();
	}
	
	public ReturnData vertifyToken(Invocation invocation){
		
		ReturnData data = new ReturnData();
		HttpServletRequest request = invocation.getController().getRequest();
		
		String tokens = request.getParameter("accessToken"); 
		AcceessToken token = tokenDao.queryToken(StringUtil.toInteger(request.getParameter("userId"))
											    ,request.getParameter("userTypeCd"));
		if(token == null){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有登录");
			return data;
		}
		
		if(!StringUtil.equals(token.getStr("token"), tokens)){
			data.setCode(Constants.STATUS_CODE.LOGIN_ANOTHER_PLACE);
			data.setMessage("对不起，您的账号在另一个地点登录，您被迫下线了，请重新登录");
			return data;
		}
		
		Timestamp atTime = token.getTimestamp("expire_time");
		Timestamp now = DateUtil.getNowTimestamp();
		if(atTime == null || now.after(atTime)){
			//过期了
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，登录账号过期了，请重新登录");
			return data;
		}
		return data;
	}
}
