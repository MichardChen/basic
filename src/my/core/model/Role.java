package my.core.model;

import java.util.ArrayList;
import java.util.List;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "s_role", pk = "role_id")
public class Role extends Model<Role> {
	public static final Role dao = new Role();

	public List<Role> getRole() {
		return Role.dao.find("select m.* from s_role m order by m.role_id asc");
	}
	
	public Page<Role> queryRoleListByPage(int page,int size,String date){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(date)){
			strBuf.append("and create_time>=?");
			param.add(DateUtil.formatStringForTimestamp(date+" 00:00:00"));
		}
		
		sql=" from s_role where 1=1 "+strBuf.toString()+" order by create_time desc";
		return Role.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<Role> queryByPage(int page,int size){

		String sql=" from s_role where 1=1 order by create_time desc";
		String select="select * ";
		return Role.dao.paginate(page, size, select, sql);
	}
	
	public Role queryById(int id){
		return Role.dao.findFirst("select * from s_role where role_id = ?",id);
	}
	
	public boolean updateInfo(Role data){
		return new Role().setAttrs(data).update();
	}
	
	public boolean saveInfo(Role data){
		return new Role().setAttrs(data).save();
	}
}
