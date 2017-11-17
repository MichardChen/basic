package my.core.interceptor;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import my.core.constants.Constants;
import my.core.model.AcceessToken;
import my.core.model.ReturnData;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

public class ContainFileInterceptor {

	private AcceessToken tokenDao = new AcceessToken();
	
	public ReturnData vertifyToken(HttpServletRequest request){
		
		ReturnData data = new ReturnData();
		data.setCode(Constants.STATUS_CODE.SUCCESS);
		
		String tokens = request.getParameter("accessToken"); 
		String platForm = request.getParameter("platForm");
		
		AcceessToken at = tokenDao.queryPlatToken(StringUtil.toInteger( request.getParameter("userId"))
											     ,request.getParameter("userTypeCd")
											     ,platForm
											     ,tokens);
		
		AcceessToken androidToken = tokenDao.queryToken(StringUtil.toInteger( request.getParameter("userId"))
													   ,request.getParameter("userTypeCd")
													   ,Constants.PLATFORM.ANDROID);
		
		AcceessToken iosToken = tokenDao.queryToken(StringUtil.toInteger( request.getParameter("userId"))
												   ,request.getParameter("userTypeCd")
												   ,Constants.PLATFORM.IOS);
		
		if((androidToken == null)&&(iosToken == null)){
			data.setCode(Constants.STATUS_CODE.FAIL);
			data.setMessage("对不起，您还没有登录");
			return data;
		}
		
		if(((StringUtil.equals(Constants.PLATFORM.ANDROID, platForm))
				&&(iosToken != null)
				&&(StringUtil.isNoneBlank(iosToken.getStr("token"))))||
			((StringUtil.equals(Constants.PLATFORM.IOS, platForm))
				&&(androidToken != null)
				&&StringUtil.isNoneBlank(androidToken.getStr("token")))){
			data.setCode(Constants.STATUS_CODE.LOGIN_ANOTHER_PLACE);
			data.setMessage("对不起，您的账号在另一个地点登录，您被迫下线了，请重新登录");
			return data;
		}
		
		if(at==null || !StringUtil.equals(tokens, at.getStr("token"))){
			//不相等，您的账号在另一个地点登录，您被迫下线了，请重新登录
			data.setCode(Constants.STATUS_CODE.LOGIN_ANOTHER_PLACE);
			data.setMessage("对不起，您的账号在另一个地点登录，您被迫下线了，请重新登录");
			return data;
		}else{
			//判断是不是token过期了
			Timestamp now = DateUtil.getNowTimestamp();
			if(at.getTimestamp("expire_time") == null || now.after(at.getTimestamp("expire_time"))){
				//过期了
				data.setCode(Constants.STATUS_CODE.LOGIN_ANOTHER_PLACE);
				data.setMessage("对不起，登录账号过期了，请重新登录");
				return data;
			}
		}
		return data;
	}
}
