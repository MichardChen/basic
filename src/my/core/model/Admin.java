package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "s_user", pk = "user_id")
public class Admin extends Model<Admin> {
		
		public static final Admin dao = new Admin();
		
		public Admin queryAdmin(int id){
			return Admin.dao.findFirst("select * from s_user where user_id=?",id);
		}
		
		
		public Admin queryAdminByMobile(String mobile){
			return Admin.dao.findFirst("select * from s_user where mobile=?",mobile);
		}
		
		public void updatePwd(String mobile,String userPwd){
			Db.update("update s_user set password='"+userPwd+"',update_time='"+DateUtil.getNowTimestamp()+"' where mobile='"+mobile+"'");
		}
		
		public List<Admin> queryAdminByType(String roleIds,int pageSize,int pageNum,int store){
			int fromRow = (pageNum-1)*pageSize;
			return Admin.dao.find("select a.* from s_user a left join s_user_role b on a.user_id=b.user_id where a.store_id="+store+" and b.role_id in("+roleIds+") and a.effective_mark='1' order by a.create_time asc limit "+fromRow+","+pageSize+"");
		}
		
		public List<Admin> queryAdminByStoreType(int roleId,int storeId){
			return Admin.dao.find("select s_user.* from s_user inner join s_user_role on s_user.user_id=s_user_role.user_id where s_user.store_id="+storeId+" and s_user_role.role_id="+roleId+" order by s_user.create_time asc");
		}
		
		public int updateAdmin(int userId
							  ,String username
							  ,String sex
							  ,int ship
							  ,String status
							  ,String mark){
			
			return Db.update("update s_user set username='"+username+"',sex='"+sex+"',ship="+ship+",work_status='"+status+"',remark='"+mark+"' where user_id="+userId);
		}
		
		public int updatePoint(int userId,int ship,String status){
			return Db.update("update s_user set ship=ship+"+ship+",work_status='"+status+"' where user_id="+userId);
		}
		
		public int updateUserData(int userId,String userName,int sex,String icon){
			if(StringUtil.isNoneBlank(icon)){
				return Db.update("update s_user set username='"+userName+"',sex="+sex+",icon='"+icon+"' where user_id="+userId);
			}else{
				return Db.update("update s_user set username='"+userName+"',sex="+sex+" where user_id="+userId);
			}
		}
}
