package my.core.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.StringUtil;

@TableBind(table = "s_user", pk = "user_id")
public class User extends Model<User> {
	public static final User dao = new User();

	public User getUserByUserName(String userName) {
		return dao.findFirst("select * from s_user m where m.userName=?", userName);
	}

	public User getRoleByUserId(String userName){
		String sql ="select u.*,r.role_name from s_user u,  s_user_role ur,  s_role r where u.user_id=ur.user_id and ur.role_id=r.role_id and u.username= ? ";
		return  dao.findFirst(sql, userName);
	}
	public User queryByUserName(String userName,String pwd){
		return dao.findFirst("select * from s_user where username=? and password=?",userName,pwd);
	}
	
	public Page<User> queryAllByPage(int pageNum,int size,String roleName,int agentId){
		if(agentId==0){
			return User.dao.paginate(pageNum, size, "select o.*,sum(p.m_sum) as m_sum,sum(p.t_sum) as t_sum", "from (select m.user_id,m.agent_id,m.realname,m.mobile,m.head_pic,m.join_date,z.code_name,n.user_role_id,n.role_id,o.role_name from s_user m,s_user_role n,s_role o,s_code z where m.user_id=n.user_id and m.postion_code=z.code_value and m.effective_mark=1 and n.role_id=o.role_id and o.role_name=?) o left join cust_order_type_stat p on o.user_id=p.user_id group by o.user_id order by o.join_date desc",roleName);
		}else{
			return User.dao.paginate(pageNum, size, "select o.*,sum(p.m_sum) as m_sum,sum(p.t_sum) as t_sum", "from (select m.user_id,m.agent_id,m.realname,m.mobile,m.head_pic,m.join_date,z.code_name,n.user_role_id,n.role_id,o.role_name from s_user m,s_user_role n,s_role o,s_code z where m.user_id=n.user_id and m.postion_code=z.code_value and m.effective_mark=1 and n.role_id=o.role_id and o.role_name=? and m.agent_id=? ) o left join cust_order_type_stat p on o.user_id=p.user_id group by o.user_id order by o.join_date desc",roleName,agentId);
		}
	}
	public Page<User> queryByCondition(int pageNum,int size,String roleName,int agentId,String codeName,String realname){
		StringBuffer strBuf=new StringBuffer();
		List<Object> param=new ArrayList<Object>();
		if(("请选择员工职位").equals(codeName)){
			codeName=null;
		}
		if(roleName!=null && !("").equals(roleName)){
			strBuf.append(" and o.role_name = ?");
			param.add(roleName);
		}
		if(agentId!=0){
			strBuf.append(" and m.agent_id = ?");
			param.add(agentId);
		}
		if(codeName!=null && !("").equals(codeName.trim())){
			strBuf.append(" and z.code_name like ?");
			param.add("%"+codeName+"%");
		}
		if(realname!=null && !("").equals(realname.trim())){
			strBuf.append(" and m.realname like ?");
			param.add("%"+realname+"%");
		}
		String sql="from (select m.user_id,m.agent_id,m.realname,m.mobile,m.head_pic,m.join_date,z.code_name,n.user_role_id,n.role_id,o.role_name from s_user m,s_user_role n,s_role o,s_code z where m.user_id=n.user_id and m.postion_code=z.code_value and n.role_id=o.role_id and m.effective_mark=1"+strBuf.toString()+") o left join cust_order_type_stat p on o.user_id=p.user_id group by o.user_id order by o.join_date desc";
		return User.dao.paginate(pageNum, size, "select o.*,sum(p.m_sum) as m_sum,sum(p.t_sum) as t_sum",sql,param.toArray());
	}
	
	public boolean updateEffMark(int userId){
		return User.dao.findById(userId).set("effective_mark", 0).update();
	}
	
