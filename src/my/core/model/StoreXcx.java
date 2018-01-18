package my.core.model;

import java.util.ArrayList;
import java.util.List;

import org.huadalink.plugin.tablebind.TableBind;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import my.pvcloud.util.DateUtil;
import my.pvcloud.util.StringUtil;

@TableBind(table = "t_store_xcx", pk = "id")
public class StoreXcx extends Model<StoreXcx>{
	
	public static final StoreXcx dao = new StoreXcx();

	public StoreXcx queryById(int id){
		return StoreXcx.dao.findFirst("select * from t_store_xcx where id = ?",id);
	}
	
	public boolean updateInfo(StoreXcx data){
		return new StoreXcx().setAttrs(data).update();
	}
	
	public boolean saveInfo(StoreXcx data){
		return new StoreXcx().setAttrs(data).save();
	}
	
	public Page<StoreXcx> queryListByPage(int page,int size,String appid){
		
		List<Object> param=new ArrayList<Object>();
		StringBuffer strBuf=new StringBuffer();
		String sql="";
		String select="select *";
		if(StringUtil.isNoneBlank(appid)){
			strBuf.append("and appid like '%"+appid+"%'");
		}
		
		sql=" from t_store_xcx where 1=1 "+strBuf.toString()+" order by create_time desc";
		return StoreXcx.dao.paginate(page, size, select, sql,param.toArray());
	}
	
	public Page<StoreXcx> queryByPage(int page,int size){
		String sql=" from t_store_xcx where 1=1 order by create_time desc";
		String select="select * ";
		return StoreXcx.dao.paginate(page, size, select, sql);
	}
}
