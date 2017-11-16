package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.StringUtil;

@TableBind(table = "t_service_fee", pk = "id")
public class ServiceFee extends Model<ServiceFee> {
	
	public static final ServiceFee dao = new ServiceFee();
	
	public ServiceFee queryById(int id){
		return ServiceFee.dao.findFirst("select * from t_service_fee where id = ?",id);
	}
	
	public boolean updateInfo(ServiceFee data){
		return new ServiceFee().setAttrs(data).update();
	}
	
	public boolean saveInfo(ServiceFee data){
		return new ServiceFee().setAttrs(data).save();
	}
	
	public Page<ServiceFee> queryByPage(int page,int size){
		
		String sql=" from t_service_fee where 1=1 order by create_time desc";
		String select="select * ";
		return ServiceFee.dao.paginate(page, size, select, sql);
	}
	
	public Page<ServiceFee> queryByPageParams(int page,int size,int userId,String time,String mobile){
		
		String timeStr = "";
		if(StringUtil.isNoneBlank(time)){
			timeStr = " and a.create_time like '%"+time+"%'";
		}
		if((userId == 0)&&(StringUtil.isBlank(mobile))){
			String sql=" from t_service_fee a where 1=1 "+timeStr+" order by a.create_time desc";
			String select="select * ";
			return ServiceFee.dao.paginate(page, size, select, sql);
		}else{
			String sql=" from t_service_fee a inner join t_warehouse_tea_member b on a.wtm_id=b.id where 1=1 and b.member_id="+userId+timeStr+" order by a.create_time desc";
			String select="select * ";
			return ServiceFee.dao.paginate(page, size, select, sql);
		}
	}
}
