package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "mk_assistant_appoint", pk = "id")
public class AssistantAppoint extends Model<AssistantAppoint>{
		
	public static final AssistantAppoint dao = new AssistantAppoint();
	
	public List<Integer> queryAppoint(String day,String appointTypeCd){
		return Db.query("select  c.b from(select count(*) as a,barber_id as b from mk_assistant_appoint where appoint_time like '%"+day+"%' and appoint_time_cd='"+appointTypeCd+"') as c where c.a<3");
	}
}
