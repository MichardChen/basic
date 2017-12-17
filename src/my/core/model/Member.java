package my.core.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import my.core.constants.Constants;
import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_member", pk = "id")
public class Member extends Model<Member> {
	
	public static final Member dao = new Member();
	
	public Page<Member> queryMemberListByPage(int page,int size,String mobile,String name,String storeName,String type){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isBlank(storeName)){
			String sql="";
			String select="select *";
			if(StringUtil.isNoneBlank(mobile)){
				strBuf.append("and mobile=?");
				param.add(mobile);
			}
			if(StringUtil.isNoneBlank(name)){
				strBuf.append("and name=?");
				param.add(name);
			}
			
			if(StringUtil.isNoneBlank(type)){
				strBuf.append("and role_cd=?");
				param.add(type);
			}
			
			sql=" from t_member where 1=1 "+strBuf.toString()+" order by create_time desc";
			return Member.dao.paginate(page, size, select, sql,param.toArray());
		}else{
			String sql="";
			String select="select a.*";
			if(StringUtil.isNoneBlank(mobile)){
				strBuf.append("and a.mobile=?");
				param.add(mobile);
			}
			if(StringUtil.isNoneBlank(name)){
				strBuf.append("and a.name=?");
				param.add(name);
			}
			if(StringUtil.isNoneBlank(type)){
				strBuf.append("and a.role_cd=?");
				param.add(type);
			}
			
			strBuf.append("and b.store_name like '%"+storeName+"%'");
			sql=" from t_member a inner join t_store b on a.store_id=b.id where 1=1 "+strBuf.toString()+" order by a.create_time desc";
			return Member.dao.paginate(page, size, select, sql,param.toArray());
		}
	}
	
	public List<Member> exportData(String mobile,String name,String storeName,String type){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isBlank(storeName)){
			String sql="";
			String select="select *";
			if(StringUtil.isNoneBlank(mobile)){
				strBuf.append(" and mobile='"+mobile+"'");
			}
			if(StringUtil.isNoneBlank(name)){
				strBuf.append(" and name='"+name+"'");
			}
			
			if(StringUtil.isNoneBlank(type)){
				strBuf.append(" and role_cd='"+type+"'");
				param.add(type);
			}
			
			sql=" from t_member where 1=1 "+strBuf.toString()+" order by create_time desc";
			return Member.dao.find(select+sql);
		}else{
			String sql="";
			String select="select a.*";
			if(StringUtil.isNoneBlank(mobile)){
				strBuf.append(" and a.mobile='"+mobile+"'");
				param.add(mobile);
			}
			if(StringUtil.isNoneBlank(name)){
				strBuf.append(" and a.name='"+name+"'");
				param.add(name);
			}
			if(StringUtil.isNoneBlank(type)){
				strBuf.append(" and a.role_cd='"+type+"'");
				param.add(type);
			}
			
			strBuf.append("and b.store_name like '%"+storeName+"%'");
			sql=" from t_member a inner join t_store b on a.store_id=b.id where 1=1 "+strBuf.toString()+" order by a.create_time desc";
			return Member.dao.find(select+sql);
		}
	}
	
	public Member queryMember(String mobile){
		return Member.dao.findFirst("select * from t_member where mobile=?",mobile);
	}
	
	public List<Member> queryStoreMember(int storeId,int pageSize,int pageNum,String role){
		int fromRow = pageSize*(pageNum-1);
		if(StringUtil.isBlank(role)){
			return Member.dao.find("select * from t_member where store_id=? order by create_time desc limit "+fromRow+","+pageSize,storeId);
		}else{
			return Member.dao.find("select * from t_member where store_id=? and role_cd='"+role+"' order by create_time desc limit "+fromRow+","+pageSize,storeId);
		}
	}
	
	public Member queryMemberById(int id){
		return Member.dao.findFirst("select * from t_member where id=?",id);
	}
	
	public Member queryMemberByInviteCode(String code){
		return Member.dao.findFirst("select * from t_member where id_code='"+code+"'");
	}
	
	public int saveMember(String mobile,String userPwd,int sex,String userTypeCd,String status,int storeId){
		Member member = new Member().set("mobile", mobile).set("userpwd", userPwd).set("member_grade_cd", userTypeCd).set("create_time", DateUtil.getNowTimestamp()).set("update_time", DateUtil.getNowTimestamp()).set("points", 0).set("moneys", 0).set("sex", sex).set("status", status).set("id_code", StringUtil.getIdCode()).set("store_id", storeId).set("role_cd", Constants.ROLE_CD.NORMAL_USER);
		boolean isSave = member.save();
		return member.getInt("id");
	}
	
	public void updatePwd(String mobile,String userPwd){
		Db.update("update t_member set userpwd='"+userPwd+"',update_time='"+DateUtil.getNowTimestamp()+"' where mobile="+mobile);
	}
	
	public int updateIdCardInfo(int userId,String idCardNo,String idCardImg,String ownerName){
		if(StringUtil.isNoneBlank(idCardImg)){
			return Db.update("update t_member set id_card_no='"+idCardNo+"',id_card_img='"+idCardImg+"',update_time='"+DateUtil.getNowTimestamp()+"',name='"+ownerName+"' where id="+userId);
		}else{
			return Db.update("update t_member set id_card_no='"+idCardNo+"',update_time='"+DateUtil.getNowTimestamp()+"',name='"+ownerName+"' where id="+userId);

		}
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
		return Member.dao.findFirst("select * from t_member where mobile='"+mobile+"'");
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
		return Member.dao.findFirst("select * from t_member where mobile='"+name+"' and userpwd='"+pwd+"'");
	}
	
	public int updatePoints(int userId,int points){
		return Db.update("update t_member set points=points+"+points+" where id="+userId);
	}
	
	public int updateMoneys(int userId,BigDecimal moneys){
		return Db.update("update t_member set moneys="+moneys+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public int updateRole(int userId,String role){
		return Db.update("update t_member set role_cd='"+role+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public int cutMoneys(int userId,BigDecimal moneys){
		return Db.update("update t_member set moneys=moneys-"+moneys+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public int updateCharge(int userId,BigDecimal moneys){
		return Db.update("update t_member set moneys=moneys+"+moneys+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public int updateMemberData(int userId,String userName,int sex,String icon){
		if(StringUtil.isNoneBlank(icon)){
			return Db.update("update t_member set name='"+userName+"',sex="+sex+",icon='"+icon+"' where id="+userId);
		}else{
			return Db.update("update t_member set name='"+userName+"',sex="+sex+" where id="+userId);
		}
	}
	
	public int updateIcon(int userId,String icon){
		return Db.update("update t_member set icon='"+icon+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public int updateNickName(int userId,String nickName){
		return Db.update("update t_member set nick_name='"+nickName+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public int updateSex(int userId,int sex){
		return Db.update("update t_member set sex="+sex+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public int updateCertification(int userId,String name,String status){
		return Db.update("update t_member set name='"+name+"',status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public int updateQQ(int userId,String qq){
		return Db.update("update t_member set qq='"+qq+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	} 
	
	public int updateWX(int userId,String wx){
		return Db.update("update t_member set wx='"+wx+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	} 
	
	////////////
	public Page<Member> queryByPage(int page,int size){
		
		String sql=" from t_member where 1=1 order by create_time desc";
		String select="select * ";
		return Member.dao.paginate(page, size, select, sql);
	}
	
	public List<Member> queryMemberList(int pageSize,int pageNum){
		int fromRow = pageSize*(pageNum-1);
		return Member.dao.find("select * from t_member order by update_time desc limit "+fromRow+","+pageSize);
	}
	
	public Member queryById(int id){
		return Member.dao.findFirst("select * from t_member where id = ?",id);
	}
	
	public boolean updateInfo(Member data){
		return new Member().setAttrs(data).update();
	}
	
	public boolean saveInfo(Member data){
		return new Member().setAttrs(data).save();
	}
	
	public boolean del(int id){
		return Member.dao.deleteById(id);
	}
	
	public int updateMemberStatus(int id,String status){
		return Db.update("update t_member set status='"+status+"',update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
	}
	
	public int updatePay(String mobile,String userPwd){
		return Db.update("update t_member set paypwd='"+userPwd+"',update_time='"+DateUtil.getNowTimestamp()+"' where mobile="+mobile);
	}
	
	public int bindStore(int userId,int storeId){
		return Db.update("update t_member set store_id="+storeId+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+userId);
	}
	
	public Page<Member> queryStoreMemberList(int pageNum,int pageSize,int storeId,String name,String mobile){
		String whereStr = "";
		if(StringUtil.isNoneBlank(name)){
			whereStr = " and name like '%"+name+"%'";
		}
		if(StringUtil.isNoneBlank(mobile)){
			whereStr = " and mobile like '%"+mobile+"%'";
		}
		
		String sql=" from t_member where store_id="+storeId+whereStr+" order by update_time desc";
		String select="select * ";
		return Member.dao.paginate(pageNum, pageSize, select, sql);
	}
}

