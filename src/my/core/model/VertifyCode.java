package my.core.model;

import java.sql.Timestamp;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;

@TableBind(table = "t_vertify_code", pk = "id")
public class VertifyCode extends Model<VertifyCode> {
	
	public static final VertifyCode dao = new VertifyCode();
	
	public boolean saveVertifyCode(String mobile,String userTypeCd,String code,Timestamp createTime,Timestamp updateTime){
		return new VertifyCode().set("mobile", mobile).set("user_type_cd", userTypeCd).set("code", code).set("create_time", createTime).set("update_time", updateTime).set("expire_time", DateUtil.getVertifyCodeExpireTime()).save();
	}
	
	public VertifyCode queryVertifyCode(String mobile){
		return VertifyCode.dao.findFirst("select * from t_vertify_code where mobile=? order by create_time desc limit 1", mobile);
	}
	
	public void updateVertifyCode(String mobile,String code){
		Db.update("update t_vertify_code set code='"+code+"',expire_time='"+DateUtil.getVertifyCodeExpireTime()+"',update_time='"+DateUtil.getNowTimestamp()+"' where mobile="+mobile);
	}
	
	public void updateVertifyCodeExpire(String mobile,Timestamp expireTime){
		Db.update("update t_vertify_code set expire_time='"+expireTime+"' where mobile="+mobile);
	}
}
