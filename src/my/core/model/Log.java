package my.core.model;

import java.util.ArrayList;
import java.util.List;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

@TableBind(table = "t_log", pk = "id")
public class Log extends Model<Log> {
	
	public static final Log dao = new Log();

	public boolean saveInfo(Log data){
		return new Log().setAttrs(data).save();
	}
	
	public void saveLogInfo(int userId,String userTypeCd,String mark){
		Log log = new Log();
		log.set("user_id", userId);
		log.set("user_type_cd", userTypeCd);
		log.set("mark", mark);
		log.set("create_time", DateUtil.getNowTimestamp());
		log.set("update_time", DateUtil.getNowTimestamp());
		new Log().setAttrs(log).save();
	}
	
	public Page<Log> queryLogByPage(int page,int size,String date,String operation){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(date)){
			strBuf.append("and create_time like '%"+date+"%'");
		}
		if(StringUtil.isNoneBlank(operation)){
			strBuf.append("and mark like '%"+operation+"%'");
		}
		
		sql=" from t_log where 1=1 "+strBuf.toString()+" order by create_time desc";
		return Log.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<Log> queryByPage(int page,int size){

		String sql=" from t_log where 1=1 order by create_time desc";
		String select="select * ";
		return Log.dao.paginate(page, size, select, sql);
	}
}
