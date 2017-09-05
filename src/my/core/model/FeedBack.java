package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.sun.jmx.snmp.Timestamp;
import com.sun.org.apache.bcel.internal.generic.NEW;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_feedback", pk = "id")
public class FeedBack extends Model<FeedBack> {
	
	public static final FeedBack dao = new FeedBack();

	public boolean saveInfo(FeedBack feedBack){
		return new FeedBack().setAttrs(feedBack).save();
	}
	
	public Page<FeedBack> queryFeedBackListByPage(int page,int size,String date){
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(date)){
			strBuf.append("and create_time>=?");
			param.add(DateUtil.formatStringForTimestamp(date+" 00:00:00"));
		}
		
		sql=" from t_feedback where 1=1 "+strBuf.toString()+" order by create_time desc";
		return FeedBack.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<FeedBack> queryByPage(int page,int size){

		String sql=" from t_feedback where 1=1 order by create_time desc";
		String select="select * ";
		return FeedBack.dao.paginate(page, size, select, sql);
	}
	
	public FeedBack queryById(int id){
		return FeedBack.dao.findFirst("select * from t_feedback where id = ?",id);
	}
	
	public boolean updateInfo(FeedBack tea){
		return new FeedBack().setAttrs(tea).update();
	}
	
	public boolean del(int id){
		return FeedBack.dao.deleteById(id);
	}
	
	public int updateFeedBackStatus(int id,int flg){
		Db.update("update t_feedback set readed="+flg+",update_time='"+DateUtil.getNowTimestamp()+"' where id="+id);
		FeedBack feedBack = FeedBack.dao.findFirst("select * from t_feedback where id = ?",id);
		if(feedBack != null){
			return feedBack.getInt("id");
		}else{
			return 0;
		}
	}
}
