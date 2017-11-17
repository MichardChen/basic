package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;


@TableBind(table = "t_accesstoken", pk = "id")
public class AcceessToken extends Model<AcceessToken> {

	public static final AcceessToken dao = new AcceessToken();
	
	public boolean saveToken(int userId,String userTypeCd,String token,String platform){
		return new AcceessToken().set("user_id", userId).set("user_type_cd", userTypeCd).set("token", token).set("expire_time", DateUtil.getAccessTokenExpireTime()).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).set("platform", platform).save();
	}
	
	public void updateToken(int userId,String token,String platform){
		 Db.update("update t_accesstoken set token='"+token+"',expire_time='"+DateUtil.getAccessTokenExpireTime()+"',update_time='"+DateUtil.getNowTimestamp()+"' where user_id="+userId+" and platform='"+platform+"'");
	}
	
	public AcceessToken queryById(int userId,String platform){
		return AcceessToken.dao.findFirst("select * from t_accesstoken where user_Id=? and platform=? order by update_time desc limit 1",userId,platform);
	}
	
	public AcceessToken queryToken(int userId,String userTypeCd,String platform){
		return AcceessToken.dao.findFirst("select * from t_accesstoken where user_Id=? and user_type_cd=? and platform=? order by update_time desc limit 1",userId,userTypeCd,platform);
	}
	
	public AcceessToken queryPlatToken(int userId,String userTypeCd,String platform,String token){
		return AcceessToken.dao.findFirst("select * from t_accesstoken where user_Id=? and user_type_cd=? and platform=? and token=? order by update_time desc limit 1",userId,userTypeCd,platform,token);
	}
}
