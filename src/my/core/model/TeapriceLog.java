package my.core.model;

import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;

@TableBind(table = "t_teaprice_log", pk = "id")
public class TeapriceLog extends Model<TeapriceLog> {
	
	public static final TeapriceLog dao = new TeapriceLog();

	public TeapriceLog queryById(int id){
		return TeapriceLog.dao.findFirst("select * from t_teaprice_log where id = ?",id);
	}
	
	public List<TeapriceLog> queryTeapriceLogs(int teaId,String time1,String time2){
		return TeapriceLog.dao.find("select * from t_teaprice_log where create_time>='"+time1+"' and create_time<='"+time2+"' and tea_id="+teaId+" order by create_time desc");
	}
	
	public boolean updateInfo(TeapriceLog TeapriceLog){
		return new TeapriceLog().setAttrs(TeapriceLog).update();
	}
	
	public boolean saveInfo(TeapriceLog TeapriceLog){
		return new TeapriceLog().setAttrs(TeapriceLog).save();
	}
	
	public boolean del(int id){
		return TeapriceLog.dao.deleteById(id);
	}
}
