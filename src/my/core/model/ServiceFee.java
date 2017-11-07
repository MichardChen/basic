package my.core.model;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

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
}
