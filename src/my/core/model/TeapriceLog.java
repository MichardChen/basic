package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.StringUtil;

@TableBind(table = "t_teaprice_log", pk = "id")
public class TeapriceLog extends Model<TeapriceLog> {
	
	public static final TeapriceLog dao = new TeapriceLog();

	public TeapriceLog queryById(int id){
		return TeapriceLog.dao.findFirst("select * from t_teaprice_log where id = ?",id);
	}
	
	public List<TeapriceLog> queryTeapriceLogs(int teaId,String time1,String time2){
		return TeapriceLog.dao.find("select * from t_teaprice_log where create_time>='"+time1+"' and create_time<='"+time2+"' and tea_id="+teaId+" order by create_time asc");
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
	
	public Page<TeapriceLog> queryByPageParams(int page,int size,String name){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		if(StringUtil.isBlank(name)){
			String sql=" from t_teaprice_log where 1=1 "+strBuf+" order by create_time desc";
			String select="select * ";
			return TeapriceLog.dao.paginate(page, size, select, sql);
		}else{
			String sql=" from t_teaprice_log a inner join t_tea b on a.tea_id=b.id where 1=1 and b.tea_title like '%"+name+"%' order by a.create_time desc";
			String select="select * ";
			return TeapriceLog.dao.paginate(page, size, select, sql);
		}
	}
	
	public Page<TeapriceLog> queryByPage(int page,int size){
		
		String sql=" from t_teaprice_log where 1=1 order by create_time desc";
		String select="select * ";
		return TeapriceLog.dao.paginate(page, size, select, sql);
	}
}
