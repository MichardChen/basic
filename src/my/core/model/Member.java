package my.core.model;

import java.math.BigDecimal;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.core.constants.Constants;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_member", pk = "id")
public class Member extends Model<Member> {
	
	public static final Member dao = new Member();
	
	public Member queryMember(String mobile){
		return Member.dao.findFirst("select * from t_member where mobile=? and status=1",mobile);
	}
	
	public Member queryMemberById(int id){
		return Member.dao.findFirst("select * from t_member where id=?",id);
	}
	
	public int saveMember(String mobile,String userPwd,int sex,String userTypeCd){
		Member member = new Member().set("mobile", mobile).set("userPwd", userPwd).set("member_grade_cd", userTypeCd).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).set("points", 0).set("moneys", 0).set("sex", sex);
		boolean isSave = member.save();
		return member.getInt("id");
	}
	
	public void updatePwd(String mobile,String userPwd){
		Db.update("update t_member set userPwd='"+userPwd+"',update_time='"+DateUtil.getNowTimestamp()+"' where mobile="+mobile);
	}
	
	public Long queryMemberListCount(String memberName){
		if(!StringUtil.isBlank(memberName)){
			return Db.queryLong("select count(*) from t_member where name like '%"+memberName+"%'");
		}else{
			return Db.queryLong("select count(*) from t_member");
		}
	}
	
	public List<Member> queryMemberList(String member,int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isBlank(member)){
			return Member.dao.find("select * from t_member order by update_time desc limit "+fromRow+","+pageSize);
		}else{
			return Member.dao.find("select * from t_member where name like '%"+member+"%' order by update_time desc limit "+fromRow+","+pageSize);
		}
	}
	
	public Member queryMemberByMobile(String mobile){
		return Member.dao.findFirst("select * from t_member where mobile='"+mobile+"' and status=1");
	}
	
	public int updateMember(int userId
						   ,String name
						   ,String weixinPayAccount
						   ,String aliPayAccount
						   ,int points
						   ,BigDecimal moneys){
		
		return Db.update("update t_member set name='"+name+"',weixin_pay_account='"+weixinPayAccount+"',ali_pay_account='"+aliPayAccount+"',points="+points+",moneys="+moneys+" where id="+userId);
	}
	
	public Member queryMember(String name,String pwd){
		return Member.dao.findFirst("select * from t_member where mobile='"+name+"' and userpwd='"+pwd+"' and status=1");
	}
	
	public int updatePoints(int userId,int points){
		return Db.update("update t_member set points=points+"+points+" where id="+userId);
	}
	
	public int updateMoneys(int userId,BigDecimal moneys){
		return Db.update("update t_member set moneys="+moneys+" where id="+userId);
	}
	
	public int updateMemberData(int userId,String userName,int sex,String icon){
		if(StringUtil.isNoneBlank(icon)){
			return Db.update("update t_member set name='"+userName+"',sex="+sex+",icon='"+icon+"' where id="+userId);
		}else{
			return Db.update("update t_member set name='"+userName+"',sex="+sex+" where id="+userId);
		}
	}
}