	public boolean saveAgentStaff(int agentId,String realname,String postionCode,String mobile,Date joinDate){
		return new User().set("realname", realname).set("postion_code", postionCode).set("mobile", mobile).set("join_date", joinDate).set("agent_id", agentId).save();
	}
	public boolean updateAgentStaff(int userId,String realname,String postionCode,String mobile,Date joinDate){
		return new User().findById(userId).set("realname", realname).set("postion_code", postionCode).set("mobile", mobile).set("join_date", joinDate).update();
	}
	public User queryByRealnameMobile(String realname,String mobile){
		return User.dao.findFirst("select m.user_id from s_user m where realname=? and mobile=? order by m.user_id desc",realname,mobile);
	}
	public User queryByPosition(int agentId,String status,String postionCode){
		if(agentId==0){
			return User.dao.findFirst("select count(user_id) as count from s_user m where m.status=? and m.postion_code=? and effective_mark=1",status,postionCode);
		}else{
			return User.dao.findFirst("select count(user_id) as count from s_user m where m.agent_id=? and m.status=? and m.postion_code=? and effective_mark=1",agentId,status,postionCode);
		}
	}
	public Page<User> queryUserInfoByAgentId(int pageNum,int size,int agentId,String status,String postionCode){
		if(agentId==0){
			return User.dao.paginate(pageNum,size,"select m.user_id,m.head_pic,m.mobile,m.realname,sum(n.t_sum) as sum","from s_user m left join cust_order_type_stat n  on (m.user_id=n.user_id) where m.status=? and m.postion_code=? and effective_mark=1 group by mobile order by m.user_id",status,postionCode);
		}else{
			return User.dao.paginate(pageNum,size,"select m.user_id,m.head_pic,m.mobile,m.realname,sum(n.t_sum) as sum","from s_user m left join cust_order_type_stat n  on (m.user_id=n.user_id) where m.agent_id=? and m.status=? and m.postion_code=? and effective_mark=1 group by mobile order by m.user_id",agentId,status,postionCode);
		}
	}
	public boolean updateByUserId(int userId,String status){
		return new User().findById(userId).set("status", status).update();
	}
	
	public boolean saveInfo(String username,String password,String mobile,String email,int agentId,Date joinDate ){
		return new User().set("username", username).set("password", password).set("mobile", mobile).set("email", email).set("agent_id", agentId).set("join_date", joinDate).save();
	}
	public boolean updateInfo(int userId,String username,String password,String mobile,String email,Date joinDate ){
		return new User().findById(userId).set("username", username).set("password", password).set("mobile", mobile).set("email", email).set("join_date", joinDate).update();
	}
	public boolean updateUInfo(int userId,String username,String mobile,String email,Date joinDate ){
		return new User().findById(userId).set("username", username).set("mobile", mobile).set("email", email).set("join_date", joinDate).update();
	}
	
	public User queryUserInfo(String username,String mobile,int agentId){
		return User.dao.findFirst("select user_id from s_user where username=? and mobile=? and agent_id=?",username,mobile,agentId);
	}
	public User queryById(int userId){
		return User.dao.findFirst("select * from s_user where user_id=?",userId);
	}
	public User queryByUserName(String username){
		return User.dao.findFirst("select m.user_id,p.role_name from s_user m,s_user_role n,s_role p where m.user_id=n.user_id and n.role_id=p.role_id and username=? and effective_mark=1",username);
	}
	public int updateMoneys(int userId,BigDecimal moneys){
		return Db.update("update s_user set moneys="+moneys+" where user_id="+userId);
	}
	
	public Page<User> queryUserListByPage(int page,int size,String mobile){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(mobile)){
			strBuf.append("and mobile=?");
			param.add(mobile);
		}
		
		sql=" from s_user where 1=1 "+strBuf.toString()+" order by create_time desc";
		return User.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public User queryUser(String mobile){
		return User.dao.findFirst("select * from s_user where mobile=?",mobile);
	}
	
	public User queryUserById(int id){
		return User.dao.findFirst("select * from s_user where user_id=?",id);
	}
	
	public Page<User> queryByPage(int page,int size){
		
		String sql=" from s_user where 1=1 order by create_time desc";
		String select="select * ";
		return User.dao.paginate(page, size, select, sql);
	}
	
	public boolean updateInfo(User data){
		return new User().setAttrs(data).update();
	}
	
	public boolean saveInfo(User data){
		return new User().setAttrs(data).save();
	}
	
	public int saveInfos(User data){
		User t = new User().setAttrs(data);
		t.save();
		return t.getInt("user_id");
	}
}
