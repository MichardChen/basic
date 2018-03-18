package my.core.model;

import java.sql.Timestamp;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_vertify_code", pk = "id")
public class VertifyCode extends Model<VertifyCode> {
	
	public static final VertifyCode dao = new VertifyCode();
	
	public boolean saveVertifyCode(String mobile,String userTypeCd,String code,Timestamp createTime,Timestamp updateTime,String codeTypeCd){
		return new VertifyCode().set("mobile", mobile).set("user_type_cd", userTypeCd).set("code", code).set("create_time", createTime).set("update_time", updateTime).set("code_type_cd", codeTypeCd).set("expire_time", DateUtil.getVertifyCodeExpireTime()).save();
	}
	
	public VertifyCode queryVertifyCode(String mobile,String codeTypeCd){
		return VertifyCode.dao.findFirst("select * from t_vertify_code where mobile=? and code_type_cd='"+codeTypeCd+"' order by create_time desc limit 1", mobile);
	}
	
	public void updateVertifyCode(String mobile,String code,String codeTypeCd){
		Db.update("update t_vertify_code set code='"+code+"',expire_time='"+DateUtil.getVertifyCodeExpireTime()+"',update_time='"+DateUtil.getNowTimestamp()+"' where mobile='"+mobile+"' and code_type_cd='"+codeTypeCd+"'");
	}
	
	public void updateVertifyCodeExpire(String mobile,Timestamp expireTime,String codeTypeCd){
		Db.update("update t_vertify_code set expire_time='"+expireTime+"' where mobile='"+mobile+"' and code_type_cd='"+codeTypeCd+"'");
	}
	
	public Long queryTodayCount(String date){
		return Db.queryLong("select count(*) from t_vertify_code where update_time like '%"+date+"%'");
	}
}
