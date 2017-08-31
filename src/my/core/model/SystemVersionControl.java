package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "system_version_control", pk = "id")
public class SystemVersionControl extends Model<SystemVersionControl> {
		
		public static final SystemVersionControl dao = new SystemVersionControl();
		
		public SystemVersionControl querySystemVersionControlById(int id){
			return SystemVersionControl.dao.findFirst("select * from system_version_control where id=?",id);
		}
		
		public boolean saveInfo(SystemVersionControl data){
			return new SystemVersionControl().setAttrs(data).save();
		}
		
		public SystemVersionControl querySystemVersionControl(String typeCd){
			return SystemVersionControl.dao.findFirst("select * from system_version_control where version_type_cd=?",typeCd);
		}
		
		public Page<SystemVersionControl> queryByPage(int page,int size){

			String sql=" from system_version_control where 1=1 order by create_time desc";
			String select="select * ";
			return SystemVersionControl.dao.paginate(page, size, select, sql);
		}
		
		public boolean updateInfo(SystemVersionControl data){
			return new SystemVersionControl().setAttrs(data).update();
		}
}

