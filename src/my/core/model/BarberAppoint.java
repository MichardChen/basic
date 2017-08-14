package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
@TableBind(table = "mk_barber_appoint", pk = "id")
public class BarberAppoint extends Model<BarberAppoint>{
	
		public static final BarberAppoint dao = new BarberAppoint();
		
		public List<Integer> queryAppoint(String day,String appointTypeCd){
			return Db.query("select * c.b from(select count(*) as a,barber_id as b from mk_barber_appoint where appoint_time like '%"+day+"%' and appoint_time_cd='"+appointTypeCd+"') as c where c.a<=3");
		}
}
